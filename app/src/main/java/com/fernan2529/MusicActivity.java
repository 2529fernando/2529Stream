package com.fernan2529;

import android.Manifest;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.fernan2529.ui.LoadingOverlayView; // ⬅️ Overlay de carga

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MusicActivity extends AppCompatActivity {

    // === Config ===
    private static final String ALLOWED_URL = "https://flacdownloader.com/";
    private static final String CHANNEL_ID = "downloads";
    private static final CharSequence CHANNEL_NAME = "Descargas";
    private static final int RC_WRITE = 1001;
    private static final long DEDUP_WINDOW_MS = 8000L; // 8 s

    private static final List<String> AD_DOMAINS = Arrays.asList(
            "doubleclick.net","ads.google.com","googlesyndication.com","googletagservices.com",
            "adservice.google.com","facebook.net","adform.net","outbrain.com","taboola.com"
    );

    private static final Set<String> DOWNLOAD_EXTS = new HashSet<>(Arrays.asList(
            "zip","rar","7z","tar","gz",
            "mp3","aac","flac","m4a","wav","ogg",
            "mp4","mkv","avi","mov","webm",
            "pdf","epub",
            "apk","xapk",
            "jpg","jpeg","png","gif","webp"
    ));

    private static final Set<String> AUDIO_VIDEO_IMG_EXTS = new HashSet<>(Arrays.asList(
            "mp3","flac","m4a","aac","wav","ogg","mp4","webm","mkv","avi","mov",
            "jpg","jpeg","png","gif","webp","pdf","epub","zip","rar","7z","apk","xapk"
    ));

    // === Estado ===
    private WebView webView;
    private Uri allowedOrigin;
    private final Map<String, Long> recentKeys = new LinkedHashMap<>();

    // Overlay de carga
    private LoadingOverlayView loadingOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        allowedOrigin = Uri.parse(ALLOWED_URL);
        requestLegacyWritePermissionIfNeeded();
        ensureDownloadChannel(); // <- canal de notificaciones

        // ⬇️ Inicializa y muestra el overlay de carga
        loadingOverlay = findViewById(R.id.loadingOverlay);
        if (loadingOverlay != null) loadingOverlay.showNow();

        webView = findViewById(R.id.webview);

        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setSupportMultipleWindows(false);
        s.setJavaScriptCanOpenWindowsAutomatically(false);
        s.setSupportZoom(false);
        s.setMediaPlaybackRequiresUserGesture(true);
        s.setAllowFileAccess(false);
        s.setAllowContentAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) s.setSafeBrowsingEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);

        webView.addJavascriptInterface(new BlobSaver(this), "AndroidDownloader");

        webView.setWebViewClient(new WebViewClient() {

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (request == null || request.getUrl() == null) return super.shouldInterceptRequest(view, request);
                String reqUrl = request.getUrl().toString().toLowerCase(Locale.US);
                for (String domain : AD_DOMAINS) {
                    if (reqUrl.contains(domain)) {
                        return new WebResourceResponse("text/plain", "utf-8",
                                new ByteArrayInputStream(new byte[0]));
                    }
                }
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // ⬇️ Mostrar overlay al iniciar una carga
                showLoadingOnPageStart();

                String lower = url == null ? "" : url.toLowerCase(Locale.US);
                for (String domain : AD_DOMAINS) {
                    if (lower.contains(domain)) {
                        view.stopLoading();
                        Toast.makeText(MusicActivity.this, "Popup/redirección bloqueada", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                super.onPageStarted(view, url, favicon);
            }

            @Override // >= 21
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (request == null || request.getUrl() == null) return true;
                return handleNavigation(view, request.getUrl().toString());
            }

            @Override // < 21
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return handleNavigation(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                String antiPopup = "(function(){try{"
                        + "window.open=function(){return null};"
                        + "document.addEventListener('click',function(e){"
                        + " var a=e.target.closest('a'); if(!a) return;"
                        + " if((a.getAttribute('target')||'').toLowerCase()==='_blank'){e.preventDefault();"
                        + "  var href=a.getAttribute('href')||''; if(href) location.href=href;}"
                        + "},true);}catch(e){}})();";
                view.evaluateJavascript(antiPopup, null);

                // Antiduplicado en JS para blob:
                String blobHook = "(function(){try{"
                        + "window.__androidDownloading=window.__androidDownloading||{};"
                        + "document.addEventListener('click',function(e){"
                        + " var a=e.target.closest('a'); if(!a) return;"
                        + " var href=a.getAttribute('href')||'';"
                        + " if(href.startsWith('blob:')){"
                        + "   e.preventDefault();"
                        + "   if(window.__androidDownloading[href]) return;"
                        + "   window.__androidDownloading[href]=true;"
                        + "   var name=(a.getAttribute('download')||('file_'+Date.now()));"
                        + "   fetch(href).then(r=>r.blob()).then(function(b){"
                        + "     var rd=new FileReader(); rd.onloadend=function(){"
                        + "       AndroidDownloader.saveBase64(rd.result,name);"
                        + "       setTimeout(function(){delete window.__androidDownloading[href]},8000);"
                        + "     }; rd.readAsDataURL(b);"
                        + "   }).catch(function(){delete window.__androidDownloading[href]});"
                        + " }"
                        + "},true);}catch(e){}})();";
                view.evaluateJavascript(blobHook, null);

                // ⬇️ Ocultar overlay al terminar
                if (loadingOverlay != null) loadingOverlay.fadeOut();

                super.onPageFinished(view, url);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, android.os.Message resultMsg) { return false; }
            @Override public boolean onJsBeforeUnload(WebView view, String url, String message, android.webkit.JsResult result) { if (result!=null) result.cancel(); return true; }
            @Override public boolean onShowFileChooser(WebView w, ValueCallback<Uri[]> c, FileChooserParams p) { Toast.makeText(MusicActivity.this,"Acción no permitida",Toast.LENGTH_SHORT).show(); return true; }

            // ⬇️ Progreso de carga para el overlay
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (loadingOverlay != null) {
                    float p = Math.max(0, Math.min(100, newProgress)) / 100f;
                    loadingOverlay.setProgress(p);
                    if (p >= 1f) loadingOverlay.fadeOut();
                }
                super.onProgressChanged(view, newProgress);
            }
        });

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                if (!shouldStartOnce("URL:" + (url==null?"":url))) return;
                // DownloadManager ya muestra su notificación
                startDownload(url, userAgent, contentDisposition, mimeType);
            }
        });

        webView.loadUrl(ALLOWED_URL);
    }

    // === Helper overlay ===
    private void showLoadingOnPageStart() {
        if (loadingOverlay != null) {
            loadingOverlay.setProgress(0f);
            loadingOverlay.showNow();
        }
    }

    // === Notificaciones ===
    private void ensureDownloadChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW
            );
            ch.setDescription("Progreso y finalización de descargas");
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(ch);
        }
    }

    private int notifyStart(String fileName) {
        int id = (int) (System.currentTimeMillis() & 0xFFFFFFF);
        NotificationCompat.Builder b = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle("Descargando…")
                .setContentText(fileName)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_LOW);
        NotificationManagerCompat.from(this).notify(id, b.build());
        return id;
    }

    private void notifyDone(int id, String fileName) {
        NotificationCompat.Builder b = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentTitle("Descarga completada")
                .setContentText(fileName)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_LOW);
        NotificationManagerCompat.from(this).notify(id, b.build());
    }

    // Manejo común de navegación
    private boolean handleNavigation(WebView view, String newUrl) {
        if (newUrl == null || newUrl.isEmpty()) return true;
        String lower = newUrl.toLowerCase(Locale.US);

        if (lower.startsWith("intent:")||lower.startsWith("market:")||lower.startsWith("mailto:")||
                lower.startsWith("tel:")||lower.startsWith("sms:")||lower.startsWith("geo:")||
                lower.startsWith("about:blank")) {
            Toast.makeText(this,"Enlace bloqueado",Toast.LENGTH_SHORT).show();
            return true;
        }

        if (lower.startsWith("blob:")) {
            if (!shouldStartOnce("BLOB:" + newUrl)) return true;
            String js = "(function(){var u='"+jsEscape(newUrl)+"',n='file_'+Date.now();"
                    + "try{fetch(u).then(r=>r.blob()).then(function(b){var R=new FileReader();"
                    + "R.onloadend=function(){AndroidDownloader.saveBase64(R.result,n)};"
                    + "R.readAsDataURL(b);});}catch(e){}})();";
            view.evaluateJavascript(js, null);
            return true;
        }

        if (isSameOrigin(newUrl)) {
            if (looksDownloadable(newUrl)) {
                if (!shouldStartOnce("URL:" + newUrl)) return true;
                startDownload(newUrl, null, null, null);
                return true;
            }
            return false;
        }

        Toast.makeText(this,"Navegación bloqueada",Toast.LENGTH_SHORT).show();
        return true;
    }

    // === DownloadManager (muestra su propia notificación) ===
    private void startDownload(String url, @Nullable String userAgent,
                               @Nullable String contentDisposition, @Nullable String mimeType) {
        String fileName = sanitizeFilename(URLUtil.guessFileName(url, contentDisposition, mimeType));
        try {
            DownloadManager.Request req = new DownloadManager.Request(Uri.parse(url));
            String cookies = CookieManager.getInstance().getCookie(url);
            if (cookies != null) req.addRequestHeader("Cookie", cookies);
            if (userAgent != null) req.addRequestHeader("User-Agent", userAgent);

            String ext = getExt(fileName);
            String fixedMime = strongMimeFromExt(ext);
            if (mimeType == null || mimeType.isEmpty() || "text/plain".equals(mimeType) || "application/octet-stream".equals(mimeType)) {
                mimeType = fixedMime != null ? fixedMime : mimeType;
            }
            if (mimeType != null) req.setMimeType(mimeType);

            req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            req.allowScanningByMediaScanner();
            req.setTitle(fileName);
            req.setDescription("Descargando…");
            req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

            DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            if (dm != null) dm.enqueue(req);

            Toast.makeText(this, "Descarga iniciada: " + fileName, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "No se pudo iniciar la descarga", Toast.LENGTH_SHORT).show();
        }
    }

    // === Guardado de blobs (dataURL -> bytes) con notificaciones propias ===
    private class BlobSaver {
        private final Context ctx;
        BlobSaver(Context ctx){ this.ctx = ctx.getApplicationContext(); }

        @JavascriptInterface
        public void saveBase64(String dataUrl, String suggestedName) {
            int noteId = -1;
            String cleanNameForNote = "archivo";
            try {
                String mime = "application/octet-stream";
                int comma = dataUrl != null ? dataUrl.indexOf(',') : -1;
                if (dataUrl != null && dataUrl.startsWith("data:")) {
                    int semi = dataUrl.indexOf(';');
                    int coma = dataUrl.indexOf(',');
                    if (semi > 5 && semi < coma) mime = dataUrl.substring(5, semi);
                }

                String cleanName = sanitizeFilename(suggestedName);
                String extFromName = getExt(cleanName);
                String base64 = (comma > 0) ? dataUrl.substring(comma + 1) : (dataUrl == null ? "" : dataUrl);
                byte[] bytes = Base64.decode(base64, Base64.DEFAULT);

                String extFromMime = guessExtensionFromMime(mime);
                if (extFromName == null || extFromName.isEmpty() || !AUDIO_VIDEO_IMG_EXTS.contains(extFromName)) {
                    if (extFromMime != null && !"bin".equals(extFromMime)) {
                        cleanName = stripExt(cleanName) + "." + extFromMime;
                    }
                }
                cleanNameForNote = cleanName;

                // Notificación de inicio (solo blobs; DownloadManager ya notifica sus URLs)
                noteId = notifyStart(cleanName);

                String finalExt = getExt(cleanName);
                String strongMime = strongMimeFromExt(finalExt);
                if (strongMime != null &&
                        ("text/plain".equals(mime) || "application/octet-stream".equals(mime) || mime == null)) {
                    mime = strongMime;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Downloads.DISPLAY_NAME, cleanName);
                    values.put(MediaStore.Downloads.MIME_TYPE, mime != null ? mime : "application/octet-stream");
                    values.put(MediaStore.Downloads.IS_PENDING, 1);

                    Uri collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
                    Uri item = ctx.getContentResolver().insert(collection, values);
                    if (item == null) throw new Exception("No se pudo crear archivo");

                    try (OutputStream os = ctx.getContentResolver().openOutputStream(item)) {
                        if (os == null) throw new Exception("No se pudo abrir output stream");
                        os.write(bytes);
                    }

                    values.clear();
                    values.put(MediaStore.Downloads.IS_PENDING, 0);
                    ctx.getContentResolver().update(item, values, null, null);

                } else {
                    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    if (!dir.exists()) dir.mkdirs();
                    File out = new File(dir, cleanName);
                    try (OutputStream os = new FileOutputStream(out)) {
                        os.write(bytes);
                    }
                }

                Toast.makeText(ctx, "Descargado: " + cleanName, Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Toast.makeText(ctx, "Error al guardar blob", Toast.LENGTH_SHORT).show();
            } finally {
                if (noteId != -1) notifyDone(noteId, cleanNameForNote);
            }
        }
    }

    // === util ext/mime ===
    private static String guessExtensionFromMime(String mime) {
        if (mime == null) return "bin";
        String ext = null;
        try { ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime); } catch (Exception ignored) {}
        if (ext != null && !ext.isEmpty()) return ext.toLowerCase(Locale.US);
        if ("audio/flac".equals(mime)) return "flac";
        if ("audio/mpeg".equals(mime)) return "mp3";
        if ("audio/mp4".equals(mime)) return "m4a";
        if ("application/pdf".equals(mime)) return "pdf";
        if ("image/png".equals(mime)) return "png";
        if ("image/jpeg".equals(mime)) return "jpg";
        if ("video/mp4".equals(mime)) return "mp4";
        return "bin";
    }

    private static String getExt(String name){
        if (name == null) return null;
        int dot = name.lastIndexOf('.');
        if (dot <= 0 || dot == name.length()-1) return null;
        return name.substring(dot+1).toLowerCase(Locale.US);
    }
    private static String stripExt(String name){
        if (name == null) return "";
        int dot = name.lastIndexOf('.');
        if (dot <= 0) return name;
        return name.substring(0, dot);
    }
    private static String strongMimeFromExt(String ext){
        if (ext == null) return null;
        Map<String,String> map = new HashMap<>();
        map.put("mp3","audio/mpeg"); map.put("flac","audio/flac"); map.put("m4a","audio/mp4");
        map.put("aac","audio/aac");  map.put("wav","audio/wav");   map.put("ogg","audio/ogg");
        map.put("mp4","video/mp4");  map.put("webm","video/webm"); map.put("mkv","video/x-matroska");
        map.put("avi","video/x-msvideo"); map.put("mov","video/quicktime");
        map.put("jpg","image/jpeg"); map.put("jpeg","image/jpeg"); map.put("png","image/png");
        map.put("gif","image/gif");  map.put("webp","image/webp");
        map.put("pdf","application/pdf"); map.put("epub","application/epub+zip");
        map.put("zip","application/zip"); map.put("rar","application/vnd.rar"); map.put("7z","application/x-7z-compressed");
        map.put("apk","application/vnd.android.package-archive"); map.put("xapk","application/vnd.android.package-archive");
        return map.get(ext);
    }

    // === de-dup y helpers ===
    private synchronized boolean shouldStartOnce(String key){
        long now = System.currentTimeMillis();
        recentKeys.entrySet().removeIf(e -> now - e.getValue() > DEDUP_WINDOW_MS);
        Long last = recentKeys.get(key);
        if (last != null && now - last < DEDUP_WINDOW_MS) return false;
        recentKeys.put(key, now);
        return true;
    }

    private boolean looksDownloadable(String url) {
        try {
            String lower = url.toLowerCase(Locale.US);
            int q = lower.indexOf('?');
            if (q >= 0) lower = lower.substring(0, q);
            int lastDot = lower.lastIndexOf('.');
            if (lastDot > 0 && lastDot < lower.length() - 1) {
                String ext = lower.substring(lastDot + 1);
                return DOWNLOAD_EXTS.contains(ext);
            }
        } catch (Exception ignored) {}
        return false;
    }

    private boolean isSameOrigin(String url) {
        try {
            Uri u = Uri.parse(url);
            if (u == null || allowedOrigin == null) return false;
            boolean scheme = safeEq(allowedOrigin.getScheme(), u.getScheme());
            boolean host = safeEq(allowedOrigin.getHost(), u.getHost());
            int portAllowed = allowedOrigin.getPort() == -1 ? defaultPort(allowedOrigin.getScheme()) : allowedOrigin.getPort();
            int portNew = u.getPort() == -1 ? defaultPort(u.getScheme()) : u.getPort();
            return scheme && host && portAllowed == portNew;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean safeEq(String a, String b) { return a != null && b != null && a.equalsIgnoreCase(b); }
    private static int defaultPort(String scheme) { if (scheme==null) return -1; if ("http".equalsIgnoreCase(scheme)) return 80; if ("https".equalsIgnoreCase(scheme)) return 443; return -1; }
    private static String jsEscape(String s) { if (s == null) return ""; return s.replace("\\","\\\\").replace("'","\\'"); }

    private void requestLegacyWritePermissionIfNeeded() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RC_WRITE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            if (isSameOrigin(webView.getUrl())) {
                webView.goBack();
            } else {
                webView.loadUrl(ALLOWED_URL);
            }
        } else {
            super.onBackPressed();
        }
    }

    private static String sanitizeFilename(String name) {
        if (name == null || name.trim().isEmpty()) return "file_" + System.currentTimeMillis();
        name = name.replaceAll("[\\\\/:*?\"<>|]", "_");
        name = name.replaceAll("\\s+", " ").trim();
        if (name.length() > 100) name = name.substring(0, 100);
        return name;
    }
}

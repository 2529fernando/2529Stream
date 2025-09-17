package com.fernan2529;

import android.Manifest;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MusicActivity extends AppCompatActivity {

    public static int downloadCount = 0;

    // URL única permitida para navegar (la página inicial)
    private static final String ALLOWED_URL = "https://flacdownloader.com/";

    // Canal de notificaciones para blobs
    private static final String BLOB_CHANNEL_ID = "blob_downloads";

    // Dominios de anuncios a bloquear (simple)
    private static final List<String> AD_DOMAINS = Arrays.asList(
            "doubleclick.net",
            "ads.google.com",
            "googlesyndication.com",
            "googletagservices.com",
            "adservice.google.com",
            "facebook.net",
            "adform.net",
            "outbrain.com",
            "taboola.com"
    );

    // Extensiones típicas descargables
    private static final Set<String> DOWNLOAD_EXTS = new HashSet<>(Arrays.asList(
            "zip","rar","7z","tar","gz",
            "mp3","aac","flac","m4a","wav","ogg",
            "mp4","mkv","avi","mov","webm",
            "pdf","epub",
            "apk","xapk",
            "jpg","jpeg","png","gif","webp"
    ));

    private static final int RC_WRITE = 1001;

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        // Crear canal para notificaciones de blobs (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(
                    BLOB_CHANNEL_ID,
                    "Descargas (blob)",
                    NotificationManager.IMPORTANCE_LOW
            );
            ch.setDescription("Notificaciones para descargas guardadas desde blobs");
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(ch);
        }

        // Permiso de escritura solo requerido en Android 9 o menor
        requestLegacyWritePermissionIfNeeded();

        webView = findViewById(R.id.webview);

        // Configuración del WebView
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setSupportMultipleWindows(false);
        s.setJavaScriptCanOpenWindowsAutomatically(false);

        // Bridge para manejar blobs desde JS
        webView.addJavascriptInterface(new BlobSaver(this), "AndroidDownloader");

        // Cliente: adblock + navegación limitada + inyección para capturar blob:
        webView.setWebViewClient(new WebViewClient() {

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String reqUrl = request.getUrl().toString().toLowerCase();
                for (String domain : AD_DOMAINS) {
                    if (reqUrl.contains(domain)) {
                        return new WebResourceResponse("text/plain", "utf-8",
                                new ByteArrayInputStream(new byte[0]));
                    }
                }
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String newUrl = request.getUrl().toString();

                // Permitir solo la URL inicial exacta
                if (newUrl.equals(ALLOWED_URL)) {
                    return false; // cargar normalmente
                }

                // Si es blob:, pedir al JS que lo convierta y lo envíe a Android
                if (newUrl.startsWith("blob:")) {
                    // Intento de nombre básico si no hay otro: file_<timestamp>
                    String fileName = "file_" + System.currentTimeMillis();
                    String js = "(function(){"
                            + "var url='" + newUrl + "';"
                            + "var name='" + fileName + "';"
                            + "try{"
                            + " fetch(url).then(function(r){return r.blob()}).then(function(b){"
                            + "   var reader=new FileReader();"
                            + "   reader.onloadend=function(){"
                            + "     // reader.result = dataURL (data:<mime>;base64,...)"
                            + "     window.AndroidDownloader.saveBase64(reader.result, name);"
                            + "   };"
                            + "   reader.readAsDataURL(b);"
                            + " }).catch(function(e){console.log(e);});"
                            + "}catch(e){console.log(e);}"
                            + "})();";
                    view.evaluateJavascript(js, null);
                    return true; // no navegar
                }

                // Si parece un archivo descargable por URL normal, descargar
                if (looksDownloadable(newUrl)) {
                    startDownload(newUrl, null, null, null);
                    return true;
                }

                // Bloquear cualquier otra navegación
                Toast.makeText(MusicActivity.this, "Navegación bloqueada", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // Inyecta un capturador de clicks a <a> con href blob: para obtener filename del atributo download, si existe.
                String hook = "(function(){"
                        + "document.addEventListener('click', function(e){"
                        + "  var a = e.target.closest('a');"
                        + "  if(!a) return;"
                        + "  var href = a.getAttribute('href')||'';"
                        + "  if(href.startsWith('blob:')){"
                        + "    e.preventDefault();"
                        + "    var name = a.getAttribute('download')||('file_'+Date.now());"
                        + "    fetch(href).then(r=>r.blob()).then(function(b){"
                        + "      var reader=new FileReader();"
                        + "      reader.onloadend=function(){"
                        + "        window.AndroidDownloader.saveBase64(reader.result, name);"
                        + "      };"
                        + "      reader.readAsDataURL(b);"
                        + "    }).catch(console.error);"
                        + "  }"
                        + "}, true);"
                        + "})();";
                view.evaluateJavascript(hook, null);
                super.onPageFinished(view, url);
            }
        });

        // Bloquear nuevas ventanas / popups
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog,
                                          boolean isUserGesture, android.os.Message resultMsg) {
                return false; // no crear nuevas ventanas
            }
        });

        // Listener de descargas (para attachments reales)
        webView.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {
            startDownload(url, userAgent, contentDisposition, mimeType);
        });

        // Cargar la página inicial
        webView.loadUrl(ALLOWED_URL);
    }

    // === Descargas por URL http/https (DownloadManager) ===
    private void startDownload(String url, @Nullable String userAgent,
                               @Nullable String contentDisposition, @Nullable String mimeType) {

        // Ignorar la segunda descarga
        if (downloadCount == 1) {
            downloadCount++;
            Toast.makeText(this, "La segunda descarga fue eliminada", Toast.LENGTH_SHORT).show();
            return;
        }

        downloadCount++; // contar este intento

        String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);

        try {
            DownloadManager.Request req = new DownloadManager.Request(Uri.parse(url));

            String cookies = CookieManager.getInstance().getCookie(url);
            if (cookies != null) req.addRequestHeader("Cookie", cookies);
            if (userAgent != null) req.addRequestHeader("User-Agent", userAgent);
            if (mimeType != null) req.setMimeType(mimeType);

            // 👉 Notificación del sistema para descargas normales
            req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            req.allowScanningByMediaScanner();
            req.setTitle(fileName);
            req.setDescription("Descargando…");
            req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

            DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            dm.enqueue(req);

            Toast.makeText(this, "Descarga iniciada: " + fileName, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "No se pudo iniciar la descarga", Toast.LENGTH_SHORT).show();
        }
    }

    // === Guardado de blobs (dataURL -> bytes) ===
    private static class BlobSaver {
        private final Context ctx;
        BlobSaver(Context ctx){ this.ctx = ctx.getApplicationContext(); }

        @JavascriptInterface
        public void saveBase64(String dataUrl, String fileName) {
            // Ignorar la segunda descarga
            if (MusicActivity.downloadCount == 1) {
                MusicActivity.downloadCount++;
                Toast.makeText(ctx, "La segunda descarga (blob) fue eliminada", Toast.LENGTH_SHORT).show();
                return;
            }
            MusicActivity.downloadCount++;

            try {
                int comma = dataUrl.indexOf(',');
                String base64 = (comma > 0) ? dataUrl.substring(comma + 1) : dataUrl;
                byte[] bytes = Base64.decode(base64, Base64.DEFAULT);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                    values.put(MediaStore.Downloads.MIME_TYPE, "application/octet-stream");
                    values.put(MediaStore.Downloads.IS_PENDING, 1);

                    Uri collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
                    Uri item = ctx.getContentResolver().insert(collection, values);
                    if (item == null) throw new Exception("No se pudo crear archivo");

                    try (OutputStream os = ctx.getContentResolver().openOutputStream(item)) {
                        os.write(bytes);
                    }

                    values.clear();
                    values.put(MediaStore.Downloads.IS_PENDING, 0);
                    ctx.getContentResolver().update(item, values, null, null);

                } else {
                    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    if (!dir.exists()) dir.mkdirs();
                    File out = new File(dir, fileName);
                    try (OutputStream os = new FileOutputStream(out)) {
                        os.write(bytes);
                    }
                }

                Toast.makeText(ctx, "Descargado: " + fileName, Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Toast.makeText(ctx, "Error al guardar blob", Toast.LENGTH_SHORT).show();
            }
        }
        private String guessExtensionFromMime(String mime) {
            String ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime);
            if (ext == null || ext.isEmpty()) {
                if (mime.equals("audio/flac")) return "flac";
                if (mime.equals("audio/mpeg")) return "mp3";
                if (mime.equals("audio/mp4")) return "m4a";
                if (mime.equals("application/pdf")) return "pdf";
                if (mime.equals("image/png")) return "png";
                if (mime.equals("image/jpeg")) return "jpg";
                if (mime.equals("video/mp4")) return "mp4";
                return "bin";
            }
            return ext;
        }
    }

    private boolean looksDownloadable(String url) {
        try {
            String lower = url.toLowerCase();
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

    // === Permisos (solo Android 9 o menor) ===
    private void requestLegacyWritePermissionIfNeeded() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) { // API 28 o menor
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        RC_WRITE
                );
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Normalmente no habrá historial, pero por si recargan la misma URL
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}

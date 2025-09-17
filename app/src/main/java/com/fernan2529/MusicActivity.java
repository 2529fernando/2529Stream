package com.fernan2529;

import android.Manifest;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.LongSparseArray;
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

    private static final String ALLOWED_URL = "https://flacdownloader.com/";

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

    private static final int RC_WRITE = 1001;
    private static final int RC_POST_NOTIF = 1002;

    private static final String DL_CHANNEL_ID = "downloads_progress";
    private static final String BLOB_CHANNEL_ID = "blob_saves";

    private WebView webView;

    // Para cerrar bien notificaciones al finalizar
    private final LongSparseArray<Integer> idToNotif = new LongSparseArray<>();
    private final LongSparseArray<String>  idToName  = new LongSparseArray<>();
    private BroadcastReceiver downloadDoneReceiver;

    // Action para el botón "Cerrar" de la notificación
    public static final String ACTION_DISMISS = "com.fernan2529.ACTION_DISMISS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        requestLegacyWritePermissionIfNeeded();
        requestPostNotificationsIfNeeded();
        ensureDownloadChannel();
        ensureBlobChannel();

        webView = findViewById(R.id.webview);

        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setSupportMultipleWindows(false);
        s.setJavaScriptCanOpenWindowsAutomatically(false);

        webView.addJavascriptInterface(new BlobSaver(getApplicationContext()), "AndroidDownloader");

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

                if (newUrl.equals(ALLOWED_URL)) {
                    return false;
                }

                if (newUrl.startsWith("blob:")) {
                    String fileName = "file_" + System.currentTimeMillis();
                    String js = "(function(){"
                            + "var url='" + newUrl + "';"
                            + "var name='" + fileName + "';"
                            + "try{"
                            + " fetch(url).then(function(r){return r.blob()}).then(function(b){"
                            + "   var reader=new FileReader();"
                            + "   reader.onloadend=function(){"
                            + "     window.AndroidDownloader.saveBase64(reader.result, name);"
                            + "   };"
                            + "   reader.readAsDataURL(b);"
                            + " }).catch(function(e){console.log(e);});"
                            + "}catch(e){console.log(e);}"
                            + "})();";
                    view.evaluateJavascript(js, null);
                    return true;
                }

                if (looksDownloadable(newUrl)) {
                    startDownload(newUrl, null, null, null);
                    return true;
                }

                Toast.makeText(MusicActivity.this, "Navegación bloqueada", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
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

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog,
                                          boolean isUserGesture, android.os.Message resultMsg) {
                return false;
            }
        });

        // DownloadListener correcto (5 params)
        webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            startDownload(url, userAgent, contentDisposition, mimetype);
        });

        // Receiver para cerrar notificación al terminar (y poner botón Abrir si quieres)
        downloadDoneReceiver = new BroadcastReceiver() {
            @Override public void onReceive(Context ctx, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (id == -1) return;

                Integer notifId = idToNotif.get(id);
                String fileName = idToName.get(id);
                if (notifId == null) return;

                DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Query q = new DownloadManager.Query().setFilterById(id);
                try (Cursor c = dm.query(q)) {
                    if (c != null && c.moveToFirst()) {
                        int status = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
                        String mime  = c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_MEDIA_TYPE));
                        String local = c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI));

                        NotificationManagerCompat nmc = NotificationManagerCompat.from(MusicActivity.this);
                        NotificationCompat.Builder nb = new NotificationCompat.Builder(MusicActivity.this, DL_CHANNEL_ID)
                                .setSmallIcon(status == DownloadManager.STATUS_SUCCESSFUL
                                        ? android.R.drawable.stat_sys_download_done
                                        : android.R.drawable.stat_notify_error)
                                .setContentTitle(fileName != null ? fileName : "Descarga")
                                .setOnlyAlertOnce(true)
                                .setOngoing(false)
                                .setPriority(NotificationCompat.PRIORITY_LOW)
                                .setProgress(0, 0, false)
                                .setContentText(status == DownloadManager.STATUS_SUCCESSFUL
                                        ? "Descarga completa"
                                        : "Descarga fallida")
                                .setAutoCancel(true);

                        // Botón "Cerrar" también en la final
                        nb.addAction(android.R.drawable.ic_menu_close_clear_cancel,
                                "Cerrar", makeDismissPendingIntent(notifId));

                        if (status == DownloadManager.STATUS_SUCCESSFUL && local != null) {
                            Uri uri = Uri.parse(local);
                            Intent open = new Intent(Intent.ACTION_VIEW);
                            open.setDataAndType(uri, mime != null ? mime : "*/*");
                            open.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            int flags = Build.VERSION.SDK_INT >= 23
                                    ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                                    : PendingIntent.FLAG_UPDATE_CURRENT;
                            PendingIntent pi = PendingIntent.getActivity(MusicActivity.this, notifId, open, flags);
                            nb.setContentIntent(pi);
                        }

                        nmc.notify(notifId, nb.build());
                    }
                } catch (Exception ignored) {}
                idToNotif.remove(id);
                idToName.remove(id);
            }
        };
        registerReceiver(downloadDoneReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        webView.loadUrl(ALLOWED_URL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (downloadDoneReceiver != null) {
            try { unregisterReceiver(downloadDoneReceiver); } catch (Exception ignored) {}
            downloadDoneReceiver = null;
        }
    }

    // === Descarga por URL con notificación y botón "Cerrar" ===
    private void startDownload(String url, @Nullable String userAgent,
                               @Nullable String contentDisposition, @Nullable String mimeType) {

        if (downloadCount == 1) {
            downloadCount++;
            Toast.makeText(this, "La segunda descarga fue eliminada", Toast.LENGTH_SHORT).show();
            return;
        }
        downloadCount++;

        String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);

        try {
            DownloadManager.Request req = new DownloadManager.Request(Uri.parse(url));

            String cookies = CookieManager.getInstance().getCookie(url);
            if (cookies != null) req.addRequestHeader("Cookie", cookies);
            if (userAgent != null) req.addRequestHeader("User-Agent", userAgent);
            if (mimeType != null) req.setMimeType(mimeType);

            // Evitar notificación del sistema (usamos la nuestra)
            req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
            req.allowScanningByMediaScanner();
            req.setTitle(fileName);
            req.setDescription("Descargando…");
            req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

            DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            long downloadId = dm.enqueue(req);

            // Mapea ids para el cierre final
            int notifId = (int) (downloadId & 0x7FFFFFFF);
            idToNotif.put(downloadId, notifId);
            idToName.put(downloadId, fileName);

            NotificationManagerCompat nmc = NotificationManagerCompat.from(this);
            NotificationCompat.Builder nb = new NotificationCompat.Builder(this, DL_CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setContentTitle(fileName)
                    .setContentText("Descargando…")
                    .setOnlyAlertOnce(true)
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setProgress(100, 0, true);

            // Botón "Cerrar" en la notificación de progreso
            nb.addAction(android.R.drawable.ic_menu_close_clear_cancel,
                    "Cerrar", makeDismissPendingIntent(notifId));

            nmc.notify(notifId, nb.build());

            // Polling progreso
            new Thread(() -> {
                boolean done = false;
                int lastPercent = -1;
                while (!done) {
                    try {
                        DownloadManager.Query q = new DownloadManager.Query().setFilterById(downloadId);
                        try (Cursor c = dm.query(q)) {
                            if (c != null && c.moveToFirst()) {
                                int bytes = c.getInt(c.getColumnIndexOrThrow(
                                        DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                                int total = c.getInt(c.getColumnIndexOrThrow(
                                        DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                                int status = c.getInt(c.getColumnIndexOrThrow(
                                        DownloadManager.COLUMN_STATUS));

                                if (status == DownloadManager.STATUS_SUCCESSFUL ||
                                        status == DownloadManager.STATUS_FAILED) {
                                    // El receiver pintará la final
                                    done = true;
                                    break;
                                }

                                if (total > 0) {
                                    int percent = (int) (bytes * 100L / total);
                                    if (percent != lastPercent) {
                                        lastPercent = percent;
                                        nb.setProgress(100, percent, false)
                                                .setContentText(percent + "%");
                                        nmc.notify(notifId, nb.build());
                                    }
                                } else {
                                    nb.setProgress(0, 0, true);
                                    nmc.notify(notifId, nb.build());
                                }
                            }
                        }
                        Thread.sleep(700);
                    } catch (Exception ignored) { }
                }
            }).start();

            Toast.makeText(this, "Descarga iniciada: " + fileName, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "No se pudo iniciar la descarga", Toast.LENGTH_SHORT).show();
        }
    }

    // Crea el PendingIntent para el botón "Cerrar"
    private PendingIntent makeDismissPendingIntent(int notifId) {
        Intent dismissIntent = new Intent(this, NotificationDismissReceiver.class);
        dismissIntent.setAction(ACTION_DISMISS);
        dismissIntent.putExtra("notifId", notifId);
        int flags = Build.VERSION.SDK_INT >= 23
                ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                : PendingIntent.FLAG_UPDATE_CURRENT;
        return PendingIntent.getBroadcast(this, notifId, dismissIntent, flags);
    }

    // === Guardado de blobs con notificación y botón "Cerrar" ===
    private static class BlobSaver {
        private final Context ctx;
        BlobSaver(Context ctx){ this.ctx = ctx.getApplicationContext(); }

        @JavascriptInterface
        public void saveBase64(String dataUrl, String fileName) {
            if (MusicActivity.downloadCount == 1) {
                MusicActivity.downloadCount++;
                Toast.makeText(ctx, "La segunda descarga (blob) fue eliminada", Toast.LENGTH_SHORT).show();
                return;
            }
            MusicActivity.downloadCount++;

            int notifId = ("blob_" + System.currentTimeMillis()).hashCode();
            NotificationManagerCompat nmc = NotificationManagerCompat.from(ctx);
            NotificationCompat.Builder nb = new NotificationCompat.Builder(ctx, BLOB_CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setContentTitle(fileName)
                    .setContentText("Guardando…")
                    .setOnlyAlertOnce(true)
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setProgress(100, 0, false);

            // Botón "Cerrar" (reutiliza el receiver)
            Intent dismissIntent = new Intent(ctx, NotificationDismissReceiver.class);
            dismissIntent.setAction(MusicActivity.ACTION_DISMISS);
            dismissIntent.putExtra("notifId", notifId);
            int flags = Build.VERSION.SDK_INT >= 23
                    ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                    : PendingIntent.FLAG_UPDATE_CURRENT;
            PendingIntent dismissPending = PendingIntent.getBroadcast(ctx, notifId, dismissIntent, flags);
            nb.addAction(android.R.drawable.ic_menu_close_clear_cancel, "Cerrar", dismissPending);

            try {
                int comma = dataUrl.indexOf(',');
                String base64 = (comma > 0) ? dataUrl.substring(comma + 1) : dataUrl;
                byte[] bytes = Base64.decode(base64, Base64.DEFAULT);

                nmc.notify(notifId, nb.build());

                Uri target;
                OutputStream os;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                    values.put(MediaStore.Downloads.MIME_TYPE, "application/octet-stream");
                    values.put(MediaStore.Downloads.IS_PENDING, 1);
                    target = ctx.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                    if (target == null) throw new Exception("No se pudo crear archivo");
                    os = ctx.getContentResolver().openOutputStream(target);
                } else {
                    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    if (!dir.exists()) dir.mkdirs();
                    File out = new File(dir, fileName);
                    target = Uri.fromFile(out);
                    os = new FileOutputStream(out);
                }
                if (os == null) throw new Exception("No se pudo abrir OutputStream");

                int total = bytes.length;
                int written = 0, lastPercent = -1;
                try (OutputStream out = os) {
                    int off = 0;
                    int chunk = 64 * 1024;
                    while (off < total) {
                        int len = Math.min(chunk, total - off);
                        out.write(bytes, off, len);
                        off += len;
                        written = off;

                        int percent = (int) (written * 100L / total);
                        if (percent != lastPercent) {
                            lastPercent = percent;
                            nb.setProgress(100, percent, false)
                                    .setContentText(percent + "%");
                            nmc.notify(notifId, nb.build());
                        }
                    }
                    out.flush();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentValues done = new ContentValues();
                    done.put(MediaStore.Downloads.IS_PENDING, 0);
                    ctx.getContentResolver().update(target, done, null, null);
                }

                nb.setOngoing(false)
                        .setSmallIcon(android.R.drawable.stat_sys_download_done)
                        .setProgress(0, 0, false)
                        .setContentText("Guardado")
                        .setAutoCancel(true);
                nmc.notify(notifId, nb.build());

                Toast.makeText(ctx, "Descargado: " + fileName, Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                NotificationCompat.Builder err = new NotificationCompat.Builder(ctx, BLOB_CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.stat_notify_error)
                        .setContentTitle(fileName)
                        .setContentText("Error al guardar")
                        .setAutoCancel(true);
                nmc.notify(notifId, err.build());
                Toast.makeText(ctx, "Error al guardar blob", Toast.LENGTH_SHORT).show();
            }
        }

        @SuppressWarnings("unused")
        private String guessExtensionFromMime(String mime) {
            String ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime);
            if (ext == null || ext.isEmpty()) {
                if ("audio/flac".equals(mime)) return "flac";
                if ("audio/mpeg".equals(mime)) return "mp3";
                if ("audio/mp4".equals(mime)) return "m4a";
                if ("application/pdf".equals(mime)) return "pdf";
                if ("image/png".equals(mime)) return "png";
                if ("image/jpeg".equals(mime)) return "jpg";
                if ("video/mp4".equals(mime)) return "mp4";
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

    // === Permisos ===
    private void requestLegacyWritePermissionIfNeeded() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
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

    private void requestPostNotificationsIfNeeded() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        RC_POST_NOTIF
                );
            }
        }
    }

    // === Canales ===
    private void ensureDownloadChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm.getNotificationChannel(DL_CHANNEL_ID) == null) {
                NotificationChannel ch = new NotificationChannel(
                        DL_CHANNEL_ID, "Descargas", NotificationManager.IMPORTANCE_LOW);
                ch.setDescription("Progreso de descargas");
                nm.createNotificationChannel(ch);
            }
        }
    }

    private void ensureBlobChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm.getNotificationChannel(BLOB_CHANNEL_ID) == null) {
                NotificationChannel ch = new NotificationChannel(
                        BLOB_CHANNEL_ID, "Guardado de archivos", NotificationManager.IMPORTANCE_LOW);
                ch.setDescription("Progreso al guardar blobs");
                nm.createNotificationChannel(ch);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}

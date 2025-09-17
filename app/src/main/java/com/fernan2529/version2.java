package com.fernan2529;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import okhttp3.ConnectionPool;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class version2 extends AppCompatActivity {

    private WebView webView;

    // <input type="file">
    private ValueCallback<Uri[]> filePathCallback;
    private ActivityResultLauncher<Intent> fileChooserLauncher;

    // Permisos (solo <= Android 9)
    private ActivityResultLauncher<String[]> legacyPermsLauncher;

    public static final String EXTRA_URL = "EXTRA_URL";
    private String initialUrl = "https://2529sebastian.blogspot.com/2025/09/2529stream.html";

    // Downloader
    private static final int MAX_PARTS = 8;
    private static final int CONNECT_TIMEOUT = 15;
    private static final int READ_TIMEOUT = 60;
    private static final int WRITE_TIMEOUT = 60;

    private OkHttpClient http;
    private final ExecutorService bg = Executors.newCachedThreadPool();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version2);

        String urlFromIntent = getIntent().getStringExtra(EXTRA_URL);
        if (urlFromIntent != null && !urlFromIntent.trim().isEmpty()) {
            initialUrl = urlFromIntent.trim();
        }

        http = new OkHttpClient.Builder()
                .protocols(java.util.Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1))
                .connectionPool(new ConnectionPool(16, 5, TimeUnit.MINUTES))
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();

        setupActivityResultLaunchers();

        webView = findViewById(R.id.webView);
        setupWebView(webView);

        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        } else {
            webView.loadUrl(initialUrl);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bg.shutdownNow();
    }

    private void setupActivityResultLaunchers() {
        fileChooserLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (filePathCallback == null) return;
                        Uri[] uris = null;
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Intent data = result.getData();
                            if (data.getData() != null) {
                                uris = new Uri[]{ data.getData() };
                            } else if (data.getClipData() != null) {
                                ClipData clip = data.getClipData();
                                uris = new Uri[clip.getItemCount()];
                                for (int i = 0; i < clip.getItemCount(); i++) {
                                    uris[i] = clip.getItemAt(i).getUri();
                                }
                            }
                        }
                        filePathCallback.onReceiveValue(uris);
                        filePathCallback = null;
                    }
                }
        );

        legacyPermsLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> { /* sin UI */ }
        );
    }

    private void setupWebView(@NonNull WebView wv) {
        WebSettings ws = wv.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setAllowFileAccess(true);
        ws.setAllowContentAccess(true);
        ws.setLoadWithOverviewMode(true);
        ws.setUseWideViewPort(true);
        ws.setBuiltInZoomControls(true);
        ws.setDisplayZoomControls(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager.getInstance().setAcceptThirdPartyCookies(wv, true);
        }
        CookieManager.getInstance().setAcceptCookie(true);

        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String scheme = uri.getScheme() != null ? uri.getScheme() : "";
                if (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https")) {
                    return false;
                } else {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(version2.this, "No hay app para abrir este enlace.", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            }
        });

        // Importante: solo disparamos una TAREA EN BACKGROUND
        wv.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) ->
                bg.execute(() -> handleFastApkDownload_BG(url, userAgent, contentDisposition, mimetype))
        );

        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView v, ValueCallback<Uri[]> filePath, FileChooserParams fileChooserParams) {
                filePathCallback = filePath;
                Intent intent;
                try {
                    intent = fileChooserParams.createIntent();
                } catch (Exception e) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                }
                try {
                    fileChooserLauncher.launch(intent);
                } catch (ActivityNotFoundException e) {
                    Intent fallback = new Intent(Intent.ACTION_GET_CONTENT);
                    fallback.addCategory(Intent.CATEGORY_OPENABLE);
                    fallback.setType("*/*");
                    fileChooserLauncher.launch(fallback);
                }
                return true;
            }
        });
    }

    // ================== BACKGROUND DOWNLOAD ==================

    private void uiToast(String msg) {
        runOnUiThread(() -> Toast.makeText(version2.this, msg, Toast.LENGTH_LONG).show());
    }

    private void handleFastApkDownload_BG(String url, String userAgent, String contentDisposition, String mimeTypeIn) {
        try {
            // Validación APK
            String guessed = URLUtil.guessFileName(url, contentDisposition, mimeTypeIn);
            if (guessed == null || guessed.trim().isEmpty()) guessed = "app-" + System.currentTimeMillis() + ".apk";
            String filename = ensureApkExtension(guessed);

            String lowerUrl = url == null ? "" : url.toLowerCase(Locale.US);
            String lowerCd  = contentDisposition == null ? "" : contentDisposition.toLowerCase(Locale.US);
            String lowerMime = mimeTypeIn == null ? "" : mimeTypeIn.toLowerCase(Locale.US);

            boolean looksLikeApk =
                    filename.toLowerCase(Locale.US).endsWith(".apk") ||
                            lowerUrl.contains(".apk") ||
                            lowerCd.contains(".apk") ||
                            "application/vnd.android.package-archive".equals(lowerMime) ||
                            "application/octet-stream".equals(lowerMime);

            if (!looksLikeApk) {
                uiToast("Solo se permiten descargas .apk");
                return;
            }

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    legacyPermsLauncher.launch(new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE });
                    uiToast("Otorga permisos y vuelve a intentar la descarga.");
                    return;
                }
            }

            File base = getExternalFilesDir(null);
            if (base == null) {
                uiToast("No hay almacenamiento disponible.");
                return;
            }
            File outApk = new File(base, filename);

            // Cabeceras comunes
            okhttp3.Headers.Builder hb = new okhttp3.Headers.Builder();
            String cookies = CookieManager.getInstance().getCookie(url);
            if (cookies != null) hb.add("Cookie", cookies);
            if (userAgent != null) hb.add("User-Agent", userAgent);
            okhttp3.Headers commonHeaders = hb.build();

            // HEAD
            Request headReq = new Request.Builder().url(url).headers(commonHeaders).head().build();
            try (Response head = http.newCall(headReq).execute()) {
                long length = parseContentLength(head);
                boolean supportsRanges = "bytes".equalsIgnoreCase(head.header("Accept-Ranges"))
                        || hasContentRange(head);

                if (length > 0 && supportsRanges) {
                    turboDownloadWithRanges(url, commonHeaders, length, outApk);
                    uiToast("Descarga completada (multiconexión)");
                } else {
                    singleStreamDownload(url, commonHeaders, outApk);
                    uiToast("Descarga completada");
                }
            }

            if (maybeRequestUnknownSources()) {
                uiToast("Habilita 'Instalar apps de esta fuente' y vuelve a intentar.");
                return;
            }
            installApk(outApk);

        } catch (Exception e) {
            uiToast("Error en descarga: " + e.getMessage());
        }
    }

    private void singleStreamDownload(String url, okhttp3.Headers headers, File outFile) throws IOException {
        Request req = new Request.Builder().url(url).headers(headers).get().build();
        try (Response resp = http.newCall(req).execute()) {
            if (!resp.isSuccessful()) throw new IOException("HTTP " + resp.code());
            ResponseBody body = resp.body();
            if (body == null) throw new IOException("Cuerpo vacío");
            if (outFile.exists()) outFile.delete();
            try (RandomAccessFile raf = new RandomAccessFile(outFile, "rw")) {
                byte[] buf = new byte[512 * 1024];
                int n;
                while ((n = body.byteStream().read(buf)) != -1) {
                    raf.write(buf, 0, n);
                }
            }
        }
    }

    private void turboDownloadWithRanges(String url, okhttp3.Headers headers, long length, File outFile) throws IOException {
        int cores = Math.max(2, Runtime.getRuntime().availableProcessors());
        int parts = Math.min(MAX_PARTS, cores * 2);

        if (outFile.exists()) outFile.delete();
        try (RandomAccessFile raf = new RandomAccessFile(outFile, "rw")) {
            raf.setLength(length);
        }

        long partSize = Math.max(1, length / parts);
        ExecutorService pool = Executors.newFixedThreadPool(parts);
        CountDownLatch latch = new CountDownLatch(parts);
        long startPos = 0;
        for (int i = 0; i < parts; i++) {
            final long start = startPos;
            final long end = (i == parts - 1) ? (length - 1) : (start + partSize - 1);
            startPos = end + 1;

            pool.execute(() -> {
                try {
                    okhttp3.Headers h = headers.newBuilder()
                            .add("Range", "bytes=" + start + "-" + end)
                            .build();
                    Request partReq = new Request.Builder().url(url).headers(h).get().build();
                    try (Response partResp = http.newCall(partReq).execute()) {
                        if (partResp.code() != 206 && partResp.code() != 200) {
                            throw new IOException("HTTP " + partResp.code());
                        }
                        ResponseBody body = partResp.body();
                        if (body == null) throw new IOException("Cuerpo vacío");
                        try (RandomAccessFile raf = new RandomAccessFile(outFile, "rw")) {
                            raf.seek(start);
                            byte[] buf = new byte[512 * 1024];
                            int n;
                            while ((n = body.byteStream().read(buf)) != -1) {
                                raf.write(buf, 0, n);
                            }
                        }
                    }
                } catch (Exception ignore) {
                    pool.shutdownNow();
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            boolean ok = latch.await(15, TimeUnit.MINUTES);
            pool.shutdownNow();
            if (!ok) throw new IOException("Tiempo de descarga agotado");
        } catch (InterruptedException e) {
            pool.shutdownNow();
            throw new IOException("Descarga interrumpida");
        }
    }

    private long parseContentLength(Response head) {
        try {
            String cl = head.header("Content-Length");
            if (cl != null) return Long.parseLong(cl);
        } catch (Exception ignored) {}
        return -1;
    }

    private boolean hasContentRange(Response head) {
        String cr = head.header("Content-Range");
        return cr != null && cr.toLowerCase(Locale.US).startsWith("bytes");
    }

    private boolean maybeRequestUnknownSources() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean allowed = getPackageManager().canRequestPackageInstalls();
            if (!allowed) {
                Intent i = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                        Uri.parse("package:" + getPackageName()));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                return true;
            }
        }
        return false;
    }

    private void installApk(@NonNull File apkFile) {
        try {
            Uri contentUri = FileProvider.getUriForFile(
                    this, getPackageName() + ".fileprovider", apkFile);
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setDataAndType(contentUri, "application/vnd.android.package-archive");
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(install);
        } catch (ActivityNotFoundException e) {
            uiToast("No se encontró instalador de paquetes.");
        } catch (Exception e) {
            uiToast("Error al iniciar instalación: " + e.getMessage());
        }
    }

    private String ensureApkExtension(String name) {
        String trimmed = name == null ? "" : name.trim();
        if (!trimmed.toLowerCase(Locale.US).endsWith(".apk")) {
            int dot = trimmed.lastIndexOf('.');
            if (dot > 0) trimmed = trimmed.substring(0, dot);
            trimmed = trimmed + ".apk";
        }
        return trimmed;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (webView != null) webView.saveState(outState);
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

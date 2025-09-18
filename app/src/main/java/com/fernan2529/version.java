package com.fernan2529;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.fernan2529.ui.LoadingOverlayView;

import java.util.Locale;

public class version extends AppCompatActivity {

    public static final String EXTRA_URL = "EXTRA_URL";

    private WebView webView;
    private LoadingOverlayView loadingOverlay;

    private ValueCallback<Uri[]> filePathCallback;
    private ActivityResultLauncher<Intent> fileChooserLauncher;
    private ActivityResultLauncher<String[]> legacyPermsLauncher;

    private String initialUrl = "https://2529sebastian.blogspot.com/2025/09/2529stream.html";
    private long currentDownloadId = -1L;

    private final BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L);
            if (id == currentDownloadId && id != -1L) {
                handleDownloadComplete(id);
                currentDownloadId = -1L; // evitar repeticiones
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version);

        loadingOverlay = findViewById(R.id.loadingOverlay);
        if (loadingOverlay != null) loadingOverlay.showNow();

        // Registrar receiver con compat para API 33+
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(downloadReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(downloadReceiver, filter);
        }

        String urlFromIntent = getIntent().getStringExtra(EXTRA_URL);
        if (urlFromIntent != null && !urlFromIntent.trim().isEmpty()) {
            initialUrl = urlFromIntent.trim();
        }

        setupActivityResultLaunchers();

        webView = findViewById(R.id.webView);
        if (webView == null) {
            Toast.makeText(this, "Falta el WebView con id=webView en activity_version.xml", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
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
        try { unregisterReceiver(downloadReceiver); } catch (Exception ignored) {}
    }

    private void setupActivityResultLaunchers() {
        fileChooserLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                (ActivityResult result) -> {
                    if (filePathCallback == null) return;
                    Uri[] uris = null;
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        if (data.getData() != null) {
                            uris = new Uri[]{ data.getData() };
                        } else if (data.getClipData() != null) {
                            int count = data.getClipData().getItemCount();
                            uris = new Uri[count];
                            for (int i = 0; i < count; i++) {
                                uris[i] = data.getClipData().getItemAt(i).getUri();
                            }
                        }
                    }
                    filePathCallback.onReceiveValue(uris);
                    filePathCallback = null;
                }
        );

        legacyPermsLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> { /* informativo */ }
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
            // Compat (API < 21)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                try {
                    return handleOverride(Uri.parse(url));
                } catch (Exception e) {
                    return false;
                }
            }

            // API 21+
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return handleOverride(request.getUrl());
            }

            private boolean handleOverride(Uri uri) {
                String scheme = uri.getScheme() != null ? uri.getScheme() : "";
                if (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https")) {
                    return false; // seguir en el WebView
                }
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(version.this, "No hay app para abrir este enlace.", Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                showLoadingOnPageStart();
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (loadingOverlay != null) loadingOverlay.fadeOut();
                super.onPageFinished(view, url);
            }
        });

        wv.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                handleDownloadApkOnly(url, userAgent, contentDisposition, mimetype);
            }
        });

        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView v, ValueCallback<Uri[]> filePath, FileChooserParams fileChooserParams) {
                filePathCallback = filePath;
                Intent intent;
                try {
                    intent = fileChooserParams.createIntent();
                } catch (Exception e) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT)
                            .addCategory(Intent.CATEGORY_OPENABLE)
                            .setType("*/*");
                }
                try {
                    fileChooserLauncher.launch(intent);
                } catch (ActivityNotFoundException e) {
                    Intent fallback = new Intent(Intent.ACTION_GET_CONTENT)
                            .addCategory(Intent.CATEGORY_OPENABLE)
                            .setType("*/*");
                    fileChooserLauncher.launch(fallback);
                }
                return true;
            }

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
    }

    private void showLoadingOnPageStart() {
        if (loadingOverlay != null) {
            loadingOverlay.setProgress(0f);
            loadingOverlay.showNow();
        }
    }

    /**
     * Descarga únicamente APKs a la carpeta pública "Download" y al terminar inicia la instalación.
     */
    private void handleDownloadApkOnly(String url, String userAgent, String contentDisposition, String mimeTypeIn) {
        String guessedName = URLUtil.guessFileName(url, contentDisposition, mimeTypeIn);
        if (guessedName == null || guessedName.trim().isEmpty()) {
            guessedName = "app_" + System.currentTimeMillis() + ".apk";
        }
        String filename = ensureApkExtension(guessedName);

        String lowerUrl  = url == null ? "" : url.toLowerCase(Locale.US);
        String lowerCd   = contentDisposition == null ? "" : contentDisposition.toLowerCase(Locale.US);
        String lowerMime = mimeTypeIn == null ? "" : mimeTypeIn.toLowerCase(Locale.US);

        boolean looksLikeApk =
                filename.toLowerCase(Locale.US).endsWith(".apk") ||
                        lowerUrl.contains(".apk") ||
                        lowerCd.contains(".apk") ||
                        "application/vnd.android.package-archive".equals(lowerMime) ||
                        "application/octet-stream".equals(lowerMime);

        if (!looksLikeApk) {
            Toast.makeText(this, "Solo se permiten descargas .apk", Toast.LENGTH_LONG).show();
            return;
        }

        // Permisos para <= Android 9 (P)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                legacyPermsLauncher.launch(new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE });
                Toast.makeText(this, "Otorga permisos y vuelve a intentar la descarga.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

            String cookies = CookieManager.getInstance().getCookie(url);
            if (cookies != null) request.addRequestHeader("Cookie", cookies);
            if (userAgent != null) request.addRequestHeader("User-Agent", userAgent);

            request.setMimeType("application/vnd.android.package-archive");
            request.setTitle(filename);
            request.setDescription("Descargando APK…");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.allowScanningByMediaScanner();

            // Carpeta pública "Download" (válido en Q+ sin permiso; en <=P requiere WRITE_EXTERNAL_STORAGE)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);

            DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            if (dm != null) {
                currentDownloadId = dm.enqueue(request);
                Toast.makeText(this, "Descarga iniciada en /Download", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No se pudo acceder al DownloadManager.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "No se pudo iniciar la descarga: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void startInstall(@NonNull Uri apkUri) {
        Intent install = new Intent(Intent.ACTION_VIEW)
                .setDataAndType(apkUri, "application/vnd.android.package-archive")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(install);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No se encontró instalador de paquetes.", Toast.LENGTH_LONG).show();
        }
    }

    private void handleDownloadComplete(long downloadId) {
        try {
            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            if (dm == null) return;

            DownloadManager.Query q = new DownloadManager.Query().setFilterById(downloadId);
            Cursor c = dm.query(q);
            if (c != null) {
                if (c.moveToFirst()) {
                    int status = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        Uri fileUri = dm.getUriForDownloadedFile(downloadId); // content:// seguro
                        if (fileUri != null) {
                            Toast.makeText(this, "APK descargado. Abriendo instalador…", Toast.LENGTH_SHORT).show();
                            startInstall(fileUri);
                        } else {
                            Toast.makeText(this, "Descargado, pero no se pudo abrir el archivo.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, "Descarga fallida.", Toast.LENGTH_LONG).show();
                    }
                }
                c.close();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error tras la descarga: " + e.getMessage(), Toast.LENGTH_LONG).show();
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

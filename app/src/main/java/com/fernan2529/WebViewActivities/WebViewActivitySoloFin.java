package com.fernan2529.WebViewActivities;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fernan2529.R;

public class WebViewActivitySoloFin extends AppCompatActivity {

    private static final int REQ_WRITE_EXTERNAL = 1010;

    private WebView webView;

    // Para continuar una descarga tras permiso (solo API < 29)
    private String pendingUrl;
    private String pendingContentDisposition;
    private String pendingMimeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_solo_fin);

        webView = findViewById(R.id.webView);

        // --- Config básica y segura ---
        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setLoadWithOverviewMode(true);
        ws.setUseWideViewPort(true);
        ws.setAllowFileAccess(true);
        ws.setAllowContentAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ws.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return handleExternal(request.getUrl().toString());
            }
            @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return handleExternal(url);
            }
        });

        // --- Descargas ---
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                        String mimeType, long contentLength) {
                // En Android 10+ no hace falta WRITE_EXTERNAL_STORAGE para DownloadManager
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    if (ContextCompat.checkSelfPermission(
                            WebViewActivitySoloFin.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED) {
                        pendingUrl = url;
                        pendingContentDisposition = contentDisposition;
                        pendingMimeType = mimeType;
                        ActivityCompat.requestPermissions(
                                WebViewActivitySoloFin.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQ_WRITE_EXTERNAL
                        );
                        return;
                    }
                }
                startDownload(url, contentDisposition, mimeType);
            }
        });

        // --- Cargar URL recibida ---
        String url = getIntent().getStringExtra("url");
        if (url == null || url.trim().isEmpty()) {
            Toast.makeText(this, "URL no válida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        webView.loadUrl(url);
    }

    /** Maneja esquemas no http/https con apps externas. */
    private boolean handleExternal(String url) {
        if (url == null) return false;
        if (url.startsWith("http://") || url.startsWith("https://")) return false;
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (ActivityNotFoundException e) {
            if (url.startsWith("intent://")) {
                try {
                    Intent i = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    if (i != null) startActivity(i);
                } catch (Exception ignored) {}
            }
        }
        return true;
    }

    private void startDownload(String url, String contentDisposition, String mimeType) {
        DownloadManager.Request req = new DownloadManager.Request(Uri.parse(url));

        String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
        req.setTitle(fileName);
        req.setDescription("Descargando…");
        req.setMimeType(mimeType);

        String cookie = CookieManager.getInstance().getCookie(url);
        if (cookie != null) req.addRequestHeader("cookie", cookie);
        req.addRequestHeader("User-Agent", webView.getSettings().getUserAgentString());

        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (dm != null) {
            dm.enqueue(req);
            Toast.makeText(this, "Descarga iniciada", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No se pudo iniciar la descarga", Toast.LENGTH_SHORT).show();
        }
    }

    // Permisos (solo API < 29)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_WRITE_EXTERNAL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && pendingUrl != null) {
                startDownload(pendingUrl, pendingContentDisposition, pendingMimeType);
            } else {
                Toast.makeText(this, "Permiso denegado. No se pueden guardar descargas.", Toast.LENGTH_SHORT).show();
            }
            pendingUrl = pendingContentDisposition = pendingMimeType = null;
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

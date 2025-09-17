package com.fernan2529;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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

import java.util.Locale;

public class version extends AppCompatActivity {

    private WebView webView;

    // <input type="file">
    private ValueCallback<Uri[]> filePathCallback;
    private ActivityResultLauncher<Intent> fileChooserLauncher;

    // Permisos (solo <= Android 9)
    private ActivityResultLauncher<String[]> legacyPermsLauncher;

    // URL por defecto (puedes sobreescribirla con putExtra(EXTRA_URL, "https://..."))
    public static final String EXTRA_URL = "EXTRA_URL";
    private String initialUrl = "https://2529sebastian.blogspot.com/2025/09/2529stream.html";

    // ID de la descarga en curso (para instalar al completar)
    private long currentDownloadId = -1L;

    // Receiver para saber cuándo acaba la descarga
    private final android.content.BroadcastReceiver downloadReceiver = new android.content.BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L);
            if (id == currentDownloadId && id != -1L) {
                handleDownloadComplete(id);
                // Evitar instalaciones repetidas
                currentDownloadId = -1L;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version);

        // Registrar receiver del DownloadManager
        registerReceiver(downloadReceiver, new android.content.IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        String urlFromIntent = getIntent().getStringExtra(EXTRA_URL);
        if (urlFromIntent != null && !urlFromIntent.trim().isEmpty()) {
            initialUrl = urlFromIntent.trim();
        }

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
        try { unregisterReceiver(downloadReceiver); } catch (Exception ignored) {}
    }

    private void setupActivityResultLaunchers() {
        // File chooser (subidas)
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

        // Permisos de almacenamiento (solo para APIs antiguas)
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

        // Navegación interna
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String scheme = uri.getScheme() != null ? uri.getScheme() : "";
                if (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https")) {
                    return false; // manejar en el WebView
                } else {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(version.this, "No hay app para abrir este enlace.", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            }
        });

        // Descargas (solo APK, guardar en /Download, instalar al terminar)
        wv.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                handleDownloadApkOnly(url, userAgent, contentDisposition, mimetype);
            }
        });

        // Soporte para <input type="file">
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

    /**
     * Descarga solo APK y los guarda en la carpeta pública "Download".
     * Al terminar, se instala automáticamente (con intervención del usuario).
     */
    private void handleDownloadApkOnly(String url, String userAgent, String contentDisposition, String mimeTypeIn) {
        // Nombre sugerido
        String guessedName = URLUtil.guessFileName(url, contentDisposition, mimeTypeIn);
        if (guessedName == null || guessedName.trim().isEmpty()) {
            guessedName = "app_" + System.currentTimeMillis() + ".apk";
        }
        String filename = ensureApkExtension(guessedName);

        // Heurística: validar que "parece" un APK
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

            // Cookies y UA (útil si requiere sesión)
            String cookies = CookieManager.getInstance().getCookie(url);
            if (cookies != null) request.addRequestHeader("Cookie", cookies);
            if (userAgent != null) request.addRequestHeader("User-Agent", userAgent);

            // Forzar MIME de APK
            request.setMimeType("application/vnd.android.package-archive");
            request.setTitle(filename);
            request.setDescription("Descargando APK…");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.allowScanningByMediaScanner();

            // SIEMPRE: carpeta pública "Download"
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
                        Uri apkUri = dm.getUriForDownloadedFile(downloadId); // content:// URI gestionado por DM
                        if (apkUri != null) {
                            if (maybeRequestUnknownSources()) {
                                Toast.makeText(this,
                                        "Habilita 'Instalar apps de esta fuente' y vuelve a intentar.",
                                        Toast.LENGTH_LONG).show();
                                c.close();
                                return;
                            }
                            startInstall(apkUri);
                        } else {
                            Toast.makeText(this, "No se pudo obtener el archivo descargado.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, "Descarga fallida.", Toast.LENGTH_LONG).show();
                    }
                }
                c.close();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al preparar instalación: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void startInstall(@NonNull Uri apkUri) {
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setDataAndType(apkUri, "application/vnd.android.package-archive");
        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(install);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No se encontró instalador de paquetes.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Android 8.0+ requiere que el usuario permita "Instalar apps de esta fuente".
     * Devuelve true si hay que llevar al usuario a Ajustes (aún no permitido).
     */
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

    private String ensureApkExtension(String name) {
        String trimmed = name == null ? "" : name.trim();
        if (!trimmed.toLowerCase(Locale.US).endsWith(".apk")) {
            int dot = trimmed.lastIndexOf('.');
            if (dot > 0) {
                trimmed = trimmed.substring(0, dot);
            }
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

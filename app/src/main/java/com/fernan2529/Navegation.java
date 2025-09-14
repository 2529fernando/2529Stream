package com.fernan2529;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class Navegation extends AppCompatActivity {

    EditText urlInput;
    ImageView clearUrl;
    WebView webView;
    ProgressBar progressBar;
    ImageView webBack, webForward, webRefresh, webShare;
    boolean isVideoFullscreen = false;
    FrameLayout mFullscreenContainer;
    LinearLayout bottomLayout;

    private ImageView link_icon;
    private ImageView clear_icon;
    private ImageView web_back;
    private ImageView web_forward;
    private ImageView web_refresh;
    private ImageView web_share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navegation);

        // Asignaciones de ImageView
        link_icon = findViewById(R.id.link_icon);
        clear_icon = findViewById(R.id.clear_icon);
        web_back = findViewById(R.id.web_back);
        web_forward = findViewById(R.id.web_forward);
        web_refresh = findViewById(R.id.web_refresh);
        web_share = findViewById(R.id.web_share);

        urlInput = findViewById(R.id.url_input);
        clearUrl = findViewById(R.id.clear_icon);
        progressBar = findViewById(R.id.progressBar);
        webView = findViewById(R.id.web_view);

        webBack = findViewById(R.id.web_back);
        webForward = findViewById(R.id.web_forward);
        webRefresh = findViewById(R.id.web_refresh);
        webShare = findViewById(R.id.web_share);

        mFullscreenContainer = findViewById(R.id.fullscreen_container); // Obtener referencia del FrameLayout

        progressBar.setVisibility(View.VISIBLE); // Make ProgressBar always visible

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false; // Avoid automatic redirection
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // You may choose to show/hide the progress bar based on your requirements here
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Hide the progress bar when the page has finished loading
                progressBar.setVisibility(View.INVISIBLE);

                // Set the URL to the EditText
                urlInput.setText(url);
            }
        });

        // Set up DownloadListener to handle file downloads
        webView.setDownloadListener(new MyDownloadListener());

        webView.setWebChromeClient(new MyWebChromeClient());

        loadMyUrl("https://www.google.com");

        urlInput.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_GO || i == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(urlInput.getWindowToken(), 0);
                loadMyUrl(urlInput.getText().toString());
                return true;
            }
            return false;
        });

        clearUrl.setOnClickListener(view -> urlInput.setText(""));

        webBack.setOnClickListener(view -> {
            if (webView.canGoBack()) {
                webView.goBack();
            }
        });

        webForward.setOnClickListener(view -> {
            if (webView.canGoForward()) {
                webView.goForward();
            }
        });

        webRefresh.setOnClickListener(view -> webView.reload());

        webShare.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, webView.getUrl());
            intent.setType("text/plain");
            startActivity(intent);
        });
    }

    void loadMyUrl(String url) {
        // Check if URL does not start with "http://" or "https://"
        if (!url.startsWith("https://") && !url.startsWith("https://")) {
            // Check if it's not just a search query
            if (Patterns.WEB_URL.matcher(url).matches()) {
                url = "https://" + url;
            } else {
                // Treat it as a search query
                url = "https://www.google.com/search?q=" + url;
            }
        }
        progressBar.setVisibility(View.VISIBLE); // Make ProgressBar visible when loading URL
        webView.loadUrl(url);
    }


    @Override
    public void onBackPressed() {
        if (isVideoFullscreen) {
            webView.loadUrl("about:blank");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            isVideoFullscreen = false;
        } else if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    // WebChromeClient personalizado para manejar la visualización de videos en pantalla completa
    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            progressBar.setProgress(newProgress); // Update progress bar
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            if (isVideoFullscreen) {
                callback.onCustomViewHidden();
                return;
            }
            isVideoFullscreen = true;
            progressBar.setVisibility(View.GONE); // Hide ProgressBar when entering fullscreen
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE); // Add SYSTEM_UI_FLAG_LAYOUT_STABLE flag

            // Hide all ImageView
            hideImageViews();

            mFullscreenContainer.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)); // Add the view to the FrameLayout
            mFullscreenContainer.setVisibility(View.VISIBLE);
        }

        // Método para ocultar todas las ImageView
        private void hideImageViews() {
            link_icon.setVisibility(View.GONE);
            clear_icon.setVisibility(View.GONE);
            web_back.setVisibility(View.GONE);
            web_forward.setVisibility(View.GONE);
            web_refresh.setVisibility(View.GONE);
            web_share.setVisibility(View.GONE);
        }

        // Método para mostrar todas las ImageView
        private void showImageViews() {
            link_icon.setVisibility(View.VISIBLE);
            clear_icon.setVisibility(View.VISIBLE);
            web_back.setVisibility(View.VISIBLE);
            web_forward.setVisibility(View.VISIBLE);
            web_refresh.setVisibility(View.VISIBLE);
            web_share.setVisibility(View.VISIBLE);
        }

        @Override
        public void onHideCustomView() {
            if (!isVideoFullscreen) {
                return;
            }
            isVideoFullscreen = false;
            progressBar.setVisibility(View.VISIBLE); // Show ProgressBar when exiting fullscreen
            // Show all ImageView
            showImageViews();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            mFullscreenContainer.removeAllViews(); // Remove all views from the FrameLayout
            mFullscreenContainer.setVisibility(View.GONE);
        }
    }

    // Custom DownloadListener to handle file downloads
    private class MyDownloadListener implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
            // Start the DownloadActivity to handle the download
            DownloadActivity.start(Navegation.this, url);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int orientation = newConfig.orientation;
        switch (orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
            case Configuration.ORIENTATION_PORTRAIT:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                break;
            default:
                break;
        }
    }
}

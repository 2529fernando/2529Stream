package com.fernan2529;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class WebViewActivity4 extends AppCompatActivity {

    private WebView webView;
    private boolean initialLoadComplete = false;
    private ImageView loadingAnimation;
    private View mCustomView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private boolean isFullscreen = false;
    private String defaultUrl;
    private FrameLayout mContentView;
    private FrameLayout mFullscreenContainer;
    private ImageButton btnAspectRatio;

    private int aspectMode = 0; // 0 = Ajustar, 1 = Llenar, 2 = Original

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view4);

        defaultUrl = getIntent().getStringExtra("VIDEO_URL");

        webView = findViewById(R.id.webview);
        loadingAnimation = findViewById(R.id.loadingAnimation);
        mContentView = findViewById(R.id.webview_container);
        mFullscreenContainer = findViewById(R.id.fullscreen_container);
        btnAspectRatio = findViewById(R.id.btnAspectRatio);

        // Configuración del WebView
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new MyWebChromeClient());

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);

        if (savedInstanceState != null) {
            defaultUrl = savedInstanceState.getString("DEFAULT_URL");
        }
        webView.loadUrl(defaultUrl);

        // Acción del botón
        btnAspectRatio.setOnClickListener(v -> toggleAspectRatio());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("DEFAULT_URL", defaultUrl);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            initialLoadComplete = true;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Solo permitir la URL inicial
            return !url.equals(defaultUrl);
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int progress) {
            if (progress < 100 && loadingAnimation.getVisibility() == View.GONE) {
                loadingAnimation.setVisibility(View.VISIBLE);
            }
            if (progress == 100) {
                loadingAnimation.setVisibility(View.GONE);
            }
        }

        @Override
        public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mCustomView = view;
            mCustomViewCallback = callback;
            setFullscreen(true);

            // Botón SIEMPRE visible en fullscreen
            btnAspectRatio.setVisibility(View.VISIBLE);
        }

        @Override
        public void onHideCustomView() {
            if (mCustomView == null) return;

            setFullscreen(false);

            // Ocultar botón al salir de fullscreen
            btnAspectRatio.setVisibility(View.GONE);
        }
    }

    private void setFullscreen(boolean fullscreen) {
        if (fullscreen) {
            isFullscreen = true;
            mContentView.setVisibility(View.GONE);
            mFullscreenContainer.addView(mCustomView,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
            mFullscreenContainer.setVisibility(View.VISIBLE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        } else {
            isFullscreen = false;
            mContentView.setVisibility(View.VISIBLE);
            mFullscreenContainer.removeView(mCustomView);
            mFullscreenContainer.setVisibility(View.GONE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            mCustomViewCallback.onCustomViewHidden();
            mCustomView = null;
        }
    }

    private void toggleAspectRatio() {
        if (mCustomView == null) return;

        ViewGroup.LayoutParams params = mCustomView.getLayoutParams();

        switch (aspectMode) {
            case 0: // Ajustar
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                aspectMode = 1;
                break;
            case 1: // Llenar pantalla
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                aspectMode = 2;
                break;
            case 2: // Original
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                aspectMode = 0;
                break;
        }

        mCustomView.setLayoutParams(params);
        mCustomView.requestLayout();
    }

    @Override
    public void onBackPressed() {
        if (isFullscreen) {
            mCustomViewCallback.onCustomViewHidden();
        } else {
            super.onBackPressed();
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
        }
    }
}

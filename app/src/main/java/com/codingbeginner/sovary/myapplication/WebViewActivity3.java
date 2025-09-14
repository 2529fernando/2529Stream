package com.codingbeginner.sovary.myapplication;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
public class WebViewActivity3 extends AppCompatActivity {

    private WebView webView;
    private String defaultUrl;
    private ImageView loadingAnimation;
    private View mCustomView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private FrameLayout mContentView;
    private FrameLayout mFullscreenContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view3);

        webView = findViewById(R.id.webView);
        loadingAnimation = findViewById(R.id.loadingAnimation);
        mContentView = findViewById(R.id.webview_container);
        mFullscreenContainer = findViewById(R.id.fullscreen_container);

        // Configurar WebView
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                // Always return true to indicate that WebView should handle the URL loading
                return true;
            }
        });
        webView.setWebChromeClient(new MyWebChromeClient());

        // Configurar el WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Habilitar JavaScript
        webSettings.setSupportZoom(true); // Habilitar zoom
        webSettings.setBuiltInZoomControls(true); // Controles de zoom incorporados

        // Obtener la URL predeterminada del Intent
        defaultUrl = getIntent().getStringExtra("url");
        webView.loadUrl(defaultUrl); // Cargar la URL predeterminada en el WebView
    }

    // WebChromeClient personalizado para manejar la visualización de videos en pantalla completa
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
            // Mostrar la vista personalizada (video) en pantalla completa
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mCustomView = view;
            mCustomViewCallback = callback;
            webView.setVisibility(View.GONE);
            mFullscreenContainer.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mFullscreenContainer.setVisibility(View.VISIBLE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

        @Override
        public void onHideCustomView() {
            // Ocultar la vista personalizada (video) en pantalla completa
            if (mCustomView == null) {
                return;
            }
            webView.setVisibility(View.VISIBLE);
            mFullscreenContainer.removeView(mCustomView);
            mFullscreenContainer.setVisibility(View.GONE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            mCustomView = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (mCustomView != null) {
            // Si hay una vista personalizada en pantalla completa, ocultarla
            webView.getSettings().setJavaScriptEnabled(false); // Deshabilitar JavaScript temporalmente
            onHideCustomView();
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
            default:
                break;
        }
    }

    // Método para ocultar la vista personalizada en pantalla completa
    private void onHideCustomView() {
        mCustomViewCallback.onCustomViewHidden();
    }
}


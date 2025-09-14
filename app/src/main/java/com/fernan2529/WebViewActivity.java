package com.fernan2529;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;
    private String defaultUrl;
    private ImageView loadingAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        webView = findViewById(R.id.webView);
        loadingAnimation = findViewById(R.id.loadingAnimation);

        // Configurar WebView
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                // Always return true to indicate that WebView should handle the URL loading
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100 && loadingAnimation.getVisibility() == ImageView.GONE) {
                    loadingAnimation.setVisibility(ImageView.VISIBLE);
                }
                if (progress == 100) {
                    loadingAnimation.setVisibility(ImageView.GONE);
                }
            }
        });

        // Configurar el WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Habilitar JavaScript

        // Obtener la URL predeterminada del Intent
        defaultUrl = getIntent().getStringExtra("url");
        webView.loadUrl(defaultUrl); // Cargar la URL predeterminada en el WebView
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


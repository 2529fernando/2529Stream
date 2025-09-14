package com.fernan2529;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.ImageView;

public class webthree extends AppCompatActivity {

    private WebView webView;
    private boolean isVideoFullscreen = false;

    private ImageView loadingAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webthree);

        webView = findViewById(R.id.webview);
        loadingAnimation = findViewById(R.id.loadingAnimation);

        // Configurar WebView
        setUpWebView();

        // Configurar animación de carga
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                loadingAnimation.setVisibility(View.GONE);
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setUpWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true); // Habilitar zoom
        webSettings.setBuiltInZoomControls(true); // Controles de zoom incorporados

        webView.setWebViewClient(new WebViewClient());

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
                isVideoFullscreen = true;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                if (view instanceof android.webkit.WebView) {
                    android.webkit.WebView webView = (android.webkit.WebView) view;
                    webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
                }
            }

            @Override
            public void onHideCustomView() {
                super.onHideCustomView();
                isVideoFullscreen = false;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress < 100 && loadingAnimation.getVisibility() == ImageView.GONE) {
                    loadingAnimation.setVisibility(ImageView.VISIBLE);
                }
                if (newProgress == 100) {
                    loadingAnimation.setVisibility(ImageView.GONE);
                }
            }
        });

        webView.loadUrl("https://television-libre.online/html/dtv/?id=1190"); // Reemplaza con tu URL de video
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
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
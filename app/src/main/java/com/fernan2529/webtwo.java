package com.fernan2529;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import android.widget.ImageView;

public class webtwo extends AppCompatActivity {

    private WebView webView;
    private boolean isVideoFullscreen = false;
    private ImageView loadingAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webtwo);

        webView = findViewById(R.id.webview);
        loadingAnimation = findViewById(R.id.loadingAnimation);
        setUpWebView();
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
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress < 100 && loadingAnimation.getVisibility() == View.GONE) {
                    loadingAnimation.setVisibility(View.VISIBLE);
                }
                if (newProgress == 100) {
                    loadingAnimation.setVisibility(View.GONE);
                }
            }

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
        });

        webView.loadUrl("https://television-libre.online/html/clarovideo.html?r=ESPNCL"); // Reemplaza con tu URL de video
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
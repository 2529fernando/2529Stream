package com.codingbeginner.sovary.myapplication;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class WebViewActivity7 extends AppCompatActivity {

    private ImageView loadingAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view7);

        loadingAnimation = findViewById(R.id.loadingAnimation);

        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);  // Habilitamos JavaScript
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAllowFileAccess(true);

        String url = getIntent().getStringExtra("url");
        webView.loadUrl(url);  // Carga la URL

        // No redirigir enlaces dentro del WebView
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;  // No redirigir
            }
        });

        // Permitir videos en pantalla completa
        webView.setWebChromeClient(new WebChromeClient() {

            // Mostrar el video en pantalla completa
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
                // Si es necesario, añade un contenedor para mostrar el video en pantalla completa
                FrameLayout fullscreenContainer = new FrameLayout(WebViewActivity7.this);
                fullscreenContainer.addView(view);
                setContentView(fullscreenContainer); // Cambia la vista a pantalla completa

                // Aquí puedes agregar lógica para manejar la salida de pantalla completa
            }

            // Ocultar el video cuando termina la pantalla completa
            @Override
            public void onHideCustomView() {
                super.onHideCustomView();
                // Vuelve a la actividad original, pero sin recargar la página
                setContentView(R.layout.activity_web_view7); // Vuelve a la vista original sin recargar
            }

            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100 && loadingAnimation.getVisibility() == View.GONE) {
                    loadingAnimation.setVisibility(View.VISIBLE);
                }
                if (progress == 100) {
                    loadingAnimation.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        WebView webView = findViewById(R.id.webView);
        if (webView.canGoBack()) {
            webView.goBack();  // Si hay páginas previas, regresa a la anterior
        } else {
            super.onBackPressed();  // Si no, sale de la actividad
        }
    }
}

package com.codingbeginner.sovary.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.DownloadListener;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class musicactivity extends AppCompatActivity {

    private static final int DOWNLOAD_REQUEST_CODE = 1; // Código para identificar la selección de descarga

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicactivity);

        WebView webView = findViewById(R.id.webView);

        // Habilitar JavaScript si es necesario
        webView.getSettings().setJavaScriptEnabled(true);

        // Establecer un WebViewClient para que las URLs se carguen dentro del WebView
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });



        // Cargar la URL que deseas mostrar
        String url = "https://flacdownloader.com/"; // Reemplaza con la URL de tu preferencia
        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        WebView webView = findViewById(R.id.webView);
        if (webView.canGoBack()) {
            webView.goBack();  // Si el WebView tiene historial, navega atrás
        } else {
            super.onBackPressed();  // Si no hay historial, se comporta como una Activity normal
        }
    }
}



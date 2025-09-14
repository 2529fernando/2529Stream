package com.codingbeginner.sovary.myapplication;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class webnavegation1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webnavegation1);

        WebView webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true); // Habilita JavaScript (opcional)

        // Configura un WebViewClient para manejar las interacciones dentro del WebView
        webView.setWebViewClient(new WebViewClient());

        webView.loadUrl("https://2529fernan.blogspot.com/2024/02/v_7.html"); // URL de tu página de inicio
    }
}

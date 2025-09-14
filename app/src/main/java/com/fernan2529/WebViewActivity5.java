package com.fernan2529;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
public class WebViewActivity5 extends AppCompatActivity {
    private WebView webView;
    private ImageView loadingAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ocultar la barra de estado
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_web_view5);

        // Obtener la URL de la intent
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        // Inicializar el WebView y cargar la URL
        webView = findViewById(R.id.webView);
        loadingAnimation = findViewById(R.id.loadingAnimation); // Referenciar la animación de carga
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Configurar WebViewClient para manejar las páginas web
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.startsWith("intent://ak")) {
                    // Bloquear la carga de páginas que empiecen con "intent://ak"
                    return true;
                } else if (url.startsWith("https://tutlehd")) {
                    // Permitir la carga de páginas que comiencen con "https://tutlehd"
                    return true;
                } else if (url.startsWith("http://") || url.startsWith("https://")) {
                    // Permitir la carga de enlaces externos
                    return true;
                } else {
                    // Permitir la carga de enlaces internos en la misma WebView
                    view.loadUrl(url);
                    return false;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                // Mostrar la animación de carga cuando se inicia la carga de la página
                loadingAnimation.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // Ocultar la animación de carga cuando se completa la carga de la página
                loadingAnimation.setVisibility(View.GONE);
            }
        });
    }
}
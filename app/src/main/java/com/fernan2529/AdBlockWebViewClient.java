package com.fernan2529;

import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class AdBlockWebViewClient extends WebViewClient {
    // Lista de patrones de URL de anuncios conocidos
    private static final Set<Pattern> adPatterns = new HashSet<>();
    static {
        adPatterns.add(Pattern.compile(".*\\.doubleclick\\.net/.*"));
        // Agrega más patrones según sea necesario
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        // Permite que la WebView maneje la navegación normalmente
        return false;
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        // Obtiene la URL de la solicitud
        String url = request.getUrl().toString();

        // Verifica si la URL coincide con algún patrón de anuncio conocido
        for (Pattern pattern : adPatterns) {
            if (pattern.matcher(url).matches()) {
                // Devuelve una respuesta vacía para bloquear la solicitud de anuncio
                return new WebResourceResponse("text/plain", "utf-8", null);
            }
        }

        // Permite que la WebView cargue la solicitud normalmente
        return super.shouldInterceptRequest(view, request);
    }
}
package com.codingbeginner.sovary.myapplication;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WatchActivitythree extends AppCompatActivity {

    private String mp3Url;
    private WebView webView;
    private static final int WRITE_REQUEST_CODE = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_activitythree);

        // Obtener la URL del MP3 que se pasó desde la otra actividad
        mp3Url = getIntent().getStringExtra("mp3_url");

        // Configurar el WebView
        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true); // Habilitar JavaScript
        webView.setWebViewClient(new WebViewClient()); // Navegar dentro del WebView

        // Mostrar la URL en el EditText
        EditText mp3UrlEditText = findViewById(R.id.editTextMp3Url);
        mp3UrlEditText.setText(mp3Url);

        // Copiar URL al portapapeles
        Button copyButton = findViewById(R.id.copyButton);
        copyButton.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("MP3 URL", mp3Url);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(WatchActivitythree.this, "URL copiada al portapapeles", Toast.LENGTH_SHORT).show();
        });

        // Botón para descargar el archivo desde el blob URL
        Button downloadButton = findViewById(R.id.downloadButton);
        downloadButton.setOnClickListener(view -> {
            if (mp3Url != null && mp3Url.startsWith("blob:")) {
                // Solicitar la ubicación donde guardar el archivo
                openFileChooser();
            } else {
                Toast.makeText(WatchActivitythree.this, "URL no es un blob válido", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Abrir el selector de archivos para elegir dónde guardar el archivo
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("audio/mpeg"); // Tipo de archivo a guardar
        intent.putExtra(Intent.EXTRA_TITLE, "downloaded_file.mp3"); // Nombre predeterminado del archivo
        startActivityForResult(intent, WRITE_REQUEST_CODE);
    }

    // Procesar la respuesta del selector de archivos
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == WRITE_REQUEST_CODE && resultCode == RESULT_OK) {
            // Obtener la URI seleccionada
            Uri uri = resultData.getData();
            if (uri != null) {
                // Descargar el archivo y guardarlo en la ubicación seleccionada
                downloadFile(uri);
            }
        }
    }

    // Descargar el archivo y guardarlo en la ubicación seleccionada por el usuario
    private void downloadFile(Uri uri) {
        new Thread(() -> {
            try {
                // Realizar la descarga
                HttpURLConnection connection = (HttpURLConnection) new URL(mp3Url).openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                try (FileOutputStream outputStream = (FileOutputStream) getContentResolver().openOutputStream(uri)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    runOnUiThread(() -> {
                        Toast.makeText(WatchActivitythree.this, "Archivo descargado exitosamente", Toast.LENGTH_SHORT).show();
                    });
                } catch (IOException e) {
                    runOnUiThread(() -> Toast.makeText(WatchActivitythree.this, "Error al guardar el archivo", Toast.LENGTH_SHORT).show());
                }
            } catch (IOException e) {
                runOnUiThread(() -> Toast.makeText(WatchActivitythree.this, "Error al descargar el archivo", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
        }
    }


}

package com.fernan2529.Anime;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fernan2529.R;
import com.fernan2529.WatchViewActivities.WatchActivityViewGeneral; // 👈 Usa tu reproductor general

import java.util.Random;

public class anime4 extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1001;

    private Spinner spinnerVideos;
    private Spinner spinnerDescargas;
    private ArrayAdapter<String> adapter;
    private final Random random = new Random();

    private boolean ignoreFirstVideoSelect = true;
    private boolean ignoreFirstDownloadSelect = true;

    // ===== Listas unificadas =====
    private final String[] names = {
            "Seleccione el Capitulo",
            "01 - Renacimiento",
            "02 - Confrontación",
            "03 - Negociaciones",
            "04 - Persecución",
            "05 - Estrategia",
            "06 - Florecer",
            "07 - Nublado",
            "08 - Mirada",
            "09 - Contacto",
            "10 - Sospechas",
            "11 - Asalto",
            "12 - Enamoramiento",
            "13 - Confesión",
            "14 - Amigo",
            "15 - Apuesta",
            "16 - Decisión",
            "17 - Ejecución",
            "18 - Compañeros",
            "19 - Matsuda",
            "20 - Improvisación",
            "21 - Actividad",
            "22 - Inducción",
            "23 - Frenesí",
            "24 - Resurrección",
            "25 - Silencio",
            "26 - Regeneración",
            "27 - Secuestro",
            "28 - Impaciencia",
            "29 - Padre",
            "30 - Justicia",
            "31 - Traspaso",
            "32 - Selección",
            "33 - Desprecio",
            "34 - Acecho",
            "35 - Malicia",
            "36 - 28 de enero",
            "37 - Nuevo Mundo"
    };

    private final String[] urls = {
            "",
            "https://archive.org/download/01-renacimiento/01%20Renacimiento.mp4",
            "https://archive.org/download/02-batalla/02%20Batalla.mp4",
            "https://archive.org/download/03-intercambio/03%20Intercambio.mp4",
            "https://archive.org/download/04-busqueda/04%20B%C3%BAsqueda.mp4",
            "https://archive.org/download/05-diplomacia/05%20Diplomacia.mp4",
            "https://archive.org/download/06-costura-abierta/06%20Costura%20abierta.mp4",
            "https://archive.org/download/07-tentacion/07%20Tentaci%C3%B3n.mp4",
            "https://archive.org/download/08-punto-de-vista/08%20Punto%20de%20vista.mp4",
            "https://archive.org/download/09-contacto/09%20Contacto.mp4",
            "https://archive.org/download/dn-10_202402/DN10.mp4",
            "https://archive.org/download/11-asalto/11%20Asalto.mp4",
            "https://archive.org/download/12-amor/12%20Amor.mp4",
            "https://archive.org/download/13-declaracion/13%20Declaraci%C3%B3n.mp4",
            "https://archive.org/download/death-note-latino-14/Death%20Note%20%5BLatino%5D-14.mp4",
            "https://archive.org/download/death-note-latino-15/Death%20Note%20%5BLatino%5D-15.mp4",
            "https://archive.org/download/death-note-latino-16/Death%20Note%20%5BLatino%5D-16.mp4",
            "https://archive.org/download/death-note-latino-17/Death%20Note%20%5BLatino%5D-17.mp4",
            "https://archive.org/download/death-note-latino-18/Death%20Note%20%5BLatino%5D-18.mp4",
            "https://archive.org/download/death-note-latino-19/Death%20Note%20%5BLatino%5D-19.mp4",
            "https://archive.org/download/ded-note-20-espanol/ded%20note%2020%20espa%C3%B1ol.mp4",
            "https://archive.org/download/death-note-latino-21/Death%20Note%20%5BLatino%5D-21.mp4",
            "https://archive.org/download/ded-note-22-espanol/ded%20note%2022%20espa%C3%B1ol.mp4",
            "https://archive.org/download/ded-note-23-espanol/ded%20note%2023%20espa%C3%B1ol.mp4",
            "https://ia601505.us.archive.org/21/items/ded-note-33-espanol/ded%20note%2024%20espa%C3%B1ol.mp4",
            "https://ia801505.us.archive.org/21/items/ded-note-33-espanol/ded%20note%2025%20espa%C3%B1ol.mp4",
            "https://archive.org/download/ded-note-33-espanol/ded%20note%2026%20espa%C3%B1ol.mkv",
            "https://archive.org/download/ded-note-33-espanol/ded%20note%2027%20espa%C3%B1ol.mkv",
            "https://archive.org/download/ded-note-33-espanol/ded%20note%2028%20espa%C3%B1ol.mkv",
            "https://archive.org/download/ded-note-33-espanol/ded%20note%2029%20espa%C3%B1ol.mkv",
            "https://archive.org/download/ded-note-33-espanol/ded%20note%2030%20espa%C3%B1ol.mkv",
            "https://archive.org/download/ded-note-33-espanol/ded%20note%2031%20espa%C3%B1ol.mkv",
            "https://archive.org/download/ded-note-33-espanol/ded%20note%2032%20espa%C3%B1ol.mkv",
            "https://archive.org/download/ded-note-33-espanol/ded%20note%2033%20espa%C3%B1ol.mkv",
            "https://archive.org/download/ded-note-33-espanol/ded%20note%2034%20espa%C3%B1ol.mkv",
            "https://archive.org/download/ded-note-33-espanol/ded%20note%2035%20espa%C3%B1ol.mkv",
            "https://archive.org/download/ded-note-33-espanol/ded%20note%2036%20espa%C3%B1ol.mkv",
            "https://archive.org/download/ded-note-33-espanol/ded%20note%2037%20espa%C3%B1ol.mkv"

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime4);

        spinnerVideos = findViewById(R.id.spinner_videos);
        spinnerDescargas = findViewById(R.id.spinner_downloads);
        Button playRandomButton = findViewById(R.id.aleatorio);

        // Un solo adapter para ambos spinners
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVideos.setAdapter(adapter);
        spinnerDescargas.setAdapter(adapter);

        // Reproducir
        spinnerVideos.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (ignoreFirstVideoSelect) { ignoreFirstVideoSelect = false; return; }
                if (position == 0) return;
                openWatchActivity(urls[position], names[position]); // 👉 WatchActivityViewGeneral
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });

        // Descargar
        spinnerDescargas.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (ignoreFirstDownloadSelect) { ignoreFirstDownloadSelect = false; return; }
                if (position == 0) return;

                String url = urls[position];

                // Evita intentar descargar listas HLS
                if (url.endsWith(".m3u8")) {
                    Toast.makeText(anime4.this, "Ese enlace es streaming (.m3u8) y no se puede descargar directamente.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q || hasWritePermission()) {
                    downloadFile(url);
                } else {
                    requestWritePermission();
                }
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });

        // Aleatorio
        playRandomButton.setOnClickListener(v -> {
            if (urls.length <= 1) {
                Toast.makeText(this, "No hay capítulos disponibles.", Toast.LENGTH_SHORT).show();
                return;
            }
            int index = 1 + random.nextInt(urls.length - 1);
            openWatchActivity(urls[index], names[index]); // 👉 WatchActivityViewGeneral
        });
    }

    /** Abre el reproductor general con URL + título del capítulo */
    private void openWatchActivity(String videoUrl, String title) {
        Intent intent = WatchActivityViewGeneral.newIntent(anime4.this, videoUrl, title);
        startActivity(intent);
    }

    // ===== Descargas =====

    private boolean hasWritePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestWritePermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            Toast.makeText(
                    this,
                    granted ? "Permiso concedido. Vuelve a seleccionar para descargar."
                            : "Permiso denegado. No se puede descargar en este dispositivo.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void downloadFile(String downloadUrl) {
        // Nombre del archivo (limpia query si existe)
        String fileName = downloadUrl.substring(downloadUrl.lastIndexOf('/') + 1);
        int q = fileName.indexOf('?');
        if (q >= 0) fileName = fileName.substring(0, q);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl))
                .setTitle(fileName)
                .setDescription("Descargando " + fileName)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                // Para evitar permisos en todas las versiones, puedes usar setDestinationInExternalFilesDir(...)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (dm != null) {
            dm.enqueue(request);
            Toast.makeText(this, "Descargando " + fileName, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No se pudo iniciar la descarga.", Toast.LENGTH_LONG).show();
        }
    }
}

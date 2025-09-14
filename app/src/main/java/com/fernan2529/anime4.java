package com.fernan2529;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Random;

import android.widget.Toast;
import android.app.DownloadManager;


public class anime4 extends AppCompatActivity {
    private Spinner spinnerVideos;
    private Spinner spinnerDescargas;
    private ArrayAdapter<String> videosAdapter;
    private ArrayAdapter<String> descargasAdapter;
    private Random random;

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime4);

        spinnerVideos = findViewById(R.id.spinner_videos);
        spinnerDescargas = findViewById(R.id.spinner_downloads);
        Button playRandomButton = findViewById(R.id.aleatorio);

        final String[] videoNames = {"Seleccione el Capitulo",
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
                "37 - Nuevo Mundo"};

        final String[] videoUrls = {"",
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
                "https://archive.org/download/ded-note-33-espanol/ded%20note%2037%20espa%C3%B1ol.mkv"};

        final String[] descargaNames = {"Seleccione la Descarga",
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
                "37 - Nuevo Mundo"};


        final String[] descargaUrls = {"",
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
                "https://archive.org/download/ded-note-33-espanol/ded%20note%2037%20espa%C3%B1ol.mkv"};

        videosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, videoNames);
        videosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVideos.setAdapter(videosAdapter);

        descargasAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, descargaNames);
        descargasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDescargas.setAdapter(descargasAdapter);

        random = new Random();

        spinnerVideos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position != 0) {
                    String selectedVideoUrl = videoUrls[position];
                    openWatchActivity(selectedVideoUrl);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerDescargas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position != 0) {
                    String selectedDownloadUrl = descargaUrls[position];
                    if (checkPermission()) {
                        downloadFile(selectedDownloadUrl);
                    } else {
                        requestPermission();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        playRandomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int randomIndex = random.nextInt(videoUrls.length - 1) + 1;
                String randomVideoUrl = videoUrls[randomIndex];
                openWatchActivity(randomVideoUrl);
            }
        });
    }

    private void openWatchActivity(String videoUrl) {
        Intent intent = new Intent(anime4.this, WatchActivity3.class);
        intent.putExtra("VIDEO_URL", videoUrl);
        startActivity(intent);
    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permission granted. Try downloading again.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Permission denied. Cannot download the file.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void downloadFile(String downloadUrl) {
        // Extraer el nombre del archivo del URL de descarga
        String fileName = downloadUrl.substring(downloadUrl.lastIndexOf('/') + 1);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
        request.setDescription("Downloading " + fileName);
        request.setTitle(fileName); // Usar el nombre del archivo como título

        // Configurar el destino de descarga
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        // Configurar otros parámetros
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE); // Mostrar la barra de progreso
        request.allowScanningByMediaScanner();
        request.setMimeType("*/*");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // Iniciar la descarga
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

        Toast.makeText(getApplicationContext(), "Downloading " + fileName, Toast.LENGTH_LONG).show();
    }
}
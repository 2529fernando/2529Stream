package com.codingbeginner.sovary.myapplication;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;import android.widget.Toast;
import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationManager;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Random;


public class anime1 extends AppCompatActivity {
    private Spinner spinnerVideos;
    private Spinner spinnerDescargas;
    private ArrayAdapter<String> videosAdapter;
    private ArrayAdapter<String> descargasAdapter;
    private Random random;

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime1);

        spinnerVideos = findViewById(R.id.spinner_videos);
        spinnerDescargas = findViewById(R.id.spinner_downloads);
        Button playRandomButton = findViewById(R.id.aleatorio);

        final String[] videoNames = {"Seleccione el Capitulo",
                "01 - Registro",
                "02 - Términos del contrato",
                "03 - Dilema de principios",
                "04 - Entrada escrita a mano",
                "05 - Nota de voz",
                "06 - Modo vibrador",
                "07 - Señal de marca",
                "08 - Nuevo modelo",
                "09 - Número bloqueado",
                "10 - Plan familiar",
                "11 - Fin del servicio",
                "12 - Recepción fuera de rango",
                "13 - Llamada restringida",
                "14 - Memoria reseteada",
                "15 - Doble poseedor",
                "16 - Reparación",
                "17 - Familia de descuento",
                "18 - Líneas cruzadas",
                "19 - Borrando todos los mensajes",
                "20 - Transferencia de datos",
                "21 - PIN",
                "22 - Desconexión",
                "23 - Incumplimiento del contrato",
                "24 - Recuperación de datos",
                "25 - Reinicio",
                "26 - Inicialización"};

        final String[] videoUrls = {"",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-01.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-02.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-03.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-04.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-05.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-06.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-07.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-08.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-09.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-10.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-11.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-12.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-13.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-14.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-15.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-16.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-17.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-18.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-19.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-20.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-21.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-22.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-23.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-24.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-25.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-26.mp4"};

        final String[] descargaNames = {"Seleccione la Descarga",
                "01 - Registro",
                "02 - Términos del contrato",
                "03 - Dilema de principios",
                "04 - Entrada escrita a mano",
                "05 - Nota de voz",
                "06 - Modo vibrador",
                "07 - Señal de marca",
                "08 - Nuevo modelo",
                "09 - Número bloqueado",
                "10 - Plan familiar",
                "11 - Fin del servicio",
                "12 - Recepción fuera de rango",
                "13 - Llamada restringida",
                "14 - Memoria reseteada",
                "15 - Doble poseedor",
                "16 - Reparación",
                "17 - Familia de descuento",
                "18 - Líneas cruzadas",
                "19 - Borrando todos los mensajes",
                "20 - Transferencia de datos",
                "21 - PIN",
                "22 - Desconexión",
                "23 - Incumplimiento del contrato",
                "24 - Recuperación de datos",
                "25 - Reinicio",
                "26 - Inicialización"};

        final String[] descargaUrls = {"",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-01.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-02.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-03.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-04.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-05.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-06.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-07.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-08.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-09.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-10.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-11.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-12.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-13.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-14.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-15.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-16.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-17.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-18.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-19.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-20.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-21.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-22.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-23.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-24.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-25.mp4",
                "https://archive.org/download/mirai-nikki-latino/Mirai%20Nikki/%28Locura%20Anime%29%20MiNi-26.mp4"};

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
        Intent intent = new Intent(anime1.this, WatchActivity3.class);
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
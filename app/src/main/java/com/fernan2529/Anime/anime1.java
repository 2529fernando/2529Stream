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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fernan2529.R;
import com.fernan2529.WatchViewActivities.WatchActivityViewGeneral; // << IMPORTANTE

import java.util.Random;

public class anime1 extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1001;

    private Spinner spinnerVideos;
    private Spinner spinnerDescargas;
    private ArrayAdapter<String> videosAdapter;
    private ArrayAdapter<String> descargasAdapter;
    private Random random;

    private boolean ignoreFirstVideoSelect = true;
    private boolean ignoreFirstDownloadSelect = true;

    private static final String STATE_VIDEO_POS = "state_video_pos";
    private static final String STATE_DL_POS = "state_dl_pos";

    private final String[] videoNames = {"Seleccione el Capitulo",
            "01 - Registro","02 - Términos del contrato","03 - Dilema de principios",
            "04 - Entrada escrita a mano","05 - Nota de voz","06 - Modo vibrador",
            "07 - Señal de marca","08 - Nuevo modelo","09 - Número bloqueado",
            "10 - Plan familiar","11 - Fin del servicio","12 - Recepción fuera de rango",
            "13 - Llamada restringida","14 - Memoria reseteada","15 - Doble poseedor",
            "16 - Reparación","17 - Familia de descuento","18 - Líneas cruzadas",
            "19 - Borrando todos los mensajes","20 - Transferencia de datos","21 - PIN",
            "22 - Desconexión","23 - Incumplimiento del contrato","24 - Recuperación de datos",
            "25 - Reinicio","26 - Inicialización"};

    private final String[] videoUrls = {"",
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

    private final String[] descargaNames = videoNames;   // mismos nombres
    private final String[] descargaUrls = videoUrls;     // mismas URLs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime1);

        spinnerVideos = findViewById(R.id.spinner_videos);
        spinnerDescargas = findViewById(R.id.spinner_downloads);
        Button playRandomButton = findViewById(R.id.aleatorio);

        videosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, videoNames);
        videosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVideos.setAdapter(videosAdapter);

        descargasAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, descargaNames);
        descargasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDescargas.setAdapter(descargasAdapter);

        random = new Random();

        // Restaurar selección si hubo rotación
        if (savedInstanceState != null) {
            spinnerVideos.setSelection(savedInstanceState.getInt(STATE_VIDEO_POS, 0));
            spinnerDescargas.setSelection(savedInstanceState.getInt(STATE_DL_POS, 0));
        }

        spinnerVideos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (ignoreFirstVideoSelect) { ignoreFirstVideoSelect = false; return; }
                if (position != 0) {
                    String url = videoUrls[position];
                    String title = videoNames[position];
                    openPlayer(url, title); // 👉 Abre WatchActivityViewGeneral
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });

        spinnerDescargas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (ignoreFirstDownloadSelect) { ignoreFirstDownloadSelect = false; return; }
                if (position != 0) {
                    String url = descargaUrls[position];
                    startDownloadWithPermissions(url);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });

        playRandomButton.setOnClickListener(v -> {
            int randomIndex = random.nextInt(videoUrls.length - 1) + 1; // 1..N
            String url = videoUrls[randomIndex];
            String title = videoNames[randomIndex];
            openPlayer(url, title); // 👉 Abre WatchActivityViewGeneral
        });
    }

    /** Abre el reproductor WatchActivityViewGeneral con URL y título opcional */
    private void openPlayer(String videoUrl, String title) {
        // Usa el factory method de tu WatchActivity para mantener consistencia
        Intent intent = WatchActivityViewGeneral.newIntent(anime1.this, videoUrl, title);
        startActivity(intent);
    }

    /** Descarga con permisos compatibles por versión */
    private void startDownloadWithPermissions(String url) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ no requiere WRITE_EXTERNAL_STORAGE para DownloadManager
            downloadFile(url);
        } else {
            if (hasWritePermission()) {
                downloadFile(url);
            } else {
                requestWritePermission();
            }
        }
    }

    private boolean hasWritePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestWritePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_VIDEO_POS, spinnerVideos.getSelectedItemPosition());
        outState.putInt(STATE_DL_POS, spinnerDescargas.getSelectedItemPosition());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso concedido. Vuelve a iniciar la descarga.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permiso denegado. No se puede descargar.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void downloadFile(String downloadUrl) {
        try {
            String fileName = guessFileName(downloadUrl);

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
            request.setTitle(fileName);
            request.setDescription("Descargando " + fileName);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.allowScanningByMediaScanner();

            // Destino: público Descargas cuando es posible
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            } else {
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            }

            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            if (manager != null) {
                manager.enqueue(request);
                Toast.makeText(this, "Descargando " + fileName, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "DownloadManager no disponible", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al iniciar la descarga: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String guessFileName(String url) {
        try {
            String raw = Uri.parse(url).getLastPathSegment();
            if (raw == null || raw.trim().isEmpty()) return "video.mp4";
            String decoded = java.net.URLDecoder.decode(raw, "UTF-8");
            if (!decoded.contains(".")) decoded += ".mp4";
            return decoded;
        } catch (Exception e) {
            return "video.mp4";
        }
    }
}

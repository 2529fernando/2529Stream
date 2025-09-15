package com.fernan2529;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1001;

    private boolean isResetting = false;  // evita re-entrada al resetear el spinner
    private boolean isBound = false;

    private AudioService audioService;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override public void onServiceConnected(ComponentName className, IBinder service) {
            AudioService.LocalBinder binder = (AudioService.LocalBinder) service;
            audioService = binder.getService();
            // Solo mostrar notificación si tienes permiso (API 33+ requiere POST_NOTIFICATIONS)
            if (Build.VERSION.SDK_INT < 33 ||
                    ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS)
                            == PackageManager.PERMISSION_GRANTED) {
                audioService.showNotification();
            }
            isBound = true;
        }
        @Override public void onServiceDisconnected(ComponentName arg0) {
            audioService = null;
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkAndRequestPermissions();
        configureOnClickListeners();
        configureSpinner();
        // startAudioService();  // opcional: inicia/enciende el servicio al abrir la app
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Desenlaza si está enlazado para evitar fugas
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }

    /** ==== Permisos según versión de Android ==== */
    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= 33) {
            // Android 13+: permisos granulares de medios + notificaciones
            String[] needed = new String[] {
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.POST_NOTIFICATIONS
            };
            requestIfNeeded(needed);
        } else {
            // Android 12-
            String[] needed = new String[] {
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
            requestIfNeeded(needed);
        }
    }

    private void requestIfNeeded(String[] permissions) {
        java.util.ArrayList<String> toRequest = new java.util.ArrayList<>();
        for (String p : permissions) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                toRequest.add(p);
            }
        }
        if (!toRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, toRequest.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Aquí puedes actuar si faltó algo crítico; por ahora no es obligatorio
    }

    /** ==== Click listeners ==== */
    private void configureOnClickListeners() {
        // Cabecera
        findViewById(R.id.donaciones).setOnClickListener(v -> startNewActivity(donaciones.class));
        findViewById(R.id.btn_juegos).setOnClickListener(v -> startNewActivity(JuegosActivity.class));
        findViewById(R.id.potifyy).setOnClickListener(v -> startNewActivity(musicactivity.class));
        findViewById(R.id.btnchat).setOnClickListener(v -> startNewActivity(SplashActivity.class));

        // Fila 1
        findViewById(R.id.btn_navegador).setOnClickListener(v -> startNewActivity(Navegation.class));
        findViewById(R.id.btn_descargas).setOnClickListener(v -> startNewActivity(DownloadlinkActivity.class));
        findViewById(R.id.descarg).setOnClickListener(v -> startNewActivity(desactivity.class));

        // Fila 2
        findViewById(R.id.btn_reproductor).setOnClickListener(v -> startNewActivity(Reproductor.class));
        findViewById(R.id.btnaudio).setOnClickListener(v -> startNewActivity(nubeactivity.class));
        findViewById(R.id.btn_ver).setOnClickListener(v -> startNewActivity(version.class));

        // Posters
        findViewById(R.id.prop).setOnClickListener(v -> startNewActivity(propu.class));
        findViewById(R.id.avatar).setOnClickListener(v -> startNewActivity(series6.class));
        findViewById(R.id.cnn).setOnClickListener(v -> startNewActivity(WatchActivitytwo.class));
        findViewById(R.id.espn).setOnClickListener(v -> openWebView("https://tvlibreonline.org/en-vivo/espn/"));
        findViewById(R.id.spidey).setOnClickListener(v -> openWebView("https://kllamrd.org/video/tt10872600/"));
        findViewById(R.id.sony).setOnClickListener(v -> openWebView("https://www.cablevisionhd.com/canal-sony-en-vivo.html"));
        // Nota: R.id.boing se eliminó porque no existe en el XML actual.
    }

    private void startNewActivity(Class<?> activityClass) {
        startActivity(new Intent(MainActivity.this, activityClass));
    }

    private void openWebView(String url) {
        Intent intent = new Intent(MainActivity.this, WebViewActivity4.class);
        intent.putExtra("VIDEO_URL", url);
        startActivity(intent);
    }

    /** ==== Servicio de audio (opcional) ==== */
    private void startAudioService() {
        Intent intent = new Intent(MainActivity.this, AudioService.class);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        // Si NO quieres abrir una web al iniciar el servicio, comenta las 2 líneas siguientes.
        Intent intent2 = new Intent(MainActivity.this, WebActivity.class);
        startActivity(intent2);
    }

    /** ==== Spinner ==== */
    private void configureSpinner() {
        Spinner spinner = findViewById(R.id.spinner_activities);

        String[] activityNames = {
                "Seleccione la Categoria",
                "Entretenimiento", "Peliculas", "Series", "Anime",
                "Doramas", "Novelas", "Deportes", "Infantiles",
                "Comedia", "Historia", "Hogar", "Musica", "Noticias"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, activityNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isResetting) return;
                if (position != 0) {
                    startActivityBasedOnSelection(position);
                    // Reset a la opción 0 sin re-entrar
                    isResetting = true;
                    spinner.post(() -> {
                        spinner.setSelection(0, false);
                        isResetting = false;
                    });
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void startActivityBasedOnSelection(int position) {
        Class<?> targetActivity;
        switch (position) {
            case 1:  targetActivity = MainActivity2.class;  break;
            case 2:  targetActivity = MainActivity3.class;  break;
            case 3:  targetActivity = MainActivity4.class;  break;
            case 4:  targetActivity = MainActivity13.class; break;
            case 5:  targetActivity = MainActivity14.class; break;
            case 6:  targetActivity = MainActivity5.class;  break;
            case 7:  targetActivity = MainActivity6.class;  break;
            case 8:  targetActivity = MainActivity7.class;  break;
            case 9:  targetActivity = MainActivity8.class;  break;
            case 10: targetActivity = MainActivity9.class;  break;
            case 11: targetActivity = MainActivity10.class; break;
            case 12: targetActivity = MainActivity11.class; break;
            case 13: targetActivity = MainActivity12.class; break;
            default: return;
        }
        startActivity(new Intent(MainActivity.this, targetActivity));
    }
}

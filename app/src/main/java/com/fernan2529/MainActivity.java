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

    private boolean isResetting = false; // evita re-entrada al resetear el spinner
    private boolean isBound = false;

    private AudioService audioService;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override public void onServiceConnected(ComponentName className, IBinder service) {
            AudioService.LocalBinder binder = (AudioService.LocalBinder) service;
            audioService = binder.getService();
            audioService.showNotification();
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
        // Si quieres iniciar y enlazar el servicio de audio al abrir la app, descomenta:
        // startAudioService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }

    /** ==== Permisos según versión de Android ==== */
    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= 33) {
            // Android 13+ (Tiramisu): leen medios por tipo y notificaciones aparte
            String[] needed = new String[] {
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.POST_NOTIFICATIONS // si muestras notificaciones del servicio
            };
            requestIfNeeded(needed);
        } else {
            // Android 12 y anteriores
            String[] needed = new String[] {
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
            requestIfNeeded(needed);
        }
    }

    private void requestIfNeeded(String[] permissions) {
        // Reúne sólo las que faltan
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
        if (requestCode == PERMISSION_REQUEST_CODE) {
            // Aquí podrías reaccionar si faltó algo crítico
        }
    }

    /** ==== Click listeners ==== */
    private void configureOnClickListeners() {
        // Cabecera
        findViewById(R.id.donaciones).setOnClickListener(v -> startNewActivity(donaciones.class));
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

        // NOTA: Se eliminó R.id.boing porque no existe en tu XML actual.
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

        // Si NO quieres abrir una WebActivity al iniciar el servicio, comenta lo de abajo:
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

                    // Reset a la opción 0 sin animación y evitando re-llamada
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

package com.fernan2529;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.util.Pair;
import android.util.SparseArray;

import com.fernan2529.Categorias.Deportes;
import com.fernan2529.Categorias.Entretenimiento;
import com.fernan2529.Categorias.Peliculas;
import com.fernan2529.Doramas.doramas3;
import com.fernan2529.Series.series6;
import com.fernan2529.WebViewActivities.WebViewActivityGeneral;
import com.fernan2529.WatchViewActivities.WatchActivityViewGeneral;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private long lastClickAt = 0L;
    private boolean canClick() {
        long now = System.currentTimeMillis();
        if (now - lastClickAt < 300) return false;
        lastClickAt = now;
        return true;
    }

    private final ActivityResultLauncher<String[]> requestPermissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {});

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkAndRequestPermissions();
        setupHeaderAndGridButtons();
        setupWebButtons();

        /* =================== Spinner CATEGORIAS =================== */
        Spinner spinner = findViewById(R.id.spinner_activities3);

// 0 = placeholder, 1..n = categorías reales
        final String[] activityNames = {
                "-",               // 0 -> placeholder
                "Entretenimiento", // 1 -> Entretenimiento.class
                "Peliculas",       // 2 -> Peliculas.class
                "Deportes"         // 3 -> Deportes.class
        };

// Adaptador con el contexto correcto
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                MainActivity.this,
                android.R.layout.simple_spinner_item,
                activityNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

// Mostrar el placeholder desde el inicio
        final int indexThis = 0; // Esta Activity (inicio) NO está en la lista, usamos 0 como “-”
        spinner.setSelection(indexThis, false);

// Para evitar que onItemSelected dispare solo por el setSelection inicial
        final boolean[] userTouched = {false};
        spinner.setOnTouchListener((v, e) -> { userTouched[0] = true; return false; });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Si aún no hay interacción del usuario o es el placeholder, no navegamos
                if (!userTouched[0] || position == 0) return;

                Intent intent;
                switch (position) {
                    case 1: // Entretenimiento
                        intent = new Intent(MainActivity.this, Entretenimiento.class);
                        break;
                    case 2: // Peliculas
                        intent = new Intent(MainActivity.this, Peliculas.class);
                        break;
                    case 3: // Deportes
                        intent = new Intent(MainActivity.this, Deportes.class);
                        break;
                    default:
                        return;
                }
                startActivity(intent);

                // Opcional: volver a dejar el spinner en el placeholder después de navegar
                spinner.post(() -> {
                    userTouched[0] = false; // evitamos disparo extra
                    spinner.setSelection(indexThis, false);
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No se usa
            }
        });
    }


        /* =================== Permisos =================== */
    private void checkAndRequestPermissions() {
        List<String> toRequest = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= 33) {
            addIfNotGranted(toRequest, Manifest.permission.READ_MEDIA_AUDIO);
            addIfNotGranted(toRequest, Manifest.permission.READ_MEDIA_VIDEO);
            addIfNotGranted(toRequest, Manifest.permission.READ_MEDIA_IMAGES);
            if (!areNotificationsEnabled(this)) {
                addIfNotGranted(toRequest, Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            addIfNotGranted(toRequest, Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (!toRequest.isEmpty()) {
            requestPermissionsLauncher.launch(toRequest.toArray(new String[0]));
        }
    }

    private void addIfNotGranted(List<String> list, String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            list.add(permission);
        }
    }

    private boolean areNotificationsEnabled(@NonNull Context ctx) {
        NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return nm != null && nm.areNotificationsEnabled();
        }
        return true;
    }

    /* =================== Botones a Activities =================== */
    private void setupHeaderAndGridButtons() {
        SparseArray<Class<?>> map = new SparseArray<>();
        // Cabecera
        map.put(R.id.donaciones,       donaciones.class);
        map.put(R.id.btn_juegos,       JuegosActivity.class);
        map.put(R.id.potifyy,          MusicActivity.class);
        map.put(R.id.btnchat,          SplashActivity.class);
        // Fila 1
        map.put(R.id.btn_navegador,    Navegation.class);
        map.put(R.id.btn_descargas,    DownloadlinkActivity.class);
        map.put(R.id.descarg,          DescargasActivity.class);
        // Fila 2
        map.put(R.id.btn_reproductor,  Reproductor.class);
        map.put(R.id.btnaudio,         nubeactivity.class);
        map.put(R.id.btn_ver,          version.class);
        // Posters
        map.put(R.id.prop,             doramas3.class);
        map.put(R.id.avatar,           series6.class);

        for (int i = 0; i < map.size(); i++) {
            final int viewId = map.keyAt(i);
            final Class<?> dest = map.valueAt(i);
            View v = findViewById(viewId);
            if (v == null || dest == null) continue;
            v.setOnClickListener(_v -> {
                if (!canClick()) return;
                startActivity(new Intent(MainActivity.this, dest));
            });
        }

        // CNN a WatchActivityViewGeneral
        View cnnTile = findViewById(R.id.cnn);
        if (cnnTile != null) {
            cnnTile.setOnClickListener(v -> {
                if (!canClick()) return;
                String cnnUrl = "https://d3696l48vwq25d.cloudfront.net/v1/master/3722c60a815c199d9c0ef36c5b73da68a62b09d1/cc-0g2918mubifjw/index.m3u8";
                Intent i = WatchActivityViewGeneral.newIntent(MainActivity.this, cnnUrl, "CNN en vivo");
                startActivity(i);
            });
        }
    }

    /* =================== Botones Web =================== */
    private void setupWebButtons() {
        SparseArray<Pair<String, Class<?>>> web = new SparseArray<>();
        web.put(R.id.espn,   Pair.create("https://www.cablevisionhd.com/espn-en-vivo.html", WebViewActivityGeneral.class));
        web.put(R.id.spidey, Pair.create("https://kllamrd.org/video/tt10872600/", WebViewActivityGeneral.class));
        web.put(R.id.sony,   Pair.create("https://www.cablevisionhd.com/canal-sony-en-vivo.html", WebViewActivityGeneral.class));

        for (int i = 0; i < web.size(); i++) {
            final int viewId = web.keyAt(i);
            final Pair<String, Class<?>> dest = web.valueAt(i);
            View v = findViewById(viewId);
            if (v == null || dest == null || dest.first == null || dest.second == null) continue;
            v.setOnClickListener(_v -> {
                if (!canClick()) return;
                openWebView(dest.first, dest.second);
            });
        }
    }

    private void openWebView(String url, Class<?> webViewActivity) {
        Intent intent = new Intent(MainActivity.this, webViewActivity);
        intent.putExtra("url", url);
        startActivity(intent);
    }
}

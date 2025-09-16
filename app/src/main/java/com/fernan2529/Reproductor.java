package com.fernan2529;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.fernan2529.Categorias.Animaciones;
import com.fernan2529.Categorias.Comedia;
import com.fernan2529.Categorias.Deportes;
import com.fernan2529.Categorias.Dorama;
import com.fernan2529.Categorias.Entretenimiento;
import com.fernan2529.Categorias.Historia;
import com.fernan2529.Categorias.Hogar;
import com.fernan2529.Categorias.Infantiles;
import com.fernan2529.Categorias.Musica;
import com.fernan2529.Categorias.Noticias;
import com.fernan2529.Categorias.Novelas;
import com.fernan2529.Categorias.Peliculas;
import com.fernan2529.Categorias.Serie;
import com.fernan2529.WatchViewActivities.WatchActivityViewGeneral;

public class Reproductor extends AppCompatActivity {

    private EditText editTextLink;
    private Button buttonPlay;

    // === Datos del Spinner ===
    private static final String[] CATEGORY_NAMES = {
            "Seleccione la Categoria",
            "Entretenimiento", "Peliculas", "Series", "Anime",
            "Dorama", "Novelas", "Deportes", "Infantiles",
            "Comedia", "Historia", "Hogar", "Musica", "Noticias"
    };

    private static final Class<?>[] CATEGORY_ACTIVITIES = {
            null,                   // "Seleccione la Categoria"
            Entretenimiento.class,  // "Entretenimiento"
            Peliculas.class,        // "Peliculas"
            Serie.class,           // "Serie" (se ignora para no relanzar)
            Animaciones.class,            // "Animes"
            Dorama.class,          // "Dorama"
            Novelas.class,          // "Novelas"
            Deportes.class,         // "Deportes"
            Infantiles.class,       // "Infantiles"
            Comedia.class,          // "Comedia"
            Historia.class,         // "Historia"
            Hogar.class,            // "Hogar"
            Musica.class,           // "Musica"
            Noticias.class          // "Noticias"
    };

    private boolean firstSpinnerTrigger = true;

    // Activity Result para elegir video
    private final ActivityResultLauncher<Intent> pickVideoLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedVideoUri = result.getData().getData();
                    if (selectedVideoUri != null) {
                        String uriString = selectedVideoUri.toString(); // content://…
                        String title = guessTitleFromUri(selectedVideoUri, "Video local");
                        Intent intent = WatchActivityViewGeneral.newIntent(this, uriString, title);
                        startActivity(intent);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor);

        editTextLink = findViewById(R.id.editTextLink);
        buttonPlay   = findViewById(R.id.buttonPlay);

        // Reproducir desde link directo
        buttonPlay.setOnClickListener(v -> {
            String link = editTextLink.getText().toString().trim();
            if (link.isEmpty()) {
                Toast.makeText(this, "Ingresa un enlace válido", Toast.LENGTH_SHORT).show();
                return;
            }
            // Si no tiene esquema, intenta agregar http://
            if (!link.startsWith("http://") && !link.startsWith("https://")
                    && !link.startsWith("content://") && !link.startsWith("file://")) {
                link = "http://" + link;
            }
            String title = guessTitleFromString(link, "Reproducción directa");
            Intent intent = WatchActivityViewGeneral.newIntent(Reproductor.this, link, title);
            startActivity(intent);
        });

        // Spinner de categorías
        setupSpinner();

        // Seleccionar video de la galería
        Button pickVideoButton = findViewById(R.id.btn_seleccionar);
        if (pickVideoButton != null) {
            pickVideoButton.setOnClickListener(view -> pickVideoFromGallery());
        }
    }

    /* ================= Spinner ================= */
    private void setupSpinner() {
        Spinner spinner = findViewById(R.id.spinner_activities);
        if (spinner == null) return;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, CATEGORY_NAMES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0, false); // evita trigger inicial

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (firstSpinnerTrigger) { firstSpinnerTrigger = false; return; }
                if (position <= 0 || position >= CATEGORY_ACTIVITIES.length) return;

                Class<?> target = CATEGORY_ACTIVITIES[position];
                if (target == null) return;

                startActivity(new Intent(Reproductor.this, target));
                parent.setSelection(0); // opcional: regresar a "Seleccione…"
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { /* no-op */ }
        });
    }

    /* ============== Galería ============== */
    private void pickVideoFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        pickVideoLauncher.launch(intent);
    }

    /* ============== Helpers de título ============== */
    private String guessTitleFromString(String urlOrPath, String fallback) {
        try {
            Uri u = Uri.parse(urlOrPath);
            String last = u.getLastPathSegment();
            if (last == null || last.isEmpty()) return fallback;
            int q = last.indexOf('?');
            if (q >= 0) last = last.substring(0, q);
            if (last.isEmpty()) return fallback;
            return decode(last);
        } catch (Exception e) {
            return fallback;
        }
    }

    private String guessTitleFromUri(Uri uri, String fallback) {
        try {
            String last = uri.getLastPathSegment();
            if (last == null || last.isEmpty()) return fallback;
            int q = last.indexOf('?');
            if (q >= 0) last = last.substring(0, q);
            return decode(last);
        } catch (Exception e) {
            return fallback;
        }
    }

    private String decode(String s) {
        try {
            String decoded = java.net.URLDecoder.decode(s, "UTF-8");
            return decoded.replace('_', ' ');
        } catch (Exception e) {
            return s;
        }
    }
}

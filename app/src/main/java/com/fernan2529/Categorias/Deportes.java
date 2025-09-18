package com.fernan2529.Categorias;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.util.SparseArray;

import com.fernan2529.R;

// ===== IMPORTS =====
import com.fernan2529.MainActivity; // si es MainActivity14, cámbialo aquí
import com.fernan2529.Reproductor;
import com.fernan2529.WebViewActivities.WebViewActivityGeneral;

import com.fernan2529.data.CategoriesRepository;
import com.fernan2529.nav.CategoryNavigator;

public class Deportes extends AppCompatActivity {

    private Spinner spinner;
    private String[] categories = new String[0];
    private int indexThis = -1;           // índice real de "Deportes"
    private boolean userTouched = false;  // marca interacción real (no programática)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deportes);

        // Botones básicos
        setupButton(R.id.deportes, MainActivity.class);
        setupButton(R.id.btn_ver, MainActivity.class);
        setupButton(R.id.button_reproductor, Reproductor.class);

        setupSpinner();     // ⬅️ lógica aplicada con spinner_activities
        setupWebButtons();
    }

    // ----------------- Utilidades -----------------
    private void setupButton(int viewId, Class<?> activityClass) {
        View v = findViewById(viewId);
        if (v != null && activityClass != null) {
            v.setOnClickListener(_v -> startActivity(new Intent(this, activityClass)));
        }
    }

    private void openWebView(String url) {
        if (url == null || url.isEmpty()) return;
        Intent intent = new Intent(this, WebViewActivityGeneral.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    private static int findIndex(String[] arr, String target) {
        if (arr == null || target == null) return -1;
        for (int i = 0; i < arr.length; i++) {
            if (target.equalsIgnoreCase(arr[i])) return i;
        }
        return -1;
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    // ----------------- Spinner con lógica unificada -----------------
    private void setupSpinner() {
        // ✅ usar spinner_activities (no spinner_activities3)
        spinner = findViewById(R.id.spinner_activities);
        if (spinner == null) return;

        // Cargar categorías desde el repositorio central
        CategoriesRepository repo = new CategoriesRepository();
        String[] loaded = repo.getCategories();
        if (loaded != null) categories = loaded;

        // Detectar índice real por nombre
        indexThis = findIndex(categories, "Deportes");

        // Fallback seguro si no se encuentra (Deportes suele ser 7)
        if (indexThis < 0) {
            indexThis = Math.min(7, Math.max(0, categories.length - 1));
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Mantener “Deportes” seleccionado sin disparar navegación
        spinner.setSelection(indexThis, false);

        // Marcar interacción real (evita triggers por setSelection programático)
        spinner.setOnTouchListener((v, e) -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_UP) {
                userTouched = true;
            }
            return false;
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!userTouched) return; // ignora selección programática
                userTouched = false;

                if (categories.length == 0 || position < 0 || position >= categories.length) return;
                if (position == 0 || position == indexThis) return; // placeholder o misma pantalla

                // Navegación centralizada
                Intent intent = CategoryNavigator.buildIntent(Deportes.this, position);
                if (intent == null) {
                    toast("No se pudo abrir la categoría seleccionada.");
                    spinner.post(() -> spinner.setSelection(0, false));
                    return;
                }

                startActivity(intent);

                // Volver visualmente al placeholder para próximas selecciones
                spinner.post(() -> spinner.setSelection(0, false));
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { /* no-op */ }
        });
    }

    // ----------------- Botones Web (todos con WebViewActivityGeneral) -----------------
    private void setupWebButtons() {
        SparseArray<String> map = new SparseArray<>();
        map.put(R.id.libre,       "https://futbollibre-tv.nl/");
        map.put(R.id.dsports,     "https://www.cablevisionhd.com/directv-sports-en-vivo.html");
        map.put(R.id.dsports2,    "https://www.cablevisionhd.com/directv-sports-2-en-vivo.html");
        map.put(R.id.dsportsplus, "https://www.cablevisionhd.com/directv-sports-plus-en-vivo.html");
        map.put(R.id.espn,        "https://www.cablevisionhd.com/espn-en-vivo.html");
        map.put(R.id.espn2,       "https://www.cablevisionhd.com/espn-2-en-vivo.html");
        map.put(R.id.espn3,       "https://www.cablevisionhd.com/espn-3-en-vivo.html");
        map.put(R.id.espn4,       "https://www.cablevisionhd.com/espn-4-en-vivo.html");
        map.put(R.id.espnpre,     "https://www.cablevisionhd.com/espn-premium-en-vivo.html");
        map.put(R.id.bein,        "https://www.cablevisionhd.com/bein-sports-extra-en-vivo.html");
        map.put(R.id.movistar,    "https://www.cablevisionhd.com/movistar-deportes-en-vivo.html");
        map.put(R.id.tntsports,   "https://www.cablevisionhd.com/tnt-sports-en-vivo.html");
        map.put(R.id.appletv,     "https://ufreetv.com/fox.html");
        map.put(R.id.goltv,       "https://www.cablevisionhd.com/gol-peru-en-vivo.html");
        map.put(R.id.caracol,     "https://cdn.chatytvgratis.net/caracoltabs.php?width=640&height=410");
        map.put(R.id.nba,         "https://masdeportesonline.com/partidos-nba/");
        map.put(R.id.foxsports,   "https://www.cablevisionhd.com/fox-sports-en-vivo.html");

        for (int i = 0; i < map.size(); i++) {
            final int viewId = map.keyAt(i);
            final String url = map.valueAt(i);

            View btn = findViewById(viewId);
            if (btn == null || url == null || url.isEmpty()) continue;

            btn.setOnClickListener(v -> openWebView(url));
        }
    }
}

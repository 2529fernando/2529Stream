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
import com.fernan2529.MainActivity;
import com.fernan2529.Reproductor;
import com.fernan2529.WebViewActivities.WebViewActivityGeneral;
import com.fernan2529.data.CategoriesRepository;
import com.fernan2529.nav.CategoryNavigator;

public class Infantiles extends AppCompatActivity {

    private Spinner spinner;
    private String[] categories = new String[0];
    private int indexThis = -1;           // índice real de "Infantiles"
    private boolean userTouched = false;  // marca interacción real del usuario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infantiles);

        // Botones básicos
        setupButton(R.id.infan, MainActivity.class);
        setupButton(R.id.btn_ver, MainActivity.class);
        setupButton(R.id.button_reproductor, Reproductor.class);

        // UI
        setupSpinner();    // ⬅️ lógica aplicada
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
        intent.putExtra("url", url); // EXTRA estandarizado
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
        // ✅ usamos spinner_activities (no spinner_activities3)
        spinner = findViewById(R.id.spinner_activities);
        if (spinner == null) return;

        // Cargar categorías desde el repositorio (consistencia con las demás Activities)
        CategoriesRepository repo = new CategoriesRepository();
        String[] loaded = repo.getCategories();
        if (loaded != null) categories = loaded;

        // Detectar el índice real de esta pantalla por nombre
        indexThis = findIndex(categories, "Infantiles");

        // Fallback seguro si no se encuentra (Infantiles suele ser 8)
        if (indexThis < 0) {
            indexThis = Math.min(8, Math.max(0, categories.length - 1));
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Mantener “Infantiles” seleccionado sin disparar navegación
        spinner.setSelection(indexThis, false);

        // Marcar interacción real (evita bucles por setSelection programático)
        spinner.setOnTouchListener((v, e) -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_UP) {
                userTouched = true;
            }
            return false; // permitir comportamiento normal del spinner
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!userTouched) return; // ignora selección programática
                userTouched = false;

                if (categories.length == 0 || position < 0 || position >= categories.length) return;

                // Ignora placeholder (si es 0) o la misma pantalla
                if (position == 0 || position == indexThis) return;

                // Navegar usando tu router central para mantener consistencia
                Intent intent = CategoryNavigator.buildIntent(Infantiles.this, position);
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
        map.put(R.id.nick,   "https://www.cablevisionhd.com/nick-en-vivo.html");
        map.put(R.id.cn,     "https://www.cablevisionhd.com/cartoon-network-en-vivo.html");
        map.put(R.id.disney, "https://www.cablevisionhd.com/disney-channel-en-vivo.html");
        map.put(R.id.boing,  "https://www.mediasetinfinity.es/directo/boing/");

        for (int i = 0; i < map.size(); i++) {
            final int viewId = map.keyAt(i);
            final String url = map.valueAt(i);

            View btn = findViewById(viewId);
            if (btn == null || url == null || url.isEmpty()) continue;

            btn.setOnClickListener(v -> openWebView(url));
        }
    }
}

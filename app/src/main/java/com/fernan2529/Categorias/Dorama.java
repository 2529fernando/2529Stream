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

import com.fernan2529.Doramas.doramas1;
import com.fernan2529.Doramas.doramas2;
import com.fernan2529.Doramas.doramas3;
import com.fernan2529.R;

// IMPORTS (ajusta si tu Main es otra clase)
import com.fernan2529.MainActivity;
import com.fernan2529.Reproductor;

import com.fernan2529.data.CategoriesRepository;
import com.fernan2529.nav.CategoryNavigator;

public class Dorama extends AppCompatActivity {

    private Spinner spinner;
    private String[] categories = new String[0];
    private int indexThis = -1;           // índice real de "Dorama"
    private boolean userTouched = false;  // marca interacción real (no programática)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doramas);

        // Botones básicos
        setupButton(R.id.doramas, MainActivity.class);
        setupButton(R.id.btn_ver, MainActivity.class);
        setupButton(R.id.button_reproductor, Reproductor.class);

        setupSpinner();     // ⬅️ lógica unificada con spinner_activities
        setupImageTiles();
    }

    // ----------------- Utilidades -----------------
    private void setupButton(int viewId, Class<?> targetActivity) {
        View v = findViewById(viewId);
        if (v != null && targetActivity != null) {
            v.setOnClickListener(_v -> startActivity(new Intent(this, targetActivity)));
        }
    }

    private void openActivity(Class<?> targetActivity) {
        if (targetActivity == null) return;
        startActivity(new Intent(this, targetActivity));
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

    // ----------------- Spinner con lógica robusta -----------------
    private void setupSpinner() {
        // ✅ usar spinner_activities (no spinner_activities3)
        spinner = findViewById(R.id.spinner_activities);
        if (spinner == null) return;

        // Cargar categorías desde el repositorio central
        CategoriesRepository repo = new CategoriesRepository();
        String[] loaded = repo.getCategories();
        if (loaded != null) categories = loaded;

        // Detectar índice real por nombre
        indexThis = findIndex(categories, "Dorama");

        // Fallback seguro si no se encuentra (Dorama suele ser 5)
        if (indexThis < 0) {
            indexThis = Math.min(5, Math.max(0, categories.length - 1));
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Mantener “Dorama” seleccionado sin disparar navegación
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
                Intent intent = CategoryNavigator.buildIntent(Dorama.this, position);
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

    // ----------------- Portadas (ImageView -> Activity) -----------------
    private void setupImageTiles() {
        SparseArray<Class<?>> map = new SparseArray<>();
        map.put(R.id.lavozdetuamor, doramas1.class);
        map.put(R.id.propues,       doramas3.class);
        map.put(R.id.hada,          doramas2.class);

        for (int i = 0; i < map.size(); i++) {
            final int viewId = map.keyAt(i);
            final Class<?> dest = map.valueAt(i);

            View tile = findViewById(viewId);
            if (tile == null || dest == null) continue;

            tile.setOnClickListener(v -> openActivity(dest));
        }
    }
}

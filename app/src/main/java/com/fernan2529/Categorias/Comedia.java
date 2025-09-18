package com.fernan2529.Categorias;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import android.util.SparseArray;

import com.fernan2529.R;
import com.fernan2529.MainActivity;
import com.fernan2529.Reproductor;
import com.fernan2529.WebViewActivities.WebViewActivityGeneral;
import com.fernan2529.data.CategoriesRepository;
import com.fernan2529.nav.CategoryNavigator;

public class Comedia extends AppCompatActivity {

    private Spinner spinner;
    private String[] categories;
    private int indexThis = -1;           // índice de "Comedia" en el array
    private boolean userTouched = false;  // distinguir toque real vs. setSelection programático

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comedia);

        // Botones básicos
        setupButton(R.id.comedia, MainActivity.class);
        setupButton(R.id.btn_ver, MainActivity.class);
        setupButton(R.id.button_reproductor, Reproductor.class);

        // UI
        setupSpinner();     // ⬅️ spinner implementado con Repo + Navigator
        setupWebButtons();
    }

    /* =================== Utilidades de botones =================== */
    private void setupButton(int viewId, Class<?> targetActivity) {
        View v = findViewById(viewId);
        if (v != null && targetActivity != null) {
            v.setOnClickListener(_v -> startActivity(new Intent(Comedia.this, targetActivity)));
        }
    }

    private void openWebView(String url) {
        if (url == null || url.isEmpty()) return;
        Intent intent = new Intent(Comedia.this, WebViewActivityGeneral.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    /* =================== Spinner =================== */
    private void setupSpinner() {
        spinner = findViewById(R.id.spinner_activities);
        if (spinner == null) return;

        // Cargar categorías desde el repositorio (mismo array que usa MainActivity)
        CategoriesRepository repo = new CategoriesRepository();
        categories = repo.getCategories();

        // Detectar índice real de "Comedia" por nombre (por si cambia el orden en el repo)
        indexThis = findIndex(categories, "Comedia");
        if (indexThis < 0) indexThis = 9; // fallback seguro si no se encontrara

        // Adapter
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Selecciona esta categoría sin disparar navegación
        spinner.setSelection(indexThis, false);

        // Marcar interacción real (solo reaccionamos a toques del usuario)
        spinner.setOnTouchListener((v, e) -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_UP) {
                userTouched = true;
            }
            return false;
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!userTouched) return;         // ignora cambios programáticos
                userTouched = false;

                // Ignora placeholder o la misma pantalla
                if (position == 0 || position == indexThis) return;

                // Navega usando tu CategoryNavigator (mapea índice → Activity)
                Intent intent = CategoryNavigator.buildIntent(Comedia.this, position);
                if (intent != null) startActivity(intent);

                // Al volver, mantenemos marcada "Comedia" para evitar reentradas
                spinner.post(() -> spinner.setSelection(indexThis, false));
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { /* no-op */ }
        });
    }

    private static int findIndex(String[] arr, String target) {
        if (arr == null || target == null) return -1;
        for (int i = 0; i < arr.length; i++) {
            if (target.equalsIgnoreCase(arr[i])) return i;
        }
        return -1;
    }

    /* =================== Web buttons =================== */
    private void setupWebButtons() {
        SparseArray<String> map = new SparseArray<>();
        map.put(R.id.universalcomedy, "https://www.tvplusgratis2.com/universal-comedy-en-vivo.html");
        map.put(R.id.comedyc,         "https://www.tvporinternet2.com/comedy-central-en-vivo-por-internet.html");

        for (int i = 0; i < map.size(); i++) {
            final int viewId = map.keyAt(i);
            final String url = map.valueAt(i);
            View btn = findViewById(viewId);
            if (btn == null || url == null || url.isEmpty()) continue;
            btn.setOnClickListener(v -> openWebView(url));
        }
    }
}

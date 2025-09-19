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

import com.fernan2529.Novelas.novelas1;
import com.fernan2529.Novelas.novelas2;
import com.fernan2529.R;
import com.fernan2529.MainActivity;
import com.fernan2529.Reproductor;
import com.fernan2529.WebViewActivities.WebViewActivityGeneral;
import com.fernan2529.data.CategoriesRepository;
import com.fernan2529.nav.CategoryNavigator;

public class Novelas extends AppCompatActivity {

    private Spinner spinner;
    private String[] categories = new String[0];
    private int indexThis = -1;            // índice real de "Novelas"
    private boolean userTouched = false;   // marca interacción real (no programática)

    // Debounce para evitar doble click
    private static final long CLICK_DEBOUNCE_MS = 600L;
    private long lastClickAt = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novelas);

        // Botones básicos
        setupButton(R.id.novelas, MainActivity.class);
        setupButton(R.id.btn_ver, MainActivity.class);
        setupButton(R.id.button_reproductor, Reproductor.class);

        // UI
        setupSpinner();       // lógica unificada con spinner_activities
        setupPosters();       // cards / posters que abren Activities
        setupWebButtons();    // botones que abren WebView
    }

    // ----------------- Utilidades -----------------
    private void setupButton(int viewId, Class<?> activityClass) {
        View v = findViewById(viewId);
        if (v != null && activityClass != null) {
            v.setOnClickListener(_v -> {
                if (!canClick()) return;
                startActivity(new Intent(this, activityClass));
            });
        }
    }

    private void openWebView(String url) {
        if (url == null || url.isEmpty()) return;
        if (!canClick()) return;
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

    private boolean canClick() {
        long now = System.currentTimeMillis();
        if (now - lastClickAt < CLICK_DEBOUNCE_MS) return false;
        lastClickAt = now;
        return true;
    }

    // ----------------- Spinner con lógica unificada -----------------
    private void setupSpinner() {
        spinner = findViewById(R.id.spinner_activities);
        if (spinner == null) return;

        // Cargar categorías desde tu repositorio central
        CategoriesRepository repo = new CategoriesRepository();
        String[] loaded = repo.getCategories();
        if (loaded != null) categories = loaded;

        // Detectar índice real por nombre
        indexThis = findIndex(categories, "Novelas");

        // Fallback seguro si no se encuentra (Novelas suele ser 6)
        if (indexThis < 0) {
            indexThis = Math.min(6, Math.max(0, categories.length - 1));
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Mantener “Novelas” seleccionado sin disparar navegación
        spinner.setSelection(indexThis, false);

        // Marcar interacción real (evita bucles por setSelection programático)
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

                // Ignora placeholder (si es 0) o la misma pantalla
                if (position == 0 || position == indexThis) return;

                // Navegación centralizada
                Intent intent = CategoryNavigator.buildIntent(Novelas.this, position);
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

    // ----------------- Posters (ids -> Activities) -----------------
    private void setupPosters() {
        // Mapa de vistas a Activities destino
        SparseArray<Class<?>> posters = new SparseArray<>();


        posters.put(R.id.rbd, novelas1.class);
        posters.put(R.id.nuevorico, novelas2.class);

        for (int i = 0; i < posters.size(); i++) {
            final int viewId = posters.keyAt(i);
            final Class<?> dest = posters.valueAt(i);

            View v = findViewById(viewId);
            if (v == null || dest == null) continue;

            v.setOnClickListener(_v -> {
                if (!canClick()) return;
                startActivity(new Intent(Novelas.this, dest));
            });
        }
    }

    // ----------------- Botones Web (todos con WebViewActivityGeneral) -----------------
    private void setupWebButtons() {
        SparseArray<String> map = new SparseArray<>();
        map.put(R.id.tlnovelas,  "https://www.cablevisionhd.com/tlnovelas-en-vivo.html");
        map.put(R.id.tntnovelas, "https://www.tvplusgratis2.com/tnt-novelas-en-vivo.html");

        for (int i = 0; i < map.size(); i++) {
            final int viewId = map.keyAt(i);
            final String url = map.valueAt(i);

            View btn = findViewById(viewId);
            if (btn == null || url == null || url.isEmpty()) continue;

            btn.setOnClickListener(v -> openWebView(url));
        }
    }
}

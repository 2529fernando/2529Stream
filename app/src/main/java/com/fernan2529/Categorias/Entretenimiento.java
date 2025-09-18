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

// ===== IMPORTS NECESARIOS =====
import com.fernan2529.MainActivity;
import com.fernan2529.Reproductor;
import com.fernan2529.WebViewActivities.WebViewActivityGeneral;

// ===== Repositorio/Router para categorías =====
import com.fernan2529.data.CategoriesRepository;
import com.fernan2529.nav.CategoryNavigator;

public class Entretenimiento extends AppCompatActivity {

    private Spinner spinner;
    private String[] categories = new String[0];
    private int indexThis = -1;           // índice de "Entretenimiento" detectado por nombre
    private boolean userTouched = false;  // marca interacción real del usuario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entretenimiento);

        // Botones normales
        setupButton(R.id.entre, MainActivity.class);
        setupButton(R.id.btn_ver, MainActivity.class);
        setupButton(R.id.button_reproductor, Reproductor.class);

        // Spinner y botones web
        setupSpinner();
        setupWebButtons();
    }

    // ================= Botones simples =================
    private void setupButton(int viewId, final Class<?> targetActivity) {
        View v = findViewById(viewId);
        if (v != null && targetActivity != null) {
            v.setOnClickListener(_v ->
                    startActivity(new Intent(Entretenimiento.this, targetActivity)));
        }
    }

    // ================= Utilidades =================
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

    // ================= Spinner de categorías =================
    private void setupSpinner() {
        spinner = findViewById(R.id.spinner_activities); // ⬅️ cambiado aquí
        if (spinner == null) return;

        // Carga de categorías desde tu repositorio
        CategoriesRepository repo = new CategoriesRepository();
        String[] loaded = repo.getCategories();
        if (loaded != null) categories = loaded;

        // Detecta el índice real de esta pantalla por nombre
        indexThis = findIndex(categories, "Entretenimiento");

        // Fallback razonable
        if (indexThis < 0) {
            indexThis = Math.min(1, Math.max(0, categories.length - 1));
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Selecciona esta categoría sin disparar navegación
        spinner.setSelection(indexThis, false);

        // Marca interacción real
        spinner.setOnTouchListener((v, e) -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_UP) {
                userTouched = true;
            }
            return false;
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!userTouched) return;
                userTouched = false;

                if (categories.length == 0 || position < 0 || position >= categories.length) return;
                if (position == 0 || position == indexThis) return;

                Intent intent = CategoryNavigator.buildIntent(Entretenimiento.this, position);
                if (intent == null) {
                    toast("No se pudo abrir la categoría seleccionada.");
                    spinner.post(() -> spinner.setSelection(0, false));
                    return;
                }

                startActivity(intent);
                spinner.post(() -> spinner.setSelection(0, false));
            }

            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    // ================= Botones Web =================
    private void setupWebButtons() {
        SparseArray<String> map = new SparseArray<>();

        map.put(R.id.sony,      "https://www.cablevisionhd.com/canal-sony-en-vivo.html");
        map.put(R.id.axn,       "https://www.cablevisionhd.com/axn-en-vivo.html");
        map.put(R.id.tntseries, "https://www.cablevisionhd.com/tnt-series-en-vivo.html");
        map.put(R.id.ae,        "https://www.televisiongratishd2.com/discovery-a-y-e-en-vivo.html");
        map.put(R.id.warner,    "https://www.tvplusgratis2.com/warner-channel-en-vivo.html");

        map.put(R.id.hbo,       "https://telegratuita.com/en-vivo/hbo.php");
        map.put(R.id.space,     "https://www.tvspacehd.com/2022/10/space.html");
        map.put(R.id.tnt,       "https://www.tvspacehd.com/2022/10/canal-tnt.html");
        map.put(R.id.cww,       "https://d.daddylivehd.sx/embed/stream-300.php");
        map.put(R.id.fxicon,    "https://www.cablevisionhd.com/fx-en-vivo.html");

        for (int i = 0; i < map.size(); i++) {
            final int viewId = map.keyAt(i);
            final String url = map.valueAt(i);
            View btn = findViewById(viewId);
            if (btn == null || url == null || url.isEmpty()) continue;
            btn.setOnClickListener(v -> openWebView(url));
        }
    }

    private void openWebView(String url) {
        Intent intent = new Intent(Entretenimiento.this, WebViewActivityGeneral.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }
}

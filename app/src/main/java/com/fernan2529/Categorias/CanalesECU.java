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
import com.fernan2529.MainActivity;
import com.fernan2529.Reproductor;
import com.fernan2529.WatchViewActivities.WatchActivityViewGeneral;
import com.fernan2529.WebViewActivities.WebViewActivityGeneral;
import com.fernan2529.WebViewActivities.WebViewActivityAds;
import com.fernan2529.data.CategoriesRepository;
import com.fernan2529.nav.CategoryNavigator;

public class CanalesECU extends AppCompatActivity {

    private Spinner spinner;
    private String[] categories = new String[0];
    private int indexThis = -1;            // índice real de "Canales ECU"
    private boolean userTouched = false;   // marca interacción real (no programática)
    private long lastClickAt = 0L;         // anti doble click

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canales_ecu);

        // Botones básicos (ajusta IDs a los de tu layout)
        setupButton(R.id.btn_inicio, MainActivity.class);
        setupButton(R.id.btn_ver, MainActivity.class);
        setupButton(R.id.button_reproductor, Reproductor.class);

        // UI
        setupSpinner();
        setupWebButtons();      // WebViewActivityGeneral y Ads

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
        Intent intent = new Intent(this, WebViewActivityGeneral.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    private void openWebViewAds(String url) {
        if (url == null || url.isEmpty()) return;
        Intent intent = new Intent(this, WebViewActivityAds.class);
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
        if (now - lastClickAt < 600) return false; // 600ms debounce
        lastClickAt = now;
        return true;
    }

    // ----------------- Spinner -----------------
    private void setupSpinner() {
        spinner = findViewById(R.id.spinner_activities);
        if (spinner == null) return;

        CategoriesRepository repo = new CategoriesRepository();
        String[] loaded = repo.getCategories();
        if (loaded != null) categories = loaded;

        indexThis = findIndex(categories, "Canales ECU");
        if (indexThis < 0) {
            indexThis = Math.min(2, Math.max(0, categories.length - 1));
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setSelection(indexThis, false);

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

                Intent intent = CategoryNavigator.buildIntent(CanalesECU.this, position);
                if (intent == null) {
                    toast("No se pudo abrir la categoría seleccionada.");
                    spinner.post(() -> spinner.setSelection(indexThis, false));
                    return;
                }

                startActivity(intent);
                spinner.post(() -> spinner.setSelection(indexThis, false));
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    // ----------------- Botones Watch (HLS directo) -----------------

    // ----------------- Botones Web -----------------
    private void setupWebButtons() {
        // Abrir en WebViewActivityGeneral
        SparseArray<String> map = new SparseArray<>();
        map.put(R.id.ecuavisa, "https://rudo.video/live/ecuavisa");
        map.put(R.id.tctele,  "https://tctelevision.com/envivo/");
        map.put(R.id.teleamazonas,  "https://www.teleamazonas.com/teleamazonas-en-vivo/");

        for (int i = 0; i < map.size(); i++) {
            final int viewId = map.keyAt(i);
            final String url = map.valueAt(i);

            View btn = findViewById(viewId);
            if (btn == null || url == null || url.isEmpty()) continue;

            btn.setOnClickListener(v -> {
                if (!canClick()) return;
                openWebView(url);
            });
        }

        // ✅ RTS -> WebViewActivityAds
        View rtsBtn = findViewById(R.id.rts);
        if (rtsBtn != null) {
            rtsBtn.setOnClickListener(v -> {
                if (!canClick()) return;
                String rtsUrl = "https://www.rts.com.ec/envivo/";
                openWebViewAds(rtsUrl);
            });
        }

        // ✅ TVC -> WebViewActivityAds
        View tvcBtn = findViewById(R.id.tvc);
        if (tvcBtn != null) {
            tvcBtn.setOnClickListener(v -> {
                if (!canClick()) return;
                String tvcUrl = "https://www.tvc.com.ec/envivo/";
                openWebViewAds(tvcUrl);
            });
        }
    }
}

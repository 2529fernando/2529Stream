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

import com.fernan2529.R;
import com.fernan2529.MainActivity;
import com.fernan2529.Reproductor;
import com.fernan2529.WebViewActivities.WebViewActivityGeneral;
import com.fernan2529.data.CategoriesRepository;
import com.fernan2529.nav.CategoryNavigator;

public class Hogar extends AppCompatActivity {

    private Spinner spinner;
    private String[] categories = new String[0];
    private int indexThis = -1;
    private boolean userTouched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hogar);

        // Botones básicos
        setupButton(R.id.hogar, MainActivity.class);
        setupButton(R.id.btn_ver, MainActivity.class);
        setupButton(R.id.button_reproductor, Reproductor.class);

        // Spinner
        setupSpinner();

        // Botón Web
        View hyh = findViewById(R.id.hyh);
        if (hyh != null) {
            hyh.setOnClickListener(v ->
                    openWebView("https://www.tvporinternet2.com/discovery-hyh-en-vivo-por-internet.html"));
        }
    }

    // ----------------- Utilidades -----------------
    private void setupButton(int viewId, Class<?> targetActivity) {
        View v = findViewById(viewId);
        if (v != null && targetActivity != null) {
            v.setOnClickListener(_v -> startActivity(new Intent(Hogar.this, targetActivity)));
        }
    }

    private void openWebView(String url) {
        if (url == null || url.isEmpty()) return;
        Intent intent = new Intent(Hogar.this, WebViewActivityGeneral.class);
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
        // ✅ cambiado a spinner_activities
        spinner = findViewById(R.id.spinner_activities);
        if (spinner == null) return;

        CategoriesRepository repo = new CategoriesRepository();
        String[] loaded = repo.getCategories();
        if (loaded != null) categories = loaded;

        indexThis = findIndex(categories, "Hogar");
        if (indexThis < 0) {
            indexThis = Math.min(11, Math.max(0, categories.length - 1));
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Mantener "Hogar" seleccionado sin disparar listener
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

                Intent intent = CategoryNavigator.buildIntent(Hogar.this, position);
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
}

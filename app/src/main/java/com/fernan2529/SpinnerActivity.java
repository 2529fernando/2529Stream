package com.fernan2529;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fernan2529.data.CategoriesRepository;

public class SpinnerActivity extends AppCompatActivity {

    private Spinner spinner;
    private boolean userTouched = false;
    private String[] categories;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spinner);  // asegúrate que tenga @+id/spinner_activities

        spinner = findViewById(R.id.spinner_activities);

        // 1) Obtener categorías desde el repositorio (no desde MainActivity)
        CategoriesRepository repo = new CategoriesRepository();
        categories = repo.getCategories();

        // 2) Configurar adapter en esta Activity (la UI vive aquí, no en el repo)
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // 3) Preselección opcional enviada por quien lanza esta Activity
        int current = getIntent().getIntExtra("currentIndex", 0);
        if (current >= 0 && current < categories.length) {
            spinner.setSelection(current, false);
        }

        // 4) Marcar interacción real del usuario (para evitar el disparo inicial)
        spinner.setOnTouchListener((v, e) -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_UP) {
                userTouched = true;
            }
            return false; // permitir comportamiento normal del Spinner
        });

        // 5) Enviar resultado al seleccionar
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!userTouched) return;        // ignora selecciones no iniciadas por el usuario
                userTouched = false;             // resetea flag para la próxima vez

                Intent data = new Intent();
                data.putExtra("selectedIndex", position);
                setResult(RESULT_OK, data);
                finish();                        // cierra el overlay y vuelve al Main
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { /* no-op */ }
        });
    }
}

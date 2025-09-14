package com.fernan2529;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity4 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        setupButton(R.id.series, MainActivity.class);
        setupButton(R.id.btn_ver, MainActivity.class);
        setupButton(R.id.button_reproductor, Reproductor.class);

        setupSpinner();
        setupImageView(R.id.theend, series1.class);
        setupImageView(R.id.futurama, series2.class);
        setupImageView(R.id.luci, series3.class);
        setupImageView(R.id.supernatural, series4.class);
        setupImageView(R.id.chucky, series5.class);
        setupImageView(R.id.avatar, series6.class);
    }

    private void setupButton(int viewId, Class<?> targetActivity) {
        findViewById(viewId).setOnClickListener(v -> startActivity(new Intent(MainActivity4.this, targetActivity)));
    }

    private void setupImageView(int imageViewId, Class<?> targetActivity) {
        ((ImageView) findViewById(imageViewId)).setOnClickListener(v -> startActivity(new Intent(MainActivity4.this, targetActivity)));
    }

    private void setupSpinner() {
        Spinner spinner = findViewById(R.id.spinner_activities3);
        String[] activityNames = {"Seleccione la Categoria", "Entretenimiento", "Peliculas", "Series", "Anime", "Doramas", "Novelas", "Deportes", "Infantiles", "Comedia", "Historia", "Hogar", "Musica", "Noticias"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, activityNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    Class<?>[] activities = {null, MainActivity2.class, MainActivity3.class, MainActivity4.class, MainActivity13.class,
                            MainActivity14.class, MainActivity5.class, MainActivity6.class, MainActivity7.class, MainActivity8.class,
                            MainActivity9.class, MainActivity10.class, MainActivity11.class, MainActivity12.class};

                    startActivity(new Intent(MainActivity4.this, activities[position]));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // No se requiere acción aquí
            }
        });
    }
}




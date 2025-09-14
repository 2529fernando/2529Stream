package com.fernan2529;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        setupButton(R.id.btn_ver, MainActivity.class);
        setupButton(R.id.button_reproductor, Reproductor.class);
        setupButton(R.id.pelis, MainActivity.class);

        setupSpinner();

        setupWebButton(R.id.spidey, "https://kllamrd.org/video/tt10872600/");
        setupWebButton(R.id.superbad, "https://xupalace.org/video/tt0829482/");
        setupWebButton(R.id.jumper, "https://xupalace.org/video/tt0489099/");
    }

    private void setupButton(int viewId, Class<?> activityClass) {
        findViewById(viewId).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity3.this, activityClass);
            startActivity(intent);
        });
    }

    private void setupWebButton(int viewId, String url) {
        findViewById(viewId).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity3.this, WebViewActivity4.class);
            intent.putExtra("VIDEO_URL", url);
            startActivity(intent);
        });
    }

    private void setupSpinner() {
        Spinner spinner = findViewById(R.id.spinner_activities3);
        final String[] activityNames = {"Seleccione la Categoria", "Entretenimiento", "Peliculas",
                "Series", "Anime", "Doramas", "Novelas", "Deportes", "Infantiles", "Comedia",
                "Historia", "Hogar", "Musica", "Noticias"};

        final Class<?>[] activities = {null, MainActivity2.class, MainActivity3.class, MainActivity4.class,
                MainActivity13.class, MainActivity14.class, MainActivity5.class, MainActivity6.class,
                MainActivity7.class, MainActivity8.class, MainActivity9.class, MainActivity10.class,
                MainActivity11.class, MainActivity12.class};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, activityNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && activities[position] != null) {
                    Intent intent = new Intent(MainActivity3.this, activities[position]);
                    startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }
}

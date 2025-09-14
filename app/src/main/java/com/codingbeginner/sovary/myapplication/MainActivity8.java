package com.codingbeginner.sovary.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity8 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main8);

        findViewById(R.id.comedia).setOnClickListener(v -> navigateTo(MainActivity.class));
        findViewById(R.id.btn_ver).setOnClickListener(v -> navigateTo(MainActivity.class));
        findViewById(R.id.button_reproductor).setOnClickListener(v -> navigateTo(Reproductor.class));
        findViewById(R.id.universalcomedy).setOnClickListener(v -> openWebView("https://www.tvspacehd.com/2022/10/universal-comedy.html"));
        findViewById(R.id.comedyc).setOnClickListener(v -> openWebView("https://www.tvspacehd.com/2022/10/comedy-central.html"));

        setupSpinner();
    }

    private void navigateTo(Class<?> targetActivity) {
        startActivity(new Intent(MainActivity8.this, targetActivity));
    }

    private void openWebView(String url) {
        Intent intent = new Intent(MainActivity8.this, WebViewActivity7.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    private void setupSpinner() {
        Spinner spinner = findViewById(R.id.spinner_activities3);
        String[] activityNames = {"Seleccione la Categoria", "Entretenimiento", "Peliculas", "Series", "Anime", "Doramas", "Novelas", "Deportes", "Infantiles", "Comedia", "Historia", "Hogar", "Musica", "Noticias"};
        Class<?>[] activities = {null, MainActivity2.class, MainActivity3.class, MainActivity4.class, MainActivity13.class, MainActivity14.class, MainActivity5.class, MainActivity6.class, MainActivity7.class, MainActivity8.class, MainActivity9.class, MainActivity10.class, MainActivity11.class, MainActivity12.class};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, activityNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    navigateTo(activities[position]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}

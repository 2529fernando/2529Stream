package com.codingbeginner.sovary.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity7 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main7);

        initializeButtons();
        setupSpinner();
    }

    private void initializeButtons() {
        findViewById(R.id.infan).setOnClickListener(v -> openActivity(MainActivity.class));
        findViewById(R.id.btn_ver).setOnClickListener(v -> openActivity(MainActivity.class));
        findViewById(R.id.button_reproductor).setOnClickListener(v -> openActivity(Reproductor.class));

        findViewById(R.id.nick).setOnClickListener(v -> openWebView("https://www.cablevisionhd.com/nick-en-vivo.html"));
        findViewById(R.id.cn).setOnClickListener(v -> openWebView("https://www.cablevisionhd.com/cartoon-network-en-vivo.html"));
        findViewById(R.id.disney).setOnClickListener(v -> openWebView("https://www.cablevisionhd.com/disney-channel-en-vivo.html"));
        findViewById(R.id.boing).setOnClickListener(v -> openWebView("https://gavog.com/es/watch/span/boiiing.php"));

    }

    private void setupSpinner() {
        Spinner spinner = findViewById(R.id.spinner_activities3);
        final String[] activityNames = {"Seleccione la Categoria", "Entretenimiento", "Peliculas",
                "Series", "Anime", "Doramas", "Novelas", "Deportes", "Infantiles", "Comedia",
                "Historia", "Hogar", "Música", "Noticias"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, activityNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    Class<?>[] activities = {MainActivity2.class, MainActivity3.class, MainActivity4.class,
                            MainActivity13.class, MainActivity14.class, MainActivity5.class, MainActivity6.class,
                            MainActivity7.class, MainActivity8.class, MainActivity9.class, MainActivity10.class,
                            MainActivity11.class, MainActivity12.class};

                    if (position - 1 < activities.length) {
                        openActivity(activities[position - 1]);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void openActivity(Class<?> activityClass) {
        Intent intent = new Intent(MainActivity7.this, activityClass);
        startActivity(intent);
    }

    private void openWebView(String url) {
        Intent intent = new Intent(MainActivity7.this, WebViewActivity2.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }
}

package com.fernan2529;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity5 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        setupButtonClick(R.id.novelas, MainActivity.class);
        setupButtonClick(R.id.btn_ver, MainActivity.class);
        setupButtonClick(R.id.button_reproductor, Reproductor.class);
        setupWebViewButton(R.id.tlnovelas, "https://www.tvplusgratis2.com/tnt-novelas-en-vivo.html");
        setupWebViewButton(R.id.tntnovelas, "https://television-libre.online/html/clarovideo.html?r=TNTNOVELAS");

        setupSpinner();
    }

    private void setupButtonClick(int buttonId, Class<?> activityClass) {
        findViewById(buttonId).setOnClickListener(v -> startActivity(new Intent(MainActivity5.this, activityClass)));
    }

    private void setupWebViewButton(int buttonId, String url) {
        findViewById(buttonId).setOnClickListener(v -> openWebView(url));
    }

    private void openWebView(String url) {
        Intent intent = new Intent(MainActivity5.this, WebViewActivity2.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    private void setupSpinner() {
        Spinner spinner = findViewById(R.id.spinner_activities3);
        final String[] activityNames = {"Seleccione la Categoria", "Entretenimiento", "Peliculas", "Series", "Anime", "Doramas", "Novelas", "Deportes", "Infantiles", "Comedia", "Historia", "Hogar", "Musica", "Noticias"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, activityNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // Evitar la opción predeterminada
                    Class<?>[] activities = {MainActivity2.class, MainActivity3.class, MainActivity4.class, MainActivity13.class,
                            MainActivity14.class, MainActivity5.class, MainActivity6.class, MainActivity7.class, MainActivity8.class,
                            MainActivity9.class, MainActivity10.class, MainActivity11.class, MainActivity12.class};

                    if (position - 1 < activities.length) {
                        startActivity(new Intent(MainActivity5.this, activities[position - 1]));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // No es necesario manejar esta situación
            }
        });
    }
}

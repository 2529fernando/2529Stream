package com.fernan2529;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        setupButton(R.id.entre, MainActivity.class);
        setupButton(R.id.btn_ver, MainActivity.class);
        setupButton(R.id.button_reproductor, Reproductor.class);


        setupSpinner();
        setupWebButtons();
    }

    private void setupButton(int viewId, final Class<?> targetActivity) {
        findViewById(viewId).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity2.this, targetActivity);
            startActivity(intent);
        });
    }

    private void setupSpinner() {
        Spinner spinner = findViewById(R.id.spinner_activities3);
        final String[] activityNames = {"Seleccione la Categoria", "Entretenimiento", "Peliculas",
                "Series", "Anime", "Doramas", "Novelas", "Deportes", "Infantiles", "Comedia",
                "Historia", "Hogar", "Musica", "Noticias"};

        Class<?>[] activities = {null, MainActivity2.class, MainActivity3.class, MainActivity4.class,
                MainActivity13.class, MainActivity14.class, MainActivity5.class, MainActivity6.class,
                MainActivity7.class, MainActivity8.class, MainActivity9.class, MainActivity10.class,
                MainActivity11.class, MainActivity12.class};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, activityNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0 && activities[position] != null) {
                    startActivity(new Intent(MainActivity2.this, activities[position]));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void setupWebButtons() {
        setupWebButton(R.id.sony, "https://www.cablevisionhd.com/canal-sony-en-vivo.html");
        setupWebButton(R.id.axn, "https://www.cablevisionhd.com/axn-en-vivo.html");
        setupWebButton(R.id.hbo, "https://www.tvspacehd.com/2022/10/canal-hbo.html");
        setupWebButton(R.id.fxicon, "https://www.cablevisionhd.com/fx-en-vivo.html");
        setupWebButton(R.id.tntseries, "https://www.cablevisionhd.com/tnt-series-en-vivo.html");
        setupWebButton(R.id.tnt, "https://www.tvspacehd.com/2022/10/canal-tnt.html");
        setupWebButton(R.id.ae, "https://www.televisiongratishd2.com/discovery-a-y-e-en-vivo.html");
        setupWebButton(R.id.warner, "https://www.tvplusgratis2.com/warner-channel-en-vivo.html");
        setupWebButton(R.id.space, "https://www.tvspacehd.com/2022/10/space.html");
        setupWebButton(R.id.cww, "https://d.daddylivehd.sx/embed/stream-300.php");
    }

    private void setupWebButton(int viewId, final String url) {
        findViewById(viewId).setOnClickListener(v -> {
            // Verificamos si es el enlace de HBO, y si es así, lo abrimos en WebViewActivity3
            if (viewId == R.id.hbo) {
                openWebViewInActivity3(url);
            }
                   else if (viewId == R.id.space) {
                    openWebViewInActivity3(url);
            }
            else if (viewId == R.id.tnt) {
                openWebViewInActivity3(url);

            }
            else if (viewId == R.id.cww) {
                openWebViewInActivity3(url);

            } else {
                openWebView(url);
            }
        });
    }

    private void openWebView(String url) {
        Intent intent = new Intent(MainActivity2.this, WebViewActivity2.class);  // Para otros enlaces, abrir en WebViewActivity2
        intent.putExtra("url", url);
        startActivity(intent);
    }

    private void openWebViewInActivity3(String url) {
        Intent intent = new Intent(MainActivity2.this, WebViewActivity7.class);  // Para HBO, abrir en WebViewActivity3
        intent.putExtra("url", url);
        startActivity(intent);
    }
}


package com.codingbeginner.sovary.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity6 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);

        setupButton(R.id.deportes, MainActivity.class);
        setupButton(R.id.btn_ver, MainActivity.class);
        setupButton(R.id.button_reproductor, Reproductor.class);

        setupSpinner();
        setupWebViewButtons();
    }

    private void setupButton(int viewId, Class<?> activityClass) {
        findViewById(viewId).setOnClickListener(v -> startActivity(new Intent(MainActivity6.this, activityClass)));
    }

    private void setupSpinner() {
        Spinner spinner = findViewById(R.id.spinner_activities3);
        final String[] activityNames = {
                "Seleccione la Categoria", "Entretenimiento", "Peliculas", "Series", "Anime",
                "Doramas", "Novelas", "Deportes", "Infantiles", "Comedia",
                "Historia", "Hogar", "Musica", "Noticias"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, activityNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    Class<?>[] activities = {
                            MainActivity2.class, MainActivity3.class, MainActivity4.class, MainActivity13.class,
                            MainActivity14.class, MainActivity5.class, MainActivity6.class, MainActivity7.class,
                            MainActivity8.class, MainActivity9.class, MainActivity10.class, MainActivity11.class,
                            MainActivity12.class
                    };
                    startActivity(new Intent(MainActivity6.this, activities[position - 1]));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void setupWebViewButtons() {
        setupWebViewButton(R.id.libre, "https://www.rojadirectatv.de/", WebViewActivity6.class);
        setupWebViewButton(R.id.dsports, "https://www.cablevisionhd.com/directv-sports-en-vivo.html", WebViewActivity2.class);
        setupWebViewButton(R.id.dsports2, "https://www.cablevisionhd.com/directv-sports-2-en-vivo.html", WebViewActivity2.class);
        setupWebViewButton(R.id.dsportsplus, "https://www.cablevisionhd.com/directv-sports-plus-en-vivo.html", WebViewActivity2.class);
        setupWebViewButton(R.id.espn, "https://www.cablevisionhd.com/espn-en-vivo.html", WebViewActivity7.class);
        setupWebViewButton(R.id.espn2, "https://www.cablevisionhd.com/espn-2-en-vivo.html", WebViewActivity2.class);
        setupWebViewButton(R.id.espn3, "https://www.cablevisionhd.com/espn-3-en-vivo.html", WebViewActivity2.class);
        setupWebViewButton(R.id.espn4, "https://www.cablevisionhd.com/espn-4-en-vivo.html", WebViewActivity2.class);
        setupWebViewButton(R.id.espnpre, "https://www.cablevisionhd.com/espn-premium-en-vivo.html", WebViewActivity2.class);
        setupWebViewButton(R.id.bein, "https://www.cablevisionhd.com/bein-la-liga-en-vivo.html", WebViewActivity2.class);
        setupWebViewButton(R.id.movistar, "https://www.cablevisionhd.com/movistar-deportes-en-vivo.html", WebViewActivity2.class);
        setupWebViewButton(R.id.tntsports, "https://www.cablevisionhd.com/tnt-sports-en-vivo.html", WebViewActivity2.class);
        setupWebViewButton(R.id.appletv, "https://tucanaldeportivo.com/canal10.php", WebViewActivity2.class);
        setupWebViewButton(R.id.goltv, "https://www.cablevisionhd.com/gol-tv-en-vivo.html", WebViewActivity2.class);
        setupWebViewButton(R.id.caracol, "https://cdn.chatytvgratis.net/caracoltabs.php?width=640&amp;height=410", WebViewActivity7.class);
        setupWebViewButton(R.id.nba, "https://www.telegratishd.com/nba-en-vivo.html", WebViewActivity2.class);
        setupWebViewButton(R.id.foxsports, "https://www.cablevisionhd.com/fox-sports-en-vivo.html", WebViewActivity2.class);
    }

    private void setupWebViewButton(int viewId, String url, Class<?> webViewActivity) {
        findViewById(viewId).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity6.this, webViewActivity);
            intent.putExtra("url", url);
            startActivity(intent);
        });
    }
}

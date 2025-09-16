package com.fernan2529.Series;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.fernan2529.R;
import com.fernan2529.WebViewActivities.WebViewActivityGeneral;

import java.util.Random;

public class series1 extends AppCompatActivity {

    private Spinner spinnerVideos;
    private ArrayAdapter<String> videosAdapter;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series1);

        spinnerVideos = findViewById(R.id.spinner_videos);
        Button playRandomButton = findViewById(R.id.aleatorio);

        // Nombres visibles en el Spinner
        final String[] videoNames = {
                "Seleccione el Capitulo",
                "T1-Capitulo 1","T1-Capitulo 2","T1-Capítulo 3","T1-Capítulo 4",
                "T1-Capítulo 5","T1-Capítulo 6","T1-Capítulo 7","T1-Capítulo 8",
                "T2-Capítulo 1","T2-Capítulo 2","T2-Capítulo 3","T2-Capítulo 4",
                "T2-Capítulo 5","T2-Capítulo 6","T2-Capítulo 7","T2-Capítulo 8"
        };

        // URLs correspondientes (mismo índice que videoNames)
        final String[] videoUrls = {
                "",
                "https://entrepeliculasyseries.nz/episodios/the-end-of-the-fucking-world-2017-1x1/",
                "https://entrepeliculasyseries.nz/episodios/the-end-of-the-fucking-world-2017-1x2/",
                "https://entrepeliculasyeries.nz/episodios/the-end-of-the-fucking-world-2017-1x3/",
                "https://entrepeliculasyseries.nz/episodios/the-end-of-the-fucking-world-2017-1x4/",
                "https://entrepeliculasyseries.nz/episodios/the-end-of-the-fucking-world-2017-1x5/",
                "https://entrepeliculasyseries.nz/episodios/the-end-of-the-fucking-world-2017-1x6/",
                "https://entrepeliculasyseries.nz/episodios/the-end-of-the-fucking-world-2017-1x7/",
                "https://entrepeliculasyseries.nz/episodios/the-end-of-the-fucking-world-2017-1x8/",
                "https://entrepeliculasyseries.nz/episodios/the-end-of-the-fucking-world-2017-2x1/",
                "https://entrepeliculasyseries.nz/episodios/the-end-of-the-fucking-world-2017-2x2/",
                "https://entrepeliculasyseries.nz/episodios/the-end-of-the-fucking-world-2017-2x3/",
                "https://entrepeliculasyseries.nz/episodios/the-end-of-the-fucking-world-2017-2x4/",
                "https://entrepeliculasyseries.nz/episodios/the-end-of-the-fucking-world-2017-2x5/",
                "https://entrepeliculasyseries.nz/episodios/the-end-of-the-fucking-world-2017-2x6/",
                "https://entrepeliculasyseries.nz/episodios/the-end-of-the-fucking-world-2017-2x7/",
                "https://entrepeliculasyseries.nz/episodios/the-end-of-the-fucking-world-2017-2x8/"
        };

        // Adapter para el spinner
        videosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, videoNames);
        videosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVideos.setAdapter(videosAdapter);

        // Random
        random = new Random();

        // Selección desde el Spinner (evita el primer ítem "Seleccione...")
        spinnerVideos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && position < videoUrls.length) {
                    String selectedVideoUrl = videoUrls[position];
                    if (selectedVideoUrl != null && !selectedVideoUrl.isEmpty()) {
                        openWeb(selectedVideoUrl);
                    }
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { /* no-op */ }
        });

        // Botón Aleatorio (evita índice 0)
        playRandomButton.setOnClickListener(v -> {
            int randomIndex = random.nextInt(videoUrls.length - 1) + 1;
            String randomVideoUrl = videoUrls[randomIndex];
            if (randomVideoUrl != null && !randomVideoUrl.isEmpty()) {
                openWeb(randomVideoUrl);
            }
        });
    }

    // Abre SIEMPRE WebViewActivityGeneral con la clave "url"
    private void openWeb(String url) {
        Intent intent = new Intent(series1.this, WebViewActivityGeneral.class);
        intent.putExtra("url", url); // <- clave que usa tu WebViewActivityGeneral
        startActivity(intent);
    }
}

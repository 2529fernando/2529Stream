package com.fernan2529;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

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
        Button playRandomButton = findViewById(R.id.aleatorio); // Suponiendo que ya tienes un botón en tu diseño XML

        // Lista de nombres de opciones para el Spinner
        final String[] videoNames = {"Seleccione el Capitulo", "T1-Capitulo 1","T1-Capitulo 2",  "T1-Capítulo 3", "T1-Capítulo 4",
                "T1-Capítulo 5", "T1-Capítulo 6", "T1-Capítulo 7", "T1-Capítulo 8", "T2-Capítulo 1", "T2-Capítulo 2", "T2-Capítulo 3", "T2-Capítulo 4", "T2-Capítulo 5", "T2-Capítulo 6", "T2-Capítulo 7", "T2-Capítulo 8"};



        final String[] videoUrls = {"",
                "https://entrepeliculasyseries.nz/episodios/the-end-of-the-fucking-world-2017-1x1/",
                "https://entrepeliculasyseries.nz/episodios/the-end-of-the-fucking-world-2017-1x2/",
                "https://entrepeliculasyseries.nz/episodios/the-end-of-the-fucking-world-2017-1x3/",
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

        // Crear un ArrayAdapter usando el array de nombres de opciones
        videosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, videoNames);
        videosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Configurar el ArrayAdapter en el Spinner
        spinnerVideos.setAdapter(videosAdapter);

        // Inicializar el generador de números aleatorios
        random = new Random();

        // Manejar la selección del usuario
        spinnerVideos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                // Verificar si se seleccionó la primera opción
                if (position != 0) {
                    // Obtener el URL del video seleccionado
                    String selectedVideoUrl = videoUrls[position];
                    // Abrir WatchActivity3 con el URL del video seleccionado
                    openWatchActivity(selectedVideoUrl);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // No hacer nada si no se selecciona nada
            }
        });

        // Manejar el clic en el botón para reproducir un video aleatorio
        playRandomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtener un índice aleatorio para seleccionar un video de la lista
                int randomIndex = random.nextInt(videoUrls.length - 1) + 1; // Evitar el primer ítem "Seleccione el Capitulo"
                // Obtener el URL del video aleatorio
                String randomVideoUrl = videoUrls[randomIndex];
                // Abrir WatchActivity3 con el URL del video aleatorio
                openWatchActivity(randomVideoUrl);
            }
        });
    }

    // Método para abrir WebViewActivity4 con el enlace del video seleccionado
    private void openWatchActivity(String videoUrl) {
        Intent intent = new Intent(series1.this, WebViewActivity4.class);
        intent.putExtra("VIDEO_URL", videoUrl);
        startActivity(intent);
    }
}
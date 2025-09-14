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

public class series4 extends AppCompatActivity {
    private Spinner spinnerVideos;
    private ArrayAdapter<String> videosAdapter;

    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series4);

        spinnerVideos = findViewById(R.id.spinner_videos);
        Button playRandomButton = findViewById(R.id.aleatorio); // Suponiendo que ya tienes un botón en tu diseño XML

        // Lista de nombres de opciones para el Spinner
        final String[] videoNames = {"Seleccione el Capitulo",
                "T1-01 - Capítulo",
                "T1-02 - Capítulo",
                "T1-03 - Capítulo",
                "T1-04 - Capítulo",
                "T1-05 - Capítulo",
                "T1-06 - Capítulo",
                "T1-07 - Capítulo",
                "T1-08 - Capítulo",
                "T1-09 - Capítulo",
                "T1-10 - Capítulo",
                "T1-11 - Capítulo",
                "T1-12 - Capítulo",
                "T1-13 - Capítulo",
                "T1-14 - Capítulo",
                "T1-15 - Capítulo",
                "T1-16 - Capítulo",
                "T1-17 - Capítulo",
                "T1-18 - Capítulo",
                "T1-19 - Capítulo",
                "T1-20 - Capítulo",
                "T1-21 - Capítulo",
                "T1-22 - Capítulo"

        };

        final String[] videoUrls = {"",
                "https://kllamrd.org//video/tt0460681-1x01/",
                "https://kllamrd.org//video/tt0460681-1x02/",
                "https://kllamrd.org//video/tt0460681-1x03/",
                "https://kllamrd.org//video/tt0460681-1x04/",
                "https://kllamrd.org//video/tt0460681-1x05/",
                "https://kllamrd.org//video/tt0460681-1x06/",
                "https://kllamrd.org//video/tt0460681-1x07/",
                "https://kllamrd.org//video/tt0460681-1x08/",
                "https://kllamrd.org//video/tt0460681-1x09/",
                "https://kllamrd.org//video/tt0460681-1x10/",
                "https://kllamrd.org//video/tt0460681-1x11/",
                "https://kllamrd.org//video/tt0460681-1x12/",
                "https://kllamrd.org//video/tt0460681-1x13/",
                "https://kllamrd.org//video/tt0460681-1x14/",
                "https://kllamrd.org//video/tt0460681-1x15/",
                "https://kllamrd.org//video/tt0460681-1x16/",
                "https://kllamrd.org//video/tt0460681-1x17/",
                "https://kllamrd.org//video/tt0460681-1x18/",
                "https://kllamrd.org/video/tt0460681-1x19/",
                "https://kllamrd.org/video/tt0460681-1x20/",
                "https://kllamrd.org/video/tt0460681-1x21/",
                "https://kllamrd.org/video/tt0460681-1x22/"

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
        Intent intent = new Intent(series4.this, WebViewActivity4.class);
        intent.putExtra("VIDEO_URL", videoUrl);
        startActivity(intent);
    }
}

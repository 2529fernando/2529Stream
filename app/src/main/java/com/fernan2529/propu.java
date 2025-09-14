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

public class propu extends AppCompatActivity {
    private Spinner spinnerVideos;
    private ArrayAdapter<String> videosAdapter;

    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_propu);

        spinnerVideos = findViewById(R.id.spinner_videos);
        Button playRandomButton = findViewById(R.id.aleatorio); // Suponiendo que ya tienes un botón en tu diseño XML

        // Lista de nombres de opciones para el Spinner
        final String[] videoNames = {"Seleccione el Capitulo",
                "01 - Capítulo",
                "02 - Capítulo",
                "03 - Capítulo",
                "04 - Capítulo",
                "05 - Capítulo",
                "06 - Capítulo",
                "07 - Capítulo",
                "08 - Capítulo",
                "09 - Capítulo",
                "10 - Capítulo",
                "11 - Capítulo",
                "12 - Capítulo"

        };

        final String[] videoUrls = {"",
                "https://vk.com/video_ext.php?oid=730471731&id=456239609&hd=2&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456239610&hd=2&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456239611&hd=2&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456239612&hd=2&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456239613&hd=2&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456239614&hd=2&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456239615&hd=2&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456239616&hd=2&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456239617&hd=2&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456239618&hd=2&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456239619&hd=2&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456239620&hd=2&autoplay=1"
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
        Intent intent = new Intent(propu.this, WebViewActivity4.class);
        intent.putExtra("VIDEO_URL", videoUrl);
        startActivity(intent);
    }
}

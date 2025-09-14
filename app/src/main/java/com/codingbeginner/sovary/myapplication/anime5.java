package com.codingbeginner.sovary.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class anime5 extends AppCompatActivity {
    private Spinner spinnerVideos;
    private ArrayAdapter<String> videosAdapter;

    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime5);

        spinnerVideos = findViewById(R.id.spinner_videos);
        Button playRandomButton = findViewById(R.id.aleatorio); // Suponiendo que ya tienes un botón en tu diseño XML

        // Lista de nombres de opciones para el Spinner
        final String[] videoNames = {"Seleccione el Capitulo",

                "T1-01 - La princesa Zero",
                "T1-02 - La extraña señorita maga",
                "T1-03 - El primer beso",
                "T1-04 - La espada de la llama",
                "T1-05 - La cruz de aquella casa",
                "T1-06 - La princesa y el subordinado",
                "T1-07 - El remordimiento de Louise",
                "T1-08 - La revolución de Louise",
                "T1-09 - Los secretos de Louise",
                "T1-10 - La gala del baile",
                "T1-11 - El infierno de la brujería",
                "T1-12 - El duelo de la verdad",
                "T1-13 - La princesa de Tristain"

        };

        final String[] videoUrls = {"",
                "https://latanime.org/ver/zero-no-tsukaima-la-magia-de-zero-latino-episodio-1",
                "https://latanime.org/ver/zero-no-tsukaima-la-magia-de-zero-latino-episodio-2",
                "https://latanime.org/ver/zero-no-tsukaima-la-magia-de-zero-latino-episodio-3",
                "https://latanime.org/ver/zero-no-tsukaima-la-magia-de-zero-latino-episodio-4",
                "https://latanime.org/ver/zero-no-tsukaima-la-magia-de-zero-latino-episodio-5",
                "https://latanime.org/ver/zero-no-tsukaima-la-magia-de-zero-latino-episodio-6",
                "https://latanime.org/ver/zero-no-tsukaima-la-magia-de-zero-latino-episodio-7",
                "https://latanime.org/ver/zero-no-tsukaima-la-magia-de-zero-latino-episodio-8",
                "https://latanime.org/ver/zero-no-tsukaima-la-magia-de-zero-latino-episodio-9",
                "https://latanime.org/ver/zero-no-tsukaima-la-magia-de-zero-latino-episodio-10",
                "https://latanime.org/ver/zero-no-tsukaima-la-magia-de-zero-latino-episodio-11",
                "https://latanime.org/ver/zero-no-tsukaima-la-magia-de-zero-latino-episodio-12",
                "https://latanime.org/ver/zero-no-tsukaima-la-magia-de-zero-latino-episodio-13",


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
        Intent intent = new Intent(anime5.this, WebViewActivity4.class);
        intent.putExtra("VIDEO_URL", videoUrl);
        startActivity(intent);
    }
}

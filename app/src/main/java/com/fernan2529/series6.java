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

public class series6 extends AppCompatActivity {
    private Spinner spinnerVideos;
    private ArrayAdapter<String> videosAdapter;

    private Random random;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series6);

        spinnerVideos = findViewById(R.id.spinner_videos);
        Button playRandomButton = findViewById(R.id.aleatorio); // Suponiendo que ya tienes un botón en tu diseño XML

        // Lista de nombres de opciones para el Spinner
        final String[] videoNames = {"Seleccione el Capitulo",
                "T1-01 El muchacho en el iceberg",
                "T1-02 El regreso del Avatar",
                "T1-03 El Templo del Aire del Sur",
                "T1-04 Las guerreras de Kyoshi",
                "T1-05 El rey de Omashu",
                "T1-06 Prisionera (Imprisoned)",
                "T1-07 El mundo espíritu: Solsticio de invierno, parte 1",
                "T1-08 El Avatar Roku: Solsticio de invierno, parte 2",
                "T1-09 El pergamino del control del agua (The Waterbending Scroll)",
                "T1-10 Jet",
                "T1-11 La gran división",
                "T1-12 La tormenta",
                "T1-13 El espíritu azul",
                "T1-14 La adivina",
                "T1-15 Bato de la Tribu del Agua",
                "T1-16 El desertor",
                "T1-17 El Templo del Aire del Norte",
                "T1-18 El maestro del Agua control",
                "T1-19 El asedio del norte: parte 1",
                "T1-20 El asedio del norte: parte 2",

                "T2-01 – El Estado Avatar",
                "T2-02 – La Cueva de los Dos Enamorados",
                "T2-03 – Regreso a Omashu",
                "T2-04 – El Pantano",
                "T2-05 – El Día del Avatar",
                "T2-06 – La Bandida Ciega",
                "T2-07 – Zuko Solitario",
                "T2-08 – La Persecución",
                "T2-09 – Trabajo Duro",
                "T2-10 – La Biblioteca",
                "T2-11 – El Desierto",
                "T2-12 – El Paso de la Serpiente",
                "T2-13 – El Taladro",
                "T2-14 – La Ciudad de Muros y Secretos",
                "T2-15 – Aventuras en Ba Sing Se",
                "T2-16 – La Odisea de Appa",
                "T2-17 – El Lago Laogai",
                "T2-18 – El Rey de la Tierra",
                "T2-19 – El Gurú",
                "T2-20 – La Encrucijada del Destino",

                "T3-01 – El Despertar",
                "T3-02 – El Pañuelo en la Cabeza",
                "T3-03 – La Dama Pintada",
                "T3-04 – El Maestro de Sokka",
                "T3-05 – La Playa",
                "T3-06 – El Avatar y el Señor del Fuego",
                "T3-07 – La Fugitiva",
                "T3-08 – La Titiritera",
                "T3-09 – Pesadillas y Fantasías",
                "T3-10 – El Día del Sol Negro, Primera Parte: La Invasión",
                "T3-11 – El Día del Sol Negro, Segunda Parte: El Eclipse",
                "T3-12 – El Templo del Aire del Oeste",
                "T3-13 – Los Maestros del Fuego Control",
                "T3-14 – La Roca Hirviente, Primera Parte",
                "T3-15 – La Roca Hirviente, Segunda Parte",
                "T3-16 – Los Invasores del Sur",
                "T3-17 – Los Actores de la Isla Ember",
                "T3-18 – El Cometa de Sozin, Primera Parte: El Rey Fénix",
                "T3-19 – El Cometa de Sozin, Segunda Parte: Los Viejos Maestros",
                "T3-20 – El Cometa de Sozin, Tercera Parte: En el Infierno",
                "T3-21 – El Cometa de Sozin, Cuarta Parte: El Avatar Aang",


        };

        final String[] videoUrls = {"",
                "https://xupalace.org/video/tt0417299-1x01/",
                "https://xupalace.org/video/tt0417299-1x02/",
                "https://xupalace.org/video/tt0417299-1x03/",
                "https://xupalace.org/video/tt0417299-1x04/",
                "https://xupalace.org/video/tt0417299-1x05/",
                "https://xupalace.org/video/tt0417299-1x06/",
                "https://xupalace.org/video/tt0417299-1x07/",
                "https://xupalace.org/video/tt0417299-1x08/",
                "https://xupalace.org/video/tt0417299-1x09/",
                "https://xupalace.org/video/tt0417299-1x10/",
                "https://xupalace.org/video/tt0417299-1x11/",
                "https://xupalace.org/video/tt0417299-1x12/",
                "https://xupalace.org/video/tt0417299-1x13/",
                "https://xupalace.org/video/tt0417299-1x14/",
                "https://xupalace.org/video/tt0417299-1x15/",
                "https://xupalace.org/video/tt0417299-1x16/",
                "https://xupalace.org/video/tt0417299-1x17/",
                "https://xupalace.org/video/tt0417299-1x18/",
                "https://xupalace.org/video/tt0417299-1x19/",
                "https://xupalace.org/video/tt0417299-1x20/",

                "https://xupalace.org/video/tt0417299-2x01/",
                "https://xupalace.org/video/tt0417299-2x02/",
                "https://xupalace.org/video/tt0417299-2x03/",
                "https://xupalace.org/video/tt0417299-2x04/",
                "https://xupalace.org/video/tt0417299-2x05/",
                "https://xupalace.org/video/tt0417299-2x06/",
                "https://xupalace.org/video/tt0417299-2x07/",
                "https://xupalace.org/video/tt0417299-2x08/",
                "https://xupalace.org/video/tt0417299-2x09/",
                "https://xupalace.org/video/tt0417299-2x10/",
                "https://xupalace.org/video/tt0417299-2x11/",
                "https://xupalace.org/video/tt0417299-2x12/",
                "https://xupalace.org/video/tt0417299-2x13/",
                "https://xupalace.org/video/tt0417299-2x14/",
                "https://xupalace.org/video/tt0417299-2x15/",
                "https://xupalace.org/video/tt0417299-2x16/",
                "https://xupalace.org/video/tt0417299-2x17/",
                "https://xupalace.org/video/tt0417299-2x18/",
                "https://xupalace.org/video/tt0417299-2x19/",
                "https://xupalace.org/video/tt0417299-2x20/",

                "https://xupalace.org/video/tt0417299-3x01/",
                "https://xupalace.org/video/tt0417299-3x02/",
                "https://xupalace.org/video/tt0417299-3x03/",
                "https://xupalace.org/video/tt0417299-3x04/",
                "https://xupalace.org/video/tt0417299-3x05/",
                "https://xupalace.org/video/tt0417299-3x06/",
                "https://xupalace.org/video/tt0417299-3x07/",
                "https://xupalace.org/video/tt0417299-3x08/",
                "https://xupalace.org/video/tt0417299-3x09/",
                "https://xupalace.org/video/tt0417299-3x10/",
                "https://xupalace.org/video/tt0417299-3x11/",
                "https://xupalace.org/video/tt0417299-3x12/",
                "https://xupalace.org/video/tt0417299-3x13/",
                "https://xupalace.org/video/tt0417299-3x14/",
                "https://xupalace.org/video/tt0417299-3x15/",
                "https://xupalace.org/video/tt0417299-3x16/",
                "https://xupalace.org/video/tt0417299-3x17/",
                "https://xupalace.org/video/tt0417299-3x18/",
                "https://xupalace.org/video/tt0417299-3x19/",
                "https://xupalace.org/video/tt0417299-3x20/",
                "https://xupalace.org/video/tt0417299-3x21/"


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
        Intent intent = new Intent(series6.this, WebViewActivity4.class);
        intent.putExtra("VIDEO_URL", videoUrl);
        startActivity(intent);
    }
}
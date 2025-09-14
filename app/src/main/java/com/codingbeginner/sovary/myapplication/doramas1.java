package com.codingbeginner.sovary.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import java.util.Random;
import androidx.appcompat.app.AppCompatActivity;

public class doramas1 extends AppCompatActivity {
    private Spinner spinnerVideos;
    private ArrayAdapter<String> videosAdapter;
    private Random random;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doramas1);

        spinnerVideos = findViewById(R.id.spinner_videos);
        Button playRandomButton = findViewById(R.id.aleatorio);
        // Lista de nombres de opciones para el Spinner
        final String[] videoNames = {"Seleccione el Capitulo",
                "01- Oigo tu voz",
                "02- Chica mala, chica buena",
                "03- Ahí estaré",
                "04- Él en mi vaga memoria",
                "05- Palabras en las que no se puede confiar",
                "06- Yo, abandonado y solo en el fin del mundo",
                "07- ¿Por qué una premonición triste nunca es mala?",
                "08- Capitulo",
                "09- Capitulo",
                "10- Capitulo",
                "11- Capitulo",
                "12- Capitulo",
                "13- Capitulo",
                "14- Capitulo",
                "15- Capitulo",
                "16- Capitulo",
                "17- Capitulo",
                "18- Capitulo"
            };

        final String[] videoUrls = {"",
                "https://vk.com/video_ext.php?oid=730471731&id=456240221&hd=1&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456240222&hd=1&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456240223&hd=1&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456240224&hd=1&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456240225&hd=1&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456240226&hd=1&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456240227&hd=1&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456240228&hd=1&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456240229&hd=1&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456240230&hd=1&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456240231&hd=1&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456240232&hd=1&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456240233&hd=1&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456240234&hd=1&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456240235&hd=1&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456240236&hd=1&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456240237&hd=1&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456240238&hd=1&autoplay=1"
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

    // Método para abrir WatchActivity3 con el enlace del video seleccionado
    private void openWatchActivity(String videoUrl) {
        Intent intent = new Intent(doramas1.this, WebViewActivity4.class);
        intent.putExtra("VIDEO_URL", videoUrl);
        startActivity(intent);
    }
}
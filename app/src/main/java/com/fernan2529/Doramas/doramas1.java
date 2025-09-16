package com.fernan2529.Doramas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;

import com.fernan2529.R;
import com.fernan2529.WebViewActivities.WebViewActivityGeneral;

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

        // Nombres visibles en el Spinner
        final String[] videoNames = {
                "Seleccione el Capitulo",
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

        // URLs correspondientes (mismo índice que videoNames)
        final String[] videoUrls = {
                "",
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

        // Adapter para el spinner
        videosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, videoNames);
        videosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVideos.setAdapter(videosAdapter);

        random = new Random();

        // Abrir al seleccionar un capítulo (ignorando el primer ítem)
        spinnerVideos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    String selectedVideoUrl = videoUrls[position];
                    if (selectedVideoUrl != null && !selectedVideoUrl.isEmpty()) {
                        openWeb(selectedVideoUrl);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { /* no-op */ }
        });

        // Reproducir capítulo aleatorio (evitar el índice 0)
        playRandomButton.setOnClickListener(v -> {
            int randomIndex = random.nextInt(videoUrls.length - 1) + 1;
            String randomVideoUrl = videoUrls[randomIndex];
            if (randomVideoUrl != null && !randomVideoUrl.isEmpty()) {
                openWeb(randomVideoUrl);
            }
        });
    }

    private void openWeb(String url) {
        Intent intent = new Intent(doramas1.this, WebViewActivityGeneral.class);
        // 👇 clave que espera tu WebViewActivityGeneral
        intent.putExtra("url", url);
        startActivity(intent);
    }
}

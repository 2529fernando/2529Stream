package com.fernan2529.Novelas;

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

public class novelas2 extends AppCompatActivity {

    private Spinner spinnerVideos;
    private ArrayAdapter<String> videosAdapter;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novelas1);

        spinnerVideos = findViewById(R.id.spinner_videos);
        Button playRandomButton = findViewById(R.id.aleatorio);

        // Nombres visibles en el Spinner
        final String[] videoNames = {
                "Seleccione el Capitulo",
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

        // URLs correspondientes (mismo índice que videoNames)
        final String[] videoUrls = {
                "",
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

        // Adapter para el spinner
        videosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, videoNames);
        videosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVideos.setAdapter(videosAdapter);

        // Random
        random = new Random();

        // Selección desde el Spinner (evitando el primer ítem "Seleccione...")
        spinnerVideos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && position < videoUrls.length) {
                    String selectedVideoUrl = videoUrls[position];
                    if (selectedVideoUrl != null && !selectedVideoUrl.isEmpty()) {
                        openWeb(selectedVideoUrl);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { /* no-op */ }
        });

        // Botón Aleatorio (evita el índice 0)
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
        Intent intent = new Intent(novelas2.this, WebViewActivityGeneral.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }
}

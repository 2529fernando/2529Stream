package com.fernan2529.Doramas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.fernan2529.R;
import com.fernan2529.WebViewActivities.WebViewActivityGeneral;

import java.util.Random;

public class doramas2 extends AppCompatActivity {
    private Spinner spinnerVideos;
    private ArrayAdapter<String> videosAdapter;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doramas2);

        spinnerVideos = findViewById(R.id.spinner_videos);
        Button playRandomButton = findViewById(R.id.aleatorio);

        // Lista de nombres de opciones para el Spinner
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
                "12 - Capítulo",
                "13 - Capítulo",
                "14 - Capítulo",
                "15 - Capítulo",
                "16 - Capítulo"
        };

        final String[] videoUrls = {
                "",
                "https://vk.com/video_ext.php?oid=730471731&id=456239077&hd=2&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456239078&hd=2&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456239079&hd=2&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456239088&hd=2&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456239089&hd=2&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456239090&hd=2&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456239091&hd=2&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456239092&hd=2&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456239080&hd=2&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456239081&hd=2&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456239082&hd=2&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456239083&hd=2&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456239084&hd=2&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456239085&hd=2&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456239086&hd=2&autoplay=1",
                "https://vk.com/video_ext.php?oid=730471731&id=456239087&hd=2&autoplay=1"
        };

        // Adapter para el spinner
        videosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, videoNames);
        videosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVideos.setAdapter(videosAdapter);

        // Inicializar random
        random = new Random();

        // Selección de capítulo
        spinnerVideos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position > 0 && position < videoUrls.length) {
                    String selectedVideoUrl = videoUrls[position];
                    if (selectedVideoUrl != null && !selectedVideoUrl.isEmpty()) {
                        openWeb(selectedVideoUrl);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { /* no-op */ }
        });

        // Botón aleatorio (evita el índice 0)
        playRandomButton.setOnClickListener(v -> {
            int randomIndex = random.nextInt(videoUrls.length - 1) + 1;
            String randomVideoUrl = videoUrls[randomIndex];
            if (randomVideoUrl != null && !randomVideoUrl.isEmpty()) {
                openWeb(randomVideoUrl);
            }
        });
    }

    // Abre SIEMPRE WebViewActivityGeneral y pasa el link con la clave "url"
    private void openWeb(String videoUrl) {
        Intent intent = new Intent(doramas2.this, WebViewActivityGeneral.class);
        intent.putExtra("url", videoUrl);  // <-- clave que usa tu WebViewActivityGeneral
        startActivity(intent);
    }
}

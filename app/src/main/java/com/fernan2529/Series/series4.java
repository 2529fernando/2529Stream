package com.fernan2529.Series;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

import com.fernan2529.R;
import com.fernan2529.WebViewActivities.WebViewActivityGeneral;

public class series4 extends AppCompatActivity {
    private Spinner spinnerVideos;
    private ArrayAdapter<String> videosAdapter;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series4);

        spinnerVideos = findViewById(R.id.spinner_videos);
        Button playRandomButton = findViewById(R.id.aleatorio);

        final String[] videoNames = {
                "Seleccione el Capitulo",
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

        final String[] videoUrls = {
                "",
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

        videosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, videoNames);
        videosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVideos.setAdapter(videosAdapter);

        random = new Random();

        spinnerVideos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position != 0) {
                    String selectedVideoUrl = videoUrls[position];
                    if (selectedVideoUrl != null && !selectedVideoUrl.isEmpty()) {
                        openWatchActivity(selectedVideoUrl);
                    }
                }
            }
            @Override public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        playRandomButton.setOnClickListener(v -> {
            if (videoUrls.length > 1) {
                int randomIndex = random.nextInt(videoUrls.length - 1) + 1; // evita el índice 0
                String randomVideoUrl = videoUrls[randomIndex];
                if (randomVideoUrl != null && !randomVideoUrl.isEmpty()) {
                    openWatchActivity(randomVideoUrl);
                }
            }
        });
    }

    private void openWatchActivity(String videoUrl) {
        Intent intent = new Intent(series4.this, WebViewActivityGeneral.class);
        intent.putExtra("url", videoUrl); // clave esperada por WebViewActivityGeneral
        startActivity(intent);
    }
}

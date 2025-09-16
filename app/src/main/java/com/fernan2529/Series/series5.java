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

public class series5 extends AppCompatActivity {
    private Spinner spinnerVideos;
    private ArrayAdapter<String> videosAdapter;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series5);

        spinnerVideos = findViewById(R.id.spinner_videos);
        Button playRandomButton = findViewById(R.id.aleatorio);

        final String[] videoNames = {
                "Seleccione el Capitulo",
                "T1-01 - Muerte por accidente",
                "T1-02 - Dame algo rico para comer",
                "T1-03 - Me gusta que me abracen",
                "T1-04 - Déjalo ir",
                "T1-05 - Pequeñas mentirijillas",
                "T1-06 - Cabo Queer",
                "T1-07 - Doble dolor, doble pérdida",
                "T1-08 - Un asunto para inmortalizar",

                "T2-01 - Halloween II",
                "T2-02 - Los pecadores son mucho más divertidos",
                "T2-03 - ¡Ave María!",
                "T2-04 - Muerte por negación",
                "T2-05 - Muñeco con muñeco",
                "T2-06 - Sí que ha resucitado",
                "T2-07 - En capilla",
                "T2-08 - El Chucky real",

                "T3-01 - Asesinato en la Casa Blanca",
                "T3-02 - Déjame entrar",
                "T3-03 - El cuerpo de Jennifer",
                "T3-04 - Vestida para matar",
                "T3-05 - Negación, Ira, Negociación, Depresión, Asesinato",
                "T3-06 - Capítulo",
                "T3-07 - Pozos de ambición"
        };

        final String[] videoUrls = {
                "",
                "https://kllamrd.org/video/tt8388390-1x01",
                "https://kllamrd.org/video/tt8388390-1x02",
                "https://kllamrd.org/video/tt8388390-1x03",
                "https://kllamrd.org/video/tt8388390-1x04",
                "https://kllamrd.org/video/tt8388390-1x05",
                "https://kllamrd.org/video/tt8388390-1x06",
                "https://kllamrd.org/video/tt8388390-1x07",
                "https://kllamrd.org/video/tt8388390-1x08",

                "https://kllamrd.org/video/tt8388390-2x01",
                "https://kllamrd.org/video/tt8388390-2x02",
                "https://kllamrd.org/video/tt8388390-2x03",
                "https://kllamrd.org/video/tt8388390-2x04",
                "https://kllamrd.org/video/tt8388390-2x05",
                "https://kllamrd.org/video/tt8388390-2x06",
                "https://kllamrd.org/video/tt8388390-2x07",
                "https://kllamrd.org/video/tt8388390-2x08",

                "https://kllamrd.org/video/tt8388390-3x01",
                "https://kllamrd.org/video/tt8388390-3x02",
                "https://kllamrd.org/video/tt8388390-3x03",
                "https://kllamrd.org/video/tt8388390-3x04",
                "https://kllamrd.org/video/tt8388390-3x05",
                "https://kllamrd.org/video/tt8388390-3x06",
                "https://kllamrd.org/video/tt8388390-3x07",
                "https://kllamrd.org/video/tt8388390-3x08"
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
                int randomIndex = random.nextInt(videoUrls.length - 1) + 1; // evita índice 0
                String randomVideoUrl = videoUrls[randomIndex];
                if (randomVideoUrl != null && !randomVideoUrl.isEmpty()) {
                    openWatchActivity(randomVideoUrl);
                }
            }
        });
    }

    // Abre WebViewActivityGeneral con la URL seleccionada
    private void openWatchActivity(String videoUrl) {
        Intent intent = new Intent(series5.this, WebViewActivityGeneral.class);
        intent.putExtra("url", videoUrl); // clave que WebViewActivityGeneral espera
        startActivity(intent);
    }
}

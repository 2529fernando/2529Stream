package com.fernan2529.Anime;

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

public class anime5 extends AppCompatActivity {

    private Spinner spinnerVideos;
    private ArrayAdapter<String> videosAdapter;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime5);

        spinnerVideos = findViewById(R.id.spinner_videos);
        Button playRandomButton = findViewById(R.id.aleatorio);

        // Nombres visibles en el Spinner
        final String[] videoNames = {
                "Seleccione el Capitulo",
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

        // URLs correspondientes (mismo índice que videoNames)
        final String[] videoUrls = {
                "",
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
                "https://latanime.org/ver/zero-no-tsukaima-la-magia-de-zero-latino-episodio-13"
        };

        // Adapter para el spinner
        videosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, videoNames);
        videosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVideos.setAdapter(videosAdapter);

        // Random
        random = new Random();

        // Selección desde el Spinner (evitar el primer ítem)
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
        Intent intent = new Intent(anime5.this, WebViewActivityGeneral.class);
        intent.putExtra("url", url); // <- clave que usa tu WebViewActivityGeneral
        startActivity(intent);
    }
}

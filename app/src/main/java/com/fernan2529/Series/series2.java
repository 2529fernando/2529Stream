package com.fernan2529.Series;

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

public class series2 extends AppCompatActivity {

    private Spinner spinnerVideos;
    private ArrayAdapter<String> videosAdapter;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series2);

        spinnerVideos = findViewById(R.id.spinner_videos);
        Button playRandomButton = findViewById(R.id.aleatorio);

        // Nombres visibles en el Spinner
        final String[] videoNames = {
                "Seleccione el Capitulo",
                "T1-01 - Piloto espacial 3000",
                "T1-02 - La serie ha aterrizado",
                "T1-03 - Yo, compañero",
                "T1-04 - Obras de amor en el espacio",
                "T1-05 - Temores de un planeta robot",
                "T1-06 - Unos valiosos pececitos",
                "T1-07 - Mis tres soles",
                "T1-08 - Un enorme montón de basura",
                "T1-09 - El infierno está en los demás robots",

                "T2-01 - Un vuelo inolvidable",
                "T2-02 - Universidad de Marte",
                "T2-03 - Cuando los extraterrestres atacan",
                "T2-04 - Fry y la fábrica de Slurm",
                "T2-05 - Apoyo esa emoción.",
                "T2-06 - Brannigan vuelve a empezar.",
                "T2-07 - A la cabeza de las elecciones.",
                "T2-08 - Cuentos de navidad.",
                "T2-09 - ¿Por qué debo ser un crustáceo enamorado?",
                "T2-10 - Pon la cabeza sobre mis hombros.",
                "T2-11 - El menor de dos malos.",
                "T2-12 - Bender salvaje.",
                "T2-13 - Bicíclope para dos.",
                "T2-14 - Cómo Hermes requisó su ilusión.",
                "T2-15 - Un clon propio.",
                "T2-16 - El sur profundo.",
                "T2-17 - Bender ingresa en la mafia.",
                "T2-18 - Mi primer problema con los Poppler.",
                "T2-19 - El día de la madre.",
                "T2-20 - Antología del interés I."
        };

        // URLs correspondientes (mismo índice que videoNames)
        final String[] videoUrls = {
                "",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-1x1/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-1x2/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-1x3/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-1x4/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-1x5/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-1x6/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-1x7/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-1x8/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-1x9/",

                // Temporada 2 (MEGA embebidos)
                "https://mega.nz/embed/gNp1ma6K#EuoCNGKT4Q9qts7dPNtqsoJZSViiw9VvLq5QL8tZmQ4",
                "https://mega.nz/embed/FUwBjIKK#Ag9T-QxnOYsdxp7F2LMNOWUZmPPm--qRqgwUW6TrXG4",
                "https://mega.nz/embed/hRYEHQCD#7BOoTBqieolQ4HEUHcy08ah1c-5DXAAsdTlfosqFnkQ",
                "https://mega.nz/embed/ZMYEEAzK#fudd-8eBeFNhl8c2vyTT5vQFToSwGDpC_kBiPeZjMI8",
                "https://mega.nz/embed/tRZ2zKAA#bXWIZLbxvWyQN5Pp34ebizG0ZMQdcAVHxM6olvNbRbM",
                "https://mega.nz/embed/xB5nna6L#jVKx6-lR4Da6zmxkHWRS2KtC8fKw3qfNCF7ItwNnTtw",
                "https://mega.nz/embed/oB4lxY5C#I2Al529S5AUKYZJlilHTMha0zozBqE_EHAAt-1CA7vc",
                "https://mega.nz/embed/NQxH2SYY#0onk28UfZrPcfmoyuVEpg87QAfcTVwLb8BAnZacch-Q",
                "https://mega.nz/embed/IU41GATR#ZXxqNeXRWbQYTOOxMVufFifCAhqNGYHUHGq2K_4PNu0",
                "https://mega.nz/embed/FY4lxKga#cgu6Azct2KHQkqcFE2OlViu_INDeFHrjT6KLBMep_VE",
                "https://mega.nz/embed/gc4zhQxT#LWsSn6PoI3mCu22gRBdQeR-qbDX5veYAuNXx-r4HWSE",
                "https://mega.nz/embed/lV5jTCTL#5-DDJE22E_2CoqIcvYPxvrqeVxcs-rWft3K0NAVq7eE",
                "https://mega.nz/embed/xQAyBKxK#QGbIR85-Z_XdmouDwioKPblnMvzvSF7UHGXVaMenIZA",
                "https://mega.nz/embed/kUYSkCpB#ytE2wJEssQ17QjwhNbYpm8-uv1li8lfj6szeSquqyhA",
                "https://mega.nz/embed/FNJm2IoJ#ClgYZ_wmYzNenJbIm8s9Cq3AkX8dNIEDzpxD-Ozqz4A",
                "https://mega.nz/embed/4BIE2Ywb#PtdfkYYrXldR9bWqD0C4-9HGVZwZmoHVx8tQtpA_lhE"
        };

        // Adapter para el spinner
        videosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, videoNames);
        videosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVideos.setAdapter(videosAdapter);

        // Random
        random = new Random();

        // Selección desde el Spinner (evita el primer ítem "Seleccione...")
        spinnerVideos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && position < videoUrls.length) {
                    String selectedVideoUrl = videoUrls[position];
                    if (selectedVideoUrl != null && !selectedVideoUrl.isEmpty()) {
                        openWeb(selectedVideoUrl);
                    }
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { /* no-op */ }
        });

        // Botón Aleatorio (evita índice 0)
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
        Intent intent = new Intent(series2.this, WebViewActivityGeneral.class);
        intent.putExtra("url", url); // <- clave que usa tu WebViewActivityGeneral
        startActivity(intent);
    }
}

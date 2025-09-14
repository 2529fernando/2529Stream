package com.fernan2529;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity12 extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main12);

        View prop = findViewById(R.id.noticias);

        prop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar el nuevo Activity
                Intent intent = new Intent(MainActivity12.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Manejar el botón de volver
        View btn_inicio = findViewById(R.id.btn_ver);
        btn_inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar la actividad deseada al presionar el botón de volver
                Intent intent = new Intent(MainActivity12.this, MainActivity.class);
                startActivity(intent);
            }
        });
        // Reproductor
        View btn_reproductor = findViewById(R.id.button_reproductor);
        btn_reproductor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar la actividad deseada al presionar el botón de volver
                Intent intent = new Intent(MainActivity12.this, Reproductor.class);
                startActivity(intent);
            }
        });

        Spinner spinner = findViewById(R.id.spinner_activities3);

// Define las actividades disponibles
        final String[] activityNames = {"Seleccione la Categoria", "Entretenimiento", "Peliculas",
                "Series", "Anime","Doramas", "Novelas", "Deportes", "Infantiles", "Comedia", "Historia", "Hogar", "Musica", "Noticias"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, activityNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) { // Si no es la opción de selección predeterminada
                    Intent intent;
                    switch (position) {
                        case 1:
                            intent = new Intent(MainActivity12.this, MainActivity2.class);
                            break;
                        case 2:
                            intent = new Intent(MainActivity12.this, MainActivity3.class);
                            break;
                        case 3:
                            intent = new Intent(MainActivity12.this, MainActivity4.class);
                            break;
                        case 4:
                            intent = new Intent(MainActivity12.this, MainActivity13.class);
                            break;
                        case 5:
                            intent = new Intent(MainActivity12.this, MainActivity14.class);
                            break;
                        case 6:
                            intent = new Intent(MainActivity12.this, MainActivity5.class);
                            break;
                        case 7:
                            intent = new Intent(MainActivity12.this, MainActivity6.class);
                            break;
                        case 8:
                            intent = new Intent(MainActivity12.this, MainActivity7.class);
                            break;
                        case 9:
                            intent = new Intent(MainActivity12.this, MainActivity8.class);
                            break;
                        case 10:
                            intent = new Intent(MainActivity12.this, MainActivity9.class);
                            break;
                        case 11:
                            intent = new Intent(MainActivity12.this, MainActivity10.class);
                            break;
                        case 12:
                            intent = new Intent(MainActivity12.this, MainActivity11.class);
                            break;
                        case 13:
                            intent = new Intent(MainActivity12.this, MainActivity12.class);
                            break;
                        default:
                            return;
                    }
                    startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        findViewById(R.id.nbcnews).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebView("https://www.telegratishd.com/nbc-news-en-vivo.html");
            }
        });

        findViewById(R.id.cbs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebView("https://www.cbsnews.com/live/");
            }
        });


        findViewById(R.id.bbcnews).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebView("https://livingabroad.tv/app/bbc-news");
            }
        });



        ImageView imageViewCnn = findViewById(R.id.cnn);
        imageViewCnn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí proporciona la URL del video m3u
                String videoUrl = "https://d3696l48vwq25d.cloudfront.net/v1/master/3722c60a815c199d9c0ef36c5b73da68a62b09d1/cc-0g2918mubifjw/index.m3u8";

                // Inicia WatchActivity4 y pasa la URL del video m3u
                Intent intent = new Intent(MainActivity12.this, WatchActivity4.class);
                intent.putExtra("VIDEO_URL", videoUrl);
                startActivity(intent);
            }
        });


    }



    private void openWebView(String url) {
        Intent intent = new Intent(MainActivity12.this, WebViewActivity2.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }
}

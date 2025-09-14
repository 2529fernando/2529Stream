package com.fernan2529;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity10 extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main10);

        View prop = findViewById(R.id.hogar);

        prop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar el nuevo Activity
                Intent intent = new Intent(MainActivity10.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Manejar el botón de volver
        View btn_inicio = findViewById(R.id.btn_ver);
        btn_inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar la actividad deseada al presionar el botón de volver
                Intent intent = new Intent(MainActivity10.this, MainActivity.class);
                startActivity(intent);
            }
        });
        // Reproductor
        View btn_reproductor = findViewById(R.id.button_reproductor);
        btn_reproductor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar la actividad deseada al presionar el botón de volver
                Intent intent = new Intent(MainActivity10.this, Reproductor.class);
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
                            intent = new Intent(MainActivity10.this, MainActivity2.class);
                            break;
                        case 2:
                            intent = new Intent(MainActivity10.this, MainActivity3.class);
                            break;
                        case 3:
                            intent = new Intent(MainActivity10.this, MainActivity4.class);
                            break;
                        case 4:
                            intent = new Intent(MainActivity10.this, MainActivity13.class);
                            break;
                        case 5:
                            intent = new Intent(MainActivity10.this, MainActivity14.class);
                            break;
                        case 6:
                            intent = new Intent(MainActivity10.this, MainActivity5.class);
                            break;
                        case 7:
                            intent = new Intent(MainActivity10.this, MainActivity6.class);
                            break;
                        case 8:
                            intent = new Intent(MainActivity10.this, MainActivity7.class);
                            break;
                        case 9:
                            intent = new Intent(MainActivity10.this, MainActivity8.class);
                            break;
                        case 10:
                            intent = new Intent(MainActivity10.this, MainActivity9.class);
                            break;
                        case 11:
                            intent = new Intent(MainActivity10.this, MainActivity10.class);
                            break;
                        case 12:
                            intent = new Intent(MainActivity10.this, MainActivity11.class);
                            break;
                        case 13:
                            intent = new Intent(MainActivity10.this, MainActivity12.class);
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


        findViewById(R.id.hyh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebView("https://www.telegratishd.com/discovery-hyh-en-vivo.html");
            }
        });






    }



    private void openWebView(String url) {
        Intent intent = new Intent(MainActivity10.this, WebViewActivity2.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }
}

package com.codingbeginner.sovary.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Reproductor extends AppCompatActivity {

    private static final int REQUEST_PICK_VIDEO = 101;
    EditText editTextLink;
    Button buttonPlay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor);



        editTextLink = findViewById(R.id.editTextLink);
        buttonPlay = findViewById(R.id.buttonPlay);

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = editTextLink.getText().toString().trim();
                if (!link.isEmpty()) {
                    Intent intent = new Intent(Reproductor.this, WatchActivity6.class);
                    intent.putExtra("videoUrl", link);
                    startActivity(intent);
                } else {
                    Toast.makeText(Reproductor.this, "Ingresa un enlace válido", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Spinner spinner = findViewById(R.id.spinner_activities);

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
                            intent = new Intent(Reproductor.this, MainActivity2.class);
                            break;
                        case 2:
                            intent = new Intent(Reproductor.this, MainActivity3.class);
                            break;
                        case 3:
                            intent = new Intent(Reproductor.this, MainActivity4.class);
                            break;
                        case 4:
                            intent = new Intent(Reproductor.this, MainActivity13.class);
                            break;
                        case 5:
                            intent = new Intent(Reproductor.this, MainActivity14.class);
                            break;
                        case 6:
                            intent = new Intent(Reproductor.this, MainActivity5.class);
                            break;
                        case 7:
                            intent = new Intent(Reproductor.this, MainActivity6.class);
                            break;
                        case 8:
                            intent = new Intent(Reproductor.this, MainActivity7.class);
                            break;
                        case 9:
                            intent = new Intent(Reproductor.this, MainActivity8.class);
                            break;
                        case 10:
                            intent = new Intent(Reproductor.this, MainActivity9.class);
                            break;
                        case 11:
                            intent = new Intent(Reproductor.this, MainActivity10.class);
                            break;
                        case 12:
                            intent = new Intent(Reproductor.this, MainActivity11.class);
                            break;
                        case 13:
                            intent = new Intent(Reproductor.this, MainActivity12.class);
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

        Button pickVideoButton = findViewById(R.id.btn_seleccionar);
        pickVideoButton.setOnClickListener(view -> pickVideoFromGallery());
    }

    private void pickVideoFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_VIDEO && resultCode == RESULT_OK && data != null) {
            Uri selectedVideoUri = data.getData();
            if (selectedVideoUri != null) {
                String videoPath = selectedVideoUri.toString();
                Intent intent = new Intent(this, WatchActivity5.class);
                intent.putExtra("VIDEO_URI", videoPath);
                startActivity(intent);
            }
        }
    }




    }



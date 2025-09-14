package com.fernan2529;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.Random;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.widget.Toast;
import android.app.DownloadManager;


public class anime3 extends AppCompatActivity {
    private Spinner spinnerVideos;
    private Spinner spinnerDescargas;
    private ArrayAdapter<String> videosAdapter;
    private ArrayAdapter<String> descargasAdapter;
    private Random random;

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime3);

        spinnerVideos = findViewById(R.id.spinner_videos);
        spinnerDescargas = findViewById(R.id.spinner_downloads);
        Button playRandomButton = findViewById(R.id.aleatorio);

        final String[] videoNames = {"Seleccione el Capitulo",
                "T1-01 - ¡Esta autoproclamada Diosa y la reencarnación en otro mundo!",
                "T1-02 - ¡Una explosión para esta chūnibyō!",
                "T1-03 - ¡Con un tesoro en la mano derecha!",
                "T1-04 - ¡Magia explosiva contra este enemigo formidable!",
                "T1-05 - ¡El precio de esta espada maldita!",
                "T1-06 - ¡El final de esta pelea sin sentido!",
                "T1-07 - ¡Una segunda muerte en esta estación gélida!",
                "T1-08 - ¡Una mano amada para nuestra fiesta cuando no podemos sobrevivir a este invierno!",
                "T1-09 - ¡Bendita sea esta tienda maravillosa!",
                "T1-10 - ¡Una última llama para esta fortaleza absurda!",
                "Ova1 - ¡Bendita sea esta maravillosa gargantilla!",

                "T2-01 - ¡Entrégame la liberación de esta injusticia judicial!",
                "T2-02 - ¡Un amigo para esta chica demonio carmesí!",
                "T2-03 - ¡Paz para el amo de este laberinto!",
                "T2-04 - ¡Un prometido para esta hija noble!",
                "T2-05 - ¡Servidumbre para este caballero enmascarado!",
                "T2-06 - ¡Adiós a este irritante mundo de los vivos!",
                "T2-07 - ¡Una invitación para este cabeza de nudillo!",
                "T2-08 - ¡Turismo en esta lamentable ciudad!",
                "T2-09 - ¡Una diosa para esta corrupta ciudad de aguas termales!",
                "T2-10 - ¡Dar bendiciones a este maravilloso grupo!",
                "Ova2 - ¡Bendito sea este equipo maravilloso!",

                "Película - God's Blessing on This Wonderful World!",

                "TM-01 - Los hechiceros con ojos carmesí",
                "TM-02- El tabú de la academia de magia",
                "TM-03 - Los guardianes de la aldea",
                "TM-04 - La solitaria con ojos color carmesí",
                "TM-05 - Preludio a un estallido de locura",
                "TM-06 - La razón de ser de una nini explosiva",
                "TM-07 - Alborotadores en la ciudad del agua",
                "TM-08 - Los fanáticos de la ciudad del agua",
                "TM-09 - La destructora de la aldea de los demonios carmesí",
                "TM-10 - Aventureras en la ciudad de los principiantes",
                "TM-11 - La chica explosiva y el bosque irregular",
                "TM-12 - Una explosión en este mundo maravilloso!",

                "T3-01 - ¡Brindemos por este brillante futuro!",
                "T3-02 - ¡Una sonrisa para esta niña que no se ríe!",
                "T3-03 - ¡Reeducando a esta niña tan lista!"
        };

        final String[] videoUrls = {"",
                "https://archive.org/download/konosuba-1/K-01.mkv",
                "https://archive.org/download/konosuba-1/K-02.mkv",
                "https://archive.org/download/konosuba-1/K-03.mkv",
                "https://archive.org/download/konosuba-1/K-04.mkv",
                "https://archive.org/download/konosuba-1/K-05.mkv",
                "https://archive.org/download/konosuba-1/K-06.mkv",
                "https://archive.org/download/konosuba-1/K-07.mkv",
                "https://archive.org/download/konosuba-1/K-08.mkv",
                "https://archive.org/download/konosuba-1/K-09.mkv",
                "https://archive.org/download/konosuba-1/K-10.mkv",
                "https://archive.org/download/konosuba-s-1-ova-1-latino-prueba/Konosuba%20S1%20Ova%201%20Latino%20prueba.mp4",

                "https://archive.org/download/konosuba-tem-2-01/Konosuba%21_Tem2_01.mp4",
                "https://archive.org/download/konosuba-tem-2-02/Konosuba%21_Tem2_02.mp4",
                "https://archive.org/download/konosuba-tem-2-03/Konosuba%21_Tem2_03.mp4",
                "https://archive.org/download/konosuba-tem-2-04/Konosuba%21_Tem2_04.mp4",
                "https://archive.org/download/konosuba-tem-2-05/Konosuba%21_Tem2_05.mp4",
                "https://archive.org/download/konosuba-tem-2-06/Konosuba%21_Tem2_06.mp4",
                "https://archive.org/download/konosuba-tem-2-07/Konosuba%21_Tem2_07.mp4",
                "https://archive.org/download/konosuba-tem-2-08/Konosuba%21_Tem2_08.mp4",
                "https://archive.org/download/konosuba-tem-2-09/Konosuba%21_Tem2_09.mp4",
                "https://archive.org/download/konosuba-tem-2-10/Konosuba%21_Tem2_10.mp4",
                "https://archive.org/download/konosuba-tem-2-ova/Konosuba%21_Tem2_Ova.mp4",

                "https://scontent.fuio7-1.fna.fbcdn.net/o1/v/t2/f2/m69/AQPU58NvFjMIBaQ1gHSp46Q0zBruwddllfq6RA52LDScbkr3grGOiKt9W6bagnb5Z68an3Mp1UtZfWjlU_hqHzeT.mp4?strext=1&_nc_cat=109&_nc_oc=Adl6N0v6kJdxGBONzy2cx2cqKhPdHcJ4k-yP3XkQ0zTjubpSpj1VlDlS0kwxcGerDyw&_nc_sid=5e9851&_nc_ht=scontent.fuio7-1.fna.fbcdn.net&_nc_ohc=OcyDT6xd1bIQ7kNvgH3zfHk&efg=eyJ2ZW5jb2RlX3RhZyI6Inhwdl9wcm9ncmVzc2l2ZS5GQUNFQk9PSy4uQzMuMTI4MC5kYXNoX2gyNjQtYmFzaWMtZ2VuMl83MjBwIiwieHB2X2Fzc2V0X2lkIjozNjUxMTk1MTY1MDE0NjQsImFzc2V0X2FnZV9kYXlzIjozODIsInZpX3VzZWNhc2VfaWQiOjEwMTIyLCJkdXJhdGlvbl9zIjo1Mzk1LCJ1cmxnZW5fc291cmNlIjoid3d3In0%3D&ccb=17-1&vs=68c7a8ae1345f761&_nc_vs=HBksFQIYOnBhc3N0aHJvdWdoX2V2ZXJzdG9yZS9HSUNXbUFCdFo2YXlwVlFDQVBieVBrMWkzY2RUYm1kakFBQUYVAALIAQAVAhg6cGFzc3Rocm91Z2hfZXZlcnN0b3JlL0dJQ1dtQUFfcXZTT05ITUZBQzNkTGlkSER0a0xidjRHQUFBRhUCAsgBACgAGAAbAogHdXNlX29pbAExEnByb2dyZXNzaXZlX3JlY2lwZQExFQAAJrCXp7HghKYBFQIoAkMzLBdAtRO6n752yRgZZGFzaF9oMjY0LWJhc2ljLWdlbjJfNzIwcBEAdQIA&_nc_zt=28&oh=00_AYHXnCrf_UZuPy8nFfURS3KXl58SEfEc7jeagUYGEHnoSA&oe=67ED85DD",

                "https://www2.mp4upload.com:183/d/xcxrnchnz3b4quuos2qaei2wcgclymk677tpnp7zfgbyn6ho2cdwnleczggdw6vswh3kzh5l/video.mp4",
                "https://www14.mp4upload.com:183/d/qwx4ropnz3b4quuoscqauy2uipjvklt73nubyk7lkvazvc6bsyfnt7ufs5eb756v7cwo37jj/video.mp4",
                "https://archive.org/download/05_20240215_20240215_2308/03.mp4",
                "https://archive.org/download/05_20240215_20240215_2308/04.mp4",
                "https://archive.org/download/05_20240215_20240215_2308/05.mp4",
                "https://archive.org/download/05_20240215_20240215_2308/06.mp4",
                "https://archive.org/download/05_20240215_20240215_2308/07.mp4",
                "https://archive.org/download/08_20240216_202402/08.mp4",
                "https://archive.org/download/08_20240216_202402/09.mp4",
                "https://archive.org/download/08_20240216_202402/10.mp4",
                "https://archive.org/download/08_20240216_202402/11.mp4",
                "https://archive.org/download/08_20240216_202402/12.mp4",

                "https://w2r.biananset.net/_v7/fa13cbd510d922de02db505876ea0c999378ae7afc4a672fb4b599c8e8bc7258f4510f6e2b98f45e73265ef37e727b8f22aee0d500e57a703acc16cc4357bade08df05b93e20249588fc372e26e0ce528beeed50348562b38f4477af73ede325cb088db88369de0255de493b227567320886c5c888564995ddc07bb9b7b74573/index-f1-v1-a1.m3u8",
                "https://vd2.biananset.net/_v7/7d9a88da91a3c2bc5485d8485594243e56478839e3d5614592a02d59ad33d288cfe54ec1ccb83a94f94e8de6bd327a9bfbe90a726ca8cb3bdbab1d4c90a7b78ab57182d31081c62a153c853bf6b1ef7e95497540d9464901f1f49e0dc765865eaf07efb06798229aaa0f82796d5f35d0ec04ae52c3c328e06edd9b8dd5c5dc5f/index-f1-v1-a1.m3u8",
                "https://mmd.biananset.net/_v7/354247bc6103c5d32cfb6b0576004333c5ed881faaae10ec4c29952eac1183b96f01e1ba7f831c962c9d3af17e6d1ac13e9cd1b19b999a54dcf68fe1aa33af2533ee1922f1e4a380feb3fb246ada96eb3ebd6a5d414297c77c3ca6933e705bef1ea539e7de72c1ea5afd001a124f6fd003cb112282cc00042b7ca553444eeb5e/index-f1-v1-a1.m3u8"
        };

        final String[] descargaNames = {"Seleccione el Capitulo",
                "T1-01 - ¡Esta autoproclamada Diosa y la reencarnación en otro mundo!",
                "T1-02 - ¡Una explosión para esta chūnibyō!",
                "T1-03 - ¡Con un tesoro en la mano derecha!",
                "T1-04 - ¡Magia explosiva contra este enemigo formidable!",
                "T1-05 - ¡El precio de esta espada maldita!",
                "T1-06 - ¡El final de esta pelea sin sentido!",
                "T1-07 - ¡Una segunda muerte en esta estación gélida!",
                "T1-08 - ¡Una mano amada para nuestra fiesta cuando no podemos sobrevivir a este invierno!",
                "T1-09 - ¡Bendita sea esta tienda maravillosa!",
                "T1-10 - ¡Una última llama para esta fortaleza absurda!",
                "Ova1 - ¡Bendita sea esta maravillosa gargantilla!",

                "T2-01 - ¡Entrégame la liberación de esta injusticia judicial!",
                "T2-02 - ¡Un amigo para esta chica demonio carmesí!",
                "T2-03 - ¡Paz para el amo de este laberinto!",
                "T2-04 - ¡Un prometido para esta hija noble!",
                "T2-05 - ¡Servidumbre para este caballero enmascarado!",
                "T2-06 - ¡Adiós a este irritante mundo de los vivos!",
                "T2-07 - ¡Una invitación para este cabeza de nudillo!",
                "T2-08 - ¡Turismo en esta lamentable ciudad!",
                "T2-09 - ¡Una diosa para esta corrupta ciudad de aguas termales!",
                "T2-10 - ¡Dar bendiciones a este maravilloso grupo!",
                "Ova2 - ¡Bendito sea este equipo maravilloso!",

                "Película - God's Blessing on This Wonderful World!",

                "TM-01 - Los hechiceros con ojos carmesí",
                "TM-02- El tabú de la academia de magia",
                "TM-03 - Los guardianes de la aldea",
                "TM-04 - La solitaria con ojos color carmesí",
                "TM-05 - Preludio a un estallido de locura",
                "TM-06 - La razón de ser de una nini explosiva",
                "TM-07 - Alborotadores en la ciudad del agua",
                "TM-08 - Los fanáticos de la ciudad del agua",
                "TM-09 - La destructora de la aldea de los demonios carmesí",
                "TM-10 - Aventureras en la ciudad de los principiantes",
                "TM-11 - La chica explosiva y el bosque irregular",
                "TM-12 - Una explosión en este mundo maravilloso!",

                "T3-01 - ¡Brindemos por este brillante futuro!",
                "T3-02 - ¡Una sonrisa para esta niña que no se ríe!",
                "T3-03 - ¡Reeducando a esta niña tan lista!"
        };

        final String[] descargaUrls ={"",
                "https://archive.org/download/konosuba-1/K-01.mkv",
                "https://archive.org/download/konosuba-1/K-02.mkv",
                "https://archive.org/download/konosuba-1/K-03.mkv",
                "https://archive.org/download/konosuba-1/K-04.mkv",
                "https://archive.org/download/konosuba-1/K-05.mkv",
                "https://archive.org/download/konosuba-1/K-06.mkv",
                "https://archive.org/download/konosuba-1/K-07.mkv",
                "https://archive.org/download/konosuba-1/K-08.mkv",
                "https://archive.org/download/konosuba-1/K-09.mkv",
                "https://archive.org/download/konosuba-1/K-10.mkv",
                "https://archive.org/download/konovas1/konovas1.mp4",

                "https://archive.org/download/konosuba-tem-2-01/Konosuba%21_Tem2_01.mp4",
                "https://archive.org/download/konosuba-tem-2-02/Konosuba%21_Tem2_02.mp4",
                "https://archive.org/download/konosuba-tem-2-03/Konosuba%21_Tem2_03.mp4",
                "https://archive.org/download/konosuba-tem-2-04/Konosuba%21_Tem2_04.mp4",
                "https://archive.org/download/konosuba-tem-2-05/Konosuba%21_Tem2_05.mp4",
                "https://archive.org/download/konosuba-tem-2-06/Konosuba%21_Tem2_06.mp4",
                "https://archive.org/download/konosuba-tem-2-07/Konosuba%21_Tem2_07.mp4",
                "https://archive.org/download/konosuba-tem-2-08/Konosuba%21_Tem2_08.mp4",
                "https://archive.org/download/konosuba-tem-2-09/Konosuba%21_Tem2_09.mp4",
                "https://archive.org/download/konosuba-tem-2-10/Konosuba%21_Tem2_10.mp4",
                "https://archive.org/download/konosuba-tem-2-ova/Konosuba%21_Tem2_Ova.mp4",

                "https://archive.org/download/gods-blessing-on-this-wonderful-world-legend-of-crimson/God%27s%20Blessing%20On%20This%20Wonderful%20World%21%20Legend%20Of%20Crimson.mp4",

                "https://archive.org/download/01_20240215_20240215_2143/01.mp4",
                "https://archive.org/download/05_20240215_20240215_2308/02.mp4",
                "https://archive.org/download/05_20240215_20240215_2308/03.mp4",
                "https://archive.org/download/05_20240215_20240215_2308/04.mp4",
                "https://archive.org/download/05_20240215_20240215_2308/05.mp4",
                "https://archive.org/download/05_20240215_20240215_2308/06.mp4",
                "https://archive.org/download/05_20240215_20240215_2308/07.mp4",
                "https://archive.org/download/08_20240216_202402/08.mp4",
                "https://archive.org/download/08_20240216_202402/09.mp4",
                "https://archive.org/download/08_20240216_202402/10.mp4",
                "https://archive.org/download/08_20240216_202402/11.mp4",
                "https://archive.org/download/08_20240216_202402/12.mp4",

                "https://w2r.biananset.net/_v7/fa13cbd510d922de02db505876ea0c999378ae7afc4a672fb4b599c8e8bc7258f4510f6e2b98f45e73265ef37e727b8f22aee0d500e57a703acc16cc4357bade08df05b93e20249588fc372e26e0ce528beeed50348562b38f4477af73ede325cb088db88369de0255de493b227567320886c5c888564995ddc07bb9b7b74573/index-f1-v1-a1.m3u8",
                "https://vd2.biananset.net/_v7/7d9a88da91a3c2bc5485d8485594243e56478839e3d5614592a02d59ad33d288cfe54ec1ccb83a94f94e8de6bd327a9bfbe90a726ca8cb3bdbab1d4c90a7b78ab57182d31081c62a153c853bf6b1ef7e95497540d9464901f1f49e0dc765865eaf07efb06798229aaa0f82796d5f35d0ec04ae52c3c328e06edd9b8dd5c5dc5f/index-f1-v1-a1.m3u8",
                "https://mmd.biananset.net/_v7/354247bc6103c5d32cfb6b0576004333c5ed881faaae10ec4c29952eac1183b96f01e1ba7f831c962c9d3af17e6d1ac13e9cd1b19b999a54dcf68fe1aa33af2533ee1922f1e4a380feb3fb246ada96eb3ebd6a5d414297c77c3ca6933e705bef1ea539e7de72c1ea5afd001a124f6fd003cb112282cc00042b7ca553444eeb5e/index-f1-v1-a1.m3u8"
        };

        videosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, videoNames);
        videosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVideos.setAdapter(videosAdapter);

        descargasAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, descargaNames);
        descargasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDescargas.setAdapter(descargasAdapter);

        random = new Random();

        spinnerVideos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position != 0) {
                    String selectedVideoUrl = videoUrls[position];
                    openWatchActivity(selectedVideoUrl);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerDescargas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position != 0) {
                    String selectedDownloadUrl = descargaUrls[position];
                    if (checkPermission()) {
                        downloadFile(selectedDownloadUrl);
                    } else {
                        requestPermission();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        playRandomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int randomIndex = random.nextInt(videoUrls.length - 1) + 1;
                String randomVideoUrl = videoUrls[randomIndex];
                openWatchActivity(randomVideoUrl);
            }
        });
    }

    private void openWatchActivity(String videoUrl) {
        Intent intent = new Intent(anime3.this, WatchActivity3.class);
        intent.putExtra("VIDEO_URL", videoUrl);
        startActivity(intent);
    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permission granted. Try downloading again.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Permission denied. Cannot download the file.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void downloadFile(String downloadUrl) {
        // Extraer el nombre del archivo del URL de descarga
        String fileName = downloadUrl.substring(downloadUrl.lastIndexOf('/') + 1);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
        request.setDescription("Downloading " + fileName);
        request.setTitle(fileName); // Usar el nombre del archivo como título

        // Configurar el destino de descarga
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        // Configurar otros parámetros
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE); // Mostrar la barra de progreso
        request.allowScanningByMediaScanner();
        request.setMimeType("*/*");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // Iniciar la descarga
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

        Toast.makeText(getApplicationContext(), "Downloading " + fileName, Toast.LENGTH_LONG).show();
    }
}
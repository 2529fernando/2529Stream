package com.fernan2529;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class desactivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION = 1001;
    private ListView listView;
    private List<File> videoFiles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desactivity);

        listView = findViewById(R.id.listView);

        // Verifica y solicita permiso de almacenamiento si no está concedido
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        } else {
            // Si el permiso está concedido, muestra los archivos
            displayFiles();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, muestra los archivos
                displayFiles();
            } else {
                // Permiso denegado, muestra un mensaje de error
                Toast.makeText(this, "Permiso denegado para acceder al almacenamiento.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void displayFiles() {
        // Obtiene el directorio de descargas
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        if (downloadsDir == null || !downloadsDir.exists()) {
            Toast.makeText(this, "El directorio de descargas no está disponible.", Toast.LENGTH_SHORT).show();
            return;
        }

        File[] files = downloadsDir.listFiles();
        if (files != null) {
            for (File file : files) {
                // Filtra solo archivos de video
                if (isVideoFile(file)) {
                    videoFiles.add(file);
                }
            }
        }

        if (videoFiles.isEmpty()) {
            Toast.makeText(this, "No se encontraron archivos de video.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crea un adaptador para la lista de videos
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getFileNames());
        listView.setAdapter(adapter);

        // Agrega un listener de clic en los elementos de la lista
        listView.setOnItemClickListener((parent, view, position, id) -> {
            File selectedFile = videoFiles.get(position);
            Uri fileUri = Uri.fromFile(selectedFile);

            Intent intent = new Intent(desactivity.this, WatchActivity3.class);
            intent.setData(fileUri);
            startActivity(intent);
        });
    }

    private boolean isVideoFile(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".mp4") ||
                fileName.endsWith(".avi") ||
                fileName.endsWith(".mkv") ||
                fileName.endsWith(".mov") ||
                fileName.endsWith(".wmv");
    }

    private List<String> getFileNames() {
        List<String> fileNames = new ArrayList<>();
        for (File file : videoFiles) {
            fileNames.add(file.getName());
        }
        return fileNames;
    }
}

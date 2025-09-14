package com.codingbeginner.sovary.myapplication;
import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.net.MalformedURLException;
import java.net.URL;

public class DownloadlinkActivity extends AppCompatActivity {

    EditText editTextUrl, editTextFileName;
    Button buttonDownload, buttonSelectDirectory;
    ProgressBar progressBar;
    TextView textViewProgress;

    private static final int PERMISSION_STORAGE_CODE = 1000;

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            if (downloadManager != null) {
                Cursor cursor = downloadManager.query(query);
                if (cursor.moveToFirst()) {
                    int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        // Descarga completada
                        progressBar.setProgress(100);
                        textViewProgress.setText("Descarga completada");
                    } else if (status == DownloadManager.STATUS_FAILED) {
                        // Descarga fallida
                        Toast.makeText(DownloadlinkActivity.this, "Descarga fallida", Toast.LENGTH_SHORT).show();
                        textViewProgress.setText("Descarga fallida");
                    } else {
                        // Descarga en progreso
                        int bytesDownloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                        int bytesTotal = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                        int progress = (bytesDownloaded * 100) / bytesTotal;
                        progressBar.setProgress(progress);
                        textViewProgress.setText("Descargando... " + progress + "%");
                    }
                }
                cursor.close();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloadlink);

        editTextUrl = findViewById(R.id.editTextUrl);
        editTextFileName = findViewById(R.id.editTextFileName);
        buttonDownload = findViewById(R.id.buttonDownload);
        progressBar = findViewById(R.id.progressBar);
        textViewProgress = findViewById(R.id.textViewProgress);

        // Solicitar permiso de almacenamiento si no está otorgado
        if (!checkStoragePermission()) {
            requestStoragePermission();
        }

        editTextUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String url = editTextUrl.getText().toString();
                String fileName = getFileNameFromUrl(url);
                editTextFileName.setText(fileName);
            }
        });

        buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = editTextUrl.getText().toString();
                String fileName = editTextFileName.getText().toString();
                if (!url.isEmpty() && !fileName.isEmpty()) {
                    downloadFile(url, fileName);
                } else {
                    Toast.makeText(DownloadlinkActivity.this, "Por favor ingrese la URL y el nombre del archivo", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(downloadReceiver);
    }

    private String getFileNameFromUrl(String url) {
        String fileName = "";
        try {
            URL urlObj = new URL(url);
            String path = urlObj.getPath();
            fileName = path.substring(path.lastIndexOf('/') + 1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    private void downloadFile(String url, String fileName) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Descargando archivo");
        request.setTitle(fileName);

        // Configuración de la ruta de destino para la descarga en la carpeta de descargas
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        if (manager != null) {
            manager.enqueue(request);
            Toast.makeText(this, "Descarga iniciada", Toast.LENGTH_SHORT).show();
            // Mostrar el texto de progreso y la barra de progreso
            textViewProgress.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "Error al iniciar la descarga", Toast.LENGTH_SHORT).show();
        }
    }

    // Verificar si se otorgó el permiso de almacenamiento
    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    // Solicitar permiso de almacenamiento
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE_CODE);
    }
}
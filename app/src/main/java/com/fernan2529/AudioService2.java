package com.fernan2529;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RemoteViews;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AudioService2 extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private static final String CHANNEL_ID = "AudioService2Channel";
    private static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_audio_service2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar el reproductor de medios con el archivo de audio
        mediaPlayer = MediaPlayer.create(this, R.raw.ventanass); // Asegúrate de tener un archivo de audio en res/raw

        // Manejar el clic del botón "Play"
        findViewById(R.id.play_button).setOnClickListener(v -> {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                showNotification();
            }
        });

        // Manejar el clic del botón "Pausa"
        findViewById(R.id.pause_button).setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                showNotification();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    private void showNotification() {
        // Crear el canal de notificación si es necesario
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Audio Service 2 Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }

        // Configurar la acción de reproducción
        Intent playIntent = new Intent(this, AudioService2.class);
        playIntent.setAction("PLAY");
        PendingIntent playPendingIntent = PendingIntent.getActivity(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Configurar la acción de pausa
        Intent pauseIntent = new Intent(this, AudioService2.class);
        pauseIntent.setAction("PAUSE");
        PendingIntent pausePendingIntent = PendingIntent.getActivity(this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Obtener los estilos de los botones de la actividad
        Button playButton = findViewById(R.id.play_button);
        Button pauseButton = findViewById(R.id.pause_button);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout);
        remoteViews.setOnClickPendingIntent(R.id.play_button, playPendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.pause_button, pausePendingIntent);

        // Crear la notificación con botones de control
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Audio Service 2")
                .setContentText(mediaPlayer.isPlaying() ? "Audio is playing" : "Audio is paused")
                .setSmallIcon(R.drawable.ic_notification)
                .setCustomContentView(remoteViews)
                .build();

        // Mostrar la notificación
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification);
    }}
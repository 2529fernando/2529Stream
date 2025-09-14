package com.fernan2529;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class Notification1 extends AppCompatActivity {
    private Button btnSN;
    private static final String CHANNEL_ID = "canal";
    private PendingIntent pendingIntent;
    private Uri videoUri; // URI del video

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification1);

        btnSN = findViewById(R.id.btnSN);
        btnSN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    showNotification();
                } else {
                    showNewNotification();
                }
            }
        });

        // Obtener la URI del video del intent
        if (getIntent() != null && getIntent().hasExtra("VIDEO_URI")) {
            videoUri = Uri.parse(getIntent().getStringExtra("VIDEO_URI"));
        }
    }

    private void showNotification() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                "NEW", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
        showNewNotification();
    }

    private void showNewNotification() {
        Intent notificationIntent = new Intent(this, Notification1.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Botón de reproducción
        Intent playIntent = new Intent(this, YourPlaybackService.class);
        playIntent.setAction("PLAY_ACTION");
        PendingIntent playPendingIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action playAction = new NotificationCompat.Action.Builder(R.drawable.ic_play, "Play", playPendingIntent).build();

        // Botón de pausa
        Intent pauseIntent = new Intent(this, YourPlaybackService.class);
        pauseIntent.setAction("PAUSE_ACTION");
        PendingIntent pausePendingIntent = PendingIntent.getService(this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action pauseAction = new NotificationCompat.Action.Builder(R.drawable.ic_pause, "Pause", pausePendingIntent).build();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Reproductor")
                .setContentText("segundopla")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)  // Usamos el PendingIntent de Notification1
                .addAction(playAction)
                .addAction(pauseAction);
        // Agregar otros botones de control aquí

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplicationContext());
        managerCompat.notify(1, builder.build());
    }

    private void setPendingIntent(Class<?> clsActivity) {
        Intent intent = new Intent(this, clsActivity);
        // Pasar la URI del video a WatchActivity5
        intent.putExtra("VIDEO_URI", videoUri.toString());
        // Asegurarse de que la actividad se abra al hacer clic en la notificación
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
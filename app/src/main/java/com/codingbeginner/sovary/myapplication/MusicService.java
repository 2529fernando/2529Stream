package com.codingbeginner.sovary.myapplication;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media.app.NotificationCompat.MediaStyle;


public class MusicService extends Service {

    private MediaSessionCompat mediaSession;

    @Override
    public void onCreate() {
        super.onCreate();

        mediaSession = new MediaSessionCompat(this, "MusicService");
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
                // Controlar reproducción
            }

            @Override
            public void onPause() {
                super.onPause();
                // Controlar pausa
            }

            @Override
            public void onStop() {
                super.onStop();
                // Controlar parada
            }
        });

        startForeground(1, createNotification());
    }

    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "music_channel")
                .setContentTitle("Reproduciendo música")
                .setContentText("Título de la canción")
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_notification))
                .addAction(new NotificationCompat.Action(R.drawable.ic_pause, "Pausar", getActionIntent(PlaybackStateCompat.ACTION_PAUSE)))
                .addAction(new NotificationCompat.Action(R.drawable.ic_play, "Parar", getActionIntent(PlaybackStateCompat.ACTION_STOP)))
                .setStyle(new MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(0, 1));

        return builder.build();
    }

    private PendingIntent getActionIntent(long action) {
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(Long.toString(action));
        return PendingIntent.getService(this, (int) action, intent, 0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            long action = Long.parseLong(intent.getAction());
            if (action == PlaybackStateCompat.ACTION_PLAY) {
                mediaSession.getController().getTransportControls().play();
            } else if (action == PlaybackStateCompat.ACTION_PAUSE) {
                mediaSession.getController().getTransportControls().pause();
            } else if (action == PlaybackStateCompat.ACTION_STOP) {
                mediaSession.getController().getTransportControls().stop();
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaSession.release();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
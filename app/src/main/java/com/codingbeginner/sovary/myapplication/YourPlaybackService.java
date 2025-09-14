package com.codingbeginner.sovary.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

public class YourPlaybackService extends android.app.Service {
    private static final String CHANNEL_ID = "your_playback_channel";
    private static final int NOTIFICATION_ID = 1;

    private final IBinder binder = new LocalBinder();
    private SimpleExoPlayer exoPlayer;

    public class LocalBinder extends Binder {
        YourPlaybackService getService() {
            return YourPlaybackService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case "PLAY_ACTION":
                    play();
                    break;
                case "PAUSE_ACTION":
                    pause();
                    break;
                default:
                    break;
            }
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        exoPlayer = new SimpleExoPlayer.Builder(getApplicationContext())
                .setTrackSelector(new DefaultTrackSelector(getApplicationContext()))
                .setLoadControl(new DefaultLoadControl())
                .build();
    }

    private void startForegroundService() {
        createNotificationChannel();

        Intent notificationIntent = new Intent(this, YourPlaybackService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                0
        );

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Your Playback Service")
                .setContentText("Está reproduciendo audio")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Your Playback Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void play() {
        Uri uri = Uri.parse("url_del_audio");
        MediaSource mediaSource = buildMediaSource(uri);
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);

        startForegroundService();
    }

    private void pause() {
        exoPlayer.setPlayWhenReady(false);
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ProgressiveMediaSource.Factory(
                new DefaultDataSourceFactory(getApplicationContext(), "your_agent_name")
        ).createMediaSource(MediaItem.fromUri(uri));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        exoPlayer.release();
    }
}
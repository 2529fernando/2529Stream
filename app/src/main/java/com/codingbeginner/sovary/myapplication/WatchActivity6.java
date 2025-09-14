package com.codingbeginner.sovary.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;

public class WatchActivity6 extends AppCompatActivity {

    private ExoPlayer exoPlayer;
    private ImageView bt_fullscreen;
    private boolean isFullScreen = false;
    private boolean isLock = false;
    private Handler handler;
    private long playbackPosition = 0; // Guardar la posición de reproducción

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch6);
        // Mostrar la notificación
        showNotification();

        String videoUrl = getIntent().getStringExtra("videoUrl");

        handler = new Handler(Looper.getMainLooper());

        PlayerView playerView = findViewById(R.id.player);
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        bt_fullscreen = findViewById(R.id.bt_fullscreen);
        ImageView bt_lockscreen = findViewById(R.id.exo_lock);

        // Restaurar la posición de reproducción si existe
        if (savedInstanceState != null) {
            playbackPosition = savedInstanceState.getLong("PLAYBACK_POSITION", 0);
        }

        bt_fullscreen.setOnClickListener(view -> {
            if (!isFullScreen) {
                bt_fullscreen.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_fullscreen_exit));
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            } else {
                bt_fullscreen.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_fullscreen));
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            isFullScreen = !isFullScreen;
        });

        bt_lockscreen.setOnClickListener(view -> {
            if (!isLock) {
                bt_lockscreen.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_lock));
            } else {
                bt_lockscreen.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_outline_lock_open));
            }
            isLock = !isLock;
            lockScreen(isLock);
        });

        exoPlayer = new ExoPlayer.Builder(this)
                .setSeekBackIncrementMs(5000)
                .setSeekForwardIncrementMs(5000)
                .build();
        playerView.setPlayer(exoPlayer);
        playerView.setKeepScreenOn(true);

        exoPlayer = new SimpleExoPlayer.Builder(this).build();
        PlayerView playView = findViewById(R.id.player);
        playView.setPlayer(exoPlayer);

        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_BUFFERING) {
                    progressBar.setVisibility(View.VISIBLE);
                } else if (playbackState == Player.STATE_READY) {
                    progressBar.setVisibility(View.GONE);
                }

                if (!exoPlayer.getPlayWhenReady()) {
                    handler.removeCallbacks(updateProgressAction);
                } else {
                    onProgress();
                }
            }
        });

        // Construir el MediaItem
        MediaItem mediaItem = MediaItem.fromUri(videoUrl);

        // Preparar el reproductor
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.play();

        // Crear una instancia del receptor de difusión y pasar la referencia del ExoPlayer
        NotificationActionReceiver notificationActionReceiver = new NotificationActionReceiver(exoPlayer);
        IntentFilter filter = new IntentFilter();
        filter.addAction("PLAY");
        filter.addAction("PAUSE");
        registerReceiver(notificationActionReceiver, filter);
    }

    private Runnable updateProgressAction = this::onProgress;

    long ad = 4000;
    boolean check = false;

    private void onProgress() {
        ExoPlayer player = exoPlayer;
        long position = player == null ? 0 : player.getCurrentPosition();
        handler.removeCallbacks(updateProgressAction);
        int playbackState = player == null ? Player.STATE_IDLE : player.getPlaybackState();
        if (playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED) {
            long delayMs;
            if (player.getPlayWhenReady() && playbackState == Player.STATE_READY) {
                delayMs = 1000 - position % 1000;
                if (delayMs < 200) {
                    delayMs += 1000;
                }
            } else {
                delayMs = 1000;
            }

            if ((ad - 3000 <= position && position <= ad) && !check) {
                check = true;
                initAd();
            }
            handler.postDelayed(updateProgressAction, delayMs);
        }
    }

    RewardedInterstitialAd rewardedInterstitialAd = null;

    private void initAd() {
        if (rewardedInterstitialAd != null) return;
        MobileAds.initialize(this);
        RewardedInterstitialAd.load(this, "ca-app-pub-3940256099942544/5354046379",
                new AdRequest.Builder().build(), new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull RewardedInterstitialAd p0) {
                        rewardedInterstitialAd = p0;
                        rewardedInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                Log.d("WatchActivity_AD", adError.getMessage());
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                handler.removeCallbacks(updateProgressAction);
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                exoPlayer.setPlayWhenReady(true);
                                rewardedInterstitialAd = null;
                                check = false;
                            }
                        });
                        LinearLayout sec_ad_countdown = findViewById(R.id.sect_ad_countdown);
                        TextView tx_ad_countdown = findViewById(R.id.tx_ad_countdown);
                        sec_ad_countdown.setVisibility(View.VISIBLE);
                        new CountDownTimer(4000, 1000) {
                            @Override
                            public void onTick(long l) {
                                tx_ad_countdown.setText("Ad in " + l / 1000);
                            }

                            @Override
                            public void onFinish() {
                                sec_ad_countdown.setVisibility(View.GONE);
                                rewardedInterstitialAd.show(WatchActivity6.this, rewardItem -> {

                                });
                            }
                        }.start();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        rewardedInterstitialAd = null;
                    }
                });
    }

    void lockScreen(boolean lock) {
        LinearLayout sec_mid = findViewById(R.id.sec_controlvid1);
        LinearLayout sec_bottom = findViewById(R.id.sec_controlvid2);
        if (lock) {
            sec_mid.setVisibility(View.INVISIBLE);
            sec_bottom.setVisibility(View.INVISIBLE);
        } else {
            sec_mid.setVisibility(View.VISIBLE);
            sec_bottom.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (isLock) return;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            bt_fullscreen.performClick();
        } else super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        exoPlayer.stop();
        // Guardar la posición de reproducción cuando la actividad se detiene
        playbackPosition = exoPlayer.getCurrentPosition();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exoPlayer.release();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("PLAY");
        intentFilter.addAction("PAUSE");
        registerReceiver(notificationReceiver, intentFilter);
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(notificationReceiver);
        exoPlayer.pause();
    }

    private BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case "PLAY":
                        exoPlayer.setPlayWhenReady(true);
                        break;
                    case "PAUSE":
                        exoPlayer.setPlayWhenReady(false);
                        break;
                }
            }
        }
    };
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Guardar la posición de reproducción antes de que se destruya la actividad
        outState.putLong("PLAYBACK_POSITION", playbackPosition);
    }

    // Método para mostrar la notificación
    private void showNotification() {
        // Crear un Intent para abrir la actividad cuando se hace clic en la notificación
        Intent notificationIntent = new Intent(this, WatchActivity6.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Crear un canal de notificación para Android Oreo y superior
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "ExoPlayerNotificationChannel";
            CharSequence channelName = "ExoPlayer Notification Channel";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(channel);
        }

        // Definir acciones para los botones de reproducción y pausa
        Intent playIntent = new Intent(this, NotificationActionService.class);
        playIntent.setAction("PLAY");
        PendingIntent playPendingIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseIntent = new Intent(this, NotificationActionService.class);
        pauseIntent.setAction("PAUSE");
        PendingIntent pausePendingIntent = PendingIntent.getService(this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Construir la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "ExoPlayerNotificationChannel")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Reproducción en curso")
                .setContentText("Toque para abrir la aplicación")
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_play, "Play", playPendingIntent) // Agregar acción de reproducción
                .addAction(R.drawable.ic_pause, "Pause", pausePendingIntent) // Agregar acción de pausa
                .setAutoCancel(true);

        // Mostrar la notificación
        notificationManager.notify(1, builder.build());
    }

    // Método para manejar las acciones de los botones de notificación
    public static class NotificationActionReceiver extends BroadcastReceiver {

        private ExoPlayer exoPlayer;

        // Constructor que acepta una referencia del ExoPlayer
        public NotificationActionReceiver(ExoPlayer exoPlayer) {
            this.exoPlayer = exoPlayer;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && exoPlayer != null) {
                switch (action) {
                    case "PLAY":
                        exoPlayer.setPlayWhenReady(true);
                        break;
                    case "PAUSE":
                        exoPlayer.setPlayWhenReady(false);
                        break;
                }
            }
        }
    }
}
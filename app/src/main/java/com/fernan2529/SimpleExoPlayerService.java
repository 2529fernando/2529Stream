package com.fernan2529;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.SimpleExoPlayer;

public class SimpleExoPlayerService extends Service {
    private SimpleExoPlayer exoPlayer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case "PLAY_ACTION":
                    play();
                    break;
                case "PAUSE_ACTION":
                    pause();
                    break;
                // Otros casos de acción si es necesario
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void play() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(true);
        }
    }

    private void pause() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
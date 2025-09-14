package com.fernan2529;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.google.android.exoplayer2.ExoPlayer;

public class NotificationActionReceiver extends BroadcastReceiver {

    private ExoPlayer exoPlayer;

    // Constructor que acepta una referencia del ExoPlayer
    public NotificationActionReceiver(ExoPlayer exoPlayer) {
        this.exoPlayer = exoPlayer;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case "PLAY":
                    // Manejar la acción de reproducción
                    if (exoPlayer != null) {
                        exoPlayer.setPlayWhenReady(true);
                    }
                    break;
                case "PAUSE":
                    // Manejar la acción de pausa
                    if (exoPlayer != null) {
                        exoPlayer.setPlayWhenReady(false);
                    }
                    break;
            }
        }
    }
}
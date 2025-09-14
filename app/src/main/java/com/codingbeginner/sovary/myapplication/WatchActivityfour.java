package com.codingbeginner.sovary.myapplication;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
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
import androidx.core.content.ContextCompat;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;

public class WatchActivityfour extends AppCompatActivity {
    private ExoPlayer exoPlayer;
    private ImageView btFullScreen, btLockScreen;
    private ProgressBar progressBar;
    private Handler handler;
    private boolean isFullScreen = false, isLock = false;
    private RewardedInterstitialAd rewardedInterstitialAd;
    private boolean adDisplayed = false;
    private static final long AD_POSITION = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);

        handler = new Handler(Looper.getMainLooper());
        PlayerView playerView = findViewById(R.id.player);
        progressBar = findViewById(R.id.progress_bar);
        btFullScreen = findViewById(R.id.bt_fullscreen);
        btLockScreen = findViewById(R.id.exo_lock);

        setupFullScreenToggle();
        setupLockScreenToggle();
        setupPlayer(playerView);

        String videoUrl = getIntent().getStringExtra("videoUrl");
        if (videoUrl != null) {
            playVideo(videoUrl);
        }
    }

    private void setupFullScreenToggle() {
        btFullScreen.setOnClickListener(view -> {
            isFullScreen = !isFullScreen;
            btFullScreen.setImageDrawable(ContextCompat.getDrawable(this, isFullScreen ? R.drawable.ic_baseline_fullscreen_exit : R.drawable.ic_baseline_fullscreen));
            setRequestedOrientation(isFullScreen ? ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        });
    }

    private void setupLockScreenToggle() {
        btLockScreen.setOnClickListener(view -> {
            isLock = !isLock;
            btLockScreen.setImageDrawable(ContextCompat.getDrawable(this, isLock ? R.drawable.ic_baseline_lock : R.drawable.ic_outline_lock_open));
            lockScreen(isLock);
        });
    }

    private void setupPlayer(PlayerView playerView) {
        exoPlayer = new ExoPlayer.Builder(this)
                .setSeekBackIncrementMs(5000)
                .setSeekForwardIncrementMs(5000)
                .build();

        playerView.setPlayer(exoPlayer);
        playerView.setKeepScreenOn(true);

        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                progressBar.setVisibility(playbackState == Player.STATE_BUFFERING ? View.VISIBLE : View.GONE);
                if (exoPlayer.getPlayWhenReady()) {
                    onProgress();
                } else {
                    handler.removeCallbacks(updateProgressAction);
                }
            }
        });
    }

    private void playVideo(String videoUrl) {
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.setPlayWhenReady(true);
    }

    private final Runnable updateProgressAction = this::onProgress;

    private void onProgress() {
        long position = exoPlayer.getCurrentPosition();
        handler.removeCallbacks(updateProgressAction);

        if (exoPlayer.getPlaybackState() != Player.STATE_IDLE && exoPlayer.getPlaybackState() != Player.STATE_ENDED) {
            long delayMs = 1000 - (position % 1000);
            if (delayMs < 200) delayMs += 1000;

            if (position >= AD_POSITION - 3000 && position <= AD_POSITION && !adDisplayed) {
                adDisplayed = true;
                initAd();
            }
            handler.postDelayed(updateProgressAction, delayMs);
        }
    }

    private void initAd() {
        if (rewardedInterstitialAd != null) return;

        MobileAds.initialize(this);
        RewardedInterstitialAd.load(this, "ca-app-pub-3940256099942544/5354046379",
                new AdRequest.Builder().build(), new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull RewardedInterstitialAd ad) {
                        rewardedInterstitialAd = ad;
                        showAdWithCountdown();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        rewardedInterstitialAd = null;
                    }
                });
    }

    private void showAdWithCountdown() {
        LinearLayout secAdCountdown = findViewById(R.id.sect_ad_countdown);
        TextView txAdCountdown = findViewById(R.id.tx_ad_countdown);

        secAdCountdown.setVisibility(View.VISIBLE);
        new CountDownTimer(4000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                txAdCountdown.setText("Ad in " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                secAdCountdown.setVisibility(View.GONE);
                rewardedInterstitialAd.show(WatchActivityfour.this, rewardItem -> {});
            }
        }.start();
    }

    private void lockScreen(boolean lock) {
        findViewById(R.id.sec_controlvid1).setVisibility(lock ? View.INVISIBLE : View.VISIBLE);
        findViewById(R.id.sec_controlvid2).setVisibility(lock ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (isLock) return;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            btFullScreen.performClick();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        exoPlayer.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exoPlayer.release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        exoPlayer.pause();
    }
}
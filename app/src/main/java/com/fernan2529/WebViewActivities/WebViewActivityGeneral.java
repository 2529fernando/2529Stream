package com.fernan2529.WebViewActivities;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.fernan2529.R;
import com.fernan2529.ui.LoadingOverlayView;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;

public class WebViewActivityGeneral extends AppCompatActivity {

    private static final long AUTO_HIDE_DELAY_MS = 3000L;

    private WebView webView;

    // Fullscreen
    private FrameLayout fullscreenContainer;
    private View customView;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private int originalSystemUiVisibility;
    private boolean isInFullscreen = false;

    // Overlay de tap (transparente) y overlay de carga
    private View tapOverlay;
    private LoadingOverlayView loadingOverlay;

    // Controles (columna)
    private LinearLayout controlsGroup;
    private ImageButton rotateBtn;
    private ImageButton aspectBtn;
    private boolean controlsVisible = true;

    // Auto-hide
    private final Handler autoHideHandler = new Handler(Looper.getMainLooper());
    private final Runnable hideControlsRunnable = () -> showControls(false, true);

    // Zoom nativo sobre la vista del video
    private View zoomTarget;
    private final float[] ZOOMS = new float[]{1.0f, 1.15f, 1.33f, 1.5f, 1.75f, 2.0f};
    private int zoomIndex = 0;

    // Navegación bloqueada
    private String initialUrl;
    private String initialUrlNorm;

    // Bloqueador de anuncios
    private static final List<String> AD_DOMAINS = Arrays.asList(
            "doubleclick.net",
            "ads.google.com",
            "googlesyndication.com",
            "googletagservices.com",
            "adservice.google.com",
            "facebook.net",
            "adform.net",
            "outbrain.com",
            "taboola.com"
    );

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_general);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        webView = findViewById(R.id.webview);
        fullscreenContainer = findViewById(R.id.fullscreen_container);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        if (loadingOverlay != null) loadingOverlay.showNow();

        initialUrl = getIntent().getStringExtra("url");
        initialUrlNorm = normalizeStrict(initialUrl);

        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setMediaPlaybackRequiresUserGesture(true);
        ws.setLoadWithOverviewMode(true);
        ws.setUseWideViewPort(true);
        ws.setAllowFileAccess(false);
        ws.setAllowContentAccess(true);
        ws.setSupportZoom(true);
        ws.setBuiltInZoomControls(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ws.setSafeBrowsingEnabled(true);
        ws.setSupportMultipleWindows(false);
        ws.setJavaScriptCanOpenWindowsAutomatically(false);

        webView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Permite solo la URL inicial exacta (normalizada), bloquea lo demás
                return !equalsStrict(url, initialUrlNorm);
            }

            @RequiresApi(21)
            @Override
            public boolean shouldOverrideUrlLoading(@NonNull WebView view, @NonNull WebResourceRequest req) {
                return !equalsStrict(req.getUrl().toString(), initialUrlNorm);
            }

            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap icon) {
                if (loadingOverlay != null) {
                    loadingOverlay.setProgress(0f);
                    loadingOverlay.showNow();
                }
                // Si intenta salir, regrésalo
                if (!equalsStrict(url, initialUrlNorm)) {
                    view.stopLoading();
                    if (initialUrl != null) view.loadUrl(initialUrl);
                }
                super.onPageStarted(view, url, icon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (!equalsStrict(url, initialUrlNorm)) {
                    if (initialUrl != null) view.loadUrl(initialUrl);
                } else {
                    view.clearHistory();
                    if (loadingOverlay != null) loadingOverlay.fadeOut();
                }
                super.onPageFinished(view, url);
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                // Bloqueo simple de dominios de anuncios
                String reqUrl = request.getUrl().toString().toLowerCase();
                for (String domain : AD_DOMAINS) {
                    if (reqUrl.contains(domain)) {
                        return new WebResourceResponse(
                                "text/plain",
                                "utf-8",
                                new ByteArrayInputStream(new byte[0])
                        );
                    }
                }
                return super.shouldInterceptRequest(view, request);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView v, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                // No permitir popups/ventanas nuevas
                return false;
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                if (customView != null) {
                    callback.onCustomViewHidden();
                    return;
                }
                customView = view;
                customViewCallback = callback;
                isInFullscreen = true;

                originalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
                enterFullscreen();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

                // 1) VIDEO
                FrameLayout.LayoutParams videoLp = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                videoLp.gravity = Gravity.CENTER;
                fullscreenContainer.addView(customView, videoLp);
                fullscreenContainer.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);

                // Target de zoom
                zoomTarget = findVideoSurface(customView);
                if (zoomTarget == null) zoomTarget = customView;
                applyZoomToTarget(1.0f);
                zoomIndex = 0;

                // 2) OVERLAY TAP
                addTapOverlay();

                // 3) CONTROLES
                addControlsGroup();
                showControls(true, false);
                bringControlsToFront();
                scheduleAutoHide();
            }

            @Override
            public void onHideCustomView() {
                if (customView == null) return;

                cancelAutoHide();
                removeTapOverlay();
                removeControlsGroup();

                if (zoomTarget != null) applyZoomToTarget(1.0f);
                zoomTarget = null;

                fullscreenContainer.removeView(customView);
                fullscreenContainer.setVisibility(View.GONE);
                customView = null;

                exitFullscreen();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

                if (customViewCallback != null) customViewCallback.onCustomViewHidden();
                webView.setVisibility(View.VISIBLE);
                isInFullscreen = false;
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (loadingOverlay != null) {
                    float p = Math.max(0, Math.min(100, newProgress)) / 100f;
                    loadingOverlay.setProgress(p);
                    if (p >= 1f) loadingOverlay.fadeOut();
                }
                super.onProgressChanged(view, newProgress);
            }
        });

        if (initialUrl != null) webView.loadUrl(initialUrl);
    }

    // ===== Auto-ocultado =====
    private void scheduleAutoHide() {
        autoHideHandler.removeCallbacks(hideControlsRunnable);
        if (controlsVisible) autoHideHandler.postDelayed(hideControlsRunnable, AUTO_HIDE_DELAY_MS);
    }

    private void cancelAutoHide() { autoHideHandler.removeCallbacks(hideControlsRunnable); }

    private void userActivity() { if (isInFullscreen) scheduleAutoHide(); }

    // ===== Overlay de TAP =====
    private void addTapOverlay() {
        if (tapOverlay != null) return;

        tapOverlay = new View(this);
        tapOverlay.setBackgroundColor(0x00000000);
        tapOverlay.setClickable(true);
        tapOverlay.setFocusable(false);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.FILL;
        tapOverlay.setLayoutParams(lp);

        final GestureDetector detector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        showControls(!controlsVisible, true);
                        if (controlsVisible) scheduleAutoHide(); else cancelAutoHide();
                        return true;
                    }
                });

        tapOverlay.setOnTouchListener((v, ev) -> {
            detector.onTouchEvent(ev);
            userActivity();

            // Reenviar SIEMPRE al video real para no bloquear controles del player
            if (customView != null) {
                customView.dispatchTouchEvent(ev);
            }
            return true;
        });

        fullscreenContainer.addView(tapOverlay);
    }

    private void removeTapOverlay() {
        if (tapOverlay != null) {
            fullscreenContainer.removeView(tapOverlay);
            tapOverlay = null;
        }
    }

    // ===== Controles en columna =====
    private void addControlsGroup() {
        if (controlsGroup != null) return;

        controlsGroup = new LinearLayout(this);
        controlsGroup.setOrientation(LinearLayout.VERTICAL);
        controlsGroup.setClickable(false);
        controlsGroup.setFocusable(false);
        controlsGroup.setAlpha(1f);

        FrameLayout.LayoutParams grpLp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.END | Gravity.CENTER_VERTICAL
        );
        int m = dp(12);
        grpLp.setMargins(m, m, m, m);
        controlsGroup.setLayoutParams(grpLp);

        // Rotar
        rotateBtn = new ImageButton(this);
        rotateBtn.setImageResource(android.R.drawable.ic_menu_always_landscape_portrait);
        rotateBtn.setBackgroundResource(android.R.drawable.btn_default_small);
        rotateBtn.setContentDescription("Alternar orientación");
        rotateBtn.setAlpha(0.9f);
        rotateBtn.setOnClickListener(v -> {
            int cur = getResources().getConfiguration().orientation;
            if (cur == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
            userActivity();
        });

        // Aspecto/Zoom
        aspectBtn = new ImageButton(this);
        aspectBtn.setImageResource(android.R.drawable.ic_menu_crop);
        aspectBtn.setBackgroundResource(android.R.drawable.btn_default_small);
        aspectBtn.setContentDescription("Ajustar relación de aspecto (zoom)");
        aspectBtn.setAlpha(0.9f);
        LinearLayout.LayoutParams gap = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        gap.topMargin = dp(10);
        aspectBtn.setLayoutParams(gap);
        aspectBtn.setOnClickListener(v -> {
            zoomIndex = (zoomIndex + 1) % ZOOMS.length;
            applyZoomToTarget(ZOOMS[zoomIndex]);
            userActivity();
        });

        controlsGroup.addView(rotateBtn);
        controlsGroup.addView(aspectBtn);
        fullscreenContainer.addView(controlsGroup);
        bringControlsToFront();
    }

    private void removeControlsGroup() {
        if (controlsGroup != null) {
            fullscreenContainer.removeView(controlsGroup);
            controlsGroup = null;
            rotateBtn = null;
            aspectBtn = null;
        }
    }

    private void showControls(boolean show, boolean animate) {
        controlsVisible = show;
        if (controlsGroup == null) return;

        if (show) {
            controlsGroup.setVisibility(View.VISIBLE);
            if (animate) {
                controlsGroup.setAlpha(0f);
                controlsGroup.animate().alpha(1f).setDuration(160).start();
            }
        } else {
            if (animate) {
                controlsGroup.animate().alpha(0f).setDuration(160).withEndAction(() -> {
                    if (controlsGroup != null) controlsGroup.setVisibility(View.GONE);
                }).start();
            } else {
                controlsGroup.setVisibility(View.GONE);
            }
        }
    }

    private void bringControlsToFront() {
        if (controlsGroup != null) {
            controlsGroup.setElevation(20f);
            controlsGroup.bringToFront();
        }
        if (tapOverlay != null) tapOverlay.setElevation(10f); // overlay debajo de controles
        fullscreenContainer.requestLayout();
        fullscreenContainer.invalidate();
    }

    // ===== Zoom nativo en video =====
    private View findVideoSurface(View root) {
        if (root == null) return null;
        if (root instanceof SurfaceView || root instanceof TextureView || root instanceof VideoView) return root;
        if (root instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) root;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View v = findVideoSurface(vg.getChildAt(i));
                if (v != null) return v;
            }
        }
        return null;
    }

    private void applyZoomToTarget(float scale) {
        if (zoomTarget == null) return;
        zoomTarget.setPivotX(zoomTarget.getWidth() / 2f);
        zoomTarget.setPivotY(zoomTarget.getHeight() / 2f);
        zoomTarget.setScaleX(scale);
        zoomTarget.setScaleY(scale);
        if (zoomTarget.getLayoutParams() instanceof FrameLayout.LayoutParams) {
            ((FrameLayout.LayoutParams) zoomTarget.getLayoutParams()).gravity = Gravity.CENTER;
        }
        zoomTarget.requestLayout();
        bringControlsToFront();
    }

    // ===== Utilidades/UI =====
    private int dp(int v) { return Math.round(v * getResources().getDisplayMetrics().density); }

    private void enterFullscreen() {
        View decor = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController c = decor.getWindowInsetsController();
            if (c != null) {
                c.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                c.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            int flags = View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            decor.setSystemUiVisibility(flags);
        }
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void exitFullscreen() {
        View decor = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController c = decor.getWindowInsetsController();
            if (c != null) c.show(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
        } else {
            decor.setSystemUiVisibility(originalSystemUiVisibility);
        }
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onBackPressed() {
        if (customView != null) {
            WebChromeClient wcc = (WebChromeClient) webView.getWebChromeClient();
            if (wcc != null) wcc.onHideCustomView();
            return;
        }
        super.onBackPressed();
    }

    @Override protected void onPause() { super.onPause(); if (webView != null) webView.onPause(); }
    @Override protected void onResume() { super.onResume(); if (webView != null) webView.onResume(); }
    @Override
    protected void onDestroy() {
        if (webView != null) {
            ViewGroup p = (ViewGroup) webView.getParent();
            if (p != null) p.removeView(webView);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    // Comparación estricta de URL (host+path+query+fragment), ignorando esquema
    private boolean equalsStrict(String candidate, String initialNorm) {
        if (candidate == null || initialNorm == null) return false;
        return normalizeStrict(candidate).equals(initialNorm);
    }

    private String normalizeStrict(String url) {
        if (url == null) return "";
        try {
            java.net.URI u = new java.net.URI(url);
            String host = (u.getHost() == null ? "" : u.getHost().toLowerCase());
            String path = (u.getPath() == null || u.getPath().isEmpty()) ? "/" : u.getPath();
            path = path.replaceAll("/+$", "/");
            String query = (u.getQuery() == null) ? "" : ("?" + u.getQuery());
            String fragment = (u.getFragment() == null) ? "" : ("#" + u.getFragment());
            return host + path + query + fragment;
        } catch (Exception e) {
            return url.replaceFirst("^https?://", "").replaceAll("/+$", "/");
        }
    }
}

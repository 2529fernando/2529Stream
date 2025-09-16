package com.fernan2529;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class Navegation extends AppCompatActivity {

    // ===== UI =====
    private EditText urlInput;
    private ImageView linkIcon, clearIcon, webBack, webForward, webRefresh, webShare, webTabs;
    private ProgressBar progressBar;
    private FrameLayout webContainer;        // donde montamos la WebView de la pestaña activa
    private FrameLayout fullscreenContainer; // contenedor para video en fullscreen

    // ===== Tabs =====
    private final TabManager tabs = new TabManager();

    // ===== Fullscreen avanzado (estilo WebViewActivityGeneral) =====
    private View customView;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private int originalSystemUiVisibility;
    private boolean isInFullscreen = false;

    // Overlay táctil transparente que reenvía los taps al player (para no bloquear controles)
    private View tapOverlay;

    // Controles flotantes en columna (rotar / zoom)
    private LinearLayout controlsGroup;
    private ImageButton rotateBtn, aspectBtn;
    private boolean controlsVisible = true;

    // Auto-hide de controles
    private static final long AUTO_HIDE_DELAY_MS = 3000L;
    private final Handler autoHideHandler = new Handler(Looper.getMainLooper());
    private final Runnable hideControlsRunnable = () -> showControls(false, true);

    // Zoom nativo sobre la superficie del video
    private View zoomTarget;
    private final float[] ZOOMS = new float[]{1.0f, 1.15f, 1.33f, 1.5f, 1.75f, 2.0f};
    private int zoomIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navegation);

        // ===== Find Views =====
        linkIcon   = findViewById(R.id.link_icon);
        clearIcon  = findViewById(R.id.clear_icon);
        webBack    = findViewById(R.id.web_back);
        webForward = findViewById(R.id.web_forward);
        webRefresh = findViewById(R.id.web_refresh);
        webShare   = findViewById(R.id.web_share);
        webTabs    = findViewById(R.id.web_tabs);

        urlInput   = findViewById(R.id.url_input);
        progressBar= findViewById(R.id.progressBar);
        webContainer = findViewById(R.id.web_container);
        fullscreenContainer = findViewById(R.id.fullscreen_container);

        // ===== Listeners básicos =====
        urlInput.setOnEditorActionListener((v, actionId, event) -> {
            boolean imeGo = (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE);
            boolean enter = event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN;
            if (imeGo || enter) {
                hideKeyboard();
                loadMyUrl(urlInput.getText().toString().trim());
                return true;
            }
            return false;
        });

        clearIcon.setOnClickListener(v -> urlInput.setText(""));

        webBack.setOnClickListener(v -> {
            WebView wv = tabs.current();
            if (wv != null && wv.canGoBack()) wv.goBack();
        });

        webForward.setOnClickListener(v -> {
            WebView wv = tabs.current();
            if (wv != null && wv.canGoForward()) wv.goForward();
        });

        webRefresh.setOnClickListener(v -> {
            WebView wv = tabs.current();
            if (wv != null) wv.reload();
        });

        webShare.setOnClickListener(v -> {
            WebView wv = tabs.current();
            if (wv == null) return;
            String currentUrl = wv.getUrl();
            if (currentUrl == null || currentUrl.isEmpty()) return;
            Intent share = new Intent(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_TEXT, currentUrl);
            share.setType("text/plain");
            startActivity(Intent.createChooser(share, "Compartir enlace"));
        });

        webTabs.setOnClickListener(v -> showTabsDialog());

        // ===== Pestaña inicial =====
        progressBar.setVisibility(View.VISIBLE);
        openNewTab("https://www.google.com");
    }

    // ================== Tabs ==================

    private static class Tab {
        WebView view;
        String title = "Nueva pestaña";
        String url = "";
        Tab(WebView v) { this.view = v; }
    }

    private class TabManager {
        final List<Tab> list = new ArrayList<>();
        int currentIndex = -1;

        WebView current() {
            if (currentIndex < 0 || currentIndex >= list.size()) return null;
            return list.get(currentIndex).view;
        }
        Tab currentTab() {
            if (currentIndex < 0 || currentIndex >= list.size()) return null;
            return list.get(currentIndex);
        }
        void add(Tab t) {
            list.add(t);
            currentIndex = list.size() - 1;
        }
        void switchTo(int idx) {
            if (idx < 0 || idx >= list.size()) return;
            currentIndex = idx;
            mountCurrentIntoContainer();
            refreshUrlAndButtons();
        }
        void closeCurrent() {
            if (list.isEmpty() || currentIndex < 0) return;
            Tab toRemove = list.remove(currentIndex);
            if (toRemove.view != null) {
                webContainer.removeView(toRemove.view);
                toRemove.view.destroy();
            }
            if (list.isEmpty()) {
                currentIndex = -1;
                openNewTab("https://www.google.com");
            } else {
                if (currentIndex >= list.size()) currentIndex = list.size() - 1;
                mountCurrentIntoContainer();
                refreshUrlAndButtons();
            }
        }
    }

    private void openNewTab(String initialUrl) {
        WebView wv = createWebViewForTab();
        Tab t = new Tab(wv);
        tabs.add(t);
        mountCurrentIntoContainer();
        loadUrlOn(wv, initialUrl);
        updateTabsBadge();
    }

    private void mountCurrentIntoContainer() {
        webContainer.removeAllViews();
        WebView current = tabs.current();
        if (current != null) {
            webContainer.addView(current, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
        }
        updateTabsBadge();
    }

    private WebView createWebViewForTab() {
        WebView wv = new WebView(getApplicationContext());

        WebSettings ws = wv.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setBuiltInZoomControls(true);
        ws.setDisplayZoomControls(false);
        ws.setLoadWithOverviewMode(true);
        ws.setUseWideViewPort(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            ws.setMediaPlaybackRequiresUserGesture(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ws.setSafeBrowsingEnabled(true);
        }
        ws.setSupportMultipleWindows(false);
        ws.setJavaScriptCanOpenWindowsAutomatically(false);

        wv.setFocusable(true);
        wv.setFocusableInTouchMode(true);

        wv.setWebViewClient(new InnerWebViewClient());
        wv.setWebChromeClient(new InnerWebChromeClient());
        wv.setDownloadListener(new InnerDownloadListener());

        return wv;
    }

    private void showTabsDialog() {
        if (tabs.list.isEmpty()) return;

        String[] items = new String[tabs.list.size()];
        for (int i = 0; i < tabs.list.size(); i++) {
            Tab t = tabs.list.get(i);
            String title = (t.title != null && !t.title.isEmpty()) ? t.title : (t.url != null ? t.url : "Nueva pestaña");
            items[i] = (i == tabs.currentIndex ? "• " : "  ") + title;
        }

        new AlertDialog.Builder(this)
                .setTitle("Pestañas (" + tabs.list.size() + ")")
                .setSingleChoiceItems(items, tabs.currentIndex, (dialog, which) -> tabs.switchTo(which))
                .setPositiveButton("Nueva pestaña", (d, w) -> openNewTab("https://www.google.com"))
                .setNeutralButton("Cerrar actual", (d, w) -> tabs.closeCurrent())
                .setNegativeButton("Cerrar", null)
                .show();
    }

    private void updateTabsBadge() {
        if (webTabs != null) {
            webTabs.setContentDescription("Pestañas: " + Math.max(0, tabs.list.size()));
        }
    }

    // ================== Navegación ==================

    private void loadMyUrl(String input) {
        WebView wv = tabs.current();
        if (wv == null) return;
        loadUrlOn(wv, normalizeInputToUrl(input));
    }

    private void loadUrlOn(WebView wv, String url) {
        progressBar.setVisibility(View.VISIBLE);
        wv.loadUrl(url);
    }

    private String normalizeInputToUrl(String input) {
        String url = input;
        if (!(url.startsWith("http://") || url.startsWith("https://"))) {
            if (Patterns.WEB_URL.matcher(url).matches()) {
                url = "https://" + url;
            } else {
                url = "https://www.google.com/search?q=" + Uri.encode(input);
            }
        }
        return url;
    }

    private void refreshUrlAndButtons() {
        WebView wv = tabs.current();
        if (wv != null) {
            String u = wv.getUrl();
            if (u != null) urlInput.setText(u);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(urlInput.getWindowToken(), 0);
    }

    // ================== Clients por pestaña ==================

    private class InnerWebViewClient extends WebViewClient {
        @Override @SuppressWarnings("deprecation")
        public boolean shouldOverrideUrlLoading(WebView view, String url) { return false; }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) { return false; }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
            Tab ct = tabs.currentTab();
            if (ct != null && ct.view == view) ct.url = url;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (progressBar != null) progressBar.setVisibility(View.INVISIBLE);

            String title = view.getTitle();
            for (Tab t : tabs.list) {
                if (t.view == view) {
                    t.url = url;
                    if (title != null && !title.isEmpty()) t.title = title;
                    break;
                }
            }
            if (tabs.current() == view) urlInput.setText(url);
        }
    }

    private class InnerWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (progressBar != null) {
                progressBar.setProgress(newProgress);
                if (newProgress >= 100) progressBar.setVisibility(View.INVISIBLE);
                else progressBar.setVisibility(View.VISIBLE);
            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            if (isInFullscreen) { callback.onCustomViewHidden(); return; }
            isInFullscreen = true;

            customView = view;
            customViewCallback = callback;

            // UI de sistema y orientación libre
            enterFullscreenUi();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

            // Montar video en contenedor fullscreen
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    Gravity.CENTER
            );
            fullscreenContainer.addView(customView, lp);
            fullscreenContainer.setVisibility(View.VISIBLE);

            // Ocultar WebView activa
            WebView current = tabs.current();
            if (current != null) current.setVisibility(View.GONE);

            // Target de zoom
            zoomTarget = findVideoSurface(customView);
            if (zoomTarget == null) zoomTarget = customView;
            applyZoomToTarget(1.0f);
            zoomIndex = 0;

            // Overlay táctil y controles flotantes
            addTapOverlay();
            addControlsGroup();
            showControls(true, false);
            bringControlsToFront();
            scheduleAutoHide();

            if (progressBar != null) progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onHideCustomView() {
            if (!isInFullscreen) return;
            isInFullscreen = false;

            cancelAutoHide();
            removeTapOverlay();
            removeControlsGroup();

            if (zoomTarget != null) applyZoomToTarget(1.0f);
            zoomTarget = null;

            // desmontar video
            if (customView != null) {
                fullscreenContainer.removeView(customView);
                customView = null;
            }
            fullscreenContainer.setVisibility(View.GONE);

            // restaurar UI sistema y orientación
            exitFullscreenUi();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

            // callback del custom view
            if (customViewCallback != null) {
                customViewCallback.onCustomViewHidden();
                customViewCallback = null;
            }

            // mostrar WebView activa
            WebView current = tabs.current();
            if (current != null) current.setVisibility(View.VISIBLE);

            if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        }
    }

    private class InnerDownloadListener implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
            // Deriva a tu actividad de descargas
            DownloadActivity.start(Navegation.this, url);
        }
    }

    // ================== Fullscreen helpers (UI system bars) ==================

    private void enterFullscreenUi() {
        View decor = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController c = decor.getWindowInsetsController();
            if (c != null) {
                c.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                c.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            originalSystemUiVisibility = decor.getSystemUiVisibility();
            int flags = View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            decor.setSystemUiVisibility(flags);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void exitFullscreenUi() {
        View decor = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController c = decor.getWindowInsetsController();
            if (c != null) c.show(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
        } else {
            decor.setSystemUiVisibility(originalSystemUiVisibility);
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    // ================== Overlay y controles flotantes ==================

    private void scheduleAutoHide() {
        autoHideHandler.removeCallbacks(hideControlsRunnable);
        if (controlsVisible) autoHideHandler.postDelayed(hideControlsRunnable, AUTO_HIDE_DELAY_MS);
    }
    private void cancelAutoHide() { autoHideHandler.removeCallbacks(hideControlsRunnable); }
    private void userActivity() { if (isInFullscreen) scheduleAutoHide(); }

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
                    @Override public boolean onSingleTapConfirmed(MotionEvent e) {
                        showControls(!controlsVisible, true);
                        if (controlsVisible) scheduleAutoHide(); else cancelAutoHide();
                        return true;
                    }
                });

        tapOverlay.setOnTouchListener((v, ev) -> {
            detector.onTouchEvent(ev);
            userActivity();
            if (customView != null) customView.dispatchTouchEvent(ev); // reenviar SIEMPRE al player
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

        // Aspect/Zoom
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
            controlsGroup = null; rotateBtn = null; aspectBtn = null;
        }
    }

    private void showControls(boolean show, boolean animate) {
        controlsVisible = show;
        if (controlsGroup == null) return;

        if (show) {
            controlsGroup.setVisibility(View.VISIBLE);
            if (animate) { controlsGroup.setAlpha(0f); controlsGroup.animate().alpha(1f).setDuration(160).start(); }
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

    // ================== Zoom nativo en video ==================

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

    private int dp(int v) { return Math.round(v * getResources().getDisplayMetrics().density); }

    // ================== Back / Rotación / Ciclo de vida ==================

    @Override
    public void onBackPressed() {
        if (isInFullscreen) {
            WebView wv = tabs.current();
            if (wv != null && wv.getWebChromeClient() instanceof InnerWebChromeClient) {
                ((InnerWebChromeClient) wv.getWebChromeClient()).onHideCustomView();
                return;
            }
        }
        WebView wv = tabs.current();
        if (wv != null && wv.canGoBack()) { wv.goBack(); return; }
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    protected void onDestroy() {
        // Destruye todas las WebViews de las pestañas
        for (Tab t : tabs.list) {
            if (t.view != null) {
                try {
                    t.view.loadUrl("about:blank");
                    t.view.stopLoading();
                    t.view.setWebChromeClient(null);
                    t.view.setWebViewClient(null);
                    ViewGroup p = (ViewGroup) t.view.getParent();
                    if (p != null) p.removeView(t.view);
                    t.view.destroy();
                } catch (Throwable ignored) {}
            }
        }
        tabs.list.clear();
        super.onDestroy();
    }
}

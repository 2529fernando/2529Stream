package com.fernan2529.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;

import androidx.annotation.Nullable;

import com.fernan2529.R;

/**
 * Overlay de carga con PNG de fondo, barra de progreso y porcentaje "NN%".
 * Ajusta los porcentajes BAR_* para alinear la barra al hueco de tu imagen.
 */
public class LoadingOverlayView extends View {

    // Progreso 0..1
    private float progress = 0f;

    // Imagen de fondo (tu diseño)
    private Bitmap bg;

    // Pinturas
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    // Rectángulos de dibujo
    private final RectF dst = new RectF();
    private final RectF barRect = new RectF();

    // ---- Porcentajes relativos del área de la barra en la imagen ----
    // Ajústalos para que la barra coincida con el hueco del PNG.
    private static final float BAR_LEFT_PCT   = 0.085f; // 8.5% desde la izquierda
    private static final float BAR_RIGHT_PCT  = 0.915f; // 91.5% desde la izquierda (borde derecho)
    private static final float BAR_TOP_PCT    = 0.58f;  // 58% desde arriba
    private static final float BAR_HEIGHT_PCT = 0.16f;  // altura ≈16% del alto

    // Radio de esquinas de la barra (en px)
    private float barRadiusPx;

    public LoadingOverlayView(Context ctx) { super(ctx); init(ctx); }
    public LoadingOverlayView(Context ctx, @Nullable AttributeSet attrs) { super(ctx, attrs); init(ctx); }
    public LoadingOverlayView(Context ctx, @Nullable AttributeSet attrs, int defStyleAttr) { super(ctx, attrs, defStyleAttr); init(ctx); }

    private void init(Context ctx) {
        setWillNotDraw(false);

        // Relleno de la barra (rojo)
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(0xFFFF0000); // #FF0000

        // Texto de porcentaje
        textPaint.setColor(0xFFFFFFFF);      // Blanco
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
        textPaint.setShadowLayer(6f, 0f, 2f, 0x80000000); // sombra para contraste

        bg = BitmapFactory.decodeResource(getResources(), R.mipmap.loading_fernan);

        float density = getResources().getDisplayMetrics().density;
        barRadiusPx = 12f * density;

        setAlpha(1f);
        setVisibility(VISIBLE);
    }

    /** Progreso 0..1 */
    public void setProgress(float p) {
        float clamped = Math.max(0f, Math.min(1f, p));
        if (clamped != progress) {
            progress = clamped;
            invalidate();
        }
    }

    /** Aparece con fade corto */
    public void fadeIn() {
        if (getVisibility() != VISIBLE) setVisibility(VISIBLE);
        AlphaAnimation a = new AlphaAnimation(getAlpha(), 1f);
        a.setDuration(150);
        a.setFillAfter(true);
        startAnimation(a);
    }

    /** Desaparece con fade corto */
    public void fadeOut() {
        AlphaAnimation a = new AlphaAnimation(getAlpha(), 0f);
        a.setDuration(180);
        a.setFillAfter(true);
        startAnimation(a);
        postDelayed(() -> { clearAnimation(); setAlpha(0f); setVisibility(GONE); }, 200);
    }

    /** Muestra sin animación */
    public void showNow() {
        clearAnimation();
        setAlpha(1f);
        setVisibility(VISIBLE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bg == null || bg.isRecycled()) return;

        // 1) Encajar la imagen manteniendo aspecto y centrando
        float vw = getWidth(), vh = getHeight();
        float bw = bg.getWidth(), bh = bg.getHeight();
        if (vw <= 0 || vh <= 0 || bw <= 0 || bh <= 0) return;

        float scale = Math.min(vw / bw, vh / bh); // "fit inside"
        float dw = bw * scale, dh = bh * scale;
        float left = (vw - dw) / 2f, top = (vh - dh) / 2f;
        dst.set(left, top, left + dw, top + dh);

        // 2) Barra de relleno (debajo del marco)
        float barLeft   = dst.left + BAR_LEFT_PCT * dst.width();
        float barRightM = dst.left + BAR_RIGHT_PCT * dst.width();
        float barTop    = dst.top  + BAR_TOP_PCT  * dst.height();
        float barBot    = barTop + BAR_HEIGHT_PCT * dst.height();

        float barRight = barLeft + (barRightM - barLeft) * progress;
        barRect.set(barLeft, barTop, Math.max(barLeft, barRight), barBot);
        canvas.drawRoundRect(barRect, barRadiusPx, barRadiusPx, fillPaint);

        // 3) Marco PNG encima
        canvas.drawBitmap(bg, null, dst, null);

        // 4) Porcentaje "NN%" CENTRADO en la barra
        int percent = Math.round(progress * 100f);
        // Tamaño del texto proporcional a la altura de la barra
        float textSize = barRect.height() * 0.6f;
        textPaint.setTextSize(textSize);
        // Centrado vertical correcto (baseline)
        float textCenterY = barRect.centerY() - (textPaint.descent() + textPaint.ascent()) / 2f;
        canvas.drawText(percent + "%", barRect.centerX(), textCenterY, textPaint);
    }
}

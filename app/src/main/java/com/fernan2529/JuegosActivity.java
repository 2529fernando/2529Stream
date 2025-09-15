package com.fernan2529;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class JuegosActivity extends AppCompatActivity {

    private TetrisView tetrisView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fullscreen inmersivo
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat insets =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insets.hide(WindowInsetsCompat.Type.systemBars());
        insets.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_juegos);
        FrameLayout container = findViewById(R.id.game_container);
        tetrisView = new TetrisView(this);
        container.addView(tetrisView);
    }

    @Override public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            WindowInsetsControllerCompat insets =
                    new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
            insets.hide(WindowInsetsCompat.Type.systemBars());
            insets.setSystemBarsBehavior(
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
    }

    @Override protected void onResume() { super.onResume(); if (tetrisView!=null) tetrisView.resumeGame(); }
    @Override protected void onPause()  { super.onPause();  if (tetrisView!=null) tetrisView.pauseGame(); }

    /** Tetris con filas dinámicas (-2), niveles por puntaje, preview, y TOP 5 con nombres. */
    private static class TetrisView extends View {
        // Dimensiones lógicas
        private static final int COLS = 10;        // columnas fijas
        private static final int PREVIEW_COLS = 4; // ancho "deseado" del panel
        private static final int SWIPE_THRESHOLD = 40;

        // Filas dinámicas
        private int rows = 20;           // se recalcula
        private int[][] board;           // [rows][COLS]

        // Geometría (floats)
        private float cell;
        private float boardW, boardH;
        private float offsetX, offsetY;
        private float previewX, previewW;

        // Juego / velocidad
        private int speedMs = 550;
        private boolean gameOver = false;
        private int score = 0, highScore = 0;

        // >>> NIVELES <<<
        private int level = 1;              // nivel actual
        private int levelStep = 500;        // puntos por nivel
        private int nextLevelScore = 500;   // siguiente umbral

        // Piezas
        private static final int[][][] SHAPES = {
                { {0,1, 1,1, 2,1, 3,1}, {2,0, 2,1, 2,2, 2,3}, {0,2, 1,2, 2,2, 3,2}, {1,0, 1,1, 1,2, 1,3} }, // I
                { {0,0, 0,1, 1,1, 2,1}, {1,0, 2,0, 1,1, 1,2}, {0,1, 1,1, 2,1, 2,2}, {1,0, 1,1, 0,2, 1,2} }, // J
                { {2,0, 0,1, 1,1, 2,1}, {1,0, 1,1, 1,2, 2,2}, {0,1, 1,1, 2,1, 0,2}, {0,0, 1,0, 1,1, 1,2} }, // L
                { {1,0, 2,0, 1,1, 2,1}, {1,0, 2,0, 1,1, 2,1}, {1,0, 2,0, 1,1, 2,1}, {1,0, 2,0, 1,1, 2,1} }, // O
                { {1,0, 2,0, 0,1, 1,1}, {1,0, 1,1, 2,1, 2,2}, {1,1, 2,1, 0,2, 1,2}, {0,0, 0,1, 1,1, 1,2} }, // S
                { {1,0, 0,1, 1,1, 2,1}, {1,0, 1,1, 2,1, 1,2}, {0,1, 1,1, 2,1, 1,2}, {1,0, 0,1, 1,1, 1,2} }, // T
                { {0,0, 1,0, 1,1, 2,1}, {2,0, 1,1, 2,1, 1,2}, {0,1, 1,1, 1,2, 2,2}, {1,0, 0,1, 1,1, 0,2} }, // Z
        };
        private static final int[] COLORS = {
                Color.CYAN, Color.rgb(0,102,204), Color.rgb(255,153,0),
                Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.RED
        };
        private int curType, nextType, rotation, curX, curY;
        private final Random rng = new Random();

        // Persistencia
        private static final String PREFS = "tetris_prefs";
        private static final String KEY_HIGH = "high_score";
        private static final String KEY_PLAYER_NAME = "player_name";
        private static final String KEY_LB = "leaderboard_json"; // JSON con top 5

        // Leaderboard en memoria
        private static class Entry {
            String name; int sc;
            Entry(String n, int s){ name=n; sc=s; }
        }
        private final ArrayList<Entry> leaderboard = new ArrayList<>();

        // Pinturas
        private final Paint pCell = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint pGrid = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint pText = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint pPanelBg = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint pPanelBorder = new Paint(Paint.ANTI_ALIAS_FLAG);

        // Gestos
        private float touchStartX, touchStartY; private long touchStartT;

        // Loop
        private final Runnable tick = new Runnable() {
            @Override public void run() {
                if (!gameOver) {
                    step(); invalidate();
                    postDelayed(this, speedMs);
                }
            }
        };

        public TetrisView(Context ctx) {
            super(ctx);
            setFocusable(true); setFocusableInTouchMode(true);

            pGrid.setColor(Color.argb(60, 255,255,255)); pGrid.setStrokeWidth(2f);
            pText.setColor(Color.WHITE); pText.setTextSize(48f);
            pPanelBg.setColor(Color.argb(24, 255,255,255));
            pPanelBorder.setColor(Color.argb(120, 255,255,255));
            pPanelBorder.setStyle(Paint.Style.STROKE); pPanelBorder.setStrokeWidth(3f);

            // Cargar prefs
            highScore = getContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .getInt(KEY_HIGH, 0);
            loadLeaderboard(); // carga top 5 si existe

            nextType = rng.nextInt(7); // se usa en el primer spawn
        }

        // --- Ajuste de tamaño: más filas para llenar pantalla y -2 filas ---
        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            // celda por ancho total (tablero + panel mínimo)
            cell = (float) w / (COLS + PREVIEW_COLS);

            // filas que llenan la altura, quitando 2
            rows = (int) Math.ceil(h / cell) - 1;
            if (rows < 20) rows = 20;

            boardW = cell * COLS;
            boardH = cell * rows;
            previewW = w - boardW;
            previewX = boardW;

            offsetX = 0f; offsetY = 0f;

            board = new int[rows][COLS];

            // reset de partida
            score = 0; speedMs = 550; gameOver = false;
            level = 1; levelStep = 500; nextLevelScore = levelStep;

            rotation = 0; curX = 3; curY = -2;
            curType = nextType; nextType = rng.nextInt(7);

            removeCallbacks(tick);
            postDelayed(tick, speedMs);
        }

        // --- Niveles por puntaje ---
        private void maybeLevelUp() {
            boolean leveled = false;
            while (score >= nextLevelScore) {
                level++;
                nextLevelScore += levelStep;
                speedMs = Math.max(80, speedMs - 50); // acelera por nivel
                leveled = true;
            }
            if (leveled && !gameOver) {
                removeCallbacks(tick);
                postDelayed(tick, speedMs);
            }
        }

        // --- Lógica base ---
        private boolean collides(int nx, int ny, int type, int rot) {
            int[] s = SHAPES[type][rot];
            for (int i = 0; i < s.length; i += 2) {
                int x = nx + s[i], y = ny + s[i + 1];
                if (x < 0 || x >= COLS || y >= rows) return true;
                if (y >= 0 && board[y][x] != 0) return true;
            }
            return false;
        }

        private void spawnNewPiece() {
            curType = nextType; nextType = rng.nextInt(7);
            rotation = 0; curX = 3; curY = -2;
            if (collides(curX, curY, curType, rotation)) triggerGameOver();
        }

        private void lockPiece() {
            int[] s = SHAPES[curType][rotation];
            boolean above = false;
            for (int i = 0; i < s.length; i += 2) {
                int x = curX + s[i], y = curY + s[i + 1];
                if (y < 0) { above = true; continue; }
                board[y][x] = curType + 1;
            }
            if (above) { triggerGameOver(); return; }
            clearLines();
            spawnNewPiece();
        }

        private void triggerGameOver() {
            gameOver = true;

            // High score global
            if (score > highScore) {
                highScore = score;
                getContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                        .edit().putInt(KEY_HIGH, highScore).apply();
            }

            // Si califica al top-5, pedir nombre y guardar
            if (qualifiesForLeaderboard(score)) {
                post(this::promptForNameAndSave); // asegurar UI thread
            }

            removeCallbacks(tick); invalidate();
        }

        private void clearLines() {
            int lines = 0;
            for (int r = rows - 1; r >= 0; r--) {
                boolean full = true;
                for (int c = 0; c < COLS; c++) if (board[r][c] == 0) { full = false; break; }
                if (full) {
                    lines++;
                    for (int rr = r; rr > 0; rr--) System.arraycopy(board[rr - 1], 0, board[rr], 0, COLS);
                    for (int c = 0; c < COLS; c++) board[0][c] = 0;
                    r++;
                }
            }
            if (lines > 0) {
                int add = (lines == 1) ? 100 : (lines == 2) ? 300 : (lines == 3) ? 500 : 800;
                score += add;
                maybeLevelUp();
            }
        }

        private void step() { if (!tryMove(curX, curY + 1, rotation)) lockPiece(); }

        private boolean tryMove(int nx, int ny, int rot) {
            if (collides(nx, ny, curType, rot)) return false;
            curX = nx; curY = ny; rotation = rot; return true;
        }

        private void rotate() {
            int nr = (rotation + 1) & 3;
            if (tryMove(curX, curY, nr)) return;
            if (tryMove(curX - 1, curY, nr)) return;
            if (tryMove(curX + 1, curY, nr)) return;
        }

        private void hardDrop() { while (tryMove(curX, curY + 1, rotation)) {} lockPiece(); invalidate(); }

        // --- Leaderboard (Top 5) ---
        private boolean qualifiesForLeaderboard(int sc) {
            if (sc <= 0) return false;
            if (leaderboard.size() < 5) return true;
            int min = leaderboard.get(leaderboard.size()-1).sc;
            return sc > min;
        }

        private void promptForNameAndSave() {
            final Context ctx = getContext();
            final EditText input = new EditText(ctx);
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

            // Sugerir el último nombre usado
            String lastName = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .getString(KEY_PLAYER_NAME, "");
            input.setText(lastName);

            new AlertDialog.Builder(ctx)
                    .setTitle("Nuevo récord")
                    .setMessage("¡Entraste al Top 5! Escribe tu nombre:")
                    .setView(input)
                    .setPositiveButton("Guardar", (d, w) -> {
                        String name = input.getText().toString().trim();
                        if (name.isEmpty()) name = "Jugador";
                        // Guardar última elección
                        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                                .edit().putString(KEY_PLAYER_NAME, name).apply();
                        // Insertar en tabla y persistir
                        addToLeaderboard(name, score);
                        saveLeaderboard();
                        invalidate();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        }

        private void addToLeaderboard(String name, int sc) {
            leaderboard.add(new Entry(name, sc));
            // Ordenar desc por puntaje
            Collections.sort(leaderboard, new Comparator<Entry>() {
                @Override public int compare(Entry a, Entry b) { return Integer.compare(b.sc, a.sc); }
            });
            // Limitar a 5
            while (leaderboard.size() > 5) leaderboard.remove(leaderboard.size()-1);
        }

        private void loadLeaderboard() {
            leaderboard.clear();
            String json = getContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .getString(KEY_LB, "");
            if (json == null || json.isEmpty()) return;
            try {
                JSONArray arr = new JSONArray(json);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    leaderboard.add(new Entry(o.optString("name","Jugador"), o.optInt("score",0)));
                }
                // asegurar orden
                Collections.sort(leaderboard, (a,b)->Integer.compare(b.sc, a.sc));
                if (leaderboard.size() > 5) {
                    while (leaderboard.size() > 5) leaderboard.remove(leaderboard.size()-1);
                }
            } catch (JSONException ignored) {}
        }

        private void saveLeaderboard() {
            JSONArray arr = new JSONArray();
            try {
                for (Entry e : leaderboard) {
                    JSONObject o = new JSONObject();
                    o.put("name", e.name);
                    o.put("score", e.sc);
                    arr.put(o);
                }
            } catch (JSONException ignored) {}
            getContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .edit().putString(KEY_LB, arr.toString()).apply();
        }

        // --- Dibujo ---
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawColor(Color.BLACK);

            // celdas fijas
            for (int r = 0; r < rows; r++) for (int c = 0; c < COLS; c++) {
                if (board[r][c] != 0) { pCell.setColor(COLORS[board[r][c] - 1]); drawCell(canvas, c, r, pCell); }
            }

            // pieza actual
            if (!gameOver) {
                int[] s = SHAPES[curType][rotation];
                pCell.setColor(COLORS[curType]);
                for (int i = 0; i < s.length; i += 2) {
                    int x = curX + s[i], y = curY + s[i + 1];
                    if (y >= 0) drawCell(canvas, x, y, pCell);
                }
            }

            // rejilla
            for (int c = 0; c <= COLS; c++) {
                float x = offsetX + c * cell;
                canvas.drawLine(x, offsetY, x, offsetY + boardH, pGrid);
            }
            for (int r = 0; r <= rows; r++) {
                float y = offsetY + r * cell;
                canvas.drawLine(offsetX, y, offsetX + boardW, y, pGrid);
            }

            // panel preview (con NIVEL debajo y luego el TOP 5)
            drawPreviewPanel(canvas);

            // textos superiores
            pText.setTextSize(Math.max(36f, cell * 0.7f));
            canvas.drawText("Puntos: " + score, offsetX, offsetY + pText.getTextSize(), pText);
            String best = "Mejor: " + highScore;
            canvas.drawText(best, offsetX + boardW - pText.measureText(best),
                    offsetY + pText.getTextSize(), pText);

            // NIVEL centrado arriba del tablero
            String lvlTop = "Nivel: " + level;
            float lvlX = offsetX + (boardW - pText.measureText(lvlTop)) / 2f;
            canvas.drawText(lvlTop, lvlX, offsetY + pText.getTextSize(), pText);

            if (gameOver) {
                pText.setTextSize(Math.max(42f, cell));
                String go1 = "GAME OVER";
                String go2 = "Puntos: " + score + "   Mejor: " + highScore;
                String go3 = "Nivel alcanzado: " + level;
                String go4 = "Toca para reiniciar";
                float y = offsetY + boardH / 2f;
                float x1 = (getWidth() - pText.measureText(go1)) / 2f;
                float x2 = (getWidth() - pText.measureText(go2)) / 2f;
                float x3 = (getWidth() - pText.measureText(go3)) / 2f;
                float x4 = (getWidth() - pText.measureText(go4)) / 2f;
                canvas.drawText(go1, x1, y - pText.getTextSize(), pText);
                canvas.drawText(go2, x2, y + 4, pText);
                canvas.drawText(go3, x3, y + pText.getTextSize() + 8, pText);
                canvas.drawText(go4, x4, y + pText.getTextSize()*2 + 14, pText);
            }
        }

        private void drawPreviewPanel(Canvas canvas) {
            float pad = Math.max(6f, cell * 0.35f);
            float left = previewX + pad * 0.5f, top = offsetY;
            float right = previewX + previewW - pad * 0.5f, bottom = offsetY + boardH;

            // fondo y borde
            canvas.drawRect(left, top, right, bottom, pPanelBg);
            canvas.drawRect(left, top, right, bottom, pPanelBorder);

            // título
            pText.setTextSize(Math.max(28f, cell * 0.6f));
            String title = "Siguiente";
            canvas.drawText(title, left + (right - left - pText.measureText(title)) / 2f,
                    top + pText.getTextSize() + pad * 0.5f, pText);

            // caja de preview
            float boxTop = top + pText.getTextSize() + pad * 1.2f;
            float boxH = Math.min(boardH * 0.35f, cell * 7f);
            float boxBottom = Math.min(bottom - pad, boxTop + boxH);
            float boxLeft = left + pad, boxRight = right - pad;
            canvas.drawRect(boxLeft, boxTop, boxRight, boxBottom, pPanelBorder);

            // próxima pieza
            int[] shape = SHAPES[nextType][0];
            int minX = 99, maxX = -99, minY = 99, maxY = -99;
            for (int i = 0; i < shape.length; i += 2) {
                minX = Math.min(minX, shape[i]); maxX = Math.max(maxX, shape[i]);
                minY = Math.min(minY, shape[i + 1]); maxY = Math.max(maxY, shape[i + 1]);
            }
            int wBlocks = (maxX - minX + 1), hBlocks = (maxY - minY + 1);
            float cellPrev = Math.min((boxRight - boxLeft) / (wBlocks + 1f),
                    (boxBottom - boxTop) / (hBlocks + 1f));
            float startX = boxLeft + ((boxRight - boxLeft) - (wBlocks * cellPrev)) / 2f - (minX * cellPrev);
            float startY = boxTop  + ((boxBottom - boxTop) - (hBlocks * cellPrev)) / 2f - (minY * cellPrev);

            pCell.setColor(COLORS[nextType]);
            for (int i = 0; i < shape.length; i += 2) {
                float x = startX + shape[i] * cellPrev;
                float y = startY + shape[i + 1] * cellPrev;
                canvas.drawRect(x + 1, y + 1, x + cellPrev - 1, y + cellPrev - 1, pCell);
                canvas.drawRect(x, y, x + cellPrev, y + cellPrev, pPanelBorder);
            }

            // NIVEL debajo de la caja
            String lvlText = "Nivel: " + level;
            float lvlY = boxBottom + pText.getTextSize() + pad * 0.8f;
            float lvlX = left + (right - left - pText.measureText(lvlText)) / 2f;
            canvas.drawText(lvlText, lvlX, lvlY, pText);

            // --- TOP 5 debajo del nivel ---
            float listY = lvlY + pText.getTextSize() + pad * 0.6f;
            pText.setTextSize(Math.max(22f, cell * 0.5f));
            float lineH = pText.getTextSize() + Math.max(4f, cell * 0.1f);

            for (int i = 0; i < Math.min(5, leaderboard.size()); i++) {
                Entry e = leaderboard.get(i);
                String line = "#" + (i+1) + " " + e.name + " — " + e.sc;
                float tx = left + (right - left - pText.measureText(line)) / 2f;
                if (listY + lineH < bottom - pad) {
                    canvas.drawText(line, tx, listY, pText);
                    listY += lineH;
                } else {
                    break; // no hay espacio
                }
            }
        }

        private void drawCell(Canvas canvas, int cx, int cy, Paint paint) {
            float x = offsetX + cx * cell, y = offsetY + cy * cell;
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(x + 1, y + 1, x + cell - 1, y + cell - 1, paint);
            canvas.drawRect(x, y, x + cell, y + cell, pGrid);
        }

        // Gestos
        @Override
        public boolean onTouchEvent(MotionEvent e) {
            if (gameOver && e.getAction() == MotionEvent.ACTION_DOWN) { resetGame(); return true; }
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchStartX = e.getX(); touchStartY = e.getY(); touchStartT = System.currentTimeMillis(); return true;
                case MotionEvent.ACTION_UP:
                    float dx = e.getX() - touchStartX, dy = e.getY() - touchStartY;
                    long dt = System.currentTimeMillis() - touchStartT;
                    if (Math.abs(dx) < SWIPE_THRESHOLD && Math.abs(dy) < SWIPE_THRESHOLD && dt < 250) { rotate(); invalidate(); return true; }
                    if (Math.abs(dx) > Math.abs(dy)) {
                        if (dx > SWIPE_THRESHOLD) tryMove(curX + 1, curY, rotation);
                        else if (dx < -SWIPE_THRESHOLD) tryMove(curX - 1, curY, rotation);
                    } else if (dy > SWIPE_THRESHOLD) {
                        hardDrop();
                    }
                    invalidate(); return true;
            }
            return super.onTouchEvent(e);
        }

        private void resetGame() {
            for (int r = 0; r < rows; r++) for (int c = 0; c < COLS; c++) board[r][c] = 0;
            score = 0; speedMs = 550; gameOver = false;
            level = 1; levelStep = 500; nextLevelScore = levelStep;
            nextType = rng.nextInt(7); spawnNewPiece();
            removeCallbacks(tick); postDelayed(tick, speedMs); invalidate();
        }

        public void pauseGame() { removeCallbacks(tick); }
        public void resumeGame() { if (!gameOver) { removeCallbacks(tick); postDelayed(tick, speedMs); } }

        @Override protected void onDetachedFromWindow() { super.onDetachedFromWindow(); removeCallbacks(tick); }
    }
}

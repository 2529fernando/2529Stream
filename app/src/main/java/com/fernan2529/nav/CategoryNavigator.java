package com.fernan2529.nav;

import android.content.Context;
import android.content.Intent;

public final class CategoryNavigator {

    private CategoryNavigator() {}

    /** Devuelve el Intent a abrir según el índice del spinner. */
    public static Intent buildIntent(Context ctx, int idx) {
        switch (idx) {
            case 1:  return new Intent(ctx, com.fernan2529.Categorias.Entretenimiento.class);
            case 2:  return new Intent(ctx, com.fernan2529.Categorias.Peliculas.class);
            case 3:  return new Intent(ctx, com.fernan2529.Categorias.Serie.class);
            case 4:  return new Intent(ctx, com.fernan2529.Categorias.Animaciones.class);
            case 5:  return new Intent(ctx, com.fernan2529.Categorias.Dorama.class);
            case 6:  return new Intent(ctx, com.fernan2529.Categorias.Novelas.class);
            case 7:  return new Intent(ctx, com.fernan2529.Categorias.Deportes.class);
            case 8:  return new Intent(ctx, com.fernan2529.Categorias.Infantiles.class);
            case 9:  return new Intent(ctx, com.fernan2529.Categorias.Comedia.class);
            case 10: return new Intent(ctx, com.fernan2529.Categorias.Historia.class);
            case 11: return new Intent(ctx, com.fernan2529.Categorias.Hogar.class);
            case 12: return new Intent(ctx, com.fernan2529.Categorias.Musica.class);
            case 13: return new Intent(ctx, com.fernan2529.Categorias.Noticias.class);
            default: return null; // 0 u otros: no navega
        }
    }

    /** Lanza la navegación si existe destino. */
    public static void navigate(Context ctx, int idx) {
        Intent intent = buildIntent(ctx, idx);
        if (intent != null) {
            ctx.startActivity(intent);
        }
    }
}

package com.fernan2529.data;

public class CategoriesRepository {
    // Fuente de datos única
    private static final String[] CATEGORIES = {
            "-",
            "Entretenimiento",
            "Peliculas",
            "Series",
            "Anime",
            "Dorama",
            "Novelas",
            "Deportes",
            "Infantiles",
            "Comedia",
            "Historia",
            "Hogar",
            "Musica",
            "Ecuador",
            "Noticias"
    };

    // Devuelve el arreglo
    public String[] getCategories() {
        return CATEGORIES;
    }
}

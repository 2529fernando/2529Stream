package com.fernan2529.data;

public class CategoriesRepository {
    // Fuente de datos única
    private static final String[] CATEGORIES = {
            "Seleccione la Categoria",
            "Entretenimiento",
            "Peliculas",
            "Series",
            "Animaciones",
            "Dorama",
            "Novelas",
            "Deportes",
            "Infantiles",
            "Comedia",
            "Historia",
            "Hogar",
            "Musica",
            "Noticias"
    };

    // Devuelve el arreglo
    public String[] getCategories() {
        return CATEGORIES;
    }
}

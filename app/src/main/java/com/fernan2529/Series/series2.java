package com.fernan2529.Series;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.fernan2529.R;
import com.fernan2529.WebViewActivities.WebViewActivityGeneral;

import java.util.Random;

public class series2 extends AppCompatActivity {

    private Spinner spinnerVideos;
    private ArrayAdapter<String> videosAdapter;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series2);

        spinnerVideos = findViewById(R.id.spinner_videos);
        Button playRandomButton = findViewById(R.id.aleatorio);

        // Nombres visibles en el Spinner
        final String[] videoNames = {
                "Seleccione el Capitulo",
                "T1-01 - Piloto espacial 3000",
                "T1-02 - La serie ha aterrizado",
                "T1-03 - Yo, compañero",
                "T1-04 - Obras de amor en el espacio",
                "T1-05 - Temores de un planeta robot",
                "T1-06 - Unos valiosos pececitos",
                "T1-07 - Mis tres soles",
                "T1-08 - Un enorme montón de basura",
                "T1-09 - El infierno está en los demás robots",
                "T1-10 – Un vuelo inolvidable",
                "T1-11 – Universidad de Marte",
                "T1-12 – Cuando los extraterrestres atacan",
                "T1-13 – Fry y la fábrica de Slurm",

                "T2-01 - Apoyo esa emoción.",
                "T2-02 - Brannigan vuelve a empezar.",
                "T2-03 - A la cabeza de las elecciones.",
                "T2-04 - Cuentos de navidad.",
                "T2-05 - ¿Por qué debo ser un crustáceo enamorado?",
                "T2-06 - Pon la cabeza sobre mis hombros.",
                "T2-07 - El menor de dos malos.",
                "T2-08 - Bender salvaje.",
                "T2-09 - Bicíclope para dos.",
                "T2-10 - Cómo Hermes requisó su ilusión.",
                "T2-11 - Un clon propio.",
                "T2-12 - El sur profundo.",
                "T2-13 - Bender ingresa en la mafia.",
                "T2-14 - Mi primer problema con los Poppler.",
                "T2-15 - El día de la madre.",
                "T2-16 - Antología del interés I.",
                "T2-17 – La guerra es la guerra",
                "T2-18 – El aullido",
                "T2-19 – La mujer criónica",

                "T3-01 – Mujeres amazonas enamoradas",
                "T3-02 – Parásitos perdidos",
                "T3-03 – Un cuento de dos Santa Claus",
                "T3-04 – La suerte del Fryrish",
                "T3-05 – El avebot de Ice-Catraz",
                "T3-06 – Amor sin dobleces",
                "T3-07 – El día que la Tierra resistió estúpida",
                "T3-08 – ¡Eso es langostertainment!",
                "T3-09 – Las reglas de la ciber-casa",
                "T3-10 – Donde pastan los buggalo",
                "T3-11 – Loco en el manicomio",
                "T3-12 – La ruta de todo mal",
                "T3-13 – Bendin’ en el viento",
                "T3-14 – El tiempo sigue resbalando",
                "T3-15 – Salí con un robot",
                "T3-16 – Una Leela propia",
                "T3-17 – Un faraón para recordar",
                "T3-18 – Antología del interés II",
                "T3-19 – Roswell termina bien",
                "T3-20 – Dios mío",
                "T3-21 – Acciones futuras",
                "T3-22 – El chef de hierro al 30%",

                "T4-01 – Kif se embaraza",
                "T4-02 – El mundo natal de Leela",
                "T4-03 – Amor y cohete",
                "T4-04 – Menos que un héroe",
                "T4-05 – Un sabor de libertad",
                "T4-06 – Bender no debería estar en la televisión",
                "T4-07 – Ladrido jurásico",
                "T4-08 – Crímenes del calor",
                "T4-09 – Las barreras mutantes de Leela",
                "T4-10 – El porqué de Fry",
                "T4-11 – Donde ningún fan ha ido antes",
                "T4-12 – La picadura",
                "T4-13 – Dóblala",
                "T4-14 – Obsoletamente fabuloso",
                "T4-15 – La parabox de Farnsworth",
                "T4-16 – Trescientos chicos grandes",
                "T4-17 – El codiciado Fry",
                "T4-18 – Las manos del diablo son juguetes ociosos",

                "T5y6-01 – El gran golpe de bender",
                "T5y6-02 – La bestia con billones de brazos",
                "T5y6-03 – El juego de Bender",
                "T5y6-04 – En el lejano y salvoje verde",

                "T7-01 – Renacimiento",
                "T7-02 – En el jardín de Leela",
                "T7-03 – El ataque de la aplicación asesina",
                "T7-04 – Proposición infinito",
                "T7-05 – El código de Duh-Vinci",
                "T7-06 – Inspección letal",
                "T7-07 – El difunto Philip J. Fry",
                "T7-08 – ¡Esos malditos Katz!",
                "T7-09 – Un origen mecánico",
                "T7-10 – El prisionero de Benda",
                "T7-11 – Ndndiferencias Lrreconciliables",
                "T7-12 – Los mutantes se rebelan",
                "T7-13 – Espectacular de Navidad",


                "T8-01 – Benderama",
                "T8-02 – Catrasutopía",
                "T8-03 – Fantasma en las máquinas",
                "T8-04 – Ley y oráculo",
                "T8-05 – El silencio de las pinzas",
                "T8-06 – Yo Leela Leela",
                "T8-07 – Todas las cabezas de los presidentes",
                "T8-08 – Möbius Dick",
                "T8-09 – Yo soy Fry el huevero",
                "T8-10 – La punta del Zoiberg",
                "T8-11 – Guerreros contra catarro",
                "T8-12 – Sobrecalentamiento",
                "T8-13 – Reencarnacion",

                "T9-01 – Los robots y la cigüeña",
                "T9-02 – A brazo partido",
                "T9-03 – Decisión 3012",
                "T9-04 – El ladrón de Bolsafaz",
                "T9-05 – Zapp el idiota",
                "T9-06 – El efecto mariborla",
                "T9-07 – El tío de los seis millones de dólares",
                "T9-08 – La monda con pan",
                "T9-09 – Libre albedrío, tío",
                "T9-10 – Deseos de casi morir",
                "T9-11 – Viva Las Vegas de Marte",
                "T9-12 – 31st Century Fox",
                "T9-13 – Naturama",

                "T10-01 – Pavimento en 2D",
                "T10-02 – El gran romance de Fry y Leela",
                "T10-03 – T.: El terrícola",
                "T10-04 – 40 % folk",
                "T10-05 – La antorcha inhumana",
                "T10-06 – Pozo de diversión del sábado por la mañana",
                "T10-07 – Calculón 2.0",
                "T10-08 – Culo, vuelve a casa",
                "T10-09 – Leela y la planta de los genes",
                "T10-10 – Juego de tonos",
                "T10-11 – Asesinato en el Planet Express",
                "T10-12 – Hedor y pestilencia",
                "T10-13 – Mientras tanto...",

                "T11-01 – El maratón de lo imposible",
                "T11-02 – Hijos de la ciénaga",
                "T11-03 – Así que el oeste era 1010001",
                "T11-04 – El retorno de los parásitos",
                "T11-05 – Productos relacionados con este producto",
                "T11-06 – Sé lo que hiciste las próximas Navidades",
                "T11-07 – Ira contra la vacuna",
                "T11-08 – Zapp es cancelado",
                "T11-09 – El príncipe y el producto",
                "T11-10 – Hasta abajo",

                "T12-01 – El único amigo",
                "T12-02 – El juego del almejar",
                "T12-03 – El puesto temporal",
                "T12-04 – La bella y el bicho",
                "T12-05 – Uno es silic y otro oro",
                "T12-06 – El ataque de los pantalones",
                "T12-07 – Planet Expresso",
                "T12-08 – Exceso de ricura",
                "T12-09 – La libroteca misteriosa de Futurama",
                "T12-10 – Al contrario",

                "T13-01 – Destruir monstruos altos",
                "T13-02 – El mundo ya está bastante caliente",
                "T13-03 – Cincuenta sombras de verde",
                "T13-04 – La brecha numérica",
                "T13-05 – Con miedo y sin pantallas (Parte 1)",
                "T13-06 – Con miedo y sin pantallas (Parte 2)",
                "T13-07 – Criminoni",
                "T13-08 – Salpicadura de cangrejo",
                "T13-09 – Problemas con las trufas",
                "T13-10 – El agujero blanco",



        };

        // URLs correspondientes (mismo índice que videoNames)
        final String[] videoUrls = {
                "",
                "https://www.lacartoons.com/serie/capitulo/19845?t=1",
                "https://www.lacartoons.com/serie/capitulo/19846?t=1",
                "https://www.lacartoons.com/serie/capitulo/19847?t=1",
                "https://www.lacartoons.com/serie/capitulo/19848?t=1",
                "https://www.lacartoons.com/serie/capitulo/19849?t=1",
                "https://www.lacartoons.com/serie/capitulo/19850?t=1",
                "https://www.lacartoons.com/serie/capitulo/19851?t=1",
                "https://www.lacartoons.com/serie/capitulo/19852?t=1",
                "https://www.lacartoons.com/serie/capitulo/19853?t=1",
                "https://www.lacartoons.com/serie/capitulo/19854?t=1",
                "https://www.lacartoons.com/serie/capitulo/19855?t=1",
                "https://www.lacartoons.com/serie/capitulo/19856?t=1",
                "https://www.lacartoons.com/serie/capitulo/19857?t=1",

                "https://www.lacartoons.com/serie/capitulo/19858?t=1",
                "https://www.lacartoons.com/serie/capitulo/19859?t=1",
                "https://www.lacartoons.com/serie/capitulo/19860?t=1",
                "https://www.lacartoons.com/serie/capitulo/19861?t=1",
                "https://www.lacartoons.com/serie/capitulo/19862?t=1",
                "https://www.lacartoons.com/serie/capitulo/19863?t=1",
                "https://www.lacartoons.com/serie/capitulo/19864?t=1",
                "https://www.lacartoons.com/serie/capitulo/19865?t=1",
                "https://www.lacartoons.com/serie/capitulo/19866?t=1",
                "https://www.lacartoons.com/serie/capitulo/19867?t=1",
                "https://www.lacartoons.com/serie/capitulo/19868?t=1",
                "https://www.lacartoons.com/serie/capitulo/19869?t=1",
                "https://www.lacartoons.com/serie/capitulo/19870?t=1",
                "https://www.lacartoons.com/serie/capitulo/19871?t=1",
                "https://www.lacartoons.com/serie/capitulo/19872?t=1",
                "https://www.lacartoons.com/serie/capitulo/19873?t=1",
                "https://www.lacartoons.com/serie/capitulo/19874?t=1",
                "https://www.lacartoons.com/serie/capitulo/19875?t=1",
                "https://www.lacartoons.com/serie/capitulo/19876?t=1",

                "https://www.lacartoons.com/serie/capitulo/19878?t=1",
                "https://www.lacartoons.com/serie/capitulo/19877?t=1",
                "https://www.lacartoons.com/serie/capitulo/19890?t=1",
                "https://www.lacartoons.com/serie/capitulo/19883?t=1",
                "https://www.lacartoons.com/serie/capitulo/19882?t=1",
                "https://www.lacartoons.com/serie/capitulo/19879?t=1",
                "https://www.lacartoons.com/serie/capitulo/19880?t=1",
                "https://www.lacartoons.com/serie/capitulo/19881?t=1",
                "https://www.lacartoons.com/serie/capitulo/19884?t=1",
                "https://www.lacartoons.com/serie/capitulo/19894?t=1",
                "https://www.lacartoons.com/serie/capitulo/19885?t=1",
                "https://www.lacartoons.com/serie/capitulo/19903?t=1",
                "https://www.lacartoons.com/serie/capitulo/19886?t=1",
                "https://www.lacartoons.com/serie/capitulo/19887?t=1",
                "https://www.lacartoons.com/serie/capitulo/19888?t=1",
                "https://www.lacartoons.com/serie/capitulo/19898?t=1",
                "https://www.lacartoons.com/serie/capitulo/19895?t=1",
                "https://www.lacartoons.com/serie/capitulo/19891?t=1",
                "https://www.lacartoons.com/serie/capitulo/19888?t=1",
                "https://www.lacartoons.com/serie/capitulo/19896?t=1",
                "https://www.lacartoons.com/serie/capitulo/19897?t=1",
                "https://www.lacartoons.com/serie/capitulo/19899?t=1",

                "https://www.lacartoons.com/serie/capitulo/19905?t=1",
                "https://www.lacartoons.com/serie/capitulo/19906?t=1",
                "https://www.lacartoons.com/serie/capitulo/19892?t=1",
                "https://www.lacartoons.com/serie/capitulo/19906?t=1",
                "https://www.lacartoons.com/serie/capitulo/19904?t=1",
                "https://www.lacartoons.com/serie/capitulo/19915?t=1",
                "https://www.lacartoons.com/serie/capitulo/19902?t=1",
                "https://www.lacartoons.com/serie/capitulo/19901?t=1",
                "https://www.lacartoons.com/serie/capitulo/19907?t=1",
                "https://www.lacartoons.com/serie/capitulo/19908?t=1",
                "https://www.lacartoons.com/serie/capitulo/19900?t=1",
                "https://www.lacartoons.com/serie/capitulo/19909?t=1",
                "https://www.lacartoons.com/serie/capitulo/19913?t=1",
                "https://www.lacartoons.com/serie/capitulo/19914?t=1",
                "https://www.lacartoons.com/serie/capitulo/19910?t=1",
                "https://www.lacartoons.com/serie/capitulo/19910?t=1",
                "https://www.lacartoons.com/serie/capitulo/19912?t=1",
                "https://www.lacartoons.com/serie/capitulo/19916?t=1",

                "https://www.verfuturamaonlinegratis.com/movies/ver-futurama-la-gran-pelicula-de-bender/",
                "https://www.verfuturamaonlinegratis.com/movies/ver-futurama-la-bestia-con-billones-de-brazos/",
                "https://www.verfuturamaonlinegratis.com/movies/ver-futurama-el-juego-de-bender/",
                "https://www.verfuturamaonlinegratis.com/movies/ver-futurama-en-el-lejano-y-salvaje-verde/",

                "https://entrepeliculasyseries.nz/episodios/futurama-1999-7x1/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-7x2/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-7x3/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-7x4/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-7x5/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-7x6/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-7x7/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-7x8/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-7x9/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-7x10/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-7x11/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-7x12/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-7x13/",

                "https://entrepeliculasyseries.nz/episodios/futurama-1999-8x1/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-8x2/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-8x3/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-8x4/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-8x5/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-8x6/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-8x7/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-8x8/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-8x9/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-8x10/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-8x11/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-8x12/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-8x13/",

                "https://entrepeliculasyseries.nz/episodios/futurama-1999-9x1/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-9x2/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-9x3/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-9x4/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-9x5/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-9x6/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-9x7/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-9x8/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-9x9/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-9x10/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-9x11/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-9x12/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-9x13/",

                "https://entrepeliculasyseries.nz/episodios/futurama-1999-10x1/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-10x2/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-10x3/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-10x4/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-10x5/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-10x6/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-10x7/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-10x8/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-10x9/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-10x10/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-10x11/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-10x12/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-10x13/",

                "https://entrepeliculasyseries.nz/episodios/futurama-1999-11x1/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-11x2/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-11x3/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-11x4/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-11x5/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-11x6/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-11x7/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-11x8/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-11x9/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-11x10/",

                "https://entrepeliculasyseries.nz/episodios/futurama-1999-12x1/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-12x2/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-12x3/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-12x4/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-12x5/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-12x6/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-12x7/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-12x8/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-12x9/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-12x10/",

                "https://entrepeliculasyseries.nz/episodios/futurama-1999-13x1/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-13x2/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-13x3/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-13x4/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-13x5/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-13x6/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-13x7/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-13x8/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-13x9/",
                "https://entrepeliculasyseries.nz/episodios/futurama-1999-13x10/",


        };

        // Adapter para el spinner
        videosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, videoNames);
        videosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVideos.setAdapter(videosAdapter);

        // Random
        random = new Random();

        // Selección desde el Spinner (evita el primer ítem "Seleccione...")
        spinnerVideos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && position < videoUrls.length) {
                    String selectedVideoUrl = videoUrls[position];
                    if (selectedVideoUrl != null && !selectedVideoUrl.isEmpty()) {
                        openWeb(selectedVideoUrl);
                    }
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { /* no-op */ }
        });

        // Botón Aleatorio (evita índice 0)
        playRandomButton.setOnClickListener(v -> {
            int randomIndex = random.nextInt(videoUrls.length - 1) + 1;
            String randomVideoUrl = videoUrls[randomIndex];
            if (randomVideoUrl != null && !randomVideoUrl.isEmpty()) {
                openWeb(randomVideoUrl);
            }
        });
    }

    // Abre SIEMPRE WebViewActivityGeneral con la clave "url"
    private void openWeb(String url) {
        Intent intent = new Intent(series2.this, WebViewActivityGeneral.class);
        intent.putExtra("url", url); // <- clave que usa tu WebViewActivityGeneral
        startActivity(intent);
    }
}

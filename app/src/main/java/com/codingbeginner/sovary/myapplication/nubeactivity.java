package com.codingbeginner.sovary.myapplication;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Arrays;
import java.util.List;

public class nubeactivity extends AppCompatActivity {

    Button openWebButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPin(); // Esto pedirá el PIN antes de cargar el contenido
    }

    private void requestPin() {
        final String correctPin = "7634"; // PIN de 4 dígitos
        final EditText pinInput = new EditText(this);
        pinInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        pinInput.setHint("Ingresa el PIN");

        new AlertDialog.Builder(this)
                .setTitle("Protección con PIN")
                .setMessage("Introduce el PIN de 4 dígitos para acceder")
                .setCancelable(false)
                .setView(pinInput)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    String enteredPin = pinInput.getText().toString();
                    if (enteredPin.equals(correctPin)) {
                        setContentView(R.layout.activity_nubeactivity);
                        iniciarContenido(); // Solo se ejecuta si el PIN es correcto
                    } else {
                        Toast.makeText(this, "PIN incorrecto", Toast.LENGTH_SHORT).show();
                        finish(); // Cierra la actividad si es incorrecto
                    }
                })
                .show();
    }

    private void iniciarContenido() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        openWebButton = findViewById(R.id.openWebButton); // Inicializa el botón

        openWebButton.setOnClickListener(view -> {
            Intent intent = new Intent(nubeactivity.this, MainActivityfin.class);
            intent.putExtra("url", "https://drive.google.com/drive/folders/1ZxTMneku1OYgW3y4P3hRLRx7ABxtapGb");
            startActivity(intent);
        });

        List<String> imageUrls = getImageUrls();
        List<String> videoUrls = getVideoUrls();
        List<String> titles = getTitles();

        int minSize = Math.min(imageUrls.size(), Math.min(videoUrls.size(), titles.size()));
        recyclerView.setAdapter(new ImageAdapter(this,
                imageUrls.subList(0, minSize),
                videoUrls.subList(0, minSize),
                titles.subList(0, minSize)));
    }



    private List<String> getImageUrls() {
        return Arrays.asList(
                "android.resource://" + getPackageName() + "/" + R.drawable.akira,
                "android.resource://" + getPackageName() + "/" + R.drawable.domestic,
                "android.resource://" + getPackageName() + "/" + R.drawable.ranma,
                "android.resource://" + getPackageName() + "/" + R.drawable.school,
                "android.resource://" + getPackageName() + "/" + R.drawable.schooldos,
                "android.resource://" + getPackageName() + "/" + R.drawable.schooltres,
                "android.resource://" + getPackageName() + "/" + R.drawable.theend,
                "android.resource://" + getPackageName() + "/" + R.drawable.zero,
                "android.resource://" + getPackageName() + "/" + R.drawable.yosuga,
                "android.resource://" + getPackageName() + "/" + R.drawable.boring,
                "android.resource://" + getPackageName() + "/" + R.drawable.dora,
                "android.resource://" + getPackageName() + "/" + R.drawable.futu,
                "android.resource://" + getPackageName() + "/" + R.drawable.mirai,
                "android.resource://" + getPackageName() + "/" + R.drawable.miraitres,
                "android.resource://" + getPackageName() + "/" + R.drawable.miraicinco,
                "android.resource://" + getPackageName() + "/" + R.drawable.mirainikki,
                "android.resource://" + getPackageName() + "/" + R.drawable.musho,
                "android.resource://" + getPackageName() + "/" + R.drawable.nose,
                "android.resource://" + getPackageName() + "/" + R.drawable.bgata
        );
    }

    private List<String> getVideoUrls() {
        return Arrays.asList(
                "https://archive.org/download/n-10_20250302/n%20%2816%29.mp4",
                "https://archive.org/download/n-10_20250302/n%20%281%29.mp4",
                "https://archive.org/download/n-10_20250302/n%20%2810%29.mp4",
                "https://archive.org/download/n-10_20250302/n%20%2811%29.mp4",
                "https://archive.org/download/n-10_20250302/n%20%2812%29.mp4",
                "https://archive.org/download/n-10_20250302/n%20%2813%29.mp4",
                "https://archive.org/download/n-10_20250302/n%20%2814%29.mp4",
                "https://archive.org/download/n-10_20250302/n%20%2815%29.mp4",
                "https://archive.org/download/n-10_20250302/n%20%2817%29.mp4",
                "https://archive.org/download/n-10_20250302/n%20%2818%29.mp4",
                "https://archive.org/download/n-10_20250302/n%20%282%29.mp4",
                "https://archive.org/download/n-10_20250302/n%20%283%29.mp4",
                "https://archive.org/download/n-10_20250302/n%20%284%29.mp4",
                "https://archive.org/download/n-10_20250302/n%20%285%29.mp4",
                "https://archive.org/download/n-10_20250302/n%20%286%29.mp4",
                "https://archive.org/download/n-10_20250302/n%20%287%29.mp4",
                "https://archive.org/download/n-10_20250302/n%20%288%29.mp4",
                "https://archive.org/download/n-10_20250302/n%20%289%29.mp4",
                "https://archive.org/download/04_20250505_20250505_1952/04.mp4"
        );
    }

    private List<String> getTitles() {
        return Arrays.asList(
                "05 - La obscuridad revelada",
                "Domestic girlfriend ep1",
                "Ranma ep123 una navidad sin ranma",
                "School Days - 01",
                "School Days - 04",
                "School Days - 11",
                "The End of the F---ing World",
                "Zero no Tsukaima 2 Opening 1   I Say Yes",
                "11 - Sentimos que estamos flotando",
                "bedroom - in my head",
                "Doraemon maquina de reseteo para una nueva vida",
                "Futurama mientras tanto",
                "Mirai Nikki - 02",
                "mirai nikki 3",
                "Mirai nikki ep11",
                "mirai nikki",
                "Mushoku Tensei - 02",
                "No se   Melody   VÍDEO ORIGINAL EN HD",
                "B gata H Kei 04"
        );
    }
}

class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private final Context context;
    private final List<String> imageUrls;
    private final List<String> videoUrls;
    private final List<String> titles;

    public ImageAdapter(Context context, List<String> imageUrls, List<String> videoUrls, List<String> titles) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.videoUrls = videoUrls;
        this.titles = titles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        String videoUrl = videoUrls.get(position);
        String title = titles.get(position);

        if (imageUrl.startsWith("android.resource://")) {
            Glide.with(context).load(Uri.parse(imageUrl)).into(holder.imageView);
        } else {
            Glide.with(context).load(imageUrl).into(holder.imageView);
        }

        holder.titleText.setText(title);

        holder.imageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, WatchActivityfour.class);
            intent.putExtra("videoUrl", videoUrl);
            context.startActivity(intent);
        });

        holder.downloadButton.setOnClickListener(v -> downloadVideo(videoUrl, title));
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleText;
        Button downloadButton;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            titleText = itemView.findViewById(R.id.titleText);
            downloadButton = itemView.findViewById(R.id.downloadButton);
        }
    }

    private void downloadVideo(String videoUrl, String title) {
        Uri uri = Uri.parse(videoUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(title);
        request.setDescription("Descargando video...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title + ".mp4");

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadManager.enqueue(request);
            Toast.makeText(context, "Descarga iniciada...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Error al iniciar la descarga", Toast.LENGTH_SHORT).show();
        }
    }
}

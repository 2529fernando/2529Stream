package com.fernan2529;

import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Size;
import android.os.Build;
import android.content.ContentResolver;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class VideoGridAdapter extends RecyclerView.Adapter<VideoGridAdapter.VH> {

    public interface OnItemClick {
        void onClick(int position);
    }

    private final List<String> names;
    private final List<Uri>    uris;
    private final OnItemClick  listener;

    public VideoGridAdapter(List<String> names, List<Uri> uris, OnItemClick listener) {
        this.names = names;
        this.uris  = uris;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new VH(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        String title = names.get(position);
        Uri uri      = uris.get(position);

        h.txtTitle.setText(title);

        // Miniatura: Glide + frame 1s, centerCrop
        Glide.with(h.itemView.getContext())
                .load(uri)
                .apply(new RequestOptions().frame(1_000_000).centerCrop())
                .placeholder(new ColorDrawable(0xFF303030))
                .into(h.imgThumb);
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView imgThumb;
        TextView  txtTitle;

        VH(@NonNull View itemView, OnItemClick listener) {
            super(itemView);
            imgThumb = itemView.findViewById(R.id.videoThumbnail);
            txtTitle = itemView.findViewById(R.id.videoTitle);

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onClick(getAdapterPosition());
            });
        }
    }
}

package com.fernan2529.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.fernan2529.R;
import com.fernan2529.model.UserModel;

public class AndroidUtil {

    // --- Toast helpers ---
    public static void showToast(Context context, String message) {
        showToastLong(context, message);
    }

    public static void showToastShort(Context context, String message) {
        if (context == null) return;
        Toast.makeText(context, safe(message), Toast.LENGTH_SHORT).show();
    }

    public static void showToastLong(Context context, String message) {
        if (context == null) return;
        Toast.makeText(context, safe(message), Toast.LENGTH_LONG).show();
    }

    // --- Intent helpers (pasar/recuperar UserModel sin Parcelable) ---
    public static void passUserModelAsIntent(Intent intent, @Nullable UserModel model) {
        if (intent == null || model == null) return;
        intent.putExtra("username", safe(model.getUsername()));
        intent.putExtra("phone",    safe(model.getPhone()));
        intent.putExtra("userId",   safe(model.getUserId()));
        intent.putExtra("fcmToken", safe(model.getFcmToken()));
    }

    public static UserModel getUserModelFromIntent(@Nullable Intent intent) {
        UserModel userModel = new UserModel();
        if (intent == null) return userModel;
        userModel.setUsername(intent.getStringExtra("username"));
        userModel.setPhone(intent.getStringExtra("phone"));
        userModel.setUserId(intent.getStringExtra("userId"));
        userModel.setFcmToken(intent.getStringExtra("fcmToken"));
        return userModel;
    }

    private static String safe(String s) { return s == null ? "" : s; }

    // --- Glide helpers (con placeholder seguro) ---
    public static void setProfilePic(Context context, @Nullable Uri imageUri, ImageView imageView) {
        if (context == null || imageView == null) return;

        RequestOptions opts = RequestOptions.circleCropTransform()
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

        Glide.with(context)
                .load(imageUri != null ? imageUri : R.drawable.ic_launcher_foreground)
                .apply(opts)
                .into(imageView);
    }

    public static void setProfilePic(Context context, @Nullable String imageUrl, ImageView imageView) {
        if (context == null || imageView == null) return;

        RequestOptions opts = RequestOptions.circleCropTransform()
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

        Object source = (imageUrl == null || imageUrl.trim().isEmpty())
                ? R.drawable.ic_launcher_foreground
                : imageUrl;

        Glide.with(context)
                .load(source)
                .apply(opts)
                .into(imageView);
    }
}

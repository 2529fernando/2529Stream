package com.fernan2529;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.fernan2529.utils.FirebaseUtil;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCMNotificationService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "2529stream_channel";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        // ✅ Guarda el nuevo token en Firestore
        if (FirebaseUtil.isLoggedIn()) {
            FirebaseUtil.currentUserDetails()
                    .update("fcmToken", token)
                    .addOnSuccessListener(aVoid -> {
                        // Token actualizado en Firestore
                    })
                    .addOnFailureListener(e -> {
                        // Error al actualizar
                    });
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // ✅ Manejo de mensajes tipo "notification"
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            sendNotification(title, body, remoteMessage.getData());
        }

        // ✅ Manejo de mensajes tipo "data"
        if (remoteMessage.getData().size() > 0) {
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            sendNotification(title, body, remoteMessage.getData());
        }
    }

    private void sendNotification(String title, String messageBody, @NonNull java.util.Map<String, String> data) {
        Intent intent = new Intent(this, SplashActivity.class);

        // ✅ Si viene con userId en el payload, se pasa al intent
        if (data.containsKey("userId")) {
            intent.putExtra("userId", data.get("userId"));
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Crear canal en Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "2529Stream Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification) // 🔔 crea este ícono en res/drawable
                        .setContentTitle(title != null ? title : "2529Stream")
                        .setContentText(messageBody != null ? messageBody : "")
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
    }
}

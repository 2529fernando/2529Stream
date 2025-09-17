package com.fernan2529.utils;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class FirebaseUtil {

    // --- Auth ---
    @Nullable
    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    public static boolean isLoggedIn() {
        return currentUserId() != null;
    }

    public static void logout() {
        FirebaseAuth.getInstance().signOut();
        // Si borras el FCM token, hazlo fuera (requiere tarea async)
    }

    // --- Firestore: users ---
    public static DocumentReference currentUserDetails() {
        String uid = currentUserId();
        if (uid == null) throw new IllegalStateException("User not logged in");
        return FirebaseFirestore.getInstance().collection("users").document(uid);
    }

    public static CollectionReference allUserCollectionReference() {
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static DocumentReference getUserDoc(@NonNull String uid) {
        return FirebaseFirestore.getInstance().collection("users").document(uid);
    }

    // --- Firestore: chatrooms ---
    public static CollectionReference allChatroomCollectionReference() {
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    public static DocumentReference getChatroomReference(@NonNull String chatroomId) {
        return allChatroomCollectionReference().document(chatroomId);
    }

    public static CollectionReference getChatroomMessageReference(@NonNull String chatroomId) {
        return getChatroomReference(chatroomId).collection("chats");
    }

    /** ID estable: concatena en orden lexicográfico para evitar colisiones raras de hashCode */
    public static String getChatroomId(@NonNull String userId1, @NonNull String userId2) {
        return userId1.compareTo(userId2) < 0
                ? userId1 + "_" + userId2
                : userId2 + "_" + userId1;
    }

    /**
     * Devuelve el DocumentReference del "otro" usuario en un chat 1:1.
     * Seguro ante listas vacías, nulas o que no contengan al actual.
     */
    @Nullable
    public static DocumentReference getOtherUserFromChatroom(@Nullable List<String> userIds) {
        String me = currentUserId();
        if (userIds == null || userIds.size() < 2 || me == null) return null;

        String otherId = getOtherUserIdFromChatroom(userIds, me);
        if (otherId == null) return null;

        return allUserCollectionReference().document(otherId);
    }

    /** Útil para adapters: obtén directamente el otro userId (null si no aplica) */
    @Nullable
    public static String getOtherUserIdFromChatroom(@Nullable List<String> userIds, @NonNull String me) {
        if (userIds == null || userIds.size() < 2) return null;
        if (TextUtils.equals(userIds.get(0), me)) return userIds.get(1);
        if (TextUtils.equals(userIds.get(1), me)) return userIds.get(0);
        // El current user no está en la sala (o no es 1:1)
        return null;
    }

    // --- Timestamp formatting ---
    private static final ThreadLocal<SimpleDateFormat> TIME_FMT_24 =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("HH:mm", Locale.getDefault()));

    public static String timestampToString(@Nullable Timestamp timestamp) {
        if (timestamp == null) return "";
        return TIME_FMT_24.get().format(timestamp.toDate());
    }

    // --- Storage: profile pics ---
    public static StorageReference getCurrentProfilePicStorageRef() {
        String uid = currentUserId();
        if (uid == null) throw new IllegalStateException("User not logged in");
        return FirebaseStorage.getInstance().getReference()
                .child("profile_pic")
                .child(uid);
    }

    public static StorageReference getOtherProfilePicStorageRef(@NonNull String otherUserId) {
        return FirebaseStorage.getInstance().getReference()
                .child("profile_pic")
                .child(otherUserId);
    }
}

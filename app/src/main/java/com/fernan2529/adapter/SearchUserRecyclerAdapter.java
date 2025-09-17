package com.fernan2529.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fernan2529.ChatActivity;
import com.fernan2529.R;
import com.fernan2529.model.UserModel;
import com.fernan2529.utils.AndroidUtil;
import com.fernan2529.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.HashMap;
import java.util.Map;

public class SearchUserRecyclerAdapter
        extends FirestoreRecyclerAdapter<UserModel, SearchUserRecyclerAdapter.UserModelViewHolder> {

    private final Context appContext;
    private final Map<String, Uri> photoCache = new HashMap<>();

    public SearchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context) {
        super(options);
        this.appContext = context.getApplicationContext();
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        String id = getSnapshots().getSnapshot(position).getId();
        return id == null ? RecyclerView.NO_ID : (id.hashCode() & 0xffffffffL);
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_user_recycler_row, parent, false);
        return new UserModelViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull UserModel model) {
        // Marca el userId esperado para validar callbacks async
        String userId = model.getUserId();
        holder.bindUserId = userId;

        // Username y phone con fallbacks
        String me = FirebaseUtil.currentUserId();
        String username = safe(model.getUsername());
        String phone = safe(model.getPhone());
        if (!TextUtils.isEmpty(me) && me.equals(userId)) {
            username = username.isEmpty() ? "(Me)" : (username + " (Me)");
        }
        holder.usernameText.setText(username.isEmpty() ? "(unknown)" : username);
        holder.phoneText.setText(phone);

        // Foto: cache primero; si no, pide y valida holder
        if (!TextUtils.isEmpty(userId)) {
            Uri cached = photoCache.get(userId);
            if (cached != null) {
                AndroidUtil.setProfilePic(appContext, cached, holder.profilePic);
            } else {
                // Placeholder por si tarda (usa uno existente)
                holder.profilePic.setImageResource(R.drawable.ic_launcher_foreground);

                FirebaseUtil.getOtherProfilePicStorageRef(userId).getDownloadUrl()
                        .addOnCompleteListener(t -> {
                            if (!isHolderValid(holder, userId)) return;
                            if (t.isSuccessful() && t.getResult() != null) {
                                Uri uri = t.getResult();
                                photoCache.put(userId, uri);
                                AndroidUtil.setProfilePic(appContext, uri, holder.profilePic);
                            } else {
                                holder.profilePic.setImageResource(R.drawable.ic_launcher_foreground);
                            }
                        });
            }
        } else {
            holder.profilePic.setImageResource(R.drawable.ic_launcher_foreground);
        }

        // Click → abrir chat (sin NEW_TASK)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ChatActivity.class);
            AndroidUtil.passUserModelAsIntent(intent, model);
            v.getContext().startActivity(intent);
        });
    }

    private static String safe(String s) { return s == null ? "" : s; }

    private boolean isHolderValid(@NonNull UserModelViewHolder h, @NonNull String expectedUserId) {
        return expectedUserId.equals(h.bindUserId) && h.getBindingAdapterPosition() != RecyclerView.NO_POSITION;
    }

    static class UserModelViewHolder extends RecyclerView.ViewHolder {
        final TextView usernameText;
        final TextView phoneText;
        final ImageView profilePic;

        String bindUserId; // tag para validar callbacks async

        UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            phoneText = itemView.findViewById(R.id.phone_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}

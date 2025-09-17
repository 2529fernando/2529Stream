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
import com.fernan2529.model.ChatroomModel;
import com.fernan2529.model.UserModel;
import com.fernan2529.utils.AndroidUtil;
import com.fernan2529.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.HashMap;
import java.util.Map;

public class RecentChatRecyclerAdapter
        extends FirestoreRecyclerAdapter<ChatroomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder> {

    private final Context appContext;

    // Caches simples
    private final Map<String, UserModel> userCache = new HashMap<>();
    private final Map<String, Uri> photoCache = new HashMap<>();

    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options, Context context) {
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
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recent_chat_recycler_row, parent, false);
        return new ChatroomModelViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder,
                                    int position,
                                    @NonNull ChatroomModel model) {
        String me = FirebaseUtil.currentUserId();
        String otherUserId = FirebaseUtil.getOtherUserIdFromChatroom(model.getUserIds(), me);
        holder.bindUserId = otherUserId;

        if (!TextUtils.isEmpty(otherUserId)) {
            // USER
            UserModel cachedUser = userCache.get(otherUserId);
            if (cachedUser != null) {
                bindUser(holder, cachedUser, model, otherUserId);
            } else {
                FirebaseUtil.allUserCollectionReference().document(otherUserId).get()
                        .addOnCompleteListener(task -> {
                            if (!isHolderValid(holder, otherUserId)) return;
                            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                                UserModel other = task.getResult().toObject(UserModel.class);
                                if (other != null) {
                                    userCache.put(otherUserId, other);
                                    bindUser(holder, other, model, otherUserId);
                                } else {
                                    bindUnknown(holder, model);
                                }
                            } else {
                                bindUnknown(holder, model);
                            }
                        });
            }

            // FOTO
            Uri cachedUri = photoCache.get(otherUserId);
            if (cachedUri != null) {
                AndroidUtil.setProfilePic(appContext, cachedUri, holder.profilePic);
            } else {
                FirebaseUtil.getOtherProfilePicStorageRef(otherUserId).getDownloadUrl()
                        .addOnCompleteListener(t -> {
                            if (!isHolderValid(holder, otherUserId)) return;
                            if (t.isSuccessful() && t.getResult() != null) {
                                Uri uri = t.getResult();
                                photoCache.put(otherUserId, uri);
                                AndroidUtil.setProfilePic(appContext, uri, holder.profilePic);
                            } else {
                                holder.profilePic.setImageResource(R.drawable.ic_launcher_foreground);
                            }
                        });
            }
        } else {
            bindUnknown(holder, model);
            holder.profilePic.setImageResource(R.drawable.ic_launcher_foreground);
        }

        // Último mensaje
        boolean lastByMe = !TextUtils.isEmpty(model.getLastMessageSenderId())
                && model.getLastMessageSenderId().equals(me);

        String last = TextUtils.isEmpty(model.getLastMessage()) ? "Say hi 👋" : model.getLastMessage();
        holder.lastMessageText.setText(lastByMe ? "You: " + last : last);
        holder.lastMessageTime.setText(FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));

        // Click → abrir chat
        holder.itemView.setOnClickListener(v -> {
            UserModel other = userCache.get(otherUserId);
            if (other != null) {
                Intent intent = new Intent(v.getContext(), ChatActivity.class);
                AndroidUtil.passUserModelAsIntent(intent, other);
                v.getContext().startActivity(intent);
            }
        });
    }

    // --- Helpers ---

    private void bindUser(@NonNull ChatroomModelViewHolder h,
                          @NonNull UserModel u,
                          @NonNull ChatroomModel m,
                          @NonNull String expectedUserId) {
        if (!isHolderValid(h, expectedUserId)) return;
        String name = u.getUsername();
        h.usernameText.setText(TextUtils.isEmpty(name) ? "(unknown)" : name);
    }

    private void bindUnknown(@NonNull ChatroomModelViewHolder h, @NonNull ChatroomModel m) {
        h.usernameText.setText("(unknown)");
    }

    private boolean isHolderValid(@NonNull ChatroomModelViewHolder h, @NonNull String expectedUserId) {
        return expectedUserId.equals(h.bindUserId) && h.getBindingAdapterPosition() != RecyclerView.NO_POSITION;
    }

    // --- ViewHolder ---

    static class ChatroomModelViewHolder extends RecyclerView.ViewHolder {
        final TextView usernameText;
        final TextView lastMessageText;
        final TextView lastMessageTime;
        final ImageView profilePic;

        String bindUserId;

        ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}

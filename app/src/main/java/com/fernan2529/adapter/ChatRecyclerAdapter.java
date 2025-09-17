package com.fernan2529.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fernan2529.R;
import com.fernan2529.model.ChatMessageModel;
import com.fernan2529.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ChatRecyclerAdapter
        extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.MessageVH> {

    private final SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options) {
        super(options);
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public MessageVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_message_recycler_row, parent, false);
        return new MessageVH(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull MessageVH holder, int position, @NonNull ChatMessageModel model) {
        String me = FirebaseUtil.currentUserId();
        boolean isMine = me != null && me.equals(model.getSenderId());

        // Muestra/oculta el lado correspondiente
        holder.leftLayout.setVisibility(isMine ? View.GONE : View.VISIBLE);
        holder.rightLayout.setVisibility(isMine ? View.VISIBLE : View.GONE);

        if (isMine) {
            holder.rightMsg.setText(model.getMessage() == null ? "" : model.getMessage());
            holder.rightTime.setText(formatTs(model.getTimestamp()));
        } else {
            holder.leftMsg.setText(model.getMessage() == null ? "" : model.getMessage());
            holder.leftTime.setText(formatTs(model.getTimestamp()));
        }
    }

    @Override
    public long getItemId(int position) {
        String id = getSnapshots().getSnapshot(position).getId();
        if (id == null) return RecyclerView.NO_ID;
        // hash a long; suficiente para estabilidad básica
        return id.hashCode() & 0xffffffffL;
    }

    private String formatTs(Timestamp ts) {
        if (ts == null) return "";
        return timeFmt.format(ts.toDate());
    }

    static class MessageVH extends RecyclerView.ViewHolder {
        // Contenedores
        final LinearLayout leftLayout;
        final LinearLayout rightLayout;
        // Lado izquierdo (recibidos)
        final TextView leftMsg;
        final TextView leftTime;
        // Lado derecho (enviados)
        final TextView rightMsg;
        final TextView rightTime;

        MessageVH(@NonNull View itemView) {
            super(itemView);
            leftLayout  = itemView.findViewById(R.id.left_chat_layout);
            rightLayout = itemView.findViewById(R.id.right_chat_layout);
            leftMsg     = itemView.findViewById(R.id.left_chat_textview);
            leftTime    = itemView.findViewById(R.id.left_chat_time);
            rightMsg    = itemView.findViewById(R.id.right_chat_textview);
            rightTime   = itemView.findViewById(R.id.right_chat_time);
        }
    }
}

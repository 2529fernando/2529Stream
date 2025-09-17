package com.fernan2529;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fernan2529.adapter.RecentChatRecyclerAdapter;
import com.fernan2529.model.ChatroomModel;
import com.fernan2529.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecentChatRecyclerAdapter adapter;

    public ChatFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.recyler_view);
        setupRecyclerView();
        return view;
    }

    private void setupRecyclerView() {
        // Asegúrate de tener índice compuesto para esta combinación si Firestore lo pide.
        Query query = FirebaseUtil.allChatroomCollectionReference()
                .whereArrayContains("userIds", FirebaseUtil.currentUserId())
                .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING)
                .limit(50);

        FirestoreRecyclerOptions<ChatroomModel> options =
                new FirestoreRecyclerOptions.Builder<ChatroomModel>()
                        .setQuery(query, ChatroomModel.class)
                        // Deja que FirebaseUI maneje start/stop según el ciclo de vida de la vista
                        .setLifecycleOwner(getViewLifecycleOwner())
                        .build();

        adapter = new RecentChatRecyclerAdapter(options, requireContext());

        LinearLayoutManager lm = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(lm);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        // No llames startListening() manualmente: lo gestiona setLifecycleOwner(...)
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Evita memory leaks
        recyclerView.setAdapter(null);
        recyclerView = null;
        adapter = null;
    }

    // Si prefieres controlar manualmente el ciclo de vida, elimina setLifecycleOwner(...)
    // y descomenta estos métodos:
    /*
    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
    }
    */
}

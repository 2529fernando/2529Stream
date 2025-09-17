package com.fernan2529;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fernan2529.adapter.SearchUserRecyclerAdapter;
import com.fernan2529.model.UserModel;
import com.fernan2529.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import java.util.Locale;

public class SearchUserActivity extends AppCompatActivity {

    private EditText searchInput;
    private ImageButton searchButton;
    private ImageButton backButton;
    private RecyclerView recyclerView;

    private SearchUserRecyclerAdapter adapter;
    private Handler debounceHandler = new Handler();
    private Runnable pendingSearch;

    private static final int MIN_LEN = 3;
    private static final long DEBOUNCE_MS = 350L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        searchInput = findViewById(R.id.seach_username_input);
        searchButton = findViewById(R.id.search_user_btn);
        backButton = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.search_user_recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // Adapter vacío inicial (sin query hasta que haya texto válido)
        FirestoreRecyclerOptions<UserModel> emptyOptions =
                new FirestoreRecyclerOptions.Builder<UserModel>()
                        .setQuery(FirebaseUtil.allUserCollectionReference().limit(0), UserModel.class)
                        .setLifecycleOwner(this) // Deja a FirebaseUI manejar start/stop
                        .build();

        adapter = new SearchUserRecyclerAdapter(emptyOptions, getApplicationContext());
        recyclerView.setAdapter(adapter);

        searchInput.requestFocus();

        backButton.setOnClickListener(v ->
                getOnBackPressedDispatcher().onBackPressed()
        );

        searchButton.setOnClickListener(v -> {
            String term = normalized(searchInput.getText().toString());
            runSearch(term);
            hideKeyboard();
        });

        // Búsqueda reactiva con debounce al teclear
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String term = normalized(s.toString());
                if (pendingSearch != null) debounceHandler.removeCallbacks(pendingSearch);
                pendingSearch = () -> runSearch(term);
                debounceHandler.postDelayed(pendingSearch, DEBOUNCE_MS);
            }
        });
    }

    private void runSearch(@NonNull String term) {
        if (term.length() < MIN_LEN) {
            searchInput.setError("Invalid Username");
            // Vacía resultados si el término es demasiado corto
            FirestoreRecyclerOptions<UserModel> empty =
                    new FirestoreRecyclerOptions.Builder<UserModel>()
                            .setQuery(FirebaseUtil.allUserCollectionReference().limit(0), UserModel.class)
                            .setLifecycleOwner(this)
                            .build();
            adapter.updateOptions(empty);
            return;
        } else {
            searchInput.setError(null);
        }

        // Prefijo sobre usernameLowercase
        String start = term;
        String end = term + '\uf8ff';

        // Excluirte a ti mismo (opcional)
        String myUid = FirebaseUtil.currentUserId();

        Query base = FirebaseUtil.allUserCollectionReference()
                .orderBy("usernameLowercase")
                .startAt(start)
                .endAt(end)
                .limit(50);

        // Si quieres excluir tu propio usuario del listado:
        // Firestore no permite "whereNotEqualTo" combinando con range fácilmente.
        // Alternativas:
        // 1) Filtra en el Adapter (ViewHolder) ocultando el ítem si uid == myUid.
        // 2) O añade un campo boolean "isSelf" y usa colección/consulta separada (más complejo).
        // Aquí lo filtraremos en la vista del adapter (recomendado).

        FirestoreRecyclerOptions<UserModel> options =
                new FirestoreRecyclerOptions.Builder<UserModel>()
                        .setQuery(base, UserModel.class)
                        .setLifecycleOwner(this)
                        .build();

        adapter.updateOptions(options);
    }

    private String normalized(String raw) {
        if (TextUtils.isEmpty(raw)) return "";
        return raw.trim().toLowerCase(Locale.ROOT);
    }

    private void hideKeyboard() {
        View v = getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pendingSearch != null) debounceHandler.removeCallbacks(pendingSearch);
    }
}

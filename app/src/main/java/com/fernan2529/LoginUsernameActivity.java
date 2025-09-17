package com.fernan2529;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fernan2529.model.UserModel;
import com.fernan2529.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Locale;
import java.util.regex.Pattern;

public class LoginUsernameActivity extends AppCompatActivity {

    private static final String STATE_PHONE = "state_phone";
    private static final Pattern USERNAME_ALLOWED =
            Pattern.compile("^[A-Za-z0-9._-]{3,30}$");

    private EditText usernameInput;
    private Button letMeInBtn;
    private ProgressBar progressBar;

    private String phoneNumber;
    private UserModel userModel;
    private boolean working = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_username);

        usernameInput = findViewById(R.id.login_username);
        letMeInBtn = findViewById(R.id.login_let_me_in_btn);
        progressBar = findViewById(R.id.login_progress_bar);

        usernameInput.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(30) });

        // Recuperar phoneNumber
        if (savedInstanceState != null) {
            phoneNumber = savedInstanceState.getString(STATE_PHONE);
        }
        if (TextUtils.isEmpty(phoneNumber) && getIntent().getExtras() != null) {
            phoneNumber = getIntent().getExtras().getString("phone");
        }
        if (TextUtils.isEmpty(phoneNumber) && FirebaseAuth.getInstance().getCurrentUser() != null) {
            phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "Phone not found; continuing without it", Toast.LENGTH_SHORT).show();
        }

        getUsername();

        letMeInBtn.setEnabled(false);
        usernameInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String candidate = normalize(s.toString());
                boolean ok = isValidUsername(candidate);
                letMeInBtn.setEnabled(ok);
                if (ok) usernameInput.setError(null);
            }
        });

        letMeInBtn.setOnClickListener(v -> {
            if (working) return;
            setUsername();
        });
    }

    private String normalize(String raw) {
        if (raw == null) return "";
        return raw.trim().replaceAll("\\s+", "");
    }

    private boolean isValidUsername(String username) {
        if (TextUtils.isEmpty(username)) return false;
        if (!USERNAME_ALLOWED.matcher(username).matches()) return false;
        if (username.startsWith(".") || username.startsWith("_")) return false;
        if (username.contains("..") || username.contains("__")) return false;
        return true;
    }

    private void setUsername() {
        String candidate = normalize(usernameInput.getText().toString());
        if (!isValidUsername(candidate)) {
            usernameInput.setError("Username must be 3–30 chars (letters, numbers, . _ -)");
            return;
        }

        String uid = FirebaseUtil.currentUserId();
        if (TextUtils.isEmpty(uid)) {
            Toast.makeText(this, "Not signed in", Toast.LENGTH_SHORT).show();
            return;
        }

        setInProgress(true);

        if (userModel != null) {
            userModel.setUsername(candidate);
        } else {
            userModel = new UserModel(phoneNumber, candidate, Timestamp.now(), uid);
        }

        FirebaseUtil.currentUserDetails()
                .set(userModel, SetOptions.merge())
                .continueWithTask(t -> {
                    if (!t.isSuccessful()) throw t.getException();
                    return FirebaseUtil.currentUserDetails()
                            .set(new UsernameLowercase(candidate.toLowerCase(Locale.ROOT)), SetOptions.merge());
                })
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(LoginUsernameActivity.this, MainActivitychat.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(
                                this,
                                "Failed to save username: " +
                                        (task.getException() != null ? task.getException().getMessage() : ""),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void getUsername() {
        setInProgress(true);
        FirebaseUtil.currentUserDetails()
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        setInProgress(false);
                        if (task.isSuccessful()) {
                            DocumentSnapshot snap = task.getResult();
                            if (snap != null && snap.exists()) {
                                userModel = snap.toObject(UserModel.class);
                                if (userModel != null && !TextUtils.isEmpty(userModel.getUsername())) {
                                    usernameInput.setText(userModel.getUsername());
                                    usernameInput.setSelection(usernameInput.getText().length());
                                    letMeInBtn.setEnabled(isValidUsername(userModel.getUsername()));
                                }
                            }
                        } else {
                            Toast.makeText(LoginUsernameActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setInProgress(boolean inProgress) {
        working = inProgress;
        progressBar.setVisibility(inProgress ? View.VISIBLE : View.GONE);
        letMeInBtn.setVisibility(inProgress ? View.GONE : View.VISIBLE);
        usernameInput.setEnabled(!inProgress);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_PHONE, phoneNumber);
    }

    // POJO para guardar usernameLowercase
    public static class UsernameLowercase {
        public String usernameLowercase;
        public UsernameLowercase() {}
        public UsernameLowercase(String u) { this.usernameLowercase = u; }
    }
}

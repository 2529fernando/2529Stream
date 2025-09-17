package com.fernan2529;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fernan2529.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    private EditText loginInput;   // username o email
    private EditText passwordInput;
    private Button loginBtn;
    private Button goToRegisterBtn;
    private ProgressBar progress;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        loginInput = findViewById(R.id.login_user_or_email);
        passwordInput = findViewById(R.id.login_password);
        loginBtn = findViewById(R.id.login_btn);
        goToRegisterBtn = findViewById(R.id.go_to_register_btn);
        progress = findViewById(R.id.login_progress);

        progress.setVisibility(View.GONE);

        loginBtn.setOnClickListener(v -> doLogin());
        goToRegisterBtn.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void doLogin() {
        String id = safe(loginInput.getText().toString().trim());
        String pw = safe(passwordInput.getText().toString());

        if (id.isEmpty()) {
            loginInput.setError("Ingresa tu usuario o email");
            return;
        }
        if (pw.length() < 6) {
            passwordInput.setError("La contraseña debe tener al menos 6 caracteres");
            return;
        }

        setInProgress(true);

        if (Patterns.EMAIL_ADDRESS.matcher(id).matches()) {
            // Login directo con email
            signInWithEmail(id, pw);
        } else {
            // Buscar email por username (lowercase)
            String unameLower = id.toLowerCase(Locale.ROOT);
            FirebaseUtil.allUserCollectionReference()
                    .whereEqualTo("usernameLowercase", unameLower)
                    .limit(1)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful() || task.getResult() == null || task.getResult().isEmpty()) {
                            setInProgress(false);
                            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String email = null;
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            email = doc.getString("email");
                            break;
                        }
                        if (TextUtils.isEmpty(email)) {
                            setInProgress(false);
                            Toast.makeText(this, "El usuario no tiene email asociado", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        signInWithEmail(email, pw);
                    });
        }
    }

    private void signInWithEmail(String email, String pw) {
        mAuth.signInWithEmailAndPassword(email, pw)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override public void onComplete(@NonNull Task<AuthResult> task) {
                        setInProgress(false);
                        if (task.isSuccessful()) {
                            Intent i = new Intent(LoginActivity.this, MainActivitychat.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Credenciales inválidas", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // Si prefieres lambda y tu nivel de compatibilidad Java lo permite:
        // .addOnCompleteListener(task -> { ... });
    }

    private void setInProgress(boolean v) {
        progress.setVisibility(v ? View.VISIBLE : View.GONE);
        loginBtn.setEnabled(!v);
        goToRegisterBtn.setEnabled(!v);
    }

    private static String safe(String s) { return s == null ? "" : s; }
}

package com.fernan2529;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fernan2529.model.UserModel;
import com.fernan2529.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameInput, emailInput, passwordInput;
    private Button registerBtn;
    private ProgressBar progress;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        usernameInput = findViewById(R.id.reg_username);
        emailInput    = findViewById(R.id.reg_email);
        passwordInput = findViewById(R.id.reg_password);
        registerBtn   = findViewById(R.id.reg_btn);
        progress      = findViewById(R.id.reg_progress);

        progress.setVisibility(View.GONE);

        registerBtn.setOnClickListener(v -> doRegister());
    }

    private void doRegister() {
        String username = safe(usernameInput.getText().toString().trim());
        String email    = safe(emailInput.getText().toString().trim());
        String pw       = safe(passwordInput.getText().toString());

        if (username.length() < 3) { usernameInput.setError("Mínimo 3 caracteres"); return; }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { emailInput.setError("Email inválido"); return; }
        if (pw.length() < 6) { passwordInput.setError("Mínimo 6 caracteres"); return; }

        setInProgress(true);

        mAuth.createUserWithEmailAndPassword(email, pw)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful() || mAuth.getCurrentUser() == null) {
                        setInProgress(false);
                        String msg = "No se pudo crear la cuenta";
                        Exception e = task.getException();
                        if (e != null) {
                            // Mapea errores comunes de Firebase Auth
                            String code = e.getClass().getSimpleName() + ": " + e.getMessage();
                            if (e instanceof com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                                msg = "Ese email ya está en uso. Inicia sesión.";
                            } else if (e instanceof com.google.firebase.auth.FirebaseAuthWeakPasswordException) {
                                msg = "Contraseña débil (mín. 6).";
                            } else if (e instanceof com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
                                msg = "Email inválido.";
                            } else if (e instanceof com.google.firebase.FirebaseNetworkException) {
                                msg = "Sin conexión a Internet.";
                            } else {
                                msg = msg + "\n" + e.getMessage(); // muestra detalle
                            }
                            android.util.Log.e("Register", "Auth error: " + code, e);
                        }
                        Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_LONG).show();
                        return;
                    }

                    String uid = mAuth.getCurrentUser().getUid();

                    UserModel model = new UserModel();
                    model.setUserId(uid);
                    model.setUsername(username);
                    model.setPhone(""); // ya no usamos phone
                    model.setCreatedTimestamp(com.google.firebase.Timestamp.now());
                    model.setFcmToken(null);

                    java.util.Map<String, Object> extra = new java.util.HashMap<>();
                    extra.put("email", email);
                    extra.put("usernameLowercase", username.toLowerCase(java.util.Locale.ROOT));

                    com.google.firebase.firestore.SetOptions merge = com.google.firebase.firestore.SetOptions.merge();

                    FirebaseUtil.currentUserDetails().set(model, merge)
                            .continueWithTask(t -> FirebaseUtil.currentUserDetails().set(extra, merge))
                            .addOnCompleteListener(t2 -> {
                                setInProgress(false);
                                if (t2.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Cuenta creada. Inicia sesión.", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Exception e2 = t2.getException();
                                    android.util.Log.e("Register", "Firestore error", e2);
                                    Toast.makeText(RegisterActivity.this, "Error guardando perfil", Toast.LENGTH_SHORT).show();
                                }
                            });
                });
    }


    private void setInProgress(boolean v) {
        progress.setVisibility(v ? View.VISIBLE : View.GONE);
        registerBtn.setEnabled(!v);
    }

    private static String safe(String s) { return s == null ? "" : s; }
}

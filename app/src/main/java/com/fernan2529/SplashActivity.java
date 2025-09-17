package com.fernan2529;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.TaskStackBuilder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.fernan2529.model.UserModel;
import com.fernan2529.utils.AndroidUtil;
import com.fernan2529.utils.FirebaseUtil;
import com.google.firebase.firestore.DocumentSnapshot;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY_MS = 800L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey("userId")) {
            // Desde notificación → abre Main + Chat
            String userId = getIntent().getExtras().getString("userId");

            if (!FirebaseUtil.isLoggedIn()) {
                Intent login = new Intent(this, LoginActivity.class);
                login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(login);
                finish();
                return;
            }

            FirebaseUtil.allUserCollectionReference().document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) { routeNormally(); return; }
                        DocumentSnapshot snap = task.getResult();
                        if (snap == null || !snap.exists()) { routeNormally(); return; }
                        UserModel model = snap.toObject(UserModel.class);
                        if (model == null) { routeNormally(); return; }

                        Intent mainIntent = new Intent(this, MainActivitychat.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                        Intent chatIntent = new Intent(this, ChatActivity.class);
                        AndroidUtil.passUserModelAsIntent(chatIntent, model);

                        TaskStackBuilder.create(this)
                                .addNextIntent(mainIntent)
                                .addNextIntent(chatIntent)
                                .startActivities();
                        finish();
                    });

        } else {
            new Handler(Looper.getMainLooper()).postDelayed(this::routeNormally, SPLASH_DELAY_MS);
        }
    }

    private void routeNormally() {
        if (FirebaseUtil.isLoggedIn()) {
            startActivity(new Intent(SplashActivity.this, MainActivitychat.class));
        } else {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }
        finish();
    }
}

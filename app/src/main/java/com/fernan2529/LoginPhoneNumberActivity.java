package com.fernan2529;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.hbb20.CountryCodePicker;

public class LoginPhoneNumberActivity extends AppCompatActivity {

    private CountryCodePicker countryCodePicker;
    private EditText phoneInput;
    private Button sendOtpBtn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone_number);

        countryCodePicker = findViewById(R.id.login_countrycode);
        phoneInput = findViewById(R.id.login_mobile_number);
        sendOtpBtn = findViewById(R.id.send_otp_btn);
        progressBar = findViewById(R.id.login_progress_bar);

        progressBar.setVisibility(View.GONE);

        // Vincula el EditText con el CCP para validación y construcción del número completo
        countryCodePicker.registerCarrierNumberEditText(phoneInput);

        // Estado inicial del botón (deshabilitado hasta que sea válido)
        sendOtpBtn.setEnabled(countryCodePicker.isValidFullNumber());

        // TextWatcher estándar (no SimpleTextWatcher)
        phoneInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean valid = countryCodePicker.isValidFullNumber();
                sendOtpBtn.setEnabled(valid);
                if (valid) {
                    phoneInput.setError(null);
                }
            }

            @Override public void afterTextChanged(Editable s) {}
        });

        sendOtpBtn.setOnClickListener(v -> {
            if (!countryCodePicker.isValidFullNumber()) {
                phoneInput.setError("Phone number not valid");
                return;
            }
            String fullPhone = countryCodePicker.getFullNumberWithPlus();

            // (Opcional) muestra un pequeño progreso si quieres
            progressBar.setVisibility(View.VISIBLE);
            sendOtpBtn.setEnabled(false);

            Intent intent = new Intent(LoginPhoneNumberActivity.this, LoginOtpActivity.class);
            intent.putExtra("phone", fullPhone);
            startActivity(intent);

            // Restablece UI (si regresas a este screen por back)
            progressBar.setVisibility(View.GONE);
            sendOtpBtn.setEnabled(true);
        });
    }
}

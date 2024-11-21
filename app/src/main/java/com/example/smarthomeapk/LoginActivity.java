package com.example.smarthomeapk;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText apiUrlEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeUIComponents();

        loginButton.setOnClickListener(v -> handleLogin());
    }

    private void initializeUIComponents() {
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
      //ho  apiUrlEditText = findViewById(R.id.apiUrlEditText);
        loginButton = findViewById(R.id.loginButton);
    }

    private void handleLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (isInputValid(username, password)) {
            if (authenticate(username, password)) {
                navigateToMainActivity();
            } else {
                showToast("Tên đăng nhập hoặc mật khẩu không chính xác");
            }
        } else {
            showToast("Vui lòng nhập tên đăng nhập và mật khẩu");
        }
    }

    private boolean isInputValid(String username, String password) {
        return !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password);
    }

    private boolean authenticate(String username, String password) {
        // Giả sử xác thực thành công
        return username.equals("hoangdz") && password.equals("h123@");
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

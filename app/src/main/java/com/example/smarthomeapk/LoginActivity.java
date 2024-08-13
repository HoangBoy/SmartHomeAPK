package com.example.smarthomeapk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView forgotPasswordTextView;
    private TextView registerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);  // Đảm bảo tên layout đúng với file XML

        // Khởi tạo các phần tử giao diện
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);
        registerTextView = findViewById(R.id.registerTextView);

        // Thiết lập sự kiện cho nút đăng nhập
        loginButton.setOnClickListener(v -> login());

        // Sự kiện cho TextView quên mật khẩu và đăng ký
        forgotPasswordTextView.setOnClickListener(v -> {
            // Xử lý sự kiện quên mật khẩu (chuyển đến màn hình quên mật khẩu)
            Toast.makeText(this, "Quên mật sẽ sớm đuược phát triển", Toast.LENGTH_SHORT).show();
        });

        registerTextView.setOnClickListener(v -> {
            // Xử lý sự kiện đăng ký (chuyển đến màn hình đăng ký)
            Toast.makeText(this, "Đăng ký sẽ sớm được phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    private void login() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập tên đăng nhập và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Giả sử xác thực thành công nếu username là "admin" và password là "password"
        if (username.equals("hoangdz") && password.equals("h123@")) {
            // Lưu trạng thái đăng nhập vào SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("SmartHomePrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.apply();

            // Điều hướng tới MainActivity sau khi đăng nhập thành công
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Tên đăng nhập hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
        }
    }
}

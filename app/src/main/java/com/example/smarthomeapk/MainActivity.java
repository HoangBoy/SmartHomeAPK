package com.example.smarthomeapk;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthomeapk.api.ApiService;
import com.example.smarthomeapk.model.DeviceControlRequest;
import com.example.smarthomeapk.model.DeviceStatusResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Switch switch1, switch2, switch3;
    private Button openDoorButton, closeDoorButton;
    private TextView statusTextView;
    private ApiService apiService;
    private final String API_KEY = "Bearer aB1cD2eF3gH4iJ5kL6mN7oP8qR9sT0uVwXyZ!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo các thành phần giao diện người dùng
        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);
        switch3 = findViewById(R.id.switch3);
        openDoorButton = findViewById(R.id.button5);
        closeDoorButton = findViewById(R.id.button6);
        statusTextView = findViewById(R.id.statusTextView);

        // Khởi tạo API Service
        apiService = RetrofitClient.getClient("https://smarthomeapihoang.azurewebsites.net/").create(ApiService.class);

        // Đặt sự kiện cho các Switch
        switch1.setOnCheckedChangeListener((buttonView, isChecked) -> controlDevice(1, isChecked ? "on" : "off"));
        switch2.setOnCheckedChangeListener((buttonView, isChecked) -> controlDevice(2, isChecked ? "on" : "off"));
        switch3.setOnCheckedChangeListener((buttonView, isChecked) -> controlDevice(3, isChecked ? "on" : "off"));

        // Đặt sự kiện cho các Button
        openDoorButton.setOnClickListener(v -> controlDevice(4, "open"));
        closeDoorButton.setOnClickListener(v -> controlDevice(4, "close"));

        // Cập nhật trạng thái khi mở ứng dụng
        updateStatus();
    }

    private void controlDevice(int id, String status) {
        DeviceControlRequest request = new DeviceControlRequest(id, status);
        apiService.controlDevice(request, API_KEY).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Đồng bộ API thành công", Toast.LENGTH_SHORT).show();

                    updateStatus();  // Cập nhật trạng thái thiết bị
                } else {
                    Toast.makeText(MainActivity.this, "Thao tác thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStatus() {
        apiService.getAllDeviceStatus(API_KEY).enqueue(new Callback<List<DeviceStatusResponse>>() {
            @Override
            public void onResponse(Call<List<DeviceStatusResponse>> call, Response<List<DeviceStatusResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<DeviceStatusResponse> statuses = response.body();

                    // Duyệt qua danh sách trạng thái thiết bị và cập nhật giao diện người dùng
                    StringBuilder statusBuilder = new StringBuilder();
                    for (DeviceStatusResponse status : statuses) {
                        switch (status.getId()) {
                            case 1:
                                switch1.setChecked(status.getStatus().equals("on"));
                                break;
                            case 2:
                                switch2.setChecked(status.getStatus().equals("on"));
                                break;
                            case 3:
                                switch3.setChecked(status.getStatus().equals("on"));
                                break;
                            case 4:
                                statusTextView.setText("" + status.getMessage());
                                break;
                            default:
                                // Xử lý các thiết bị khác nếu có
                                break;
                        }
                        statusBuilder.append(status.getName()).append(": ").append(status.getStatus()).append("\n");
                    }
                    statusTextView.setText(statusBuilder.toString().trim());
                } else {
                    Toast.makeText(MainActivity.this, "Không thể lấy trạng thái thiết bị", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<DeviceStatusResponse>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

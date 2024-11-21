package com.example.smarthomeapk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.smarthomeapk.api.ApiService;
import com.example.smarthomeapk.model.DeviceControlRequest;
import com.example.smarthomeapk.model.DeviceStatusResponse;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private Switch switch1, switch2, switch3;
    private Button openDoorButton, closeDoorButton, voiceControlButton;
    private TextView statusTextView;
    private ApiService apiService;
    private final String API_KEY = "Bearer aB1cD2eF3gH4iJ5kL6mN7oP8qR9sT0uVwXyZ!";
    private SpeechRecognizer speechRecognizer;
    private boolean permissionToRecordAccepted = false;
    private final String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private EditText ipAddressEditText;
    private Button saveIpAddressButton;
    private TextView temperatureTextView, humidityTextView;
    private Mqtt5Client mqttClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);
        switch3 = findViewById(R.id.switch3);
        openDoorButton = findViewById(R.id.button5);
        closeDoorButton = findViewById(R.id.button6);
        voiceControlButton = findViewById(R.id.btnSpeak);
        statusTextView = findViewById(R.id.statusTextView);

        temperatureTextView = findViewById(R.id.tvTemperature);
        humidityTextView = findViewById(R.id.tvHumidity);

        mqttClient = Mqtt5Client.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost("b5b0a733da9d4bc5a9435dc3adf32503.s1.eu.hivemq.cloud")
                .serverPort(8883)
                .sslWithDefaultConfig()
                .simpleAuth()
                .username("viethoang")
                .password("24102003@hH".getBytes())
                .applySimpleAuth()
                .build();

        mqttClient.toBlocking().connect();

        subscribeToSensorData();

        apiService = RetrofitClient.getClient("http://smarthomenetapi.somee.com/").create(ApiService.class);

        switch1.setOnCheckedChangeListener((buttonView, isChecked) -> controlDevice(1, isChecked ? "on" : "off"));
        switch2.setOnCheckedChangeListener((buttonView, isChecked) -> controlDevice(2, isChecked ? "on" : "off"));
        switch3.setOnCheckedChangeListener((buttonView, isChecked) -> controlDevice(3, isChecked ? "on" : "off"));

        openDoorButton.setOnClickListener(v -> controlDevice(4, "open"));
        closeDoorButton.setOnClickListener(v -> controlDevice(4, "close"));

        voiceControlButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
            } else {
                initializeSpeechRecognizer();
                speechRecognizer.startListening(createSpeechRecognizerIntent());
            }
        });

        updateStatus();
    }

private void subscribeToSensorData() {
    mqttClient.toAsync().subscribeWith()
            .topicFilter("home/sensors/temperature_humidity")
            .callback(this::handleSensorData)
            .send()
            .whenComplete((subAck, throwable) -> {
                if (throwable != null) {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Không thể subscribe vào topic", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Đã subscribe vào topic cảm biến", Toast.LENGTH_SHORT).show());
                }
            });
}

    private void handleSensorData(Mqtt5Publish publish) {
        String payload = new String(publish.getPayloadAsBytes(), StandardCharsets.UTF_8);

        try {
            JSONObject jsonObject = new JSONObject(payload);
            final int temperature = jsonObject.getInt("temperature");
            final int humidity = jsonObject.getInt("humidity");

            runOnUiThread(() -> {
                temperatureTextView.setText("Nhiệt độ: " + temperature + " °C");
                humidityTextView.setText("Độ ẩm: " + humidity + " %");
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
///



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            permissionToRecordAccepted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (permissionToRecordAccepted) {
                initializeSpeechRecognizer();
                speechRecognizer.startListening(createSpeechRecognizerIntent());
            } else {
                Toast.makeText(this, "Ứng dụng cần quyền truy cập microphone để nhận diện giọng nói", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initializeSpeechRecognizer() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {}
            @Override
            public void onBeginningOfSpeech() {}
            @Override
            public void onRmsChanged(float rmsdB) {}
            @Override
            public void onBufferReceived(byte[] buffer) {}
            @Override
            public void onEndOfSpeech() {}
            @Override
            public void onError(int error) {
                String message;
                switch (error) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        message = "Lỗi âm thanh";
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        message = "Lỗi client";
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        message = "Thiếu quyền";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        message = "Lỗi mạng";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        message = "Hết thời gian chờ mạng";
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        message = "Không nhận diện được giọng nói";
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        message = "Recognizer đang bận";
                        break;
                    case SpeechRecognizer.ERROR_SERVER:
                        message = "Lỗi server";
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        message = "Hết thời gian chờ giọng nói";
                        break;
                    default:
                        message = "Lỗi không xác định";
                        break;
                }
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResults(Bundle results) {
                List<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String command = matches.get(0).toLowerCase();
                    processVoiceCommand(command);
                }
            }
            @Override
            public void onPartialResults(Bundle partialResults) {}
            @Override
            public void onEvent(int eventType, Bundle params) {}
        });
    }

    private Intent createSpeechRecognizerIntent() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hãy nói lệnh của bạn");
        return intent;
    }

    private void controlDevice(int id, String status) {
        DeviceControlRequest request = new DeviceControlRequest(id, status);
        apiService.controlDevice(request, API_KEY).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
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
                                statusTextView.setText(status.getMessage());
                                break;
                            default:
                                break;
                        }
                        statusBuilder.append(status.getName()).append(": ").append(status.getStatus()).append("\n");
                    }
                    Toast.makeText(MainActivity.this, "Update trạng thái thiết bị thành công", Toast.LENGTH_SHORT).show();

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

    private void processVoiceCommand(String command) {
        if (command.contains("bật đèn phòng khách")) {
            controlDevice(1, "on");
        } else if (command.contains("tắt đèn phòng khách")) {
            controlDevice(1, "off");
        } else if (command.contains("bật quạt phòng ngủ")) {
            controlDevice(2, "on");
        } else if (command.contains("tắt quạt phòng ngủ")) {
            controlDevice(2, "off");
        } else if (command.contains("bật bình nóng lạnh")) {
            controlDevice(3, "on");
        } else if (command.contains("tắt bình nóng lạnh")) {
            controlDevice(3, "off");
        } else if (command.contains("mở cửa")) {
            controlDevice(4, "open");
        } else if (command.contains("đóng cửa")) {
            controlDevice(4, "close");
        }
        else if (command.contains("tắt tất cả thiết bị")) {
            for (int i = 1; i < 4; i++) {
                controlDevice(i, "off");
            }
        }
        else if (command.contains("bật tất cả thiết bị")) {
                for (int i = 1; i < 4; i++){
                    controlDevice(i, "on");
                }
        }
        else {
            Toast.makeText(this, "Lệnh không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mqttClient != null) {
            try {
                mqttClient.toBlocking().disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
    }
}

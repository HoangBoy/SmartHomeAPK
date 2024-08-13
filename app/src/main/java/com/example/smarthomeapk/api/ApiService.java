package com.example.smarthomeapk.api;

// ApiService.java

import com.example.smarthomeapk.model.DeviceControlRequest;
import com.example.smarthomeapk.model.DeviceStatusResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    // Phương thức POST để điều khiển thiết bị
    @POST("api/device/control")
    Call<Void> controlDevice(@Body DeviceControlRequest request, @Header("Authorization") String apiKey);

    // Phương thức GET để lấy trạng thái của một thiết bị cụ thể
    @GET("api/device/status/{id}")
    Call<DeviceStatusResponse> getDeviceStatus(@Path("id") int id, @Header("Authorization") String apiKey);

    // Phương thức GET để lấy trạng thái của tất cả các thiết bị
    @GET("api/device/status")
    Call<List<DeviceStatusResponse>> getAllDeviceStatus(@Header("Authorization") String apiKey);
}


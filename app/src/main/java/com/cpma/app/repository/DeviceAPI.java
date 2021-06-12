package com.cpma.app.repository;

import com.cpma.app.model.request.BatteryRequest;
import com.cpma.app.model.request.DeviceRequest;
import com.cpma.app.model.response.JWTResponse;
import com.cpma.app.model.request.LocationRequest;
import com.cpma.app.model.request.LoginRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface DeviceAPI {
    @POST("api/auth/signin")
    Call<JWTResponse> login(@Body LoginRequest loginQuery);

    @PUT("api/android/saveDevice")
    Call<ResponseBody> saveDevice(@Body DeviceRequest deviceRequest);

    @POST("api/android/saveLocation")
    Call<ResponseBody> saveLocation(@Body LocationRequest locationRequest);

    @POST("api/android/saveBattery")
    Call<ResponseBody> saveBattery(@Body BatteryRequest batteryRequest);
}

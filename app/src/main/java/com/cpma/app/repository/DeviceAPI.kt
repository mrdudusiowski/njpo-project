package com.cpma.app.repository

import com.cpma.app.model.request.BatteryRequest
import com.cpma.app.model.request.DeviceRequest
import com.cpma.app.model.request.LocationRequest
import com.cpma.app.model.request.LoginRequest
import com.cpma.app.model.response.JWTResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface DeviceAPI {
    companion object {
        const val BASE_URL = "http://192.168.0.150:8080/"
    }

    @POST("api/auth/signin")
    fun login(@Body loginQuery: LoginRequest): Call<JWTResponse>

    @PUT("api/android/saveDevice")
    fun saveDevice(@Body deviceRequest: DeviceRequest): Call<ResponseBody>

    @POST("api/android/saveLocation")
    fun saveLocation(@Body locationRequest: LocationRequest): Call<ResponseBody>

    @POST("api/android/saveBattery")
    fun saveBattery(@Body batteryRequest: BatteryRequest): Call<ResponseBody>
}
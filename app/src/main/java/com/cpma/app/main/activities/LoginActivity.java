package com.cpma.app.main.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cpma.app.main.services.CPMAService;
import com.cpma.app.R;
import com.cpma.app.model.request.DeviceRequest;
import com.cpma.app.model.response.JWTResponse;
import com.cpma.app.model.request.LoginRequest;
import com.cpma.app.model.response.MessageResponse;
import com.cpma.app.repository.DeviceAPI;
import com.cpma.app.utils.EncryptedPreferencesProvider;
import com.cpma.app.utils.ScreenSizeProvider;
import com.cpma.app.utils.retrofit.RetrofitProvider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ProgressBar loadingProgressBar;
    private EncryptedPreferencesProvider encryptedPreferencesProvider;
    private ScreenSizeProvider screenSizeProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(LoginActivity.this, CPMAService.class);
        stopService(intent);

        setContentView(R.layout.activity_login);
        inicializeFields();

       if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((Activity) this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                        }


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateUsername() | !validatePassword()) {
                    return;
                }else{
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    doLoginRequest();
                }
            }
        });
    }

    private void doLoginRequest() {
        DeviceAPI deviceAPI = RetrofitProvider.createService(DeviceAPI.class);
        Call<JWTResponse> call = deviceAPI.login(new LoginRequest(usernameEditText.getText().toString(), passwordEditText.getText().toString()));
        call.enqueue(new Callback<JWTResponse>() {
            @Override
            public void onResponse(Call<JWTResponse> call, Response<JWTResponse> response) {
                if (response.isSuccessful()) {
                    saveCredentials();
                    JWTResponse jwt = response.body();
                    String token = (jwt.getTokenType() + " "+jwt.getAccessToken());
                    saveDeviceData(RetrofitProvider.createService(DeviceAPI.class, token));
                } else {
                    Gson gson = new Gson();
                    Type type = new TypeToken<MessageResponse>() {}.getType();
                    MessageResponse errorResponse = gson.fromJson(response.errorBody().charStream(),type);
                    Toast.makeText(LoginActivity.this, errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    loadingProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<JWTResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage() , Toast.LENGTH_SHORT).show();
                Log.e("Login method:", t.getMessage());
                loadingProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void saveDeviceData(DeviceAPI deviceAPI) {
        Call<ResponseBody> call = deviceAPI.saveDevice(createDeviceObject());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    loadingProgressBar.setVisibility(View.GONE);
                    Intent serviceIntent = new Intent(getApplicationContext(), CPMAService.class);
                    ContextCompat.startForegroundService(getApplicationContext(), serviceIntent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, response.message() , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage() , Toast.LENGTH_SHORT).show();
                Log.e("Save device method:", t.getMessage());
            }
        });

    }

    private void saveCredentials() {
        encryptedPreferencesProvider.getSharedPreferences(getApplicationContext()).edit()
        .putString("Username", usernameEditText.getText().toString())
        .putString("Password", passwordEditText.getText().toString())
        .apply();
    }

    private void inicializeFields() {
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        loadingProgressBar = findViewById(R.id.loading);

        SharedPreferences prfs = encryptedPreferencesProvider.getSharedPreferences(getApplicationContext());
        if (prfs != null) {
            usernameEditText.setText(prfs.getString("Username", ""));
            passwordEditText.setText(prfs.getString("Password", ""));
        }
    }

    private boolean validateUsername() {
        String username = usernameEditText.getText().toString().trim();
        if (username.isEmpty()) {
            usernameEditText.setError("Username can't be empty!");
            return false;
        } else {
            usernameEditText.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String password = passwordEditText.getText().toString().trim();
        if (password.isEmpty()) {
            passwordEditText.setError("Password field can't be empty!");
            return false;
        } else if (password.length() < 6) {
            passwordEditText.setError("Password is too short!");
            return false;
        } else {
            passwordEditText.setError(null);
            return true;
        }
    }



    private DeviceRequest createDeviceObject() {
        DeviceRequest dev = new DeviceRequest();

        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        if(android_id != null && !android_id.isEmpty())
            dev.setAndroidID(android_id);

        String manufacturer = Build.MANUFACTURER;
        if(manufacturer != null && !manufacturer.isEmpty())
            dev.setManufacturer(manufacturer);

        String phoneModel = Build.MODEL;
        if(phoneModel != null && !phoneModel.isEmpty())
            dev.setPhoneModel(phoneModel);

        String brand = Build.BRAND;
        if(brand != null && !brand.isEmpty())
            dev.setBrand(brand);

        String product = Build.PRODUCT;
        if(product != null && !product.isEmpty())
            dev.setProduct(product);

        String serial = Build.SERIAL;
        if(serial != null && !serial.isEmpty())
            dev.setSerial(serial);

        String deviceVersion = String.valueOf(Build.VERSION.SDK_INT);
        if(deviceVersion != null && !deviceVersion.isEmpty())
            dev.setDeviceVersion(deviceVersion);

        int version = Build.VERSION.SDK_INT;
        if(version != 0)
            dev.setVersion(version);

        String versionRelease = Build.VERSION.RELEASE;
        if(versionRelease != null && !versionRelease.isEmpty())
            dev.setVersionRelease(versionRelease);

        int width = screenSizeProvider.getWidth(this);
        if(width != 0)
            dev.setWidth(width);

        int height = screenSizeProvider.getHeight(this);
        if(height != 0)
            dev.setHeight(height);

        return dev;
    }
}
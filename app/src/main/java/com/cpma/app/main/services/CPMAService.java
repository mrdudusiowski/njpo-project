package com.cpma.app.main.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

import com.cpma.app.R;
import com.cpma.app.main.App;
import com.cpma.app.main.activities.LoginActivity;
import com.cpma.app.model.request.BatteryRequest;
import com.cpma.app.model.response.JWTResponse;
import com.cpma.app.model.request.LocationRequest;
import com.cpma.app.model.request.LoginRequest;
import com.cpma.app.repository.DeviceAPI;
import com.cpma.app.utils.BatteryLevelProvider;
import com.cpma.app.utils.CurrentLocationProvider;
import com.cpma.app.utils.EncryptedPreferencesProvider;
import com.cpma.app.utils.NotificationProvider;
import com.cpma.app.utils.retrofit.RetrofitProvider;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CPMAService extends Service {
    public static final int notify = 50000;  //interval between two services(Here Service run every 5 Minute)
    private Handler mHandler = new Handler();   //run on another Thread to avoid crash
    private Timer mTimer = null;    //timer handling

    @Override
    public void onCreate() {
        super.onCreate();
        if (mTimer != null) {
            mTimer.cancel();
        }
        else {
            mTimer = new Timer();
        }
        mTimer.scheduleAtFixedRate(new BackGroundServerConnection(), 0, notify);   //Schedule task
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = NotificationProvider.getNotyfication(this, App.MAIN_CHANNEL_ID,
                                                                        getResources().getString(R.string.notification_service_is_running_title),
                                                                        getResources().getString(R.string.notification_service_is_running_text)).build();
        startForeground(1, notification);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class BackGroundServerConnection extends TimerTask {
           private NotificationManagerCompat notificationManager;
           private DeviceAPI deviceAPI = RetrofitProvider.createService(DeviceAPI.class);
           private EncryptedPreferencesProvider encryptedPreferencesProvider;
           private String username;
           private String password;
           private String token;


            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        readCredentials();
                        doLoginRequest();
                    }
                });
            }

            private void readCredentials() {
                    SharedPreferences prfs = encryptedPreferencesProvider.getSharedPreferences(getApplicationContext());
                    username = prfs.getString("Username", "");
                    password = prfs.getString("Password", "");
            }

            private void doLoginRequest() {
                Call<JWTResponse> call = deviceAPI.login(new LoginRequest(username, password));
                call.enqueue(new Callback<JWTResponse>() {
                    @Override
                    public void onResponse(Call<JWTResponse> call, Response<JWTResponse> response) {
                        if (response.isSuccessful()) {
                            JWTResponse jwt = response.body();
                            token = (jwt.getTokenType() + " "+jwt.getAccessToken());
                            DeviceAPI tokenProvider = RetrofitProvider.createService(DeviceAPI.class, token);
                            saveLocalisation(tokenProvider);
                            saveBatteryLevel(tokenProvider);
                        } else {
                            Intent loginIntent = new Intent(CPMAService.this, LoginActivity.class);
                            Notification notification = NotificationProvider.getNotyficationRunActivity(CPMAService.this, loginIntent, App.CHANNEL_ID,
                                                                                                        getResources().getString(R.string.notification_invalid_credentials_title),
                                                                                                        getResources().getString(R.string.notification_invalid_credentials_text)).build();
                            startForeground(1, notification);
                        }
                    }
                    @Override
                    public void onFailure(Call<JWTResponse> call, Throwable t) {
                        Log.w("Connection problem", t.getMessage());
                    }
                });
            }

        private void saveBatteryLevel(DeviceAPI deviceAPI) {
            BatteryLevelProvider blp = new BatteryLevelProvider(CPMAService.this);
            if(blp != null){
                BatteryRequest batteryRequest = new BatteryRequest(blp.getBatteryLevel(), getAndroidId());
                Call<ResponseBody> call = deviceAPI.saveBattery(batteryRequest);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Log.i("Battery", response.message());
                        } else {
                            Log.w("Battery", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.w("Battery", t.getMessage());
                    }
                });
            }
        }

        private HashMap<String, Double> getLocation(){
                CurrentLocationProvider gpsTracker = new CurrentLocationProvider(CPMAService.this);
                if(gpsTracker.canGetLocation()) {
                    HashMap<String, Double> location = new HashMap<String, Double>();
                    location.put("latitude", gpsTracker.getLatitude());
                    location.put("longitude", gpsTracker.getLongitude());
                    return location;
                }else {
                    notificationManager = NotificationManagerCompat.from(CPMAService.this);
                    Notification notification = NotificationProvider.getNotyfication(CPMAService.this, App.CHANNEL_ID,
                            getResources().getString(R.string.notification_no_localisation_title),
                            getResources().getString(R.string.notification_no_localisation_text)).build();

                    notificationManager.notify(2, notification);
                    return null;
                }
        }

        private void saveLocalisation(DeviceAPI deviceAPI){
                HashMap<String, Double> location = getLocation();
                if(location != null){
                    LocationRequest locationRequest = new LocationRequest(location.get("latitude"), location.get("longitude"), getAndroidId());
                    Call<ResponseBody> call = deviceAPI.saveLocation(locationRequest);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Log.i("Location", response.message());
                            } else {
                                Log.w("Location", response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.w("Location", t.getMessage());
                        }
                    });
                }
        }

        private String getAndroidId(){
                return Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        }

    }
}

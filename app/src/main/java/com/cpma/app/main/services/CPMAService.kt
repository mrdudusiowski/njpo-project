package com.cpma.app.main.services

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.cpma.app.R
import com.cpma.app.main.App
import com.cpma.app.main.activities.LoginActivity
import com.cpma.app.model.request.BatteryRequest
import com.cpma.app.model.request.LocationRequest
import com.cpma.app.model.request.LoginRequest
import com.cpma.app.model.response.JWTResponse
import com.cpma.app.repository.DeviceAPI
import com.cpma.app.utils.*
import com.cpma.app.utils.retrofit.AuthenticationInterceptor
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CPMAService : Service() {

    @Inject
    lateinit var deviceAPI: DeviceAPI

    @Inject
    lateinit var authenticationInterceptor: AuthenticationInterceptor

    @Inject
    lateinit var encryptedPreferencesProvider: EncryptedPreferencesProvider

    private val notify = 50000
    private val mHandler = Handler() //run on another Thread to avoid crash
    private var mTimer: Timer? = null //timer handling

    override fun onCreate() {
        super.onCreate()
        if (mTimer != null) {
            mTimer!!.cancel()
        } else {
            mTimer = Timer()
        }
        mTimer!!.scheduleAtFixedRate(BackGroundServerConnection(), 0, notify.toLong()) //Schedule task
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val notification = NotificationProvider.getNotification(this, App.MAIN_CHANNEL_ID,
                resources.getString(R.string.notification_service_is_running_title),
                resources.getString(R.string.notification_service_is_running_text)).build()
        startForeground(1, notification)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mTimer!!.cancel()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    internal inner class BackGroundServerConnection : TimerTask() {
        override fun run() {
            mHandler.post {
                val username = encryptedPreferencesProvider.readFromEncryptedStorage("Username")
                val password = encryptedPreferencesProvider.readFromEncryptedStorage("Password")
                doLoginRequest(username, password)
            }
        }

        private fun doLoginRequest(username: String, password: String) {
            val call = deviceAPI.login(LoginRequest(username, password))
            call.enqueue(object : Callback<JWTResponse?> {
                override fun onResponse(call: Call<JWTResponse?>, response: Response<JWTResponse?>) {
                    if (response.isSuccessful) {
                        val jwt = response.body()
                        authenticationInterceptor.setAuthToken("${jwt!!.tokenType} ${jwt.accessToken}")
                        saveLocalisation()
                        saveBatteryLevel()
                    } else {
                        val loginIntent = Intent(this@CPMAService, LoginActivity::class.java)
                        val notification = NotificationProvider.getNotificationRunActivity(this@CPMAService, loginIntent, App.CHANNEL_ID,
                                resources.getString(R.string.notification_invalid_credentials_title),
                                resources.getString(R.string.notification_invalid_credentials_text)).build()
                        startForeground(1, notification)
                    }
                }

                override fun onFailure(call: Call<JWTResponse?>, t: Throwable) {
                    Log.w("Connection problem", t.message)
                }
            })
        }

        private fun saveBatteryLevel() {
            val batteryRequest = BatteryRequest(getBatteryLevel(this@CPMAService), androidId)
            val call = deviceAPI.saveBattery(batteryRequest)
            call.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                    if (response.isSuccessful) {
                        Log.i("Battery", response.message())
                    } else {
                        Log.w("Battery", response.message())
                    }
                }
                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    Log.w("Battery", t.message)
                }
            })
        }

        private val location: HashMap<String, Double>?
            get() {
                val gpsTracker = CurrentLocationProvider(this@CPMAService)
                return if (gpsTracker.canGetLocation()) {
                    val location = HashMap<String, Double>()
                    location["latitude"] = gpsTracker.getLatitude()
                    location["longitude"] = gpsTracker.getLongitude()
                    location
                } else {
                    val notificationManager = NotificationManagerCompat.from(this@CPMAService)
                    val notification = NotificationProvider.getNotification(this@CPMAService, App.CHANNEL_ID,
                            resources.getString(R.string.notification_no_localisation_title),
                            resources.getString(R.string.notification_no_localisation_text)).build()
                    notificationManager.notify(2, notification)
                    null
                }
            }

        private fun saveLocalisation() {
            val location = location
            if (location != null) {
                val locationRequest = LocationRequest(location["latitude"]!!, location["longitude"]!!, androidId)
                val call = deviceAPI.saveLocation(locationRequest)
                call.enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                        if (response.isSuccessful) {
                            Log.i("Location", response.message())
                        } else {
                            Log.w("Location", response.message())
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                        Log.w("Location", t.message)
                    }
                })
            }
        }

        private val androidId: String
            get() = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
    }
}
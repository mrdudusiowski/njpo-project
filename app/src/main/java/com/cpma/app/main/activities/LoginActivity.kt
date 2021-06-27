package com.cpma.app.main.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cpma.app.databinding.ActivityLoginBinding
import com.cpma.app.main.services.CPMAService
import com.cpma.app.model.request.DeviceRequest
import com.cpma.app.model.request.LoginRequest
import com.cpma.app.model.response.JWTResponse
import com.cpma.app.model.response.MessageResponse
import com.cpma.app.repository.DeviceAPI
import com.cpma.app.utils.EncryptedPreferencesProvider
import com.cpma.app.utils.getScreenHeight
import com.cpma.app.utils.getScreenWidth
import com.cpma.app.utils.retrofit.AuthenticationInterceptor
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var deviceAPI: DeviceAPI

    @Inject
    lateinit var authenticationInterceptor: AuthenticationInterceptor

    @Inject
    lateinit var encryptedPreferencesProvider: EncryptedPreferencesProvider

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = Intent(this@LoginActivity, CPMAService::class.java)
        stopService(intent)

        inicializeFields()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((this as Activity), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 101)
        }

        binding.login.setOnClickListener(View.OnClickListener {
            if (!validateUsername() or !validatePassword()) {
                return@OnClickListener
            } else {
               binding.loading.visibility = View.VISIBLE
                doLoginRequest()
            }
        })
    }

    private fun doLoginRequest() {
        val call = deviceAPI.login(LoginRequest(binding.username.text.toString(), binding.password.text.toString()))
        call.enqueue(object : Callback<JWTResponse> {
            override fun onResponse(call: Call<JWTResponse>, response: Response<JWTResponse>) {
                if (response.isSuccessful) {
                    saveCredentials()
                    val jwt = response.body()
                    authenticationInterceptor.setAuthToken("${jwt!!.tokenType} ${jwt.accessToken}")
                    saveDeviceData()
                } else {
                    val gson = Gson()
                    val type = object : TypeToken<MessageResponse>() {}.type
                    val (message) = gson.fromJson<MessageResponse>(response.errorBody()!!.charStream(), type)
                    Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
                    binding.loading.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<JWTResponse?>, t: Throwable) {
                Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_SHORT).show()
                Log.e("Login method:", t.message)
                binding.loading.visibility = View.GONE
            }
        })
    }

    private fun saveDeviceData() {
        val call = deviceAPI.saveDevice(createDeviceObject())
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    binding.loading.visibility = View.GONE
                    val serviceIntent = Intent(applicationContext, CPMAService::class.java)
                    ContextCompat.startForegroundService(applicationContext, serviceIntent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, response.message(), Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_SHORT).show()
                Log.e("Save device method:", t.message)
            }
        })
    }

    private fun saveCredentials() {
        encryptedPreferencesProvider.saveToEncryptedStorage("Username", binding.username.text.toString())
        encryptedPreferencesProvider.saveToEncryptedStorage("Password", binding.password.text.toString())
    }

    private fun inicializeFields() {
        binding.username.setText(encryptedPreferencesProvider.readFromEncryptedStorage("Username"))
        binding.password.setText(encryptedPreferencesProvider.readFromEncryptedStorage("Password"))
    }

    private fun validateUsername(): Boolean {
        val username = binding.username.text.toString().trim { it <= ' ' }
        return if (username.isEmpty()) {
            binding.username.error = "Username can't be empty!"
            false
        } else {
            binding.username.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        val password = binding.password.text.toString().trim { it <= ' ' }
        return if (password.isEmpty()) {
            binding.password.error = "Password field can't be empty!"
            false
        } else if (password.length < 6) {
            binding.password.error = "Password is too short!"
            false
        } else {
            binding.password.error = null
            true
        }
    }

    private fun createDeviceObject(): DeviceRequest {
        val dev = DeviceRequest()
        val android_id = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
        if (android_id != null && !android_id.isEmpty()) dev.androidID = android_id
        val manufacturer = Build.MANUFACTURER
        if (manufacturer != null && !manufacturer.isEmpty()) dev.manufacturer = manufacturer
        val phoneModel = Build.MODEL
        if (phoneModel != null && !phoneModel.isEmpty()) dev.phoneModel = phoneModel
        val brand = Build.BRAND
        if (brand != null && !brand.isEmpty()) dev.brand = brand
        val product = Build.PRODUCT
        if (product != null && !product.isEmpty()) dev.product = product
        dev.deviceVersion = Build.VERSION.SDK_INT.toString()
        val version = Build.VERSION.SDK_INT
        if (version != 0) dev.version = version
        val versionRelease = Build.VERSION.RELEASE
        if (versionRelease != null && !versionRelease.isEmpty()) dev.versionRelease = versionRelease
        val width = getScreenWidth(this)
        if (width != 0) dev.width = width
        val height = getScreenHeight(this)
        if (height != 0) dev.height = height
        return dev
    }
}
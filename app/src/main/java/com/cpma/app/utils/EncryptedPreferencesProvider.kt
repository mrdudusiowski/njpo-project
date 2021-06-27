package com.cpma.app.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.io.IOException
import java.security.GeneralSecurityException

class EncryptedPreferencesProvider(private val mContext: Context) {
    companion object {
        private const val SHARED_PREFERENCE_NAME = "cpma_encrypted_data"
    }

    val sharedPreferences: SharedPreferences
        get() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    var masterKeyAlias : String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
                    return EncryptedSharedPreferences.create(
                        SHARED_PREFERENCE_NAME,
                        masterKeyAlias,
                        mContext,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    )
                } catch (e: GeneralSecurityException) {
                    Log.w(EncryptedPreferencesProvider::class.java.canonicalName, e)
                } catch (e: IOException) {
                    Log.w(EncryptedPreferencesProvider::class.java.canonicalName, e)
                }
            } else {
                return mContext.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
            }
            return mContext.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        }

    fun saveToEncryptedStorage(fieldName: String, content: String) {
        sharedPreferences.edit()
                .putString(fieldName, content)
                .apply()
    }

    fun readFromEncryptedStorage(fieldName: String): String {
       return sharedPreferences.getString(fieldName, "") ?: ""
    }
}
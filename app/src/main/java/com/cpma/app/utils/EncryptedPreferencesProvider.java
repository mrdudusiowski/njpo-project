package com.cpma.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class EncryptedPreferencesProvider {

    private static final String SHARED_PREFERENCE_NAME = "cpma_data";

    public static SharedPreferences getSharedPreferences(Context mContext) throws NullPointerException {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String masterKeyAlias = null;
            try {
                masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
                return EncryptedSharedPreferences.create(
                        SHARED_PREFERENCE_NAME,
                        masterKeyAlias,
                        mContext,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                );
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return mContext.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        }
        return mContext.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }
}

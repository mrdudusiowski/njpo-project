package com.cpma.app.di

import android.content.Context
import com.cpma.app.utils.EncryptedPreferencesProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideEncryptedSharedPreferences(@ApplicationContext context: Context): EncryptedPreferencesProvider {
        val encryptedSharedPreferences = EncryptedPreferencesProvider(context)
        return encryptedSharedPreferences
    }

}
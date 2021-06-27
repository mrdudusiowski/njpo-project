package com.cpma.app.di

import com.cpma.app.repository.DeviceAPI
import com.cpma.app.utils.retrofit.AuthenticationInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class ApiModule {

    @Singleton
    @Provides
    fun providesGson() = GsonBuilder().create()

    @Singleton
    @Provides
    fun providesOkHttpClient(authenticationInterceptor: AuthenticationInterceptor): OkHttpClient {
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder().apply {
            addInterceptor(authenticationInterceptor)
            addInterceptor(logger)
        }.build()
    }

    @Provides
    @Singleton
    fun provideRequestInterceptor(): AuthenticationInterceptor {
        return AuthenticationInterceptor()
    }

    @Singleton
    @Provides
    fun providesRetrofit(client: OkHttpClient, gson: Gson) =
            Retrofit.Builder()
                    .baseUrl(DeviceAPI.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()

    @Singleton
    @Provides
    fun providesDeviceApi(retrofit: Retrofit) = retrofit.create(DeviceAPI::class.java)
}
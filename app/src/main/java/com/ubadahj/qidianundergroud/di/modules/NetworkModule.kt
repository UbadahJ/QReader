package com.ubadahj.qidianundergroud.di.modules

import com.ubadahj.qidianundergroud.api.MemoryCookieJar
import com.ubadahj.qidianundergroud.preferences.NetworkPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.CookieJar
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun providesCookieJar(): CookieJar {
        return MemoryCookieJar()
    }

    @Singleton
    @Provides
    fun providesOkHttpClient(
        cookieJar: CookieJar,
        pref: NetworkPreferences
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(pref.connectionTimeout.get(), TimeUnit.MINUTES)
            .writeTimeout(pref.writeTimeout.get(), TimeUnit.MINUTES) // write timeout
            .readTimeout(pref.readTimeout.get(), TimeUnit.MINUTES) // read timeout
            .apply {
                dispatcher(Dispatcher().apply {
                    maxRequestsPerHost = pref.maxHostRequests.get()
                    maxRequests = pref.maxRequests.get()
                })
                networkInterceptors().add(
                    HttpLoggingInterceptor().apply {
                        setLevel(HttpLoggingInterceptor.Level.BASIC)
                    }
                )
                cookieJar(cookieJar)
            }.build()
    }

}
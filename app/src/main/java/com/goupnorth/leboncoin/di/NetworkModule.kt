package com.goupnorth.leboncoin.di

import com.goupnorth.leboncoin.BuildConfig
import com.goupnorth.data.network.utils.UserAgentInterceptor
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    fun provideUserAgentInterceptor(): UserAgentInterceptor {
        return UserAgentInterceptor(BuildConfig.APPLICATION_ID)
    }

    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS)
    }

    @Provides
    fun provideOkHttpClient(
        interceptor: UserAgentInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .baseUrl("https://static.leboncoin.fr/")
            .build()
    }

    @Provides
    fun providesMoshi(): Moshi {
        return Moshi.Builder().build()
    }
}

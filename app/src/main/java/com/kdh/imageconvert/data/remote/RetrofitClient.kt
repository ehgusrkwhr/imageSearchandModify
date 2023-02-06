package com.kdh.imageconvert.data.remote

import androidx.core.view.DragAndDropPermissionsCompat.request
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://dapi.kakao.com/"
    private const val CONNECT_TIMEOUT = 10000L

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))

            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    private val getRetrofit: Retrofit by lazy{
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    class HeaderTokenInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            with(chain){
                val newRequest = request().newBuilder()
                    .addHeader("Authorization",)
            }
        }

    }

    val searchApi: SearchApiClient by lazy{
        getRetrofit.create(SearchApiClient::class.java)
    }

}
package com.example.mundopelota.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object RetrofitClient {
    // URLs de tus microservicios en Render
    private const val BASE_URL_USUARIOS = "https://ms-usuario-5m0i.onrender.com/"
    private const val BASE_URL_CATALOGO = "https://ms-catalogo-hora.onrender.com/"
    private const val BASE_URL_CARRITO = "https://ms-carrito-zqlc.onrender.com/"

    private var retrofitUsuarios: Retrofit? = null
    private var retrofitCatalogo: Retrofit? = null
    private var retrofitCarrito: Retrofit? = null

    private fun buildRetrofit(baseUrl: String): Retrofit {
        val httpClient = OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    fun getApiServiceUsuarios(): ApiServiceUsuarios {
        if (retrofitUsuarios == null) {
            retrofitUsuarios = buildRetrofit(BASE_URL_USUARIOS)
        }
        return retrofitUsuarios!!.create(ApiServiceUsuarios::class.java)
    }

    fun getApiServiceCatalogo(): ApiServiceCatalogo {
        if (retrofitCatalogo == null) {
            retrofitCatalogo = buildRetrofit(BASE_URL_CATALOGO)
        }
        return retrofitCatalogo!!.create(ApiServiceCatalogo::class.java)
    }

    fun getApiServiceCarrito(): ApiServiceCarrito {
        if (retrofitCarrito == null) {
            retrofitCarrito = buildRetrofit(BASE_URL_CARRITO)
        }
        return retrofitCarrito!!.create(ApiServiceCarrito::class.java)
    }
}

package com.example.mundopelota.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import android.util.Log
import java.util.Timer
import java.util.TimerTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // URLs de tus microservicios en Render
    private const val BASE_URL_USUARIOS = "https://ms-usuario-5m0i.onrender.com/"
    private const val BASE_URL_CATALOGO = "https://ms-catalogo-hora.onrender.com/"
    private const val BASE_URL_CARRITO = "https://ms-carrito-zqlc.onrender.com/"

    // URL API Externa Dólar
    private const val BASE_URL_EXTERNAL = "https://mindicador.cl/"

    private var retrofitUsuarios: Retrofit? = null
    private var retrofitCatalogo: Retrofit? = null
    private var retrofitCarrito: Retrofit? = null
    private var retrofitExternal: Retrofit? = null // Instancia para API externa

    private var keepAliveTimer: Timer? = null

    private fun buildRetrofit(baseUrl: String): Retrofit {
        val httpClient = OkHttpClient.Builder()
            // Tiempos extendidos para soportar el "despertar" de Render (Cold Start)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
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

    // --- Servicios existentes ---

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

    // --- NUEVO: Servicio Externo (Dólar) ---

    fun getApiServiceExternal(): ApiServiceExternal {
        if (retrofitExternal == null) {
            retrofitExternal = buildRetrofit(BASE_URL_EXTERNAL)
        }
        return retrofitExternal!!.create(ApiServiceExternal::class.java)
    }

    /**
     * Mantiene los servidores de Render despiertos enviando un "ping" ligero
     * cada 14 minutos (Render se duerme a los 15 min de inactividad).
     */
    fun startKeepAlive() {
        if (keepAliveTimer != null) return

        keepAliveTimer = Timer()
        // Ejecutar cada 14 minutos (14 * 60 * 1000) para evitar que Render se duerma
        keepAliveTimer!!.schedule(object : TimerTask() {
            override fun run() {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        Log.d("KeepAlive", "⚡ Enviando PING a servidores...")

                        // Intentamos despertar los servicios principales

                        // 1. Ping Usuarios (Intentamos un login falso para despertar)
                        try {
                            getApiServiceUsuarios().login(LoginRequest("ping", "ping"))
                        } catch (e: Exception) { /* Ignorar error, solo queremos despertar */ }

                        // 2. Ping Catálogo (Usamos obtenerPelotas que es un GET seguro)
                        try {
                            getApiServiceCatalogo().obtenerPelotas()
                        } catch (e: Exception) { /* Ignorar */ }

                        // 3. (Opcional) Ping Carrito si tuvieras un endpoint GET seguro
                        // try { getApiServiceCarrito().obtenerCarrito(...) } catch...

                        Log.d("KeepAlive", "✅ PING completado. Servidores despiertos.")
                    } catch (e: Exception) {
                        Log.e("KeepAlive", "⚠️ Error en KeepAlive: ${e.message}")
                    }
                }
            }
        }, 1000, 14 * 60 * 1000) // 14 Minutos

        Log.d("KeepAlive", "🚀 KeepAlive iniciado (Intervalo: 14 min)")
    }

    fun stopKeepAlive() {
        keepAliveTimer?.cancel()
        keepAliveTimer = null
        Log.d("KeepAlive", "🛑 KeepAlive detenido")
    }
}

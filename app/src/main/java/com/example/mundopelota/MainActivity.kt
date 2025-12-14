package com.example.mundopelota

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.mundopelota.network.RetrofitClient
import com.example.mundopelota.ui.MundoPelotaNavegacion
import com.example.mundopelota.viewmodel.UserAdminViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ INICIA KEEPALIVE AL ABRIR LA APP
        RetrofitClient.startKeepAlive()

        setContent {
            val userAdminViewModel = UserAdminViewModel()
            MundoPelotaNavegacion(userAdminViewModel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // ✅ DETIENE KEEPALIVE AL CERRAR LA APP
        RetrofitClient.stopKeepAlive()
    }
}


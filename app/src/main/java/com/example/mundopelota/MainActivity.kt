package com.example.mundopelota

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.mundopelota.network.RetrofitClient

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RetrofitClient.startKeepAlive()

        setContent {
            MundoPelotaNavegacion()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        RetrofitClient.stopKeepAlive()
    }
}




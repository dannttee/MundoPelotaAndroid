package com.example.mundopelota

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mundopelota.ui.*
import com.example.mundopelota.viewmodel.CartViewModel
import com.example.mundopelota.viewmodel.CatalogoViewModel
import com.example.mundopelota.viewmodel.UserAdminViewModel

@Composable
fun MundoPelotaNavegacion() {
    val navController = rememberNavController()
    // Instancias únicas de ViewModels
    val userAdminViewModel: UserAdminViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel()
    val catalogoViewModel: CatalogoViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController, userAdminViewModel)
        }

        composable("register") {
            RegisterScreen(navController, userAdminViewModel)
        }

        composable("home") {
            // SIMPLE: Solo llamamos a la pantalla, ella sabe qué hacer
            HomeScreen(
                navController = navController,
                userAdminViewModel = userAdminViewModel,
                catalogoViewModel = catalogoViewModel
            )
        }

        composable("admin_home") {
            HomeAdminScreen(
                navController = navController,
                userAdminViewModel = userAdminViewModel
            )
        }

        composable("catalogo") {
            CatalogoScreen(
                navController = navController,
                catalogoViewModel = catalogoViewModel,
                onAgregarAlCarrito = { pelota ->
                    cartViewModel.addPelotaAlCarrito(pelota)
                }
            )
        }

        composable("carrito") {
            CarritoScreen(
                navController = navController,
                carritoItems = cartViewModel.carrito,
                catalogoViewModel = catalogoViewModel
            )
        }
    }
}

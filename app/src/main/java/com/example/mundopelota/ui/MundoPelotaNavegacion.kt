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
import com.example.mundopelota.viewmodel.CatalogoAdminViewModel


@Composable
fun MundoPelotaNavegacion() {
    val navController = rememberNavController()

    // Instancias de ViewModels
    val userAdminViewModel: UserAdminViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel()
    val catalogoViewModel: CatalogoViewModel = viewModel()
    val catalogoAdminViewModel: CatalogoAdminViewModel = viewModel() // Instancia para Admin

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController, userAdminViewModel)
        }

        composable("register") {
            RegisterScreen(navController, userAdminViewModel)
        }

        composable("home") {
            HomeScreen(
                navController = navController,
                userAdminViewModel = userAdminViewModel,
                catalogoViewModel = catalogoViewModel
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

        composable("admin_catalogo") {
            CatalogoAdminScreen(
                navController = navController,
                catalogViewModel = viewModel<CatalogoViewModel>()
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

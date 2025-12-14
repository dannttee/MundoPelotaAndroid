package com.example.mundopelota.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.mundopelota.viewmodel.UserAdminViewModel
import com.example.mundopelota.viewmodel.CartViewModel
import com.example.mundopelota.viewmodel.CatalogoViewModel

@Composable
fun MundoPelotaNavegacion(userAdminViewModel: UserAdminViewModel) {
    val navController = rememberNavController()
    val cartViewModel: CartViewModel = viewModel()
    val catalogoViewModel: CatalogoViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController, userAdminViewModel)
        }
        composable("register") {  // ✅ AGREGAR ESTA RUTA
            RegisterScreen(navController, userAdminViewModel)
        }
        composable("home") {
            HomeScreen(navController, userAdminViewModel)
        }
        composable("catalogo") {
            CatalogoScreen(
                navController = navController,
                catalogoViewModel = catalogoViewModel,
                onAgregarAlCarrito = { pelota -> cartViewModel.addPelotaAlCarrito(pelota) },
                userAdminViewModel = userAdminViewModel
            )
        }
        composable("carrito") {
            CarritoScreen(navController, cartViewModel, userAdminViewModel)
        }
        composable("admin") {
            // HomeAdminScreen por implementar
            HomeScreen(navController, userAdminViewModel)
        }
        composable("catalogoAdmin") {
            CatalogoAdminScreen(
                navController = navController,
                catalogViewModel = catalogoViewModel
            )
        }
    }
}








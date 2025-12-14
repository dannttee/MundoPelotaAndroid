Mundo Pelota Android
Aplicación móvil de e-commerce para la venta de equipos deportivos desarrollada en Android con Kotlin y Jetpack Compose.

📱 Descripción
Mundo Pelota es una plataforma de compra y venta de artículos deportivos que permite a los usuarios:

Explorar un catálogo completo de productos deportivos

Buscar artículos por categoría

Agregar productos al carrito de compras

Realizar compras de manera segura

Gestionar su perfil de usuario

Acceder a funciones administrativas (para administradores)

🏗️ Arquitectura
Stack Tecnológico
Lenguaje: Kotlin

UI Framework: Jetpack Compose

Arquitectura: MVVM (Model-View-ViewModel)

Base de Datos Local: Room Database

Networking: Retrofit + OkHttp

Serialización: Gson

State Management: ViewModel + StateFlow

Navegación: Jetpack Navigation Compose

Módulos Principales
1. Screens (Pantallas)
LoginScreen.kt - Autenticación de usuarios

HomeScreen.kt - Pantalla de inicio

CatalogoScreen.kt - Catálogo de productos

CarritoScreen.kt - Carrito de compras

PerfilScreen.kt - Perfil del usuario

HomeAdminScreen.kt - Panel administrativo

CatalogoAdminScreen.kt - Gestión de catálogo (admin)

2. ViewModels
UserAdminViewModel.kt - Lógica de administración de usuarios

CatalogoAdminViewModel.kt - Lógica de gestión de catálogo

CartViewModel.kt - Lógica del carrito de compras

3. Networking
ApiServiceExternal.kt - Servicios de la API externa

ApiServiceCarrito.kt - Servicios del carrito

ApiServiceUsuarios.kt - Servicios de usuarios

ApiServiceCatalogo.kt - Servicios del catálogo

4. Modelos de Datos
RequestData.kt - Estructuras de solicitud

ResponseData.kt - Estructuras de respuesta

DolarResponse.kt - Respuesta de datos de dólar

SerieData.kt - Datos de series/productos

5. Utilidades
EmailValidator.kt - Validación de correos electrónicos

MundoPelotaNavegacion.kt - Configuración de navegación

🚀 Características
Para Usuarios
✅ Autenticación y registro de cuentas

✅ Navegación por catálogo de productos

✅ Carrito de compras persistente

✅ Sistema de búsqueda y filtrado

✅ Gestión de perfil

✅ Visualización de detalles de productos

✅ Integración con datos de mercado (dólar)

Para Administradores
✅ Gestión completa del catálogo

✅ Agregar/editar/eliminar productos

✅ Visualización de órdenes de compra

✅ Estadísticas de ventas

✅ Gestión de usuarios

📋 Requisitos
Sistema
Android SDK 33+

Android Studio Flamingo o superior

Kotlin 1.8+

Java 11+

Dependencias Principales
text
// Jetpack
androidx.compose:compose-bom:2023.10.00
androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1
androidx.navigation:navigation-compose:2.7.1

// Networking
com.squareup.retrofit2:retrofit:2.9.0
com.squareup.retrofit2:converter-gson:2.9.0
com.squareup.okhttp3:okhttp:4.11.0

// Room Database
androidx.room:room-runtime:2.5.2
androidx.room:room-compiler:2.5.2

// JSON
com.google.code.gson:gson:2.10.1
🔧 Instalación y Configuración
1. Clonar el Repositorio
bash
git clone <repository-url>
cd MundoPelotaAndroid
2. Abrir en Android Studio
Abre Android Studio

Selecciona "Open an Existing Project"

Navega a la carpeta del proyecto

Espera a que Gradle sincronice

3. Configurar la API
Edita el archivo de configuración de la API (si existe):

kotlin
// En ApiServiceExternal.kt
const val BASE_URL = "https://tu-api.com/"
4. Ejecutar la Aplicación
Conecta un dispositivo Android o inicia un emulador

Haz clic en "Run" (o presiona Shift+F10)

La aplicación se instalará y ejecutará

🔐 Permisos Requeridos
xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
📡 Integración con API
Endpoints Principales
Autenticación
POST /auth/login - Iniciar sesión

POST /auth/register - Registrar usuario

Productos
GET /productos - Obtener lista de productos

GET /productos/{id} - Obtener detalles de producto

POST /productos - Crear producto (admin)

PUT /productos/{id} - Actualizar producto (admin)

DELETE /productos/{id} - Eliminar producto (admin)

Carrito
GET /carrito - Obtener carrito del usuario

POST /carrito/items - Agregar item al carrito

DELETE /carrito/items/{id} - Eliminar item del carrito

POST /carrito/checkout - Procesar compra

Usuario
GET /usuario/perfil - Obtener perfil

PUT /usuario/perfil - Actualizar perfil

GET /usuario/ordenes - Obtener historial de compras

🎨 Interfaz de Usuario
La aplicación utiliza Jetpack Compose para construir la UI de manera declarativa:

Material 3 Design System

Tema personalizado con colores deportivos

Interfaz responsiva y adaptable a diferentes tamaños

Navegación fluida entre pantallas

📦 Estructura del Proyecto
text
MundoPelotaAndroid/
├── src/main/
│   ├── java/com/example/mundopelota/
│   │   ├── screens/
│   │   ├── viewmodel/
│   │   ├── network/
│   │   ├── utils/
│   │   ├── data/
│   │   └── MainActivity.kt
│   ├── res/
│   │   ├── drawable/
│   │   ├── values/
│   │   └── mipmap/
│   └── AndroidManifest.xml
├── build.gradle.kts
└── settings.gradle.kts
🧪 Testing
Para ejecutar pruebas unitarias:

bash
./gradlew test
Para pruebas instrumentadas (en dispositivo/emulador):

bash
./gradlew connectedAndroidTest
🔨 Compilar APK
Generar APK Debug
bash
./gradlew assembleDebug
Generar APK Release
bash
./gradlew assembleRelease
Generar Android App Bundle (AAB)
bash
./gradlew bundleRelease
📝 Variables de Entorno
Si la aplicación requiere variables sensibles, créalas en un archivo local.properties:

text
BASE_URL=https://tu-api.com/
API_KEY=tu_api_key
🐛 Troubleshooting
Problema: Error de conexión a la API
Solución: Verifica que:

La URL base sea correcta

El dispositivo tenga conexión a internet

La API esté disponible y funcionando

El firewall/proxy no bloquee las conexiones

Problema: Gradle sync fallido
Solución:

Invalida caché: File > Invalidate Caches > Invalidate and Restart

Ejecuta ./gradlew clean build

Problema: Aplicación se cierra inesperadamente
Solución:

Revisa los logs de Android Studio: Logcat

Verifica que todos los permisos estén otorgados en el dispositivo

📞 Soporte y Contribuciones
Para reportar bugs o sugerencias:

Crea un Issue en el repositorio

Proporciona:

Descripción clara del problema

Pasos para reproducir

Logs relevantes

Versión de Android utilizada

📄 Licencia
Este proyecto está bajo licencia MIT. Ver archivo LICENSE para más detalles.

👥 Autores
Desarrollador Principal: Dante Muñoz

Institución: Duoc UC

Fecha: Diciembre 2025

🙏 Agradecimientos
Equipo de Duoc UC

Jetpack Compose Team

Comunidad de desarrolladores Android

Última actualización: Diciembre 14, 2025

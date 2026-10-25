package com.example.demov2026

import androidx.compose.ui.res.stringResource
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.CancellationSignal
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.example.demov2026.ui.theme.Censista.CapturarCensoScreen
import com.example.demov2026.ui.theme.Censista.CensistaScreen
import com.example.demov2026.ui.theme.Demov2026Theme
import com.example.demov2026.ui.theme.Login.LoginScreen
import com.example.demov2026.ui.theme.Profile.ProfileScreen
import com.example.demov2026.ui.theme.Register.RegisterScreen
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.demov2026.data.TokenManager
import com.example.demov2026.ui.theme.Censista.CensoViewModel
import com.example.demov2026.ui.theme.Admin.AdminScreen
import com.example.demov2026.ui.theme.Admin.AdminViewModel
import com.example.demov2026.ui.theme.Admin.MapaCensosScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // La app dibuja detrás de las barras del sistema y los iconos
        // de la barra de estado se adaptan al tema claro u oscuro
        enableEdgeToEdge()

        setContent {

            Demov2026Theme {

                val tokenManager = remember {
                    TokenManager(this@MainActivity)
                }

                val censoViewModel: CensoViewModel = viewModel(
                    factory = object : androidx.lifecycle.ViewModelProvider.Factory {

                        override fun <T : androidx.lifecycle.ViewModel> create(
                            modelClass: Class<T>
                        ): T {

                            @Suppress("UNCHECKED_CAST")
                            return CensoViewModel(tokenManager) as T
                        }
                    }
                )

                val censoState by censoViewModel.state.collectAsState()

                val adminViewModel: AdminViewModel = viewModel(
                    factory = object : androidx.lifecycle.ViewModelProvider.Factory {

                        override fun <T : androidx.lifecycle.ViewModel> create(
                            modelClass: Class<T>
                        ): T {

                            @Suppress("UNCHECKED_CAST")
                            return AdminViewModel(tokenManager) as T
                        }
                    }
                )

                val adminState by adminViewModel.state.collectAsState()

                var pantalla by remember {
                    mutableStateOf("login")
                }

                var latitud by remember {
                    mutableStateOf<Double?>(null)
                }

                var longitud by remember {
                    mutableStateOf<Double?>(null)
                }

                // Aviso para el censista mientras se busca la ubicación
                // o cuando no fue posible obtenerla
                var mensajeUbicacion by remember {
                    mutableStateOf<Int?>(null)
                }

                fun obtenerUbicacion() {

                    val locationManager =
                        getSystemService(Context.LOCATION_SERVICE) as LocationManager

                    val permisoFine = ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )

                    val permisoCoarse = ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )

                    if (
                        permisoFine != PackageManager.PERMISSION_GRANTED &&
                        permisoCoarse != PackageManager.PERMISSION_GRANTED
                    ) {
                        mensajeUbicacion = R.string.location_permission_needed
                        return
                    }

                    try {

                        // Proveedores activos: primero GPS y luego la red
                        val proveedores = listOf(
                            LocationManager.GPS_PROVIDER,
                            LocationManager.NETWORK_PROVIDER
                        ).filter { proveedor ->
                            locationManager.isProviderEnabled(proveedor)
                        }

                        if (proveedores.isEmpty()) {
                            mensajeUbicacion = R.string.location_enable_device
                            return
                        }

                        val ubicacion: Location? =
                            proveedores.firstNotNullOfOrNull { proveedor ->
                                locationManager.getLastKnownLocation(proveedor)
                            }

                        if (ubicacion != null) {
                            latitud = ubicacion.latitude
                            longitud = ubicacion.longitude
                            mensajeUbicacion = null
                            return
                        }

                        // El dispositivo no tiene una ubicación reciente
                        // guardada: se solicita una nueva
                        mensajeUbicacion = R.string.location_searching

                        LocationManagerCompat.getCurrentLocation(
                            locationManager,
                            proveedores.first(),
                            null as CancellationSignal?,
                            ContextCompat.getMainExecutor(this@MainActivity)
                        ) { nuevaUbicacion: Location? ->

                            if (nuevaUbicacion != null) {
                                latitud = nuevaUbicacion.latitude
                                longitud = nuevaUbicacion.longitude
                                mensajeUbicacion = null
                            } else {
                                mensajeUbicacion =
                                    R.string.location_failed
                            }
                        }

                    } catch (e: SecurityException) {
                        e.printStackTrace()
                    }
                }

                val permisoUbicacionLauncher =
                    rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestMultiplePermissions()
                    ) { permisos ->

                        val permisoFine =
                            permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true

                        val permisoCoarse =
                            permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true

                        if (permisoFine || permisoCoarse) {
                            obtenerUbicacion()
                        } else {
                            mensajeUbicacion = R.string.location_permission_needed
                        }
                    }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    when (pantalla) {

                        "capturarCenso" -> {

                            CapturarCensoScreen(

                                onObtenerUbicacion = {

                                    val permisoFine = ContextCompat.checkSelfPermission(
                                        this@MainActivity,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    )

                                    val permisoCoarse = ContextCompat.checkSelfPermission(
                                        this@MainActivity,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )

                                    if (
                                        permisoFine == PackageManager.PERMISSION_GRANTED ||
                                        permisoCoarse == PackageManager.PERMISSION_GRANTED
                                    ) {

                                        obtenerUbicacion()

                                    } else {

                                        permisoUbicacionLauncher.launch(
                                            arrayOf(
                                                Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION
                                            )
                                        )
                                    }
                                },

                                onGuardarCenso = {
                                        nombre,
                                        apellido,
                                        miembros,
                                        televisiones ->

                                    censoViewModel.guardarCenso(
                                        nombre = nombre,
                                        apellido = apellido,
                                        totalMiembros = miembros,
                                        cantidadTelevisiones = televisiones,
                                        latitud = latitud,
                                        longitud = longitud
                                    )
                                },

                                onRegresar = {
                                    pantalla = "censista"
                                },

                                // El siguiente hogar tiene otra ubicación
                                onCensoGuardado = {
                                    latitud = null
                                    longitud = null
                                    mensajeUbicacion = null
                                },

                                latitud = latitud,
                                longitud = longitud,
                                mensajeUbicacion = mensajeUbicacion,
                                mensaje = censoState.mensaje,
                                cargando = censoState.cargando,
                                guardadoExitoso = censoState.guardadoExitoso
                            )
                        }

                        "perfilCensista" -> {

                            ProfileScreen(
                                onCerrarSesion = {
                                    pantalla = "login"
                                },

                                onRegresar = {
                                    pantalla = "censista"
                                }
                            )
                        }

                        "perfilAdmin" -> {

                            ProfileScreen(
                                onCerrarSesion = {
                                    pantalla = "login"
                                },

                                onRegresar = {
                                    pantalla = "admin"
                                }
                            )
                        }

                        "mapaCensos" -> {

                            MapaCensosScreen(
                                titulo = stringResource(R.string.map_all_title),
                                censos = adminState.censos,
                                cargando = adminState.cargando,
                                mensaje = adminState.mensaje,
                                usarClusters = true,

                                onCargarCensos = {
                                    adminViewModel.obtenerCensos()
                                },

                                onRegresar = {
                                    pantalla = "admin"
                                }
                            )
                        }

                        "mapaCensosMasivos" -> {

                            MapaCensosScreen(
                                titulo = stringResource(R.string.map_bulk_title),
                                censos = adminState.censos,
                                cargando = adminState.cargando,
                                mensaje = adminState.mensaje,
                                usarClusters = true,

                                onCargarCensos = {
                                    adminViewModel.obtenerCensosMasivos()
                                },

                                onRegresar = {
                                    pantalla = "admin"
                                }
                            )
                        }

                        "admin" -> {

                            AdminScreen(

                                onVerCensos = {
                                    pantalla = "mapaCensos"
                                },

                                onVerCensosMasivos = {
                                    pantalla = "mapaCensosMasivos"
                                },

                                onPerfil = {
                                    pantalla = "perfilAdmin"
                                }
                            )
                        }

                        "censista" -> {

                            CensistaScreen(

                                onCapturarCenso = {
                                    latitud = null
                                    longitud = null
                                    mensajeUbicacion = null
                                    censoViewModel.limpiarEstado()
                                    pantalla = "capturarCenso"
                                },

                                onPerfil = {
                                    pantalla = "perfilCensista"
                                }
                            )
                        }

                        "login" -> {

                            LoginScreen(

                                modifier = Modifier.safeDrawingPadding(),

                                onLoginExitoso = { rol ->

                                    if (rol.equals("ADMIN", ignoreCase = true)) {
                                        pantalla = "admin"
                                    } else {
                                        pantalla = "censista"
                                    }
                                },

                                onRegistroClic = {
                                    pantalla = "registro"
                                }
                            )
                        }

                        "registro" -> {

                            RegisterScreen(

                                modifier = Modifier.safeDrawingPadding(),

                                onVolverLogin = {
                                    pantalla = "login"
                                }
                            )
                        }

                        "perfil" -> {

                            ProfileScreen(

                                onCerrarSesion = {
                                    pantalla = "login"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

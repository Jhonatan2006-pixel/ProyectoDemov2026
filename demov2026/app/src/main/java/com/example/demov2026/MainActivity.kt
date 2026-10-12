package com.example.demov2026

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
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
import com.example.demov2026.ui.theme.Admin.ListaCensosScreen
import com.example.demov2026.ui.theme.Admin.MapaCensosScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

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
                        return
                    }

                    var ubicacion: Location? = null

                    try {

                        if (
                            locationManager.isProviderEnabled(
                                LocationManager.GPS_PROVIDER
                            )
                        ) {
                            ubicacion = locationManager.getLastKnownLocation(
                                LocationManager.GPS_PROVIDER
                            )
                        }

                        if (ubicacion == null &&
                            locationManager.isProviderEnabled(
                                LocationManager.NETWORK_PROVIDER
                            )
                        ) {
                            ubicacion = locationManager.getLastKnownLocation(
                                LocationManager.NETWORK_PROVIDER
                            )
                        }

                        if (ubicacion != null) {
                            latitud = ubicacion.latitude
                            longitud = ubicacion.longitude
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
                        }
                    }

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

                            latitud = latitud,
                            longitud = longitud,
                            mensaje = censoState.mensaje
                        )
                    }

                    "perfilCensista" -> {

                        ProfileScreen(
                            onCerrarSesion = {
                                pantalla = "login"
                            }
                        )
                    }

                    "mapaCensos" -> {

                        MapaCensosScreen()
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
                                pantalla = "capturarCenso"
                            },

                            onPerfil = {
                                pantalla = "perfilCensista"
                            }
                        )
                    }

                    "login" -> {

                        LoginScreen(

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
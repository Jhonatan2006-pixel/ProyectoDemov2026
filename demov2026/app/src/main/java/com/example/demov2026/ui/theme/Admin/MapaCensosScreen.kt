package com.example.demov2026.ui.theme.Admin

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.demov2026.model.CensoData
import com.google.gson.Gson

// Recibe desde mapa.html el clic sobre un marcador
private class PuenteMapa(
    private val onMarcadorClic: (Int) -> Unit
) {

    private val hiloPrincipal = Handler(Looper.getMainLooper())

    @JavascriptInterface
    fun mostrarCenso(id: Int) {
        hiloPrincipal.post {
            onMarcadorClic(id)
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaCensosScreen(
    titulo: String,
    censos: List<CensoData>,
    cargando: Boolean,
    mensaje: String,
    usarClusters: Boolean,
    onCargarCensos: () -> Unit,
    onRegresar: () -> Unit
) {

    val censosActuales by rememberUpdatedState(censos)

    val censosJson = remember(censos) {
        Gson().toJson(censos)
    }

    var paginaLista by remember {
        mutableStateOf(false)
    }

    var censoSeleccionado by remember {
        mutableStateOf<CensoData?>(null)
    }

    LaunchedEffect(Unit) {
        onCargarCensos()
    }

    BackHandler(onBack = onRegresar)

    // Datos del censo al tocar un marcador
    censoSeleccionado?.let { censo ->

        AlertDialog(
            onDismissRequest = {
                censoSeleccionado = null
            },

            title = {
                Text("Datos del censo")
            },

            text = {

                Column {

                    Text(
                        text = "${censo.nombre} ${censo.apellido}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Nombre: ${censo.nombre}")
                    Text("Apellido: ${censo.apellido}")
                    Text("Total de miembros: ${censo.total_miembros}")
                    Text("Cantidad de televisiones: ${censo.cantidad_televisiones}")

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Tipo de carga: ${censo.tipo_carga}")
                    Text("Latitud: ${censo.latitud}")
                    Text("Longitud: ${censo.longitud}")
                }
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        censoSeleccionado = null
                    }
                ) {
                    Text("Cerrar")
                }
            }
        )
    }

    Scaffold(
        topBar = {

            TopAppBar(
                title = {
                    Text(titulo)
                },

                navigationIcon = {

                    IconButton(
                        onClick = onRegresar
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                }
            )
        }
    ) { paddingInterno ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingInterno)
        ) {

            AndroidView(
                modifier = Modifier.fillMaxSize(),

                factory = { context ->

                    WebView(context).apply {

                        // Sin esto el WebView mide su contenido y el mapa,
                        // que usa height: 100%, queda con altura cero
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )

                        webViewClient = object : WebViewClient() {

                            override fun onPageFinished(
                                view: WebView?,
                                url: String?
                            ) {
                                paginaLista = true
                            }
                        }

                        settings.javaScriptEnabled = true

                        settings.domStorageEnabled = true

                        addJavascriptInterface(
                            PuenteMapa { id ->
                                censoSeleccionado = censosActuales.find { censo ->
                                    censo.id == id
                                }
                            },
                            "Android"
                        )

                        loadUrl("file:///android_asset/mapa.html")
                    }
                },

                // Enviar los censos al mapa cuando la página ya cargó
                // y cada vez que la lista cambie
                update = { webView ->

                    if (paginaLista && webView.tag != censosJson) {

                        webView.tag = censosJson

                        webView.evaluateJavascript(
                            "cargarCensos($censosJson, $usarClusters);",
                            null
                        )
                    }
                },

                onRelease = { webView ->
                    webView.destroy()
                }
            )

            val textoEstado = when {
                cargando -> "Cargando censos..."
                censos.isNotEmpty() -> "${censos.size} censos en el mapa"
                else -> mensaje
            }

            if (textoEstado.isNotEmpty()) {

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(12.dp),
                    shape = MaterialTheme.shapes.medium,
                    shadowElevation = 4.dp
                ) {

                    Text(
                        text = textoEstado,
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        )
                    )
                }
            }

            if (cargando) {

                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

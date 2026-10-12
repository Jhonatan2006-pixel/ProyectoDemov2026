package com.example.demov2026.ui.theme.Admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.demov2026.model.CensoData

@Composable
fun ListaCensosScreen(
    censos: List<CensoData>,
    cargando: Boolean,
    mensaje: String,
    onCargarCensos: () -> Unit,
    onRegresar: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "Censos registrados",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onCargarCensos,
            modifier = Modifier.fillMaxWidth(),
            enabled = !cargando
        ) {
            Text(
                if (cargando) {
                    "Cargando..."
                } else {
                    "Cargar censos"
                }
            )
        }

        if (mensaje.isNotEmpty()) {

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = mensaje)
        }

        Spacer(modifier = Modifier.height(20.dp))

        censos.forEach { censo ->

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "${censo.nombre} ${censo.apellido}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Total de miembros: ${censo.total_miembros}")
                    Text("Televisiones: ${censo.cantidad_televisiones}")
                    Text("Latitud: ${censo.latitud}")
                    Text("Longitud: ${censo.longitud}")
                    Text("Tipo de carga: ${censo.tipo_carga}")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        OutlinedButton(
            onClick = onRegresar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Regresar")
        }
    }
}
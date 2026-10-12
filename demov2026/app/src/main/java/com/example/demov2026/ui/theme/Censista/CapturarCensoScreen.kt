package com.example.demov2026.ui.theme.Censista

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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun CapturarCensoScreen(
    onObtenerUbicacion: () -> Unit,
    onGuardarCenso: (
        String,
        String,
        String,
        String
    ) -> Unit,
    onRegresar: () -> Unit,
    latitud: Double?,
    longitud: Double?,
    mensaje: String
) {

    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var totalMiembros by remember { mutableStateOf("") }
    var cantidadTelevisiones by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Capturar Censo",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = apellido,
            onValueChange = { apellido = it },
            label = { Text("Apellido") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = totalMiembros,
            onValueChange = {
                if (it.all { caracter -> caracter.isDigit() }) {
                    totalMiembros = it
                }
            },
            label = { Text("Total de miembros") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = cantidadTelevisiones,
            onValueChange = {
                if (it.all { caracter -> caracter.isDigit() }) {
                    cantidadTelevisiones = it
                }
            },
            label = { Text("Cantidad de televisiones") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onObtenerUbicacion,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Obtener ubicación")
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (latitud != null && longitud != null) {

            Text(
                text = "Ubicación obtenida correctamente"
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Latitud: $latitud"
            )

            Text(
                text = "Longitud: $longitud"
            )
        } else {

            Text(
                text = "Ubicación pendiente"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onGuardarCenso(
                    nombre,
                    apellido,
                    totalMiembros,
                    cantidadTelevisiones
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar censo")
        }

        if (mensaje.isNotEmpty()) {

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        OutlinedButton(
            onClick = onRegresar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Regresar")
        }
    }
}
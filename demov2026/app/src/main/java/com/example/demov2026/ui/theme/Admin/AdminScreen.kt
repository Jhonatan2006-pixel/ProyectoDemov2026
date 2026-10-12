package com.example.demov2026.ui.theme.Admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AdminScreen(
    onVerCensos: () -> Unit,
    onVerCensosMasivos: () -> Unit,
    onPerfil: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Panel del Administrador",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Seleccione una opción"
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onVerCensos,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver censos en mapa")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onVerCensosMasivos,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver censos masivos")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onPerfil,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Mi perfil")
        }
    }
}
package com.example.demov2026.ui.theme.Censista

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.demov2026.ui.theme.OpcionPanel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CensistaScreen(
    onCapturarCenso: () -> Unit,
    onPerfil: () -> Unit
) {

    Scaffold(
        topBar = {

            CenterAlignedTopAppBar(
                title = {
                    Text("Panel del Censista")
                }
            )
        }
    ) { paddingInterno ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingInterno)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Seleccione una opción",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OpcionPanel(
                icono = Icons.Filled.Create,
                titulo = "Capturar censo",
                descripcion = "Registrar los datos de un hogar",
                onClick = onCapturarCenso
            )

            OpcionPanel(
                icono = Icons.Filled.Person,
                titulo = "Mi perfil",
                descripcion = "Datos de la cuenta y foto de perfil",
                onClick = onPerfil
            )
        }
    }
}

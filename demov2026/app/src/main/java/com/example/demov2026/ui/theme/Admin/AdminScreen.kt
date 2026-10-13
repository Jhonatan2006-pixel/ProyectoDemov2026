package com.example.demov2026.ui.theme.Admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
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
fun AdminScreen(
    onVerCensos: () -> Unit,
    onVerCensosMasivos: () -> Unit,
    onPerfil: () -> Unit
) {

    Scaffold(
        topBar = {

            CenterAlignedTopAppBar(
                title = {
                    Text("Panel del Administrador")
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
                icono = Icons.Filled.Place,
                titulo = "Ver censos en mapa",
                descripcion = "Todos los censos capturados",
                onClick = onVerCensos
            )

            OpcionPanel(
                icono = Icons.Filled.LocationOn,
                titulo = "Ver censos masivos",
                descripcion = "Censos de carga masiva agrupados por zona",
                onClick = onVerCensosMasivos
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

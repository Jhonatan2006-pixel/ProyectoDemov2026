package com.example.demov2026.ui.theme.Admin

import com.example.demov2026.R
import androidx.compose.ui.res.stringResource
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
                    Text(stringResource(R.string.admin_panel_title))
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
                text = stringResource(R.string.select_option),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OpcionPanel(
                icono = Icons.Filled.Place,
                titulo = stringResource(R.string.admin_map_option),
                descripcion = stringResource(R.string.admin_map_option_desc),
                onClick = onVerCensos
            )

            OpcionPanel(
                icono = Icons.Filled.LocationOn,
                titulo = stringResource(R.string.admin_bulk_option),
                descripcion = stringResource(R.string.admin_bulk_option_desc),
                onClick = onVerCensosMasivos
            )

            OpcionPanel(
                icono = Icons.Filled.Person,
                titulo = stringResource(R.string.my_profile),
                descripcion = stringResource(R.string.my_profile_option_desc),
                onClick = onPerfil
            )
        }
    }
}

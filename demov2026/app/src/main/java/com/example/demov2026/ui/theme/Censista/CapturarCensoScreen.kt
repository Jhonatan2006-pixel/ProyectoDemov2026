package com.example.demov2026.ui.theme.Censista

import androidx.annotation.StringRes
import com.example.demov2026.R
import androidx.compose.ui.res.stringResource
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
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
    onCensoGuardado: () -> Unit,
    latitud: Double?,
    longitud: Double?,
    @StringRes mensajeUbicacion: Int?,
    @StringRes mensaje: Int?,
    cargando: Boolean,
    guardadoExitoso: Boolean
) {

    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var totalMiembros by remember { mutableStateOf("") }
    var cantidadTelevisiones by remember { mutableStateOf("") }

    val ubicacionLista = latitud != null && longitud != null

    // Al guardar un censo se deja el formulario listo para el siguiente
    LaunchedEffect(guardadoExitoso) {

        if (guardadoExitoso) {

            nombre = ""
            apellido = ""
            totalMiembros = ""
            cantidadTelevisiones = ""

            onCensoGuardado()
        }
    }

    BackHandler(onBack = onRegresar)

    Scaffold(
        topBar = {

            TopAppBar(
                title = {
                    Text(stringResource(R.string.capture_option))
                },

                navigationIcon = {

                    IconButton(
                        onClick = onRegresar
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingInterno ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingInterno)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Datos del hogar
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        text = stringResource(R.string.household_data),
                        style = MaterialTheme.typography.titleMedium
                    )

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text(stringResource(R.string.name)) },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = apellido,
                        onValueChange = { apellido = it },
                        label = { Text(stringResource(R.string.last_name)) },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = totalMiembros,
                        onValueChange = {
                            if (it.all { caracter -> caracter.isDigit() }) {
                                totalMiembros = it
                            }
                        },
                        label = { Text(stringResource(R.string.total_members)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = cantidadTelevisiones,
                        onValueChange = {
                            if (it.all { caracter -> caracter.isDigit() }) {
                                cantidadTelevisiones = it
                            }
                        },
                        label = { Text(stringResource(R.string.tv_count)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            // Ubicación del hogar
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = if (ubicacionLista) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {

                            Text(
                                text = stringResource(R.string.household_location),
                                style = MaterialTheme.typography.titleMedium
                            )

                            if (ubicacionLista) {

                                Text(
                                    text = stringResource(R.string.location_obtained),
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Text(
                                    text = stringResource(R.string.latitude_value, latitud.toString()),
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Text(
                                    text = stringResource(R.string.longitude_value, longitud.toString()),
                                    style = MaterialTheme.typography.bodyMedium
                                )

                            } else {

                                Text(
                                    text = stringResource(mensajeUbicacion ?: R.string.location_pending),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    FilledTonalButton(
                        onClick = onObtenerUbicacion,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (ubicacionLista) {
                                stringResource(R.string.update_location)
                            } else {
                                stringResource(R.string.get_location)
                            }
                        )
                    }
                }
            }

            // Resultado del último intento de guardado
            if (mensaje != null) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (guardadoExitoso) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.errorContainer
                        },
                        contentColor = if (guardadoExitoso) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onErrorContainer
                        }
                    )
                ) {

                    Text(
                        text = stringResource(mensaje),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Button(
                onClick = {
                    onGuardarCenso(
                        nombre,
                        apellido,
                        totalMiembros,
                        cantidadTelevisiones
                    )
                },
                enabled = !cargando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {

                if (cargando) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )

                } else {

                    Text(stringResource(R.string.save_census))
                }
            }
        }
    }
}

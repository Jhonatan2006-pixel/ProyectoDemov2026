package com.example.demov2026.ui.theme.Register

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.demov2026.R

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    registerViewModel: RegisterViewModel = viewModel(),
    onVolverLogin: () -> Unit = {}
) {

    val state by registerViewModel.state.collectAsState()

    LaunchedEffect(state.registroExito) {

        if (state.registroExito) {

            // Evita que la pantalla de registro se cierre sola la próxima vez
            registerViewModel.limpiarEstado()

            onVolverLogin()
        }
    }

    BackHandler(onBack = onVolverLogin)

    // Cuadro para confirmar el código de administrador
    if (state.mostrarDialogoCodigo) {

        AlertDialog(
            onDismissRequest = {
                registerViewModel.onCancelarCodigo()
            },

            title = {
                Text(stringResource(R.string.admin_code_title))
            },

            text = {

                Column {

                    Text(stringResource(R.string.admin_code_text))

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = state.codigoIngresado,
                        onValueChange = {
                            registerViewModel.onCodigoChange(it)
                        },
                        label = {
                            Text(stringResource(R.string.admin_code_label))
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = null
                            )
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        isError = state.mensajeCodigo.isNotEmpty(),
                        supportingText = {
                            if (state.mensajeCodigo.isNotEmpty()) {
                                Text(state.mensajeCodigo)
                            }
                        },
                        enabled = !state.verificandoCodigo,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        registerViewModel.onVerificarCodigo()
                    },
                    enabled = !state.verificandoCodigo
                ) {

                    if (state.verificandoCodigo) {

                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )

                    } else {

                        Text(stringResource(R.string.verify))
                    }
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        registerViewModel.onCancelarCodigo()
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(R.string.register_title),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = state.nombre,
            onValueChange = {
                registerViewModel.onNombreChange(it)
            },
            label = {
                Text(stringResource(R.string.name))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null
                )
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = {
                registerViewModel.onEmailChange(it)
            },
            label = {
                Text(stringResource(R.string.email))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = null
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.telefono,
            onValueChange = {
                registerViewModel.onTelefonoChange(it)
            },
            label = {
                Text(stringResource(R.string.phone))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Phone,
                    contentDescription = null
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = {
                registerViewModel.onPasswordChange(it)
            },
            label = {
                Text(stringResource(R.string.password))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.confirmarPassword,
            onValueChange = {
                registerViewModel.onConfirmarPasswordChange(it)
            },
            label = {
                Text(stringResource(R.string.confirm_password))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Rol con el que se registra la persona
        Text(
            text = stringResource(R.string.role_label),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            FilterChip(
                selected = state.rol == "CENSISTA",
                onClick = {
                    registerViewModel.onCensistaSeleccionado()
                },
                label = {
                    Text(stringResource(R.string.role_censista))
                },
                leadingIcon = {
                    if (state.rol == "CENSISTA") {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            )

            // Al elegir administrador se pide el código de confirmación
            FilterChip(
                selected = state.rol == "ADMIN",
                onClick = {
                    registerViewModel.onAdminSeleccionado()
                },
                label = {
                    Text(stringResource(R.string.role_admin))
                },
                leadingIcon = {
                    if (state.rol == "ADMIN") {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            )
        }

        if (state.rol == "ADMIN") {

            Text(
                text = stringResource(R.string.admin_code_verified),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                registerViewModel.onRegisterClic()
            },
            enabled = !state.cargando,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {

            if (state.cargando) {

                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp
                )

            } else {

                Text(stringResource(R.string.register_button))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onVolverLogin
        ) {
            Text(stringResource(R.string.already_have_account))
        }

        if (state.mensaje.isNotEmpty()) {

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = state.mensaje,
                textAlign = TextAlign.Center,
                color = if (state.registroExito) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        }
    }
}

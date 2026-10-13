package com.example.demov2026.ui.theme.Login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.demov2026.R
import com.example.demov2026.data.TokenManager
import com.example.demov2026.data.local.AppDatabase

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginExitoso: (String) -> Unit = {},
    onRegistroClic: () -> Unit = {}
) {

    val context = LocalContext.current

    val tokenManager = TokenManager(context)

    val database = AppDatabase.getDatabase(context)

    val usuarioDao = database.usuarioDao()

    val loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(
            tokenManager,
            usuarioDao
        )
    )

    val state by loginViewModel.state.collectAsState()

    LaunchedEffect(state.loginExito, state.rol) {
        if (state.loginExito && state.rol.isNotEmpty()) {

            val rol = state.rol

            // Evita que al cerrar sesión se vuelva a entrar solo
            loginViewModel.limpiarEstado()

            onLoginExitoso(rol)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Logo de la aplicación
        Surface(
            modifier = Modifier.size(88.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {

            Box(
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = null,
                    modifier = Modifier.size(44.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.login_title),
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = stringResource(R.string.app_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = {
                loginViewModel.onEmailChange(it)
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

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = {
                loginViewModel.onPasswordChange(it)
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
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                loginViewModel.onLoginClic()
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

                Text(stringResource(R.string.login_button))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onRegistroClic
        ) {
            Text(stringResource(R.string.create_account))
        }

        if (state.mensaje.isNotEmpty()) {

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = state.mensaje,
                textAlign = TextAlign.Center,
                color = if (state.loginExito) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        }
    }
}

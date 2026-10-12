package com.example.demov2026.ui.theme.Login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
            onLoginExitoso(state.rol)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(R.string.login_title),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = {
                loginViewModel.onEmailChange(it)
            },
            label = {
                Text(stringResource(R.string.email))
            },
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
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                loginViewModel.onLoginClic()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.login_button))
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onRegistroClic
        ) {
            Text(stringResource(R.string.create_account))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = state.mensaje,
            color = if (state.loginExito) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.error
            }
        )
    }
}
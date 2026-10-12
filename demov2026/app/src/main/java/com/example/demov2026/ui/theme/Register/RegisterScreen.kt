package com.example.demov2026.ui.theme.Register

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
            onVolverLogin()
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
            visualTransformation = PasswordVisualTransformation(),
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
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                registerViewModel.onRegisterClic()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.register_button))
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onVolverLogin
        ) {
            Text(stringResource(R.string.already_have_account))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = state.mensaje,
            color = if (state.registroExito) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.error
            }
        )
    }
}
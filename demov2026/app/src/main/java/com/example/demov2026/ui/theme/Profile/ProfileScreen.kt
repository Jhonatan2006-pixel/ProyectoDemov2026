package com.example.demov2026.ui.theme.Profile

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.demov2026.R
import com.example.demov2026.data.TokenManager
import com.example.demov2026.data.local.AppDatabase

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onCerrarSesion: () -> Unit = {}
) {

    val context = LocalContext.current

    val tokenManager = TokenManager(context)
    val database = AppDatabase.getDatabase(context)
    val usuarioDao = database.usuarioDao()

    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(
            tokenManager,
            usuarioDao
        )
    )

    val state by profileViewModel.state.collectAsState()

    var mostrarOpcionesFoto by remember {
        mutableStateOf(false)
    }

    // Cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->

        if (bitmap != null) {

            profileViewModel.onFotoTomada(bitmap)

            // Guardar foto en backend
            profileViewModel.guardarFoto()
        }
    }

    // Permiso de cámara
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->

        if (concedido) {
            cameraLauncher.launch(null)
        }
    }

    // Galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->

        if (uri != null) {

            val bitmap =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

                    val source = ImageDecoder.createSource(
                        context.contentResolver,
                        uri
                    )

                    ImageDecoder.decodeBitmap(source)

                } else {

                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(
                        context.contentResolver,
                        uri
                    )
                }

            profileViewModel.onFotoTomada(bitmap)

            // Guardar foto en backend
            profileViewModel.guardarFoto()
        }
    }

    fun abrirCamara() {

        val permisoConcedido =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

        if (permisoConcedido) {

            cameraLauncher.launch(null)

        } else {

            permissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
    }

    // Opciones de fotografía
    if (mostrarOpcionesFoto) {

        AlertDialog(
            onDismissRequest = {
                mostrarOpcionesFoto = false
            },

            title = {
                Text(
                    stringResource(R.string.profile_photo)
                )
            },

            text = {
                Text(
                    stringResource(R.string.photo_question)
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        mostrarOpcionesFoto = false
                        abrirCamara()
                    }
                ) {
                    Text(
                        stringResource(R.string.take_photo)
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        mostrarOpcionesFoto = false
                        galleryLauncher.launch("image/*")
                    }
                ) {
                    Text(
                        stringResource(R.string.gallery)
                    )
                }
            }
        )
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(R.string.my_profile),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // Avatar
        Surface(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .clickable {
                    mostrarOpcionesFoto = true
                },
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {

            if (state.fotoPerfil != null) {

                Image(
                    bitmap = state.fotoPerfil!!.asImageBitmap(),
                    contentDescription = stringResource(
                        R.string.profile_photo_description
                    ),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

            } else {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    val inicial =
                        state.nombre
                            .firstOrNull()
                            ?.uppercaseChar()
                            ?.toString()
                            ?: "U"

                    Text(
                        text = inicial,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {

                Text(
                    text = stringResource(R.string.name),
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    text = state.nombre,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Text(
                    text = stringResource(R.string.email),
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    text = state.email,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Text(
                    text = stringResource(R.string.phone),
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    text = state.telefono,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = {

                profileViewModel.cerrarSesion()
                onCerrarSesion()

            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                stringResource(R.string.logout)
            )
        }

        if (state.mensaje.isNotEmpty()) {

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = state.mensaje,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
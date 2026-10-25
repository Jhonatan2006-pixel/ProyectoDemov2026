package com.example.demov2026.ui.theme.Profile

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.demov2026.R
import com.example.demov2026.data.TokenManager
import com.example.demov2026.data.local.AppDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onCerrarSesion: () -> Unit = {},
    onRegresar: () -> Unit = {}
) {

    BackHandler(onBack = onRegresar)

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

    // Cargar el perfil cada vez que se abre la pantalla, para que
    // muestre al usuario que inició sesión y no al anterior
    LaunchedEffect(Unit) {
        profileViewModel.cargarPerfil()
    }

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

                    // Bitmap en memoria normal, para poder
                    // reducirlo y comprimirlo antes de enviarlo
                    ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                        decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                    }

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

    Scaffold(
        modifier = modifier,

        topBar = {

            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.my_profile)
                    )
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Avatar
            Surface(
                modifier = Modifier
                    .size(120.dp)
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
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = stringResource(R.string.change_photo_hint),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {

                DatoPerfil(
                    icono = Icons.Filled.Person,
                    etiqueta = stringResource(R.string.name),
                    valor = state.nombre
                )

                HorizontalDivider()

                DatoPerfil(
                    icono = Icons.Filled.Email,
                    etiqueta = stringResource(R.string.email),
                    valor = state.email
                )

                HorizontalDivider()

                DatoPerfil(
                    icono = Icons.Filled.Phone,
                    etiqueta = stringResource(R.string.phone),
                    valor = state.telefono
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            FilledTonalButton(
                onClick = {

                    profileViewModel.cerrarSesion()
                    onCerrarSesion()

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    stringResource(R.string.logout)
                )
            }

            state.mensaje?.let { mensaje ->

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Text(
                    text = stringResource(mensaje),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Fila con un dato del usuario: icono, etiqueta y valor
@Composable
private fun DatoPerfil(
    icono: ImageVector,
    etiqueta: String,
    valor: String
) {

    ListItem(
        overlineContent = {
            Text(etiqueta)
        },

        headlineContent = {
            Text(valor)
        },

        leadingContent = {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },

        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        )
    )
}

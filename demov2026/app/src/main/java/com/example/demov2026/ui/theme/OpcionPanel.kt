package com.example.demov2026.ui.theme

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

// Tarjeta de opción usada en los paneles del administrador y del censista
@Composable
fun OpcionPanel(
    icono: ImageVector,
    titulo: String,
    descripcion: String,
    onClick: () -> Unit
) {

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {

        ListItem(
            headlineContent = {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium
                )
            },

            supportingContent = {
                Text(descripcion)
            },

            leadingContent = {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },

            trailingContent = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null
                )
            },

            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent
            )
        )
    }
}

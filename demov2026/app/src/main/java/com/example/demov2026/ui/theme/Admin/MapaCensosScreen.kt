package com.example.demov2026.ui.theme.Admin

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MapaCensosScreen() {

    AndroidView(
        modifier = Modifier.fillMaxSize(),

        factory = { context ->

            WebView(context).apply {

                webViewClient = WebViewClient()

                settings.javaScriptEnabled = true

                settings.domStorageEnabled = true

                loadUrl("file:///android_asset/mapa.html")
            }
        }
    )
}
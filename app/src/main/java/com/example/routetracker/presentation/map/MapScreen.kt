package com.example.routetracker.presentation.map

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.routetracker.presentation.common.MenuSuperior

private const val MAP_URL =
    "https://www.google.com/maps/d/embed?mid=1K850htztydKaWRlEgz7Qh5uLN8HIF8s"

@Composable
fun MapScreen(
    onPerfil: () -> Unit = {},
    onIrInicio: () -> Unit = {},
    onVolver: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize()) {
        MenuSuperior(
            onPerfil = onPerfil,
            onInicio = onIrInicio,
            onAtras = onVolver,
            onMapa = {},
            mapaActivo = true
        )

        MapContent(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .navigationBarsPadding()
        )
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun MapContent(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    var webView by remember { mutableStateOf<WebView?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var canGoBack by remember { mutableStateOf(false) }

    fun retry() {
        hasError = false
        isLoading = true
        webView?.loadUrl(MAP_URL)
    }

    // "Atrás" navega dentro del mapa antes de salir de la pantalla
    BackHandler(enabled = canGoBack && !hasError) { webView?.goBack() }

    // Si estaba en error y vuelve la conexión, recarga automáticamente
    DisposableEffect(context) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                // Este callback no corre en el hilo principal; WebView sí debe usarse ahí
                webView?.post { if (hasError) retry() }
            }
        }
        connectivityManager.registerNetworkCallback(
            NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build(),
            callback
        )
        onDispose { connectivityManager.unregisterNetworkCallback(callback) }
    }

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                WebView(ctx).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            isLoading = false
                            canGoBack = view?.canGoBack() == true
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: WebResourceError?
                        ) {
                            // Solo importa si falla la página principal,
                            // no recursos secundarios (imágenes, íconos, etc.)
                            if (request?.isForMainFrame == true) {
                                hasError = true
                                isLoading = false
                            }
                        }
                    }
                    loadUrl(MAP_URL)
                    webView = this
                }
            },
            onRelease = { it.destroy() }
        )

        if (isLoading && !hasError) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        if (hasError) {
            SinConexion(onReintentar = { retry() })
        }
    }
}

@Composable
private fun SinConexion(
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Surface opaco: tapa la página de error por defecto del WebView
    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.WifiOff,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Sin conexión",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "No pudimos cargar el mapa. Revisa tu conexión a internet e inténtalo de nuevo.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            Button(onClick = onReintentar) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Reintentar")
            }
        }
    }
}

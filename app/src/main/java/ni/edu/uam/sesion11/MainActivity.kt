package ni.edu.uam.sesion11

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var mostrarSplash by rememberSaveable { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                delay(1200)
                mostrarSplash = false
            }

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (mostrarSplash) {
                        SplashScreen()
                    } else {
                        AppUI()
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bienvenido",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Preparando tu saludo personalizado...",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        CircularProgressIndicator()
    }
}

@Composable
fun AppUI() {
    var nombre by rememberSaveable { mutableStateOf("") }
    var saludoBase by rememberSaveable { mutableStateOf("") }
    var saludo by rememberSaveable { mutableStateOf("") }
    var tonoAmigable by rememberSaveable { mutableStateOf(true) }
    var contadorSaludos by rememberSaveable { mutableStateOf(0) }
    var fotoPerfilUri by rememberSaveable { mutableStateOf<String?>(null) }
    var historialSaludos by rememberSaveable { mutableStateOf(listOf<String>()) }

    val pickerImagen = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            fotoPerfilUri = uri.toString()
        }
    }

    val nombreLimpio = nombre.trim()
    val saludoBaseLimpio = saludoBase.trim()
    val errorNombre = when {
        nombreLimpio.isEmpty() -> "El nombre es obligatorio"
        nombreLimpio.length < 3 -> "Escribe al menos 3 letras"
        !nombreLimpio.matches(Regex("[A-Za-zÁÉÍÓÚáéíóúÑñ ]+")) -> "Usa solo letras"
        else -> null
    }
    val errorSaludoBase = when {
        saludoBaseLimpio.isEmpty() -> "El saludo base es obligatorio"
        saludoBaseLimpio.length < 4 -> "Escribe al menos 4 caracteres"
        else -> null
    }
    val nombreValido = errorNombre == null
    val saludoBaseValido = errorSaludoBase == null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileImagePicker(
            imageUri = fotoPerfilUri,
            onPickImage = {
                pickerImagen.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        )

        Text(
            text = "Saludador Interactivo",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Escribe tu nombre y elige el estilo de saludo",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Ingresa tu nombre") },
            singleLine = true,
            isError = errorNombre != null,
            supportingText = {
                Text(errorNombre ?: "Se ve bien")
            }
        )

        OutlinedTextField(
            value = saludoBase,
            onValueChange = { saludoBase = it },
            label = { Text("Escribe tu saludo base") },
            singleLine = true,
            isError = errorSaludoBase != null,
            supportingText = {
                Text(errorSaludoBase ?: "Perfecto")
            }
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Modo divertido")
            Switch(
                checked = tonoAmigable,
                onCheckedChange = { tonoAmigable = it }
            )
        }

        Button(
            enabled = nombreValido && saludoBaseValido,
            onClick = {
                contadorSaludos += 1
                val saludoGenerado = generarSaludo(
                    nombre = nombreLimpio,
                    saludoBase = saludoBaseLimpio,
                    modoDivertido = tonoAmigable,
                    intento = contadorSaludos
                )
                saludo = saludoGenerado
                historialSaludos = listOf(saludoGenerado) + historialSaludos

                // Se limpian campos para forzar nueva entrada validada en cada saludo.
                nombre = ""
                saludoBase = ""
            }
        ) {
            Text("Generar saludo")
        }

        OutlinedButton(
            enabled = historialSaludos.isNotEmpty(),
            onClick = {
                saludo = historialSaludos.first()
            }
        ) {
            Text("Ver ultimo saludo")
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Tu mensaje",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (saludo.isBlank()) "Tu saludo aparecerá aquí" else saludo,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (contadorSaludos > 0) {
                    Text(
                        text = "Saludos generados: $contadorSaludos",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Historial de saludos",
                    style = MaterialTheme.typography.titleMedium
                )
                if (historialSaludos.isEmpty()) {
                    Text(
                        text = "Aun no hay saludos guardados",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    historialSaludos.take(5).forEachIndexed { index, item ->
                        Text(
                            text = "${index + 1}. $item",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun ProfileImagePicker(
    imageUri: String?,
    onPickImage: () -> Unit
) {
    val context = LocalContext.current
    val imagenCargada by produceState<ImageBitmap?>(initialValue = null, key1 = imageUri) {
        value = if (imageUri.isNullOrBlank()) null else loadImageBitmap(context, imageUri)
    }

    Box(contentAlignment = Alignment.BottomEnd) {
        if (imagenCargada == null) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable(onClick = onPickImage),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tu foto",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Image(
                bitmap = imagenCargada!!,
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable(onClick = onPickImage)
            )
        }

        Surface(
            modifier = Modifier.size(34.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            tonalElevation = 2.dp,
            shadowElevation = 2.dp,
            onClick = onPickImage
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "Editar",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

private suspend fun loadImageBitmap(context: Context, uriString: String): ImageBitmap? = withContext(Dispatchers.IO) {
    runCatching {
        context.contentResolver.openInputStream(Uri.parse(uriString)).use { stream ->
            BitmapFactory.decodeStream(stream)?.asImageBitmap()
        }
    }.getOrNull()
}

private fun generarSaludo(nombre: String, saludoBase: String, modoDivertido: Boolean, intento: Int): String {
    val emojis = listOf("🌟", "🎉", "😄", "🙌")
    val emoji = emojis[intento % emojis.size]
    return if (modoDivertido) {
        "$saludoBase, $nombre! $emoji Que gusto verte por aqui."
    } else {
        "$saludoBase, $nombre. Te deseo un excelente dia."
    }
}

package cl.duocuc.pagoflex.ui.screens.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cl.duocuc.pagoflex.data.model.UsuarioSesion
import cl.duocuc.pagoflex.ui.components.CampoTextoValidado
import cl.duocuc.pagoflex.ui.components.SelectorCatalogo
import cl.duocuc.pagoflex.ui.viewmodel.PerfilFormState
import cl.duocuc.pagoflex.utils.Formatters

@Composable
fun PerfilUsuarioScreen(
    usuario: UsuarioSesion,
    state: PerfilFormState,
    comunas: List<String>,
    onFormChange: (
        nombres: String,
        apellidos: String,
        correo: String,
        telefono: String,
        comuna: String,
        claveActual: String,
        nuevaClave: String,
        confirmarNuevaClave: String
    ) -> Unit,
    onGuardarPerfil: () -> Unit,
    onCambiarClave: () -> Unit,
    onCerrarSesion: () -> Unit,
    onVolver: () -> Unit
) {
    var tabSeleccionada by remember { mutableIntStateOf(0) }
    var mostrarPassword by remember { mutableStateOf(false) }
    var fotoBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var mostrarDialogoLogout by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            fotoBitmap = bitmap.asImageBitmap()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onVolver) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Mi Perfil PagoFlex",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier
                        .clickable { cameraLauncher.launch(null) }
                        .padding(2.dp)
                ) {
                    if (fotoBitmap != null) {
                        Image(
                            bitmap = fotoBitmap!!,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Tomar foto de perfil",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Abrir cámara",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = usuario.nombreCompleto,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "RUT ${Formatters.ocultarRut(usuario.rut)} · Código ${usuario.codigo}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Badge(containerColor = MaterialTheme.colorScheme.primary) {
                        Text(usuario.rolCodigo, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        TabRow(selectedTabIndex = tabSeleccionada) {
            Tab(selected = tabSeleccionada == 0, onClick = { tabSeleccionada = 0 }, text = { Text("Datos Personales") })
            Tab(selected = tabSeleccionada == 1, onClick = { tabSeleccionada = 1 }, text = { Text("Seguridad y Clave") })
        }

        if (tabSeleccionada == 0) {
            Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Editar Datos del Perfil", fontWeight = FontWeight.Bold)

                    CampoTextoValidado(
                        valor = state.nombres,
                        onValorChange = { onFormChange(it, state.apellidos, state.correo, state.telefono, state.comunaSeleccionada, state.claveActual, state.nuevaClave, state.confirmarNuevaClave) },
                        etiqueta = "Nombres",
                        icono = Icons.Default.Person,
                        errorMensaje = state.errorNombres
                    )

                    CampoTextoValidado(
                        valor = state.apellidos,
                        onValorChange = { onFormChange(state.nombres, it, state.correo, state.telefono, state.comunaSeleccionada, state.claveActual, state.nuevaClave, state.confirmarNuevaClave) },
                        etiqueta = "Apellidos",
                        icono = Icons.Default.PersonOutline,
                        errorMensaje = state.errorApellidos
                    )

                    CampoTextoValidado(
                        valor = state.correo,
                        onValorChange = { onFormChange(state.nombres, state.apellidos, it, state.telefono, state.comunaSeleccionada, state.claveActual, state.nuevaClave, state.confirmarNuevaClave) },
                        etiqueta = "Correo electrónico",
                        icono = Icons.Default.Email,
                        errorMensaje = state.errorCorreo
                    )

                    CampoTextoValidado(
                        valor = state.telefono,
                        onValorChange = { onFormChange(state.nombres, state.apellidos, state.correo, it, state.comunaSeleccionada, state.claveActual, state.nuevaClave, state.confirmarNuevaClave) },
                        etiqueta = "Teléfono móvil",
                        icono = Icons.Default.Phone
                    )

                    SelectorCatalogo(
                        etiqueta = "Comuna de residencia",
                        opciones = comunas.ifEmpty { listOf("Maipú", "Puente Alto", "San Miguel") },
                        seleccionado = state.comunaSeleccionada,
                        onSeleccionar = { onFormChange(state.nombres, state.apellidos, state.correo, state.telefono, it, state.claveActual, state.nuevaClave, state.confirmarNuevaClave) },
                        icono = Icons.Default.LocationOn
                    )

                    if (state.mensajePerfil != null) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (state.esErrorPerfil) MaterialTheme.colorScheme.errorContainer else Color(0xFFE8F5E9)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                state.mensajePerfil,
                                color = if (state.esErrorPerfil) MaterialTheme.colorScheme.onErrorContainer else Color(0xFF1B5E20),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    Button(
                        onClick = onGuardarPerfil,
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Guardar Cambios de Perfil")
                    }
                }
            }
        } else {
            Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Cambiar Contraseña", fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = state.claveActual,
                        onValueChange = { onFormChange(state.nombres, state.apellidos, state.correo, state.telefono, state.comunaSeleccionada, it, state.nuevaClave, state.confirmarNuevaClave) },
                        label = { Text("Contraseña Actual *") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        visualTransformation = PasswordVisualTransformation(),
                        isError = state.errorClaveActual != null,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (state.errorClaveActual != null) {
                        Text(state.errorClaveActual, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }

                    OutlinedTextField(
                        value = state.nuevaClave,
                        onValueChange = { onFormChange(state.nombres, state.apellidos, state.correo, state.telefono, state.comunaSeleccionada, state.claveActual, it, state.confirmarNuevaClave) },
                        label = { Text("Nueva Contraseña *") },
                        leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        visualTransformation = PasswordVisualTransformation(),
                        isError = state.errorNuevaClave != null,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (state.errorNuevaClave != null) {
                        Text(state.errorNuevaClave, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }

                    OutlinedTextField(
                        value = state.confirmarNuevaClave,
                        onValueChange = { onFormChange(state.nombres, state.apellidos, state.correo, state.telefono, state.comunaSeleccionada, state.claveActual, state.nuevaClave, it) },
                        label = { Text("Confirmar Nueva Contraseña *") },
                        leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        visualTransformation = PasswordVisualTransformation(),
                        isError = state.errorConfirmarNuevaClave != null,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (state.errorConfirmarNuevaClave != null) {
                        Text(state.errorConfirmarNuevaClave, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }

                    if (state.mensajeClave != null) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (state.esErrorClave) MaterialTheme.colorScheme.errorContainer else Color(0xFFE8F5E9)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                state.mensajeClave,
                                color = if (state.esErrorClave) MaterialTheme.colorScheme.onErrorContainer else Color(0xFF1B5E20),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    Button(
                        onClick = onCambiarClave,
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Icon(Icons.Default.Key, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Actualizar Contraseña")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = { mostrarDialogoLogout = true },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar Sesión")
        }
    }

    if (mostrarDialogoLogout) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoLogout = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro de que deseas cerrar sesión en PagoFlex?") },
            confirmButton = {
                Button(onClick = {
                    mostrarDialogoLogout = false
                    onCerrarSesion()
                }) {
                    Text("Sí, salir")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoLogout = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

package cl.duocuc.pagoflex.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cl.duocuc.pagoflex.ui.components.CampoTextoValidado
import cl.duocuc.pagoflex.ui.components.HeaderMarcaPagoFlex
import cl.duocuc.pagoflex.ui.components.SelectorCatalogo
import cl.duocuc.pagoflex.ui.viewmodel.RegistroFormState

@Composable
fun RegistroScreen(
    state: RegistroFormState,
    comunas: List<String>,
    onFormChange: (
        rut: String,
        nombres: String,
        apellidos: String,
        correo: String,
        password: String,
        confirmarPassword: String,
        telefono: String,
        comuna: String
    ) -> Unit,
    onRegistrar: () -> Unit,
    onVolverALogin: () -> Unit
) {
    var mostrarPassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .navigationBarsPadding()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onVolverALogin) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Registro de Usuario",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        HeaderMarcaPagoFlex(
            titulo = "Crea tu cuenta en PagoFlex",
            subtitulo = "Centraliza y gestiona tus compromisos de pago."
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CampoTextoValidado(
                    valor = state.rut,
                    onValorChange = { onFormChange(it, state.nombres, state.apellidos, state.correo, state.password, state.confirmarPassword, state.telefono, state.comunaSeleccionada) },
                    etiqueta = "RUT sin puntos con guion *",
                    icono = Icons.Default.Badge,
                    placeholder = "ej. 19.876.543-2",
                    errorMensaje = state.errorRut
                )

                CampoTextoValidado(
                    valor = state.nombres,
                    onValorChange = { onFormChange(state.rut, it, state.apellidos, state.correo, state.password, state.confirmarPassword, state.telefono, state.comunaSeleccionada) },
                    etiqueta = "Nombres *",
                    icono = Icons.Default.Person,
                    errorMensaje = state.errorNombres
                )

                CampoTextoValidado(
                    valor = state.apellidos,
                    onValorChange = { onFormChange(state.rut, state.nombres, it, state.correo, state.password, state.confirmarPassword, state.telefono, state.comunaSeleccionada) },
                    etiqueta = "Apellidos *",
                    icono = Icons.Default.PersonOutline,
                    errorMensaje = state.errorApellidos
                )

                CampoTextoValidado(
                    valor = state.correo,
                    onValorChange = { onFormChange(state.rut, state.nombres, state.apellidos, it, state.password, state.confirmarPassword, state.telefono, state.comunaSeleccionada) },
                    etiqueta = "Correo electrónico *",
                    icono = Icons.Default.Email,
                    placeholder = "ej. usuario@correo.com",
                    keyboardType = KeyboardType.Email,
                    errorMensaje = state.errorCorreo
                )

                CampoTextoValidado(
                    valor = state.telefono,
                    onValorChange = { onFormChange(state.rut, state.nombres, state.apellidos, state.correo, state.password, state.confirmarPassword, it, state.comunaSeleccionada) },
                    etiqueta = "Teléfono móvil (opcional)",
                    icono = Icons.Default.Phone,
                    placeholder = "+56 9 1234 5678",
                    keyboardType = KeyboardType.Phone
                )

                SelectorCatalogo(
                    etiqueta = "Comuna de residencia",
                    opciones = comunas.ifEmpty { listOf("Maipú", "Puente Alto", "San Miguel", "La Florida", "Ñuñoa") },
                    seleccionado = state.comunaSeleccionada,
                    onSeleccionar = { onFormChange(state.rut, state.nombres, state.apellidos, state.correo, state.password, state.confirmarPassword, state.telefono, it) },
                    icono = Icons.Default.LocationOn
                )

                OutlinedTextField(
                    value = state.password,
                    onValueChange = { onFormChange(state.rut, state.nombres, state.apellidos, state.correo, it, state.confirmarPassword, state.telefono, state.comunaSeleccionada) },
                    label = { Text("Contraseña *") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    trailingIcon = {
                        IconButton(onClick = { mostrarPassword = !mostrarPassword }) {
                            Icon(if (mostrarPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null)
                        }
                    },
                    visualTransformation = if (mostrarPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = state.errorPassword != null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                if (state.errorPassword != null) {
                    Text(state.errorPassword, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                OutlinedTextField(
                    value = state.confirmarPassword,
                    onValueChange = { onFormChange(state.rut, state.nombres, state.apellidos, state.correo, state.password, it, state.telefono, state.comunaSeleccionada) },
                    label = { Text("Confirmar Contraseña *") },
                    leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    visualTransformation = if (mostrarPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = state.errorConfirmarPassword != null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                if (state.errorConfirmarPassword != null) {
                    Text(state.errorConfirmarPassword, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                if (state.mensajeResultado != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(state.mensajeResultado, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(12.dp))
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = onRegistrar,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Completar Registro", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

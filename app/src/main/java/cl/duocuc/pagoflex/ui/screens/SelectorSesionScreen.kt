package cl.duocuc.pagoflex.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cl.duocuc.pagoflex.data.model.UsuarioSesion
import cl.duocuc.pagoflex.utils.Formatters

@Composable
fun SelectorSesionScreen(
    usuarios: List<UsuarioSesion>,
    codigoActual: String,
    onSeleccionarUsuario: (UsuarioSesion) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Canal Oficial PagoFlex",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Selecciona una sesión autenticada para ingresar a la plataforma.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(usuarios) { user ->
                val seleccionado = user.codigo == codigoActual
                val iconoRol = when (user.rolCodigo) {
                    "USUARIO_FINAL" -> Icons.Default.Person
                    "AGENTE" -> Icons.Default.AdminPanelSettings
                    else -> Icons.Default.BusinessCenter
                }
                val etiquetaRol = when (user.rolCodigo) {
                    "USUARIO_FINAL" -> "Usuario Final (${user.comuna})"
                    "AGENTE" -> "Agente PagoFlex (${user.areaAgente})"
                    else -> "Ejecutivo (${user.nombreEmpresaEjecutivo})"
                }

                OutlinedCard(
                    onClick = { onSeleccionarUsuario(user) },
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = if (seleccionado) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(iconoRol, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "${user.codigo} · ${user.nombreCompleto}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "$etiquetaRol · RUT ${Formatters.ocultarRut(user.rut)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        if (seleccionado) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge { Text("Activa") }
                        }
                    }
                }
            }
        }
    }
}
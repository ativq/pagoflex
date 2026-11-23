package cl.duocuc.pagoflex.ui.screens.usuario

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cl.duocuc.pagoflex.ui.components.BadgeEstadoAnimado
import cl.duocuc.pagoflex.ui.components.TarjetaCompromisoItem
import cl.duocuc.pagoflex.ui.viewmodel.UsuarioUiState
import cl.duocuc.pagoflex.utils.Formatters
import cl.duocuc.pagoflex.utils.RecursosNativosHelper

@Composable
fun HomeUsuarioScreen(
    uiState: UsuarioUiState,
    onCompromisoClick: (Int) -> Unit,
    onActualizarAvisos: (Boolean, Int) -> Unit
) {
    val context = LocalContext.current
    val usuario = uiState.usuarioActivo ?: return
    var mostrarConfigAvisos by remember { mutableStateOf(false) }
    var mostrarTodosCompromisos by remember { mutableStateOf(false) }

    var filtroEstadoSelected by remember { mutableStateOf("Todos") }
    var filtroEmpresaSelected by remember { mutableStateOf("Todas") }

    val empresasDisponibles = remember(uiState.compromisosVigentes) {
        listOf("Todas") + uiState.compromisosVigentes.map { it.nombreFantasiaEmpresa }.distinct()
    }

    val compromisosFiltrados = remember(uiState.compromisosVigentes, filtroEstadoSelected, filtroEmpresaSelected) {
        uiState.compromisosVigentes.filter { comp ->
            val cumpleEstado = when (filtroEstadoSelected) {
                "Pendientes" -> comp.estado == "Pendiente"
                "Vencidos" -> comp.estado == "Vencido"
                "En revisión" -> comp.estado == "En revisión"
                else -> true
            }
            val cumpleEmpresa = filtroEmpresaSelected == "Todas" || comp.nombreFantasiaEmpresa == filtroEmpresaSelected
            cumpleEstado && cumpleEmpresa
        }
    }

    val compromisosMostrados = if (mostrarTodosCompromisos) compromisosFiltrados else compromisosFiltrados.take(5)

    LazyColumn(
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Text(
                        text = "Hola, ${usuario.nombres}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "RUT ${Formatters.ocultarRut(usuario.rut)} · Actualizado: ${uiState.ultimaActualizacion}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Total por pagar este mes",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                    )
                    Text(
                        text = Formatters.formatearCLP(uiState.totalPorPagarMes),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Próximo vencimiento",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                            )
                            Text(
                                text = uiState.proximoVencimiento,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Compromisos por poner al día",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${uiState.cantidadVencidos} vencido(s)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }

        item {
            val cumpl = uiState.cumplimiento
            OutlinedCard(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFF9A825))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Tu cumplimiento (últimos 12 meses)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                if (cumpl?.porcentaje != null) {
                                    Text(
                                        text = "${cumpl.porcentaje}% (${cumpl.pagadosATiempo} de ${cumpl.considerados} pagos a tiempo)",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                } else {
                                    Text(
                                        text = "Aún no tienes historial suficiente (mín. 3 compromisos)",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                        if (cumpl != null) {
                            Spacer(modifier = Modifier.width(6.dp))
                            BadgeEstadoAnimado(estado = cumpl.categoria)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (usuario.avisosActivos) "Avisos activos: ${usuario.diasAnticipacion} día(s) antes" else "Avisos desactivados",
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        TextButton(onClick = { mostrarConfigAvisos = !mostrarConfigAvisos }) {
                            Text(if (mostrarConfigAvisos) "Cerrar" else "Configurar")
                        }
                    }

                    AnimatedVisibility(visible = mostrarConfigAvisos) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "Recibir recordatorios amigables (08:00 a 22:00)",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.weight(1f)
                                )
                                Switch(
                                    checked = usuario.avisosActivos,
                                    onCheckedChange = { activo ->
                                        onActualizarAvisos(activo, usuario.diasAnticipacion)
                                    }
                                )
                            }
                            if (usuario.avisosActivos) {
                                Text("Anticipación elegida:", style = MaterialTheme.typography.labelMedium)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 6.dp)) {
                                    listOf(1, 3, 5).forEach { dias ->
                                        FilterChip(
                                            selected = usuario.diasAnticipacion == dias,
                                            onClick = { onActualizarAvisos(true, dias) },
                                            label = { Text("$dias día(s)") }
                                        )
                                    }
                                }
                                FilledTonalButton(
                                    onClick = {
                                        RecursosNativosHelper.enviarNotificacionPositiva(
                                            context = context,
                                            titulo = "Recordatorio amigable PagoFlex 👋",
                                            mensaje = "Hola ${usuario.nombres}, recuerda que tu próximo vencimiento es el ${uiState.proximoVencimiento}. ¡Sigue manteniendo tu categoría ${cumpl?.categoria ?: "Al día"}!"
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Notifications, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Probar aviso positivo ahora")
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Filtros e información relevante",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    listOf("Todos", "Pendientes", "Vencidos", "En revisión").forEach { opcion ->
                        FilterChip(
                            selected = filtroEstadoSelected == opcion,
                            onClick = { filtroEstadoSelected = opcion },
                            label = { Text(opcion, style = MaterialTheme.typography.labelMedium) }
                        )
                    }
                }

                if (empresasDisponibles.size > 2) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Filtrar por organización:", style = MaterialTheme.typography.bodySmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                        empresasDisponibles.take(4).forEach { emp ->
                            FilterChip(
                                selected = filtroEmpresaSelected == emp,
                                onClick = { filtroEmpresaSelected = emp },
                                label = { Text(emp, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Tus compromisos vigentes (${compromisosFiltrados.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (compromisosFiltrados.isEmpty()) {
            item {
                OutlinedCard(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Text(
                        text = "No hay compromisos con los filtros seleccionados ($filtroEstadoSelected / $filtroEmpresaSelected).",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(compromisosMostrados, key = { it.idCompromiso }) { comp ->
                TarjetaCompromisoItem(
                    compromiso = comp,
                    onClick = { onCompromisoClick(comp.idCompromiso) }
                )
            }

            if (compromisosFiltrados.size > 5 && !mostrarTodosCompromisos) {
                item {
                    OutlinedButton(
                        onClick = { mostrarTodosCompromisos = true },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                    ) {
                        Text("Mostrar más compromisos (${compromisosFiltrados.size - 5} restantes)")
                    }
                }
            }
        }
    }
}

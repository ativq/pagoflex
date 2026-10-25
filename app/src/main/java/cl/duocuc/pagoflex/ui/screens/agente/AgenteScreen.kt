package cl.duocuc.pagoflex.ui.screens.agente

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cl.duocuc.pagoflex.data.model.ReporteUi
import cl.duocuc.pagoflex.ui.components.BadgeEstadoAnimado
import cl.duocuc.pagoflex.ui.components.CampoTextoValidado
import cl.duocuc.pagoflex.ui.components.SelectorCatalogo
import cl.duocuc.pagoflex.ui.viewmodel.AgenteUiState

@Composable
fun AgenteScreen(
    uiState: AgenteUiState,
    onFiltroReporteChange: (String) -> Unit,
    onTomarReporte: (Int) -> Unit,
    onRespuestaChange: (String) -> Unit,
    onResolverReporte: (ReporteUi, Boolean) -> Unit,
    onMotivoDescarteChange: (String) -> Unit,
    onCambiarEstadoRecomendacion: (cl.duocuc.pagoflex.data.model.RecomendacionUi, String) -> Unit,
    onCambiarSesionClick: () -> Unit
) {
    var tabSeleccionada by remember { mutableIntStateOf(0) }
    var reporteSeleccionadoParaResolver by remember { mutableStateOf<ReporteUi?>(null) }

    val reportesFiltrados = uiState.reportes.filter {
        uiState.filtroEstadoReporte == "Todos" || it.estado == uiState.filtroEstadoReporte
    }

    Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f, fill = false)) {
                Text(
                    text = "Panel Agente PagoFlex",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${uiState.agenteActivo?.nombreCompleto} · Área ${uiState.agenteActivo?.areaAgente}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            OutlinedButton(
                onClick = onCambiarSesionClick,
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.SwitchAccount, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(uiState.agenteActivo?.codigo ?: "AG-01", style = MaterialTheme.typography.labelMedium)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TabRow(selectedTabIndex = tabSeleccionada) {
            Tab(selected = tabSeleccionada == 0, onClick = { tabSeleccionada = 0 }, text = { Text("Reportes") })
            Tab(selected = tabSeleccionada == 1, onClick = { tabSeleccionada = 1 }, text = { Text("Recomendaciones") })
        }

        if (uiState.mensajeFeedback != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text(uiState.mensajeFeedback, modifier = Modifier.padding(10.dp), color = Color(0xFF1B5E20))
            }
        }

        if (uiState.errorCambioEstadoRec != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text(uiState.errorCambioEstadoRec, modifier = Modifier.padding(10.dp), color = MaterialTheme.colorScheme.onErrorContainer)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (tabSeleccionada == 0) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                listOf("Todos", "Abierto", "En gestión", "Resuelto").forEach { est ->
                    FilterChip(
                        selected = uiState.filtroEstadoReporte == est,
                        onClick = { onFiltroReporteChange(est) },
                        label = { Text(est) }
                    )
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize()) {
                items(reportesFiltrados, key = { it.idReporte }) { rep ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "${rep.numero} · Folio ${rep.folioCompromiso}",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f, fill = false),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    if (rep.estaAtrasado) {
                                        Badge(containerColor = MaterialTheme.colorScheme.error) {
                                            Text("ATRASADO > 2 días", color = Color.White)
                                        }
                                    }
                                    BadgeEstadoAnimado(estado = rep.estado)
                                }
                            }
                            Text("Usuario: ${rep.nombreUsuario} · Empresa: ${rep.nombreEmpresa}", style = MaterialTheme.typography.bodySmall)
                            Text("Motivo: ${rep.motivo} (Creado: ${rep.fechaCreacion})", fontWeight = FontWeight.Medium)
                            if (!rep.comentario.isNullOrBlank()) {
                                Text("Comentario: \"${rep.comentario}\"", style = MaterialTheme.typography.bodyMedium)
                            }
                            if (rep.nombreAgente != null) {
                                Text("Atendido por: ${rep.nombreAgente}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            }
                            if (rep.respuesta != null) {
                                Text("Respuesta entregada: ${rep.respuesta}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF2E7D32))
                            }

                            if (rep.estado == "Abierto") {
                                Button(onClick = { onTomarReporte(rep.idReporte) }, modifier = Modifier.fillMaxWidth()) {
                                    Text("Tomar caso")
                                }
                            } else if (rep.estado == "En gestión") {
                                OutlinedButton(
                                    onClick = { reporteSeleccionadoParaResolver = rep },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Escribir respuesta y resolver")
                                }
                            }
                        }
                    }
                }
            }
        } else {
            CampoTextoValidado(
                valor = uiState.motivoDescarteRec,
                onValorChange = onMotivoDescarteChange,
                etiqueta = "Motivo de descarte (Obligatorio al descartar)",
                icono = Icons.Default.Comment
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize()) {
                items(uiState.recomendaciones, key = { it.idRecomendacion }) { rec ->
                    var estadoElegido by remember(rec.estado) { mutableStateOf(rec.estado) }
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "${rec.numero} · ${rec.nombreOrganizacion}",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f, fill = false),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                BadgeEstadoAnimado(estado = rec.estado)
                            }
                            Text("${rec.rubro} · ${rec.comuna} · Recomendada por: ${rec.nombreUsuario}", style = MaterialTheme.typography.bodySmall)
                            if (rec.ultimoAgente != null) {
                                Text("Última gestión: ${rec.ultimoAgente}", style = MaterialTheme.typography.labelSmall)
                            }

                            if (rec.estado != "Se unió" && rec.estado != "Descartada") {
                                SelectorCatalogo(
                                    etiqueta = "Avanzar etapa",
                                    opciones = listOf("Contactada", "En evaluación", "Se unió", "Descartada"),
                                    seleccionado = estadoElegido,
                                    onSeleccionar = { estadoElegido = it },
                                    icono = Icons.Default.Timeline
                                )
                                Button(
                                    onClick = { onCambiarEstadoRecomendacion(rec, estadoElegido) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Guardar nuevo estado")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    val repResolver = reporteSeleccionadoParaResolver
    if (repResolver != null) {
        AlertDialog(
            onDismissRequest = { reporteSeleccionadoParaResolver = null },
            title = { Text("Resolver ${repResolver.numero}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Escribe la respuesta que verá ${repResolver.nombreUsuario}:")
                    CampoTextoValidado(
                        valor = uiState.respuestaTexto,
                        onValorChange = onRespuestaChange,
                        etiqueta = "Respuesta obligatoria *",
                        icono = Icons.Default.Reply,
                        errorMensaje = uiState.errorRespuesta,
                        singleLine = false
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResolverReporte(repResolver, true)
                        if (uiState.respuestaTexto.trim().length >= 5) {
                            reporteSeleccionadoParaResolver = null
                        }
                    }
                ) {
                    Text("Dar por Resuelto")
                }
            },
            dismissButton = {
                TextButton(onClick = { reporteSeleccionadoParaResolver = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
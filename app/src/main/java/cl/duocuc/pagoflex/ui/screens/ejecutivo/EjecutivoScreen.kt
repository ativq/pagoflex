package cl.duocuc.pagoflex.ui.screens.ejecutivo

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cl.duocuc.pagoflex.data.model.CompromisoUi
import cl.duocuc.pagoflex.ui.components.CampoTextoValidado
import cl.duocuc.pagoflex.ui.components.SelectorCatalogo
import cl.duocuc.pagoflex.ui.components.TarjetaCompromisoItem
import cl.duocuc.pagoflex.ui.viewmodel.EjecutivoUiState
import cl.duocuc.pagoflex.ui.viewmodel.NuevoCompromisoFormState
import cl.duocuc.pagoflex.utils.Formatters

@Composable
fun EjecutivoScreen(
    uiState: EjecutivoUiState,
    formState: NuevoCompromisoFormState,
    onMesChange: (String) -> Unit,
    onFiltroEstadoChange: (String) -> Unit,
    onFormChange: (
        rut: String,
        concepto: String,
        monto: String,
        fecha: String,
        esEnCuotas: Boolean,
        numCuota: String,
        totCuotas: String
    ) -> Unit,
    onRegistrarCompromiso: () -> Unit,
    onActualizarDatosAnulacion: (String, String) -> Unit,
    onAnularCompromiso: (Int) -> Unit,
    onCambiarSesionClick: () -> Unit
) {
    var tabSeleccionada by remember { mutableIntStateOf(0) }
    var compromisoParaAnular by remember { mutableStateOf<CompromisoUi?>(null) }

    val compromisosFiltrados = uiState.compromisosEmpresa.filter {
        uiState.filtroEstado == "Todos" || it.estado == uiState.filtroEstado
    }

    Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f, fill = false)) {
                Text(
                    text = "Portal Empresa: ${uiState.ejecutivoActivo?.nombreEmpresaEjecutivo}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${uiState.ejecutivoActivo?.nombreCompleto} · ${uiState.ejecutivoActivo?.cargoEjecutivo}",
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
                Text(uiState.ejecutivoActivo?.codigo ?: "EJ-01", style = MaterialTheme.typography.labelMedium)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TabRow(selectedTabIndex = tabSeleccionada) {
            Tab(selected = tabSeleccionada == 0, onClick = { tabSeleccionada = 0 }, text = { Text("Cobranza y Lista") })
            Tab(selected = tabSeleccionada == 1, onClick = { tabSeleccionada = 1 }, text = { Text("Registrar Cuota") })
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (tabSeleccionada == 0) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize()) {
                item {
                    val res = uiState.resumenCobranza
                    if (res != null) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        "Resumen Cobranza (${res.mesAnio})",
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f, fill = false),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        FilterChip(
                                            selected = uiState.mesSeleccionado == "09-2026",
                                            onClick = { onMesChange("09-2026") },
                                            label = { Text("Sept 2026", style = MaterialTheme.typography.labelSmall) }
                                        )
                                        FilterChip(
                                            selected = uiState.mesSeleccionado == "10-2026",
                                            onClick = { onMesChange("10-2026") },
                                            label = { Text("Oct 2026", style = MaterialTheme.typography.labelSmall) }
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Tasa de cobranza", style = MaterialTheme.typography.bodySmall)
                                        Text(
                                            text = "${res.tasaCobranza}%",
                                            style = MaterialTheme.typography.headlineMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                                        Text("Monto recaudado", style = MaterialTheme.typography.bodySmall)
                                        Text(
                                            text = Formatters.formatearCLP(res.montoRecaudado),
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Total mes: ${res.totalCompromisosMes} · Pagados: ${res.pagados} · Vencidos: ${res.vencidos} · Anulados: ${res.anulados}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                item {
                    SelectorCatalogo(
                        etiqueta = "Filtrar por estado",
                        opciones = listOf("Todos", "Pendiente", "Vencido", "En revisión", "Pagado", "Anulado"),
                        seleccionado = uiState.filtroEstado,
                        onSeleccionar = onFiltroEstadoChange,
                        icono = Icons.Default.FilterList
                    )
                }

                items(compromisosFiltrados, key = { it.idCompromiso }) { comp ->
                    Column {
                        TarjetaCompromisoItem(
                            compromiso = comp,
                            mostrarNombrePersona = true,
                            onClick = {}
                        )
                        if (comp.estado in listOf("Pendiente", "Vencido", "En revisión")) {
                            OutlinedButton(
                                onClick = { compromisoParaAnular = comp },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Anular compromiso ${comp.folio}")
                            }
                        }
                    }
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Registrar nuevo compromiso", fontWeight = FontWeight.Bold)

                            CampoTextoValidado(
                                valor = formState.rutPersona,
                                onValorChange = {
                                    onFormChange(it, formState.concepto, formState.montoTexto, formState.fechaVencimiento, formState.esEnCuotas, formState.numeroCuotaTexto, formState.totalCuotasTexto)
                                },
                                etiqueta = "RUT del cliente *",
                                icono = Icons.Default.Badge,
                                errorMensaje = formState.errorRut
                            )

                            CampoTextoValidado(
                                valor = formState.concepto,
                                onValorChange = {
                                    onFormChange(formState.rutPersona, it, formState.montoTexto, formState.fechaVencimiento, formState.esEnCuotas, formState.numeroCuotaTexto, formState.totalCuotasTexto)
                                },
                                etiqueta = "Concepto de cobro *",
                                icono = Icons.Default.Description,
                                errorMensaje = formState.errorConcepto
                            )

                            CampoTextoValidado(
                                valor = formState.montoTexto,
                                onValorChange = {
                                    onFormChange(formState.rutPersona, formState.concepto, it, formState.fechaVencimiento, formState.esEnCuotas, formState.numeroCuotaTexto, formState.totalCuotasTexto)
                                },
                                etiqueta = "Monto en pesos (CLP > 0) *",
                                icono = Icons.Default.AttachMoney,
                                keyboardType = KeyboardType.Number,
                                errorMensaje = formState.errorMonto
                            )

                            CampoTextoValidado(
                                valor = formState.fechaVencimiento,
                                onValorChange = {
                                    onFormChange(formState.rutPersona, formState.concepto, formState.montoTexto, it, formState.esEnCuotas, formState.numeroCuotaTexto, formState.totalCuotasTexto)
                                },
                                etiqueta = "Fecha de vencimiento (DD-MM-AAAA) *",
                                icono = Icons.Default.Event,
                                errorMensaje = formState.errorFecha
                            )

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CampoTextoValidado(
                                    valor = formState.numeroCuotaTexto,
                                    onValorChange = {
                                        onFormChange(formState.rutPersona, formState.concepto, formState.montoTexto, formState.fechaVencimiento, true, it, formState.totalCuotasTexto)
                                    },
                                    etiqueta = "N° Cuota",
                                    icono = Icons.Default.FormatListNumbered,
                                    keyboardType = KeyboardType.Number,
                                    errorMensaje = formState.errorCuotas,
                                    modifier = Modifier.weight(1f)
                                )
                                CampoTextoValidado(
                                    valor = formState.totalCuotasTexto,
                                    onValorChange = {
                                        onFormChange(formState.rutPersona, formState.concepto, formState.montoTexto, formState.fechaVencimiento, true, formState.numeroCuotaTexto, it)
                                    },
                                    etiqueta = "Total Cuotas",
                                    icono = Icons.Default.Numbers,
                                    keyboardType = KeyboardType.Number,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            if (formState.mensajeResultado != null) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (formState.esErrorResultado) MaterialTheme.colorScheme.errorContainer else Color(0xFFE8F5E9)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = formState.mensajeResultado,
                                        modifier = Modifier.padding(12.dp),
                                        color = if (formState.esErrorResultado) MaterialTheme.colorScheme.onErrorContainer else Color(0xFF1B5E20)
                                    )
                                }
                            }

                            Button(
                                onClick = onRegistrarCompromiso,
                                modifier = Modifier.fillMaxWidth().height(50.dp)
                            ) {
                                Icon(Icons.Default.AddCircle, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Registrar Compromiso")
                            }
                        }
                    }
                }
            }
        }
    }

    val compAnular = compromisoParaAnular
    if (compAnular != null) {
        AlertDialog(
            onDismissRequest = { compromisoParaAnular = null },
            title = { Text("Anular ${compAnular.folio}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Titular: ${compAnular.nombreUsuario} · ${Formatters.formatearCLP(compAnular.monto)}")
                    SelectorCatalogo(
                        etiqueta = "Motivo de anulación",
                        opciones = listOf("Pagado directamente a la empresa", "Repactación", "Error de registro", "Otro"),
                        seleccionado = uiState.motivoAnulacion,
                        onSeleccionar = { onActualizarDatosAnulacion(it, uiState.comentarioAnulacion) },
                        icono = Icons.Default.Cancel
                    )
                    CampoTextoValidado(
                        valor = uiState.comentarioAnulacion,
                        onValorChange = { onActualizarDatosAnulacion(uiState.motivoAnulacion, it) },
                        etiqueta = if (uiState.motivoAnulacion == "Otro") "Comentario obligatorio *" else "Comentario opcional",
                        icono = Icons.Default.Comment,
                        errorMensaje = uiState.errorAnulacion
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAnularCompromiso(compAnular.idCompromiso)
                        if (uiState.motivoAnulacion != "Otro" || uiState.comentarioAnulacion.trim().length >= 5) {
                            compromisoParaAnular = null
                        }
                    }
                ) {
                    Text("Confirmar Anulación")
                }
            },
            dismissButton = {
                TextButton(onClick = { compromisoParaAnular = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
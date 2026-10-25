package cl.duocuc.pagoflex.ui.screens.usuario

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import cl.duocuc.pagoflex.data.model.CompromisoUi
import cl.duocuc.pagoflex.ui.components.BadgeEstadoAnimado
import cl.duocuc.pagoflex.ui.components.SelectorCatalogo
import cl.duocuc.pagoflex.utils.Formatters
import cl.duocuc.pagoflex.utils.RecursosNativosHelper

@Composable
fun DetallePagoScreen(
    compromiso: CompromisoUi,
    errorOrdenPago: String?,
    comprobanteGenerado: String?,
    onConfirmarPago: (String) -> Unit,
    onIrAReportar: () -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    var medioPagoSeleccionado by remember { mutableStateOf("Tarjeta de débito") }
    var mostrarDialogoConfirmacion by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onVolver) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Detalle del Compromiso",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = compromiso.nombreFantasiaEmpresa,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f, fill = false),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BadgeEstadoAnimado(estado = compromiso.estado)
                }
                Text(
                    text = compromiso.razonSocialEmpresa,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                FilaDetalle("Folio del compromiso", compromiso.folio)
                FilaDetalle("Concepto", compromiso.concepto)
                FilaDetalle(
                    "Número de cuota",
                    if (compromiso.numeroCuota != null) "${compromiso.numeroCuota}/${compromiso.totalCuotas}" else "No aplica"
                )
                FilaDetalle("Fecha de vencimiento", compromiso.fechaVencimiento)
                FilaDetalle("Monto original", Formatters.formatearCLP(compromiso.monto))

                if (compromiso.estado == "Vencido") {
                    FilaDetalle("Días de atraso", "${compromiso.diasAtraso} días")
                    FilaDetalle("Recargo fijo por atraso", Formatters.formatearCLP(compromiso.recargoAplicado))
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Total a pagar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = Formatters.formatearCLP(compromiso.totalAPagar),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        if (!compromiso.respuestaReporte.isNullOrBlank()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Respuesta de Soporte PagoFlex:", fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                    Text(compromiso.respuestaReporte, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        AnimatedVisibility(
            visible = comprobanteGenerado != null,
            enter = scaleIn() + fadeIn()
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp).fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "¡Pago confirmado con éxito!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        text = "Comprobante generado: $comprobanteGenerado",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = onVolver) {
                        Text("Volver a mis compromisos")
                    }
                }
            }
        }

        AnimatedVisibility(visible = errorOrdenPago != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = errorOrdenPago ?: "",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        if (compromiso.estado == "Pendiente" || compromiso.estado == "Vencido") {
            SelectorCatalogo(
                etiqueta = "Medio de pago",
                opciones = listOf("Tarjeta de débito", "Tarjeta de crédito", "Transferencia", "Pago simulado"),
                seleccionado = medioPagoSeleccionado,
                onSeleccionar = { medioPagoSeleccionado = it },
                icono = Icons.Default.Payment
            )

            Text(
                text = "🔒 Canal seguro PagoFlex: Nunca pedimos ni guardamos números de tarjeta o claves bancarias.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                onClick = { mostrarDialogoConfirmacion = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Lock, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pagar ${Formatters.formatearCLP(compromiso.totalAPagar)} ahora", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = onIrAReportar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.ReportProblem, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tengo un problema con este cobro")
            }
        } else {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Este compromiso se encuentra en estado '${compromiso.estado}', por lo que no requiere ni admite pago en este momento.",
                    modifier = Modifier.padding(14.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    if (mostrarDialogoConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoConfirmacion = false },
            title = { Text("Confirmar pago") },
            text = {
                Text("¿Confirmas el pago total de ${Formatters.formatearCLP(compromiso.totalAPagar)} a ${compromiso.nombreFantasiaEmpresa} mediante $medioPagoSeleccionado?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoConfirmacion = false
                        onConfirmarPago(medioPagoSeleccionado)
                        RecursosNativosHelper.vibrarExito(context)
                    }
                ) {
                    Text("Sí, confirmar pago")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoConfirmacion = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun FilaDetalle(etiqueta: String, valor: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f, fill = false)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(valor, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}
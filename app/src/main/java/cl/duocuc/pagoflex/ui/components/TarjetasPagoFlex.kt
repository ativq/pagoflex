package cl.duocuc.pagoflex.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duocuc.pagoflex.data.model.CompromisoUi
import cl.duocuc.pagoflex.data.model.PagoDetalle
import cl.duocuc.pagoflex.utils.Formatters

@Composable
fun TarjetaCompromisoItem(
    compromiso: CompromisoUi,
    mostrarNombrePersona: Boolean = false,
    onClick: () -> Unit
) {
    val cuotaTexto = if (compromiso.numeroCuota != null && compromiso.totalCuotas != null) {
        "Cuota ${compromiso.numeroCuota}/${compromiso.totalCuotas}"
    } else "Pago único / mensual"

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = compromiso.nombreFantasiaEmpresa,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                BadgeEstadoAnimado(estado = compromiso.estado)
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${compromiso.concepto} ·$cuotaTexto",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (mostrarNombrePersona) {
                Text(
                    text = "Titular: ${compromiso.nombreUsuario} (${Formatters.ocultarRut(compromiso.rutUsuario)})",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Vencimiento: ${compromiso.fechaVencimiento}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (compromiso.estado == "Vencido") {
                        Text(
                            text = "Ponte al día (${compromiso.diasAtraso} d. desde venc. · Recargo ${Formatters.formatearCLP(compromiso.recargoAplicado)})",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFE65100),
                            fontWeight = FontWeight.Medium
                        )
                    } else if (compromiso.estado == "En revisión") {
                        Text(
                            text = "Caso en revisión por soporte PagoFlex",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF6A1B9A)
                        )
                    }
                }

                Text(
                    text = Formatters.formatearCLP(compromiso.totalAPagar),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun BadgeEstadoAnimado(
    estado: String,
    modifier: Modifier = Modifier
) {
    val (colorFondo, colorTexto) = when (estado) {
        "Pendiente", "Recibida", "Abierto" -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        "Vencido", "Descartada", "Con atraso" -> Color(0xFFFFEBEE) to Color(0xFFC62828)
        "Pagado", "Ejemplar", "Al día", "Se unió", "Resuelto", "A tiempo" -> Color(0xE8E8F5E9) to Color(0xFF2E7D32)
        "En revisión", "En gestión", "Contactada", "En evaluación" -> Color(0xFFEDE7F6) to Color(0xFF6A1B9A)
        "En camino" -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
        "Anulado" -> Color(0xFFECEFF1) to Color(0xFF37474F)
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        color = colorFondo,
        shape = RoundedCornerShape(50),
        modifier = modifier
    ) {
        Text(
            text = estado,
            color = colorTexto,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun TarjetaComprobantePago(
    pago: PagoDetalle,
    onCompartirClick: () -> Unit
) {
    val cuotaTxt = if (pago.numeroCuota != null && pago.totalCuotas != null) " · Cuota ${pago.numeroCuota}/${pago.totalCuotas}" else ""

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = pago.numeroComprobante,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                BadgeEstadoAnimado(estado = if (pago.fueATiempo) "A tiempo" else "Con atraso")
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${pago.nombreFantasiaEmpresa} · ${pago.concepto}$cuotaTxt",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Fecha pago: ${pago.fechaHora}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Medio: ${pago.medioPago}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(onClick = onCompartirClick) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Compartir comprobante",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Compartir")
                }

                Text(
                    text = Formatters.formatearCLP(pago.montoPagado),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
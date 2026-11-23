package cl.duocuc.pagoflex.ui.screens.usuario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cl.duocuc.pagoflex.data.model.CompromisoUi
import cl.duocuc.pagoflex.ui.components.CampoTextoValidado
import cl.duocuc.pagoflex.ui.components.SelectorCatalogo
import cl.duocuc.pagoflex.ui.viewmodel.ReporteFormUiState

@Composable
fun ReportarScreen(
    compromiso: CompromisoUi,
    formState: ReporteFormUiState,
    onMotivoChange: (String) -> Unit,
    onComentarioChange: (String) -> Unit,
    onEnviarReporte: () -> Unit,
    onVolver: () -> Unit
) {
    val motivos = listOf(
        "Ya pagué por otro medio",
        "No reconozco este cobro",
        "El monto no corresponde",
        "Otro"
    )

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
                text = "Reportar Problema",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    "Compromiso: ${compromiso.folio} · ${compromiso.nombreFantasiaEmpresa}",
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text("Concepto: ${compromiso.concepto}")
                Text(
                    text = "Al enviar este reporte, el compromiso pasará a estado 'En revisión' y no sumará días de atraso mientras nuestro equipo lo resuelve.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }

        SelectorCatalogo(
            etiqueta = "Motivo del reporte",
            opciones = motivos,
            seleccionado = formState.motivoSeleccionado,
            onSeleccionar = onMotivoChange,
            icono = Icons.Default.Report
        )

        CampoTextoValidado(
            valor = formState.comentario,
            onValorChange = onComentarioChange,
            etiqueta = if (formState.motivoSeleccionado == "Otro") "Comentario explicativo (Obligatorio)" else "Comentario adicional (Opcional)",
            placeholder = "Ej: Pagué por transferencia directa el 03-10...",
            icono = Icons.Default.Comment,
            errorMensaje = formState.errorComentario,
            singleLine = false
        )

        Button(
            onClick = onEnviarReporte,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Icon(Icons.Default.Send, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Enviar reporte a Soporte PagoFlex")
        }
    }
}
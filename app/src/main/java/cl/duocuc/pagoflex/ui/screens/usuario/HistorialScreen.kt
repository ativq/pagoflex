package cl.duocuc.pagoflex.ui.screens.usuario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cl.duocuc.pagoflex.ui.components.SelectorCatalogo
import cl.duocuc.pagoflex.ui.components.TarjetaComprobantePago
import cl.duocuc.pagoflex.ui.viewmodel.UsuarioUiState
import cl.duocuc.pagoflex.utils.RecursosNativosHelper

@Composable
fun HistorialScreen(
    uiState: UsuarioUiState,
    onFiltroChange: (String, String) -> Unit
) {
    val context = LocalContext.current
    val empresasDisponibles = listOf("Todas") + uiState.historialPagos.map { it.nombreFantasiaEmpresa }.distinct()
    val aniosDisponibles = listOf("Todos", "2026", "2025")

    val pagosFiltrados = uiState.historialPagos.filter { pago ->
        val cumpleEmpresa = uiState.filtroEmpresaHistorial == "Todas" || pago.nombreFantasiaEmpresa == uiState.filtroEmpresaHistorial
        val cumpleAnio = uiState.filtroAnioHistorial == "Todos" || pago.fechaHora.contains(uiState.filtroAnioHistorial)
        cumpleEmpresa && cumpleAnio
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Historial y Comprobantes",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "Incluye tus pagos realizados en la App, Portal de pagos y Agente conversacional.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            SelectorCatalogo(
                etiqueta = "Empresa",
                opciones = empresasDisponibles,
                seleccionado = uiState.filtroEmpresaHistorial,
                onSeleccionar = { onFiltroChange(it, uiState.filtroAnioHistorial) },
                icono = Icons.Default.Business,
                modifier = Modifier.weight(1f)
            )
            SelectorCatalogo(
                etiqueta = "Período",
                opciones = aniosDisponibles,
                seleccionado = uiState.filtroAnioHistorial,
                onSeleccionar = { onFiltroChange(uiState.filtroEmpresaHistorial, it) },
                icono = Icons.Default.CalendarMonth,
                modifier = Modifier.weight(1f)
            )
        }

        if (pagosFiltrados.isEmpty()) {
            Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Text(
                    text = "No se encontraron comprobantes para el filtro seleccionado.",
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
                items(pagosFiltrados, key = { it.idPago }) { pago ->
                    TarjetaComprobantePago(
                        pago = pago,
                        onCompartirClick = {
                            RecursosNativosHelper.compartirComprobante(context, pago)
                        }
                    )
                }
            }
        }
    }
}
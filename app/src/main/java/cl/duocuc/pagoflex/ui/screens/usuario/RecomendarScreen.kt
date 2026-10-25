package cl.duocuc.pagoflex.ui.screens.usuario

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cl.duocuc.pagoflex.data.model.RecomendacionUi
import cl.duocuc.pagoflex.ui.components.BadgeEstadoAnimado
import cl.duocuc.pagoflex.ui.components.CampoTextoValidado
import cl.duocuc.pagoflex.ui.components.SelectorCatalogo
import cl.duocuc.pagoflex.ui.viewmodel.RecomendacionFormUiState

@Composable
fun RecomendarScreen(
    formState: RecomendacionFormUiState,
    recomendaciones: List<RecomendacionUi>,
    rubros: List<String>,
    comunas: List<String>,
    onFormChange: (
        nombreOrg: String,
        rutOrg: String,
        rubro: String,
        comuna: String,
        incluirContacto: Boolean,
        nombreContacto: String,
        correoContacto: String,
        telefonoContacto: String,
        autoriza: Boolean
    ) -> Unit,
    onEnviarClick: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "Recomendar una Organización",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "¿Tu jardín infantil, club o condominio aún cobra por transferencia? Recomiéndalo a PagoFlex y sigue su avance.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        val unidas = recomendaciones.filter { it.estado == "Se unió" }
        if (unidas.isNotEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color(0xFFF57F17), modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("¡Reconocimiento Impulsor PagoFlex!", fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                            Text(
                                text = "Gracias a ti, '${unidas.first().nombreOrganizacion}' se unió a PagoFlex.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        item {
            Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    CampoTextoValidado(
                        valor = formState.nombreOrganizacion,
                        onValorChange = {
                            onFormChange(it, formState.rutOrganizacion, formState.rubroSeleccionado, formState.comunaSeleccionada, formState.incluirContacto, formState.nombreContacto, formState.correoContacto, formState.telefonoContacto, formState.autorizaContacto)
                        },
                        etiqueta = "Nombre de la organización *",
                        placeholder = "Ej: Jardín Infantil Pequeños Pasos",
                        icono = Icons.Default.Domain,
                        errorMensaje = formState.errorNombreOrg
                    )

                    CampoTextoValidado(
                        valor = formState.rutOrganizacion,
                        onValorChange = {
                            onFormChange(formState.nombreOrganizacion, it, formState.rubroSeleccionado, formState.comunaSeleccionada, formState.incluirContacto, formState.nombreContacto, formState.correoContacto, formState.telefonoContacto, formState.autorizaContacto)
                        },
                        etiqueta = "RUT de la organización (Opcional)",
                        placeholder = "Ej: 65.132.874-8",
                        icono = Icons.Default.Badge,
                        errorMensaje = formState.errorRutOrg
                    )

                    SelectorCatalogo(
                        etiqueta = "Rubro *",
                        opciones = rubros.ifEmpty { listOf("Educación parvularia", "Club deportivo", "Administración de condominio") },
                        seleccionado = formState.rubroSeleccionado,
                        onSeleccionar = {
                            onFormChange(formState.nombreOrganizacion, formState.rutOrganizacion, it, formState.comunaSeleccionada, formState.incluirContacto, formState.nombreContacto, formState.correoContacto, formState.telefonoContacto, formState.autorizaContacto)
                        },
                        icono = Icons.Default.Category
                    )

                    SelectorCatalogo(
                        etiqueta = "Comuna *",
                        opciones = comunas.ifEmpty { listOf("Maipú", "Quilicura", "Santiago", "Ñuñoa") },
                        seleccionado = formState.comunaSeleccionada,
                        onSeleccionar = {
                            onFormChange(formState.nombreOrganizacion, formState.rutOrganizacion, formState.rubroSeleccionado, it, formState.incluirContacto, formState.nombreContacto, formState.correoContacto, formState.telefonoContacto, formState.autorizaContacto)
                        },
                        icono = Icons.Default.LocationOn
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "¿Deseas agregar datos de contacto? (Opcional)",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = formState.incluirContacto,
                            onCheckedChange = {
                                onFormChange(formState.nombreOrganizacion, formState.rutOrganizacion, formState.rubroSeleccionado, formState.comunaSeleccionada, it, formState.nombreContacto, formState.correoContacto, formState.telefonoContacto, formState.autorizaContacto)
                            }
                        )
                    }

                    AnimatedVisibility(visible = formState.incluirContacto) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            CampoTextoValidado(
                                valor = formState.nombreContacto,
                                onValorChange = {
                                    onFormChange(formState.nombreOrganizacion, formState.rutOrganizacion, formState.rubroSeleccionado, formState.comunaSeleccionada, formState.incluirContacto, it, formState.correoContacto, formState.telefonoContacto, formState.autorizaContacto)
                                },
                                etiqueta = "Nombre del contacto *",
                                icono = Icons.Default.Person,
                                errorMensaje = formState.errorNombreContacto
                            )

                            CampoTextoValidado(
                                valor = formState.correoContacto,
                                onValorChange = {
                                    onFormChange(formState.nombreOrganizacion, formState.rutOrganizacion, formState.rubroSeleccionado, formState.comunaSeleccionada, formState.incluirContacto, formState.nombreContacto, it, formState.telefonoContacto, formState.autorizaContacto)
                                },
                                etiqueta = "Correo del contacto",
                                icono = Icons.Default.Email,
                                keyboardType = KeyboardType.Email,
                                errorMensaje = formState.errorCorreoContacto
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = formState.autorizaContacto,
                                    onCheckedChange = {
                                        onFormChange(formState.nombreOrganizacion, formState.rutOrganizacion, formState.rubroSeleccionado, formState.comunaSeleccionada, formState.incluirContacto, formState.nombreContacto, formState.correoContacto, formState.telefonoContacto, it)
                                    }
                                )
                                Text(
                                    text = "Declaro que esta persona autorizó ser contactada por PagoFlex",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (formState.errorAutorizacion != null) {
                                Text(
                                    text = formState.errorAutorizacion,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
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
                                color = if (formState.esErrorResultado) MaterialTheme.colorScheme.onErrorContainer else Color(0xFF1B5E20),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Button(
                        onClick = onEnviarClick,
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Enviar Recomendación")
                    }
                }
            }
        }

        item {
            Text(
                text = "Mis recomendaciones enviadas (${recomendaciones.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(recomendaciones, key = { it.idRecomendacion }) { rec ->
            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
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
                    Text("${rec.rubro} · Comuna: ${rec.comuna} · Fecha: ${rec.fecha}", style = MaterialTheme.typography.bodySmall)
                    if (!rec.motivoDescarte.isNullOrBlank()) {
                        Text("Motivo descarte: ${rec.motivoDescarte}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
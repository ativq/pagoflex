package cl.duocuc.pagoflex.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duocuc.pagoflex.ui.components.CampoTextoValidado
import cl.duocuc.pagoflex.ui.components.HeaderMarcaPagoFlex
import cl.duocuc.pagoflex.ui.viewmodel.RecuperarClaveState

@Composable
fun RecuperarClaveScreen(
    state: RecuperarClaveState,
    onIdentificadorChange: (String) -> Unit,
    onEnviar: () -> Unit,
    onVolver: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onVolver) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Recuperar Contraseña",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        HeaderMarcaPagoFlex(
            titulo = "¿Problemas para ingresar?",
            subtitulo = "Ingresa tu correo o RUT registrado para enviarte las instrucciones de restablecimiento de clave."
        )

        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                CampoTextoValidado(
                    valor = state.identificador,
                    onValorChange = onIdentificadorChange,
                    etiqueta = "Correo electrónico o RUT *",
                    icono = Icons.Default.Email,
                    placeholder = "ej. crojas@correo.ejemplo",
                    errorMensaje = state.errorIdentificador
                )

                if (state.mensajeResultado != null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (state.esErrorResultado) MaterialTheme.colorScheme.errorContainer else Color(0xFFE8F5E9)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = state.mensajeResultado,
                            color = if (state.esErrorResultado) MaterialTheme.colorScheme.onErrorContainer else Color(0xFF1B5E20),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }

                Button(
                    onClick = onEnviar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Recuperar Clave", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

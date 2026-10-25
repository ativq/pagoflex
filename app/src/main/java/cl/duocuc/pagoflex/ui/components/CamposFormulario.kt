package cl.duocuc.pagoflex.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun CampoTextoValidado(
    valor: String,
    onValorChange: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    errorMensaje: String? = null,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier
) {
    val esError = errorMensaje != null

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = valor,
            onValueChange = onValorChange,
            label = { Text(etiqueta) },
            placeholder = if (placeholder.isNotEmpty()) { { Text(placeholder) } } else null,
            leadingIcon = {
                Icon(
                    imageVector = icono,
                    contentDescription = etiqueta,
                    tint = if (esError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                if (esError) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error de validación",
                        tint = MaterialTheme.colorScheme.error
                    )
                } else if (valor.isNotBlank()) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Campo válido",
                        tint = Color(0xFF2E7D32)
                    )
                }
            },
            isError = esError,
            singleLine = singleLine,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        AnimatedVisibility(
            visible = esError,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = errorMensaje ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun SelectorCatalogo(
    etiqueta: String,
    opciones: List<String>,
    seleccionado: String,
    onSeleccionar: (String) -> Unit,
    icono: ImageVector,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = seleccionado,
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            leadingIcon = { Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            trailingIcon = {
                IconButton(onClick = { expandido = !expandido }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Desplegar opciones")
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSeleccionar(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}
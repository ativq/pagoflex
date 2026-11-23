package cl.duocuc.pagoflex

import cl.duocuc.pagoflex.data.model.ResumenCumplimiento
import org.junit.Assert.assertEquals
import org.junit.Test

class BusinessRulesUnitTest {

    @Test
    fun testCategoriaCumplimiento_ejemplar() {
        val considerados = 20
        val aTiempo = 20
        val porcentaje = (aTiempo.toDouble() / considerados.toDouble()) * 100.0
        val categoria = when {
            porcentaje >= 95.0 -> "Ejemplar"
            porcentaje >= 80.0 -> "Al día"
            else -> "En camino"
        }
        assertEquals("Ejemplar", categoria)
    }

    @Test
    fun testCategoriaCumplimiento_alDia() {
        val considerados = 10
        val aTiempo = 8
        val porcentaje = (aTiempo.toDouble() / considerados.toDouble()) * 100.0
        val categoria = when {
            porcentaje >= 95.0 -> "Ejemplar"
            porcentaje >= 80.0 -> "Al día"
            else -> "En camino"
        }
        assertEquals("Al día", categoria)
    }

    @Test
    fun testCategoriaCumplimiento_enCamino() {
        val considerados = 10
        val aTiempo = 5
        val porcentaje = (aTiempo.toDouble() / considerados.toDouble()) * 100.0
        val categoria = when {
            porcentaje >= 95.0 -> "Ejemplar"
            porcentaje >= 80.0 -> "Al día"
            else -> "En camino"
        }
        assertEquals("En camino", categoria)
    }

    @Test
    fun testHistorialInsuficiente() {
        val considerados = 2
        val resumen = ResumenCumplimiento(considerados, 2, null, "Historial insuficiente")
        assertEquals("Historial insuficiente", resumen.categoria)
    }
}

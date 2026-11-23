package cl.duocuc.pagoflex

import cl.duocuc.pagoflex.utils.Formatters
import org.junit.Assert.*
import org.junit.Test

class FormattersUnitTest {

    @Test
    fun testValidarRut_valido() {
        assertTrue(Formatters.validarRut("15.834.207-3"))
        assertTrue(Formatters.validarRut("12476318-5"))
        assertTrue(Formatters.validarRut("16248317-K"))
    }

    @Test
    fun testValidarRut_invalido() {
        assertFalse(Formatters.validarRut("15.834.207-9"))
        assertFalse(Formatters.validarRut("12345678-9"))
        assertFalse(Formatters.validarRut("123"))
    }

    @Test
    fun testFormatearCLP() {
        assertEquals("$ 310.000", Formatters.formatearCLP(310000))
        assertEquals("$ 15.000", Formatters.formatearCLP(15000))
        assertEquals("$ 0", Formatters.formatearCLP(0))
    }

    @Test
    fun testOcultarRut() {
        assertEquals("15.*.*-3", Formatters.ocultarRut("15.834.207-3"))
        assertEquals("12.*.*-5", Formatters.ocultarRut("12.476.318-5"))
    }
}

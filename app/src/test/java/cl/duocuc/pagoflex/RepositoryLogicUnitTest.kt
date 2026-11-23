package cl.duocuc.pagoflex

import org.junit.Assert.*
import org.junit.Test

class RepositoryLogicUnitTest {

    @Test
    fun testFechaOrdenable() {
        val fecha = "30-11-2026"
        val partes = fecha.split("-")
        val ordenable = "${partes[2]}${partes[1]}${partes[0]}".toLong()
        assertEquals(20261130L, ordenable)
    }

    @Test
    fun testFormatoFechaValido() {
        val regex = Regex("^\\d{2}-\\d{2}-\\d{4}$")
        assertTrue(regex.matches("30-11-2026"))
        assertFalse(regex.matches("2026-11-30"))
    }
}

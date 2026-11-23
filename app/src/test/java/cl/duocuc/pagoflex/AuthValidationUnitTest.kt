package cl.duocuc.pagoflex

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthValidationUnitTest {

    @Test
    fun testCorreoValido() {
        val correo1 = "crojas@correo.ejemplo"
        val correo2 = "invalido.com"
        assertTrue(correo1.contains("@") && correo1.contains("."))
        assertFalse(correo2.contains("@") && correo2.contains("."))
    }

    @Test
    fun testPasswordMinLength() {
        val passCorto = "123"
        val passValido = "admin123"
        assertFalse(passCorto.length >= 6)
        assertTrue(passValido.length >= 6)
    }

    @Test
    fun testCoincidenciaPassword() {
        val pass1 = "admin123"
        val pass2 = "admin123"
        val pass3 = "otro123"
        assertTrue(pass1 == pass2)
        assertFalse(pass1 == pass3)
    }
}

package cl.duocuc.pagoflex.utils

import java.text.NumberFormat
import java.util.Locale

object Formatters {

    fun formatearCLP(monto: Int): String {
        val formato = NumberFormat.getNumberInstance(Locale("es", "CL"))
        return "$ ${formato.format(monto)}"
    }

    fun ocultarRut(rut: String): String {
        val limpio = rut.replace(".", "").trim()
        val partes = limpio.split("-")
        if (partes.size != 2 || partes[0].length < 2) return rut
        val prefijo = partes[0].take(2)
        val dv = partes[1]
        return "$prefijo.*.*-$dv"
    }

    fun validarRut(rut: String): Boolean {
        val limpio = rut.replace(".", "").replace("-", "").trim().uppercase()
        if (limpio.length < 8 || !limpio.matches(Regex("^[0-9]+[0-9K]$"))) return false

        val cuerpo = limpio.dropLast(1)
        val dvIngresado = limpio.last()

        var suma = 0
        var multiplicador = 2
        for (i in cuerpo.reversed()) {
            suma += i.toString().toInt() * multiplicador
            multiplicador = if (multiplicador == 7) 2 else multiplicador + 1
        }

        val resto = 11 - (suma % 11)
        val dvEsperado = when (resto) {
            11 -> '0'
            10 -> 'K'
            else -> resto.toString().first()
        }
        return dvIngresado == dvEsperado
    }
}
package cl.duocuc.pagoflex.data.local

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SesionPreferencias(context: Context) {
    private val prefs = context.getSharedPreferences("pagoflex_prefs", Context.MODE_PRIVATE)

    fun guardarUsuarioActivo(codigoUsuario: String) {
        prefs.edit()
            .putString("codigo_usuario", codigoUsuario)
            .putString("ultima_actualizacion", obtenerFechaHoraActual())
            .apply()
    }

    fun obtenerCodigoUsuarioActivo(): String {
        return prefs.getString("codigo_usuario", "UF-01") ?: "UF-01"
    }

    fun obtenerUltimaActualizacion(): String {
        return prefs.getString("ultima_actualizacion", "06-10-2026 09:30") ?: "06-10-2026 09:30"
    }

    fun registrarSincronizacion() {
        prefs.edit().putString("ultima_actualizacion", obtenerFechaHoraActual()).apply()
    }

    private fun obtenerFechaHoraActual(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }
}
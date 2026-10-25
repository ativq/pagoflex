package cl.duocuc.pagoflex.navigation

object Rutas {
    const val SELECTOR_SESION = "selector_sesion"

    const val HOME_USUARIO = "home_usuario"
    const val DETALLE_PAGO = "detalle_pago/{idCompromiso}"
    const val REPORTAR = "reportar/{idCompromiso}"
    const val HISTORIAL = "historial"
    const val RECOMENDAR = "recomendar"

    const val HOME_AGENTE = "home_agente"

    const val HOME_EJECUTIVO = "home_ejecutivo"

    fun rutaDetallePago(idCompromiso: Int): String = "detalle_pago/$idCompromiso"
    fun rutaReportar(idCompromiso: Int): String = "reportar/$idCompromiso"
}
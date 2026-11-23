package cl.duocuc.pagoflex.data.model

data class UsuarioSesion(
    val idUsuario: Int,
    val codigo: String,
    val rolCodigo: String,
    val nombres: String,
    val apellidos: String,
    val rut: String,
    val correo: String,
    val telefono: String?,
    val comuna: String?,
    val avisosActivos: Boolean,
    val diasAnticipacion: Int,
    val areaAgente: String?,
    val idEmpresaEjecutivo: Int?,
    val nombreEmpresaEjecutivo: String?,
    val cargoEjecutivo: String?
) {
    val nombreCompleto: String get() = "$nombres $apellidos"
}

data class CompromisoUi(
    val idCompromiso: Int,
    val folio: String,
    val idUsuarioFinal: Int,
    val nombreUsuario: String,
    val rutUsuario: String,
    val idEmpresa: Int,
    val nombreFantasiaEmpresa: String,
    val razonSocialEmpresa: String,
    val concepto: String,
    val numeroCuota: Int?,
    val totalCuotas: Int?,
    val monto: Int,
    val fechaVencimiento: String,
    val estado: String,
    val recargoAplicado: Int,
    val diasAtraso: Int,
    val totalAPagar: Int,
    val tieneReporteAbierto: Boolean = false,
    val respuestaReporte: String? = null,
    val motivoAnulacion: String? = null
)

data class PagoDetalle(
    val idPago: Int,
    val numeroComprobante: String,
    val folioCompromiso: String,
    val nombreUsuario: String,
    val rutUsuario: String,
    val idEmpresa: Int,
    val nombreFantasiaEmpresa: String,
    val razonSocialEmpresa: String,
    val concepto: String,
    val numeroCuota: Int?,
    val totalCuotas: Int?,
    val fechaVencimiento: String,
    val fechaHora: String,
    val montoOriginal: Int,
    val recargoAplicado: Int,
    val montoPagado: Int,
    val canal: String,
    val medioPago: String,
    val fueATiempo: Boolean
)

data class ReporteUi(
    val idReporte: Int,
    val numero: String,
    val idCompromiso: Int,
    val folioCompromiso: String,
    val conceptoCompromiso: String,
    val nombreEmpresa: String,
    val nombreUsuario: String,
    val motivo: String,
    val comentario: String?,
    val fechaCreacion: String,
    val estado: String,
    val nombreAgente: String?,
    val respuesta: String?,
    val fechaResolucion: String?,
    val estaAtrasado: Boolean
)

data class RecomendacionUi(
    val idRecomendacion: Int,
    val numero: String,
    val idUsuarioFinal: Int,
    val nombreUsuario: String,
    val nombreOrganizacion: String,
    val rutOrganizacion: String?,
    val rubro: String,
    val comuna: String,
    val nombreContacto: String?,
    val datoContacto: String?,
    val fecha: String,
    val estado: String,
    val ordenEstado: Int,
    val motivoDescarte: String?,
    val ultimoAgente: String?
)

data class ResumenCumplimiento(
    val considerados: Int,
    val pagadosATiempo: Int,
    val porcentaje: Double?,
    val categoria: String
)

data class ResumenCobranzaEmpresa(
    val nombreEmpresa: String,
    val mesAnio: String,
    val totalCompromisosMes: Int,
    val anulados: Int,
    val pagados: Int,
    val pendientes: Int,
    val vencidos: Int,
    val montoRecaudado: Int,
    val tasaCobranza: Double
)
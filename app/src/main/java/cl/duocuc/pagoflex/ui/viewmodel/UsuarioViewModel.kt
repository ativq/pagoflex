package cl.duocuc.pagoflex.ui.viewmodel

import androidx.lifecycle.ViewModel
import cl.duocuc.pagoflex.data.local.SesionPreferencias
import cl.duocuc.pagoflex.data.model.*
import cl.duocuc.pagoflex.data.repository.PagoFlexRepository
import cl.duocuc.pagoflex.utils.Formatters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ReporteFormUiState(
    val motivoSeleccionado: String = "Ya pagué por otro medio",
    val comentario: String = "",
    val errorComentario: String? = null,
    val guardadoExitoso: Boolean = false
)

data class RecomendacionFormUiState(
    val nombreOrganizacion: String = "",
    val errorNombreOrg: String? = null,
    val rutOrganizacion: String = "",
    val errorRutOrg: String? = null,
    val rubroSeleccionado: String = "Educación parvularia",
    val comunaSeleccionada: String = "Maipú",
    val incluirContacto: Boolean = false,
    val nombreContacto: String = "",
    val errorNombreContacto: String? = null,
    val correoContacto: String = "",
    val errorCorreoContacto: String? = null,
    val telefonoContacto: String = "",
    val autorizaContacto: Boolean = false,
    val errorAutorizacion: String? = null,
    val mensajeResultado: String? = null,
    val esErrorResultado: Boolean = false
)

data class UsuarioUiState(
    val usuarioActivo: UsuarioSesion? = null,
    val ultimaActualizacion: String = "",
    val compromisosVigentes: List<CompromisoUi> = emptyList(),
    val totalPorPagarMes: Int = 0,
    val proximoVencimiento: String = "Sin pendientes",
    val cantidadVencidos: Int = 0,
    val cumplimiento: ResumenCumplimiento? = null,
    val historialPagos: List<PagoDetalle> = emptyList(),
    val recomendaciones: List<RecomendacionUi> = emptyList(),
    val filtroEmpresaHistorial: String = "Todas",
    val filtroAnioHistorial: String = "2026",
    val ultimoComprobanteGenerado: String? = null,
    val errorOrdenPagoRn04: String? = null
)

class UsuarioViewModel(
    private val repository: PagoFlexRepository,
    private val preferencias: SesionPreferencias
) : ViewModel() {

    private val _uiState = MutableStateFlow(UsuarioUiState())
    val uiState: StateFlow<UsuarioUiState> = _uiState.asStateFlow()

    private val _reporteForm = MutableStateFlow(ReporteFormUiState())
    val reporteForm: StateFlow<ReporteFormUiState> = _reporteForm.asStateFlow()

    private val _recomendacionForm = MutableStateFlow(RecomendacionFormUiState())
    val recomendacionForm: StateFlow<RecomendacionFormUiState> = _recomendacionForm.asStateFlow()

    init {
        cargarDatosUsuarioActivo()
    }

    fun cargarDatosUsuarioActivo() {
        val codigo = preferencias.obtenerCodigoUsuarioActivo()
        val usuario = repository.obtenerUsuarioPorCodigo(codigo) ?: return
        val todosCompromisos = repository.obtenerCompromisosUsuario(usuario.idUsuario)
        val vigentes = todosCompromisos.filter { it.estado in listOf("Pendiente", "Vencido", "En revisión") }

        val porPagar = vigentes.filter { it.estado == "Pendiente" || it.estado == "Vencido" }
        val totalMes = porPagar.sumOf { it.totalAPagar }
        val vencidosCount = vigentes.count { it.estado == "Vencido" }
        val proximoVenc = vigentes.firstOrNull { it.estado == "Pendiente" }?.fechaVencimiento ?: "Al día"

        _uiState.update {
            it.copy(
                usuarioActivo = usuario,
                ultimaActualizacion = preferencias.obtenerUltimaActualizacion(),
                compromisosVigentes = vigentes,
                totalPorPagarMes = totalMes,
                proximoVencimiento = proximoVenc,
                cantidadVencidos = vencidosCount,
                cumplimiento = repository.obtenerIndicadorCumplimiento(usuario.idUsuario),
                historialPagos = repository.obtenerPagosUsuario(usuario.idUsuario),
                recomendaciones = repository.obtenerRecomendaciones(usuario.idUsuario),
                errorOrdenPagoRn04 = null
            )
        }
    }

    fun cambiarSesionSimulada(codigoUsuario: String) {
        preferencias.guardarUsuarioActivo(codigoUsuario)
        cargarDatosUsuarioActivo()
    }


    fun intentarPagarCompromiso(compromiso: CompromisoUi, medioPago: String, onExito: (String) -> Unit) {
        val usuario = _uiState.value.usuarioActivo ?: return

        if (compromiso.estado == "En revisión" || compromiso.estado == "Anulado") {
            _uiState.update { it.copy(errorOrdenPagoRn04 = "Este compromiso está ${compromiso.estado} y no admite pago.") }
            return
        }

        val masAntiguo = repository.buscarCompromisoMasAntiguoPendiente(
            idUsuarioFinal = usuario.idUsuario,
            idEmpresa = compromiso.idEmpresa,
            fechaVencimientoActual = compromiso.fechaVencimiento,
            idCompromisoActual = compromiso.idCompromiso
        )

        if (masAntiguo != null) {
            val cuotaTxt = if (masAntiguo.numeroCuota != null) "cuota ${masAntiguo.numeroCuota}/${masAntiguo.totalCuotas}" else masAntiguo.concepto
            _uiState.update {
                it.copy(
                    errorOrdenPagoRn04 = "Para mantener el orden con ${compromiso.nombreFantasiaEmpresa}, primero debes pagar la $cuotaTxt (${masAntiguo.folio}) con vencimiento ${masAntiguo.fechaVencimiento}."
                )
            }
            return
        }

        val numComprobante = repository.pagarCompromiso(compromiso.idCompromiso, compromiso.totalAPagar, medioPago)
        preferencias.registrarSincronizacion()
        cargarDatosUsuarioActivo()
        _uiState.update { it.copy(ultimoComprobanteGenerado = numComprobante, errorOrdenPagoRn04 = null) }
        onExito(numComprobante)
    }

    fun limpiarMensajePago() {
        _uiState.update { it.copy(errorOrdenPagoRn04 = null, ultimoComprobanteGenerado = null) }
    }


    fun actualizarFiltroHistorial(empresa: String, anio: String) {
        _uiState.update { it.copy(filtroEmpresaHistorial = empresa, filtroAnioHistorial = anio) }
    }

    fun actualizarAvisos(activos: Boolean, dias: Int) {
        val usuario = _uiState.value.usuarioActivo ?: return
        repository.actualizarPreferenciaAvisos(usuario.idUsuario, activos, dias)
        cargarDatosUsuarioActivo()
    }


    fun onMotivoReporteChange(motivo: String) {
        _reporteForm.update { it.copy(motivoSeleccionado = motivo, errorComentario = null, guardadoExitoso = false) }
    }

    fun onComentarioReporteChange(texto: String) {
        _reporteForm.update { it.copy(comentario = texto, errorComentario = null) }
    }

    fun enviarReporteProblema(idCompromiso: Int, onSuccess: () -> Unit) {
        val form = _reporteForm.value
        if (form.motivoSeleccionado == "Otro" && form.comentario.trim().length < 5) {
            _reporteForm.update { it.copy(errorComentario = "Para el motivo 'Otro' es obligatorio explicar el problema (mín. 5 caracteres).") }
            return
        }

        repository.crearReporteProblema(idCompromiso, form.motivoSeleccionado, form.comentario.trim())
        _reporteForm.update { ReporteFormUiState(guardadoExitoso = true) }
        cargarDatosUsuarioActivo()
        onSuccess()
    }


    fun actualizarCampoRecomendacion(
        nombreOrg: String = _recomendacionForm.value.nombreOrganizacion,
        rutOrg: String = _recomendacionForm.value.rutOrganizacion,
        rubro: String = _recomendacionForm.value.rubroSeleccionado,
        comuna: String = _recomendacionForm.value.comunaSeleccionada,
        incluirContacto: Boolean = _recomendacionForm.value.incluirContacto,
        nombreContacto: String = _recomendacionForm.value.nombreContacto,
        correoContacto: String = _recomendacionForm.value.correoContacto,
        telefonoContacto: String = _recomendacionForm.value.telefonoContacto,
        autoriza: Boolean = _recomendacionForm.value.autorizaContacto
    ) {
        _recomendacionForm.update {
            it.copy(
                nombreOrganizacion = nombreOrg,
                errorNombreOrg = null,
                rutOrganizacion = rutOrg,
                errorRutOrg = null,
                rubroSeleccionado = rubro,
                comunaSeleccionada = comuna,
                incluirContacto = incluirContacto,
                nombreContacto = nombreContacto,
                errorNombreContacto = null,
                correoContacto = correoContacto,
                errorCorreoContacto = null,
                telefonoContacto = telefonoContacto,
                autorizaContacto = autoriza,
                errorAutorizacion = null,
                mensajeResultado = null
            )
        }
    }

    fun enviarRecomendacion() {
        val usuario = _uiState.value.usuarioActivo ?: return
        val f = _recomendacionForm.value
        var hayError = false

        var errNombre: String? = null
        var errRut: String? = null
        var errNomContacto: String? = null
        var errCorreo: String? = null
        var errAutoriza: String? = null

        if (f.nombreOrganizacion.trim().length < 3) {
            errNombre = "Ingresa el nombre de la organización (mínimo 3 caracteres)"
            hayError = true
        }

        if (f.rutOrganizacion.isNotBlank() && !Formatters.validarRut(f.rutOrganizacion)) {
            errRut = "El RUT ingresado no es válido."
            hayError = true
        }

        if (f.incluirContacto) {
            if (f.nombreContacto.trim().length < 3) {
                errNomContacto = "Ingresa el nombre de la persona de contacto"
                hayError = true
            }
            if (f.correoContacto.isNotBlank() && !f.correoContacto.contains("@")) {
                errCorreo = "Ingresa un correo electrónico válido"
                hayError = true
            }
            if (!f.autorizaContacto) {
                errAutoriza = "Debes confirmar que el contacto autorizó ser contactado por PagoFlex."
                hayError = true
            }
        }

        if (hayError) {
            _recomendacionForm.update {
                it.copy(
                    errorNombreOrg = errNombre,
                    errorRutOrg = errRut,
                    errorNombreContacto = errNomContacto,
                    errorCorreoContacto = errCorreo,
                    errorAutorizacion = errAutoriza
                )
            }
            return
        }

        val resultado = repository.registrarRecomendacion(
            idUsuarioFinal = usuario.idUsuario,
            nombreOrg = f.nombreOrganizacion,
            rutOrg = f.rutOrganizacion,
            rubroNombre = f.rubroSeleccionado,
            comunaNombre = f.comunaSeleccionada,
            nombreContacto = if (f.incluirContacto) f.nombreContacto else null,
            correoContacto = if (f.incluirContacto) f.correoContacto else null,
            telefonoContacto = if (f.incluirContacto) f.telefonoContacto else null,
            autorizaContacto = f.autorizaContacto
        )

        resultado.fold(
            onSuccess = { numRec ->
                _recomendacionForm.value = RecomendacionFormUiState(
                    mensajeResultado = "¡Recomendación registrada con éxito ($numRec) en estado Recibida!",
                    esErrorResultado = false
                )
                cargarDatosUsuarioActivo()
            },
            onFailure = { error ->
                _recomendacionForm.update {
                    it.copy(mensajeResultado = error.message, esErrorResultado = true)
                }
            }
        )
    }
}
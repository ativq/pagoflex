package cl.duocuc.pagoflex.ui.viewmodel

import androidx.lifecycle.ViewModel
import cl.duocuc.pagoflex.data.local.PagoFlexDbHelper
import cl.duocuc.pagoflex.data.local.SesionPreferencias
import cl.duocuc.pagoflex.data.model.CompromisoUi
import cl.duocuc.pagoflex.data.model.ResumenCobranzaEmpresa
import cl.duocuc.pagoflex.data.model.UsuarioSesion
import cl.duocuc.pagoflex.data.repository.PagoFlexRepository
import cl.duocuc.pagoflex.utils.Formatters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class NuevoCompromisoFormState(
    val rutPersona: String = "12.476.318-5",
    val errorRut: String? = null,
    val concepto: String = "Cuota pie departamento",
    val errorConcepto: String? = null,
    val montoTexto: String = "310000",
    val errorMonto: String? = null,
    val fechaVencimiento: String = "30-11-2026",
    val errorFecha: String? = null,
    val esEnCuotas: Boolean = true,
    val numeroCuotaTexto: String = "16",
    val totalCuotasTexto: String = "36",
    val errorCuotas: String? = null,
    val nombreNuevoUsuario: String = "",
    val correoNuevoUsuario: String = "",
    val mensajeResultado: String? = null,
    val esErrorResultado: Boolean = false
)

data class EjecutivoUiState(
    val ejecutivoActivo: UsuarioSesion? = null,
    val compromisosEmpresa: List<CompromisoUi> = emptyList(),
    val mesSeleccionado: String = "09-2026",
    val filtroEstado: String = "Todos",
    val resumenCobranza: ResumenCobranzaEmpresa? = null,
    val motivoAnulacion: String = "Pagado directamente a la empresa",
    val comentarioAnulacion: String = "",
    val errorAnulacion: String? = null,
    val mensajeFeedback: String? = null
)

class EjecutivoViewModel(
    private val repository: PagoFlexRepository,
    private val preferencias: SesionPreferencias
) : ViewModel() {

    private val _uiState = MutableStateFlow(EjecutivoUiState())
    val uiState: StateFlow<EjecutivoUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(NuevoCompromisoFormState())
    val formState: StateFlow<NuevoCompromisoFormState> = _formState.asStateFlow()

    init {
        cargarDatosEjecutivo()
    }

    fun cargarDatosEjecutivo() {
        val codigo = preferencias.obtenerCodigoUsuarioActivo()
        val ejecutivo = repository.obtenerUsuarioPorCodigo(codigo) ?: return
        val idEmpresa = ejecutivo.idEmpresaEjecutivo ?: 1
        val nombreEmpresa = ejecutivo.nombreEmpresaEjecutivo ?: "Altos del Maipo"

        val compromisos = repository.obtenerCompromisosPorEmpresa(idEmpresa)
        val resumen = repository.obtenerResumenCobranza(idEmpresa, nombreEmpresa, _uiState.value.mesSeleccionado)

        _uiState.update {
            it.copy(
                ejecutivoActivo = ejecutivo,
                compromisosEmpresa = compromisos,
                resumenCobranza = resumen
            )
        }
    }

    fun cambiarMesResumen(mesAnio: String) {
        _uiState.update { it.copy(mesSeleccionado = mesAnio) }
        cargarDatosEjecutivo()
    }

    fun cambiarFiltroEstado(estado: String) {
        _uiState.update { it.copy(filtroEstado = estado) }
    }


    fun actualizarFormularioCompromiso(
        rut: String = _formState.value.rutPersona,
        concepto: String = _formState.value.concepto,
        monto: String = _formState.value.montoTexto,
        fecha: String = _formState.value.fechaVencimiento,
        esEnCuotas: Boolean = _formState.value.esEnCuotas,
        numCuota: String = _formState.value.numeroCuotaTexto,
        totCuotas: String = _formState.value.totalCuotasTexto,
        nomNuevo: String = _formState.value.nombreNuevoUsuario,
        correoNuevo: String = _formState.value.correoNuevoUsuario
    ) {
        _formState.update {
            it.copy(
                rutPersona = rut, errorRut = null,
                concepto = concepto, errorConcepto = null,
                montoTexto = monto, errorMonto = null,
                fechaVencimiento = fecha, errorFecha = null,
                esEnCuotas = esEnCuotas,
                numeroCuotaTexto = numCuota, totalCuotasTexto = totCuotas, errorCuotas = null,
                nombreNuevoUsuario = nomNuevo, correoNuevoUsuario = correoNuevo,
                mensajeResultado = null
            )
        }
    }

    fun registrarCompromiso() {
        val f = _formState.value
        val ejecutivo = _uiState.value.ejecutivoActivo ?: return
        val idEmpresa = ejecutivo.idEmpresaEjecutivo ?: 1

        var errRut: String? = null
        var errConcepto: String? = null
        var errMonto: String? = null
        var errFecha: String? = null
        var errCuotas: String? = null
        var hayError = false

        if (!Formatters.validarRut(f.rutPersona)) {
            errRut = "RUT inválido. Verifica el dígito verificador."
            hayError = true
        }

        if (f.concepto.trim().length < 4) {
            errConcepto = "Ingresa un concepto descriptivo (mín. 4 caracteres)"
            hayError = true
        }

        val montoInt = f.montoTexto.toIntOrNull()
        if (montoInt == null || montoInt <= 0) {
            errMonto = "El monto debe ser un número entero en pesos mayor a $ 0."
            hayError = true
        }

        if (!f.fechaVencimiento.matches(Regex("^\\d{2}-\\d{2}-\\d{4}$"))) {
            errFecha = "Usa el formato DD-MM-AAAA (ej. 30-11-2026)."
            hayError = true
        }

        var nCuota: Int? = null
        var tCuotas: Int? = null
        if (f.esEnCuotas) {
            nCuota = f.numeroCuotaTexto.toIntOrNull()
            tCuotas = f.totalCuotasTexto.toIntOrNull()
            if (nCuota == null || tCuotas == null || nCuota <= 0 || nCuota > tCuotas) {
                errCuotas = "La cuota debe ser menor o igual al total de cuotas."
                hayError = true
            }
        }

        if (hayError) {
            _formState.update {
                it.copy(
                    errorRut = errRut,
                    errorConcepto = errConcepto,
                    errorMonto = errMonto,
                    errorFecha = errFecha,
                    errorCuotas = errCuotas
                )
            }
            return
        }

        val resultado = repository.registrarNuevoCompromiso(
            idEjecutivo = ejecutivo.idUsuario,
            idEmpresa = idEmpresa,
            rutPersona = f.rutPersona,
            nombrePersonaNuevo = f.nombreNuevoUsuario,
            correoPersonaNuevo = f.correoNuevoUsuario,
            concepto = f.concepto,
            numeroCuota = nCuota,
            totalCuotas = tCuotas,
            monto = montoInt!!,
            fechaVencimiento = f.fechaVencimiento
        )

        resultado.fold(
            onSuccess = { folio ->
                _formState.update {
                    it.copy(
                        mensajeResultado = "¡Compromiso registrado en estado Pendiente con folio $folio!",
                        esErrorResultado = false
                    )
                }
                cargarDatosEjecutivo()
            },
            onFailure = { error ->
                _formState.update {
                    it.copy(mensajeResultado = error.message, esErrorResultado = true)
                }
            }
        )
    }


    fun actualizarDatosAnulacion(motivo: String, comentario: String) {
        _uiState.update { it.copy(motivoAnulacion = motivo, comentarioAnulacion = comentario, errorAnulacion = null) }
    }

    fun anularCompromiso(idCompromiso: Int, onSuccess: () -> Unit) {
        val state = _uiState.value
        val ejecutivo = state.ejecutivoActivo ?: return

        if (state.motivoAnulacion == "Otro" && state.comentarioAnulacion.trim().length < 5) {
            _uiState.update { it.copy(errorAnulacion = "Para el motivo 'Otro' debes ingresar un comentario explicativo.") }
            return
        }

        val resultado = repository.anularCompromiso(
            idCompromiso = idCompromiso,
            idEjecutivo = ejecutivo.idUsuario,
            motivoNombre = state.motivoAnulacion,
            comentario = state.comentarioAnulacion.trim()
        )

        resultado.fold(
            onSuccess = {
                _uiState.update { it.copy(comentarioAnulacion = "", errorAnulacion = null, mensajeFeedback = "Compromiso anulado correctamente.") }
                cargarDatosEjecutivo()
                onSuccess()
            },
            onFailure = { err ->
                _uiState.update { it.copy(errorAnulacion = err.message) }
            }
        )
    }

    fun limpiarFeedback() {
        _uiState.update { it.copy(mensajeFeedback = null, errorAnulacion = null) }
    }
}
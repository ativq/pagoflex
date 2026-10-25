package cl.duocuc.pagoflex.ui.viewmodel

import androidx.lifecycle.ViewModel
import cl.duocuc.pagoflex.data.local.SesionPreferencias
import cl.duocuc.pagoflex.data.model.RecomendacionUi
import cl.duocuc.pagoflex.data.model.ReporteUi
import cl.duocuc.pagoflex.data.model.UsuarioSesion
import cl.duocuc.pagoflex.data.repository.PagoFlexRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AgenteUiState(
    val agenteActivo: UsuarioSesion? = null,
    val reportes: List<ReporteUi> = emptyList(),
    val filtroEstadoReporte: String = "Todos",
    val recomendaciones: List<RecomendacionUi> = emptyList(),
    val respuestaTexto: String = "",
    val errorRespuesta: String? = null,
    val motivoDescarteRec: String = "",
    val errorCambioEstadoRec: String? = null,
    val mensajeFeedback: String? = null
)

class AgenteViewModel(
    private val repository: PagoFlexRepository,
    private val preferencias: SesionPreferencias
) : ViewModel() {

    private val _uiState = MutableStateFlow(AgenteUiState())
    val uiState: StateFlow<AgenteUiState> = _uiState.asStateFlow()

    init {
        cargarDatosAgente()
    }

    fun cargarDatosAgente() {
        val codigo = preferencias.obtenerCodigoUsuarioActivo()
        val agente = repository.obtenerUsuarioPorCodigo(codigo)
        _uiState.update {
            it.copy(
                agenteActivo = agente,
                reportes = repository.obtenerTodosLosReportes(),
                recomendaciones = repository.obtenerRecomendaciones(),
                errorRespuesta = null,
                errorCambioEstadoRec = null
            )
        }
    }

    fun cambiarFiltroReporte(estado: String) {
        _uiState.update { it.copy(filtroEstadoReporte = estado) }
    }

    fun onRespuestaChange(texto: String) {
        _uiState.update { it.copy(respuestaTexto = texto, errorRespuesta = null) }
    }

    fun onMotivoDescarteChange(texto: String) {
        _uiState.update { it.copy(motivoDescarteRec = texto, errorCambioEstadoRec = null) }
    }

    fun tomarReporte(idReporte: Int) {
        val idAgente = _uiState.value.agenteActivo?.idUsuario ?: 9
        repository.tomarReporte(idReporte, idAgente)
        cargarDatosAgente()
        _uiState.update { it.copy(mensajeFeedback = "Reporte tomado. Ahora está En gestión a tu cargo.") }
    }

    fun resolverReporte(reporte: ReporteUi, mantenerVigente: Boolean, onResuelto: () -> Unit) {
        val respuesta = _uiState.value.respuestaTexto.trim()
        if (respuesta.length < 5) {
            _uiState.update { it.copy(errorRespuesta = "Debes escribir una respuesta clara para el usuario antes de darlo por resuelto.") }
            return
        }
        val idAgente = _uiState.value.agenteActivo?.idUsuario ?: 9
        repository.resolverReporte(reporte.idReporte, reporte.idCompromiso, idAgente, respuesta, mantenerVigente)
        _uiState.update { it.copy(respuestaTexto = "", errorRespuesta = null, mensajeFeedback = "Reporte ${reporte.numero} resuelto correctamente.") }
        cargarDatosAgente()
        onResuelto()
    }

    fun actualizarEstadoRecomendacion(rec: RecomendacionUi, nuevoEstado: String) {
        val motivo = _uiState.value.motivoDescarteRec.trim()
        if (nuevoEstado == "Descartada" && motivo.length < 5) {
            _uiState.update { it.copy(errorCambioEstadoRec = "Para descartar una recomendación es obligatorio indicar el motivo.") }
            return
        }

        val idAgente = _uiState.value.agenteActivo?.idUsuario ?: 12
        val resultado = repository.cambiarEstadoRecomendacion(rec.idRecomendacion, nuevoEstado, idAgente, motivo)
        resultado.fold(
            onSuccess = {
                _uiState.update {
                    it.copy(
                        motivoDescarteRec = "",
                        errorCambioEstadoRec = null,
                        mensajeFeedback = "Recomendación ${rec.numero} actualizada a '$nuevoEstado'."
                    )
                }
                cargarDatosAgente()
            },
            onFailure = { error ->
                _uiState.update { it.copy(errorCambioEstadoRec = error.message) }
            }
        )
    }

    fun limpiarFeedback() {
        _uiState.update { it.copy(mensajeFeedback = null, errorCambioEstadoRec = null, errorRespuesta = null) }
    }
}
package cl.duocuc.pagoflex.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cl.duocuc.pagoflex.data.repository.PagoFlexRepository
import cl.duocuc.pagoflex.ui.screens.SelectorSesionScreen
import cl.duocuc.pagoflex.ui.screens.agente.AgenteScreen
import cl.duocuc.pagoflex.ui.screens.ejecutivo.EjecutivoScreen
import cl.duocuc.pagoflex.ui.screens.usuario.*
import cl.duocuc.pagoflex.ui.viewmodel.AgenteViewModel
import cl.duocuc.pagoflex.ui.viewmodel.EjecutivoViewModel
import cl.duocuc.pagoflex.ui.viewmodel.UsuarioViewModel

@Composable
fun AppNavegacion(
    repository: PagoFlexRepository,
    usuarioViewModel: UsuarioViewModel,
    agenteViewModel: AgenteViewModel,
    ejecutivoViewModel: EjecutivoViewModel
) {
    val navController = rememberNavController()
    val usuarioState by usuarioViewModel.uiState.collectAsState()
    val reporteFormState by usuarioViewModel.reporteForm.collectAsState()
    val recomendacionFormState by usuarioViewModel.recomendacionForm.collectAsState()
    val agenteState by agenteViewModel.uiState.collectAsState()
    val ejecutivoState by ejecutivoViewModel.uiState.collectAsState()
    val ejecutivoFormState by ejecutivoViewModel.formState.collectAsState()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route ?: "home_usuario"
    val rolActivo = usuarioState.usuarioActivo?.rolCodigo ?: "USUARIO_FINAL"

    val rubros = remember { repository.obtenerRubros() }
    val comunas = remember { repository.obtenerComunas() }

    Scaffold(
        bottomBar = {
            if (rolActivo == "USUARIO_FINAL" && rutaActual in listOf("home_usuario", "historial", "recomendar", "selector_sesion")) {
                NavigationBar {
                    NavigationBarItem(
                        selected = rutaActual == "home_usuario",
                        onClick = { navController.navigate("home_usuario") { launchSingleTop = true } },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Inicio") }
                    )
                    NavigationBarItem(
                        selected = rutaActual == "historial",
                        onClick = { navController.navigate("historial") { launchSingleTop = true } },
                        icon = { Icon(Icons.Default.ReceiptLong, contentDescription = null) },
                        label = { Text("Comprobantes") }
                    )
                    NavigationBarItem(
                        selected = rutaActual == "recomendar",
                        onClick = { navController.navigate("recomendar") { launchSingleTop = true } },
                        icon = { Icon(Icons.Default.Share, contentDescription = null) },
                        label = { Text("Recomendar") }
                    )
                    NavigationBarItem(
                        selected = rutaActual == "selector_sesion",
                        onClick = { navController.navigate("selector_sesion") { launchSingleTop = true } },
                        icon = { Icon(Icons.Default.SwitchAccount, contentDescription = null) },
                        label = { Text("Rol / Sesión") }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = when (rolActivo) {
                "AGENTE" -> "home_agente"
                "EJECUTIVO" -> "home_ejecutivo"
                else -> "home_usuario"
            },
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("selector_sesion") {
                val todosUsuarios = remember { repository.obtenerUsuariosParaSelector() }
                SelectorSesionScreen(
                    usuarios = todosUsuarios,
                    codigoActual = usuarioState.usuarioActivo?.codigo ?: "UF-01",
                    onSeleccionarUsuario = { user ->
                        usuarioViewModel.cambiarSesionSimulada(user.codigo)
                        agenteViewModel.cargarDatosAgente()
                        ejecutivoViewModel.cargarDatosEjecutivo()
                        val destino = when (user.rolCodigo) {
                            "AGENTE" -> "home_agente"
                            "EJECUTIVO" -> "home_ejecutivo"
                            else -> "home_usuario"
                        }
                        navController.navigate(destino) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable("home_usuario") {
                HomeUsuarioScreen(
                    uiState = usuarioState,
                    onCompromisoClick = { idComp ->
                        usuarioViewModel.limpiarMensajePago()
                        navController.navigate("detalle_pago/$idComp")
                    },
                    onActualizarAvisos = { act, dias -> usuarioViewModel.actualizarAvisos(act, dias) },
                    onCambiarSesionClick = { navController.navigate("selector_sesion") }
                )
            }

            composable("detalle_pago/{idCompromiso}") { entry ->
                val idComp = entry.arguments?.getString("idCompromiso")?.toIntOrNull() ?: 1
                val comp = repository.obtenerCompromisoPorId(idComp)
                if (comp != null) {
                    DetallePagoScreen(
                        compromiso = comp,
                        errorOrdenPago = usuarioState.errorOrdenPagoRn04,
                        comprobanteGenerado = usuarioState.ultimoComprobanteGenerado,
                        onConfirmarPago = { medio ->
                            usuarioViewModel.intentarPagarCompromiso(comp, medio) {}
                        },
                        onIrAReportar = { navController.navigate("reportar/$idComp") },
                        onVolver = { navController.popBackStack() }
                    )
                }
            }

            composable("reportar/{idCompromiso}") { entry ->
                val idComp = entry.arguments?.getString("idCompromiso")?.toIntOrNull() ?: 1
                val comp = repository.obtenerCompromisoPorId(idComp)
                if (comp != null) {
                    ReportarScreen(
                        compromiso = comp,
                        formState = reporteFormState,
                        onMotivoChange = usuarioViewModel::onMotivoReporteChange,
                        onComentarioChange = usuarioViewModel::onComentarioReporteChange,
                        onEnviarReporte = {
                            usuarioViewModel.enviarReporteProblema(idComp) {
                                navController.popBackStack("home_usuario", inclusive = false)
                            }
                        },
                        onVolver = { navController.popBackStack() }
                    )
                }
            }

            composable("historial") {
                HistorialScreen(
                    uiState = usuarioState,
                    onFiltroChange = usuarioViewModel::actualizarFiltroHistorial
                )
            }

            composable("recomendar") {
                RecomendarScreen(
                    formState = recomendacionFormState,
                    recomendaciones = usuarioState.recomendaciones,
                    rubros = rubros,
                    comunas = comunas,
                    onFormChange = { nomOrg, rutOrg, rub, com, inc, nomCon, corCon, telCon, aut ->
                        usuarioViewModel.actualizarCampoRecomendacion(nomOrg, rutOrg, rub, com, inc, nomCon, corCon, telCon, aut)
                    },
                    onEnviarClick = usuarioViewModel::enviarRecomendacion
                )
            }

            composable("home_agente") {
                AgenteScreen(
                    uiState = agenteState,
                    onFiltroReporteChange = agenteViewModel::cambiarFiltroReporte,
                    onTomarReporte = agenteViewModel::tomarReporte,
                    onRespuestaChange = agenteViewModel::onRespuestaChange,
                    onResolverReporte = { rep, mantener -> agenteViewModel.resolverReporte(rep, mantener) {} },
                    onMotivoDescarteChange = agenteViewModel::onMotivoDescarteChange,
                    onCambiarEstadoRecomendacion = agenteViewModel::actualizarEstadoRecomendacion,
                    onCambiarSesionClick = { navController.navigate("selector_sesion") }
                )
            }

            composable("home_ejecutivo") {
                EjecutivoScreen(
                    uiState = ejecutivoState,
                    formState = ejecutivoFormState,
                    onMesChange = ejecutivoViewModel::cambiarMesResumen,
                    onFiltroEstadoChange = ejecutivoViewModel::cambiarFiltroEstado,
                    onFormChange = { rut, conc, mon, fec, cuotas, nC, tC ->
                        ejecutivoViewModel.actualizarFormularioCompromiso(rut, conc, mon, fec, cuotas, nC, tC)
                    },
                    onRegistrarCompromiso = ejecutivoViewModel::registrarCompromiso,
                    onActualizarDatosAnulacion = ejecutivoViewModel::actualizarDatosAnulacion,
                    onAnularCompromiso = { idComp -> ejecutivoViewModel.anularCompromiso(idComp) {} },
                    onCambiarSesionClick = { navController.navigate("selector_sesion") }
                )
            }
        }
    }
}
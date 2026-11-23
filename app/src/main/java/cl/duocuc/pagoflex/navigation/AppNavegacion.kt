package cl.duocuc.pagoflex.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cl.duocuc.pagoflex.data.repository.PagoFlexRepository
import cl.duocuc.pagoflex.ui.screens.agente.AgenteScreen
import cl.duocuc.pagoflex.ui.screens.auth.*
import cl.duocuc.pagoflex.ui.screens.ejecutivo.EjecutivoScreen
import cl.duocuc.pagoflex.ui.screens.usuario.*
import cl.duocuc.pagoflex.ui.viewmodel.AgenteViewModel
import cl.duocuc.pagoflex.ui.viewmodel.AuthViewModel
import cl.duocuc.pagoflex.ui.viewmodel.EjecutivoViewModel
import cl.duocuc.pagoflex.ui.viewmodel.UsuarioViewModel

@Composable
fun AppNavegacion(
    repository: PagoFlexRepository,
    authViewModel: AuthViewModel,
    usuarioViewModel: UsuarioViewModel,
    agenteViewModel: AgenteViewModel,
    ejecutivoViewModel: EjecutivoViewModel
) {
    val navController = rememberNavController()
    val usuarioState by usuarioViewModel.uiState.collectAsState()
    val loginState by authViewModel.loginState.collectAsState()
    val registroState by authViewModel.registroState.collectAsState()
    val recuperarState by authViewModel.recuperarState.collectAsState()
    val perfilState by authViewModel.perfilState.collectAsState()

    val reporteFormState by usuarioViewModel.reporteForm.collectAsState()
    val recomendacionFormState by usuarioViewModel.recomendacionForm.collectAsState()
    val agenteState by agenteViewModel.uiState.collectAsState()
    val ejecutivoState by ejecutivoViewModel.uiState.collectAsState()
    val ejecutivoFormState by ejecutivoViewModel.formState.collectAsState()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route ?: Rutas.LOGIN
    val rolActivo = usuarioState.usuarioActivo?.rolCodigo ?: "USUARIO_FINAL"

    val rubros = remember { repository.obtenerRubros() }
    val comunas = remember { repository.obtenerComunas() }

    fun navegarSegunRol(rol: String) {
        usuarioViewModel.cargarDatosUsuarioActivo()
        agenteViewModel.cargarDatosAgente()
        ejecutivoViewModel.cargarDatosEjecutivo()
        val destino = when (rol) {
            "AGENTE" -> Rutas.HOME_AGENTE
            "EJECUTIVO" -> Rutas.HOME_EJECUTIVO
            else -> Rutas.HOME_USUARIO
        }
        navController.navigate(destino) {
            popUpTo(0) { inclusive = true }
        }
    }

    Scaffold(
        bottomBar = {
            if (rolActivo == "USUARIO_FINAL" && rutaActual in listOf(Rutas.HOME_USUARIO, Rutas.HISTORIAL, Rutas.RECOMENDAR, Rutas.PERFIL_USUARIO)) {
                NavigationBar {
                    NavigationBarItem(
                        selected = rutaActual == Rutas.HOME_USUARIO,
                        onClick = { navController.navigate(Rutas.HOME_USUARIO) { launchSingleTop = true } },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Inicio") }
                    )
                    NavigationBarItem(
                        selected = rutaActual == Rutas.HISTORIAL,
                        onClick = { navController.navigate(Rutas.HISTORIAL) { launchSingleTop = true } },
                        icon = { Icon(Icons.Default.ReceiptLong, contentDescription = null) },
                        label = { Text("Comprobantes") }
                    )
                    NavigationBarItem(
                        selected = rutaActual == Rutas.RECOMENDAR,
                        onClick = { navController.navigate(Rutas.RECOMENDAR) { launchSingleTop = true } },
                        icon = { Icon(Icons.Default.Share, contentDescription = null) },
                        label = { Text("Recomendar") }
                    )
                    NavigationBarItem(
                        selected = rutaActual == Rutas.PERFIL_USUARIO,
                        onClick = {
                            usuarioState.usuarioActivo?.let { authViewModel.cargarDatosPerfil(it) }
                            navController.navigate(Rutas.PERFIL_USUARIO) { launchSingleTop = true }
                        },
                        icon = { Icon(Icons.Default.Person, contentDescription = null) },
                        label = { Text("Perfil") }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Rutas.LOGIN,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Rutas.LOGIN) {
                LoginScreen(
                    state = loginState,
                    usuariosDemo = emptyList(),
                    onIdentificadorChange = authViewModel::onLoginIdentificadorChange,
                    onPasswordChange = authViewModel::onLoginPasswordChange,
                    onIniciarSesion = {
                        authViewModel.iniciarSesion { u ->
                            navegarSegunRol(u.rolCodigo)
                        }
                    },
                    onSeleccionarDemo = {},
                    onIrARegistro = { navController.navigate(Rutas.REGISTRO) },
                    onIrARecuperarClave = { navController.navigate(Rutas.RECUPERAR_CLAVE) }
                )
            }

            composable(Rutas.REGISTRO) {
                RegistroScreen(
                    state = registroState,
                    comunas = comunas,
                    onFormChange = authViewModel::onRegistroFormChange,
                    onRegistrar = {
                        authViewModel.registrarUsuario { u ->
                            navegarSegunRol(u.rolCodigo)
                        }
                    },
                    onVolverALogin = { navController.popBackStack() }
                )
            }

            composable(Rutas.RECUPERAR_CLAVE) {
                RecuperarClaveScreen(
                    state = recuperarState,
                    onIdentificadorChange = authViewModel::onRecuperarIdentificadorChange,
                    onEnviar = authViewModel::recuperarClave,
                    onVolver = { navController.popBackStack() }
                )
            }

            composable(Rutas.PERFIL_USUARIO) {
                val usuario = usuarioState.usuarioActivo
                if (usuario != null) {
                    PerfilUsuarioScreen(
                        usuario = usuario,
                        state = perfilState,
                        comunas = comunas,
                        onFormChange = authViewModel::onPerfilFormChange,
                        onGuardarPerfil = {
                            authViewModel.guardarPerfil(usuario.idUsuario) { updated ->
                                usuarioViewModel.cargarDatosUsuarioActivo()
                            }
                        },
                        onCambiarClave = {
                            authViewModel.cambiarPassword(usuario.idUsuario)
                        },
                        onCerrarSesion = {
                            navController.navigate(Rutas.LOGIN) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onVolver = { navController.popBackStack() }
                    )
                }
            }

            composable(Rutas.HOME_USUARIO) {
                HomeUsuarioScreen(
                    uiState = usuarioState,
                    onCompromisoClick = { idComp ->
                        usuarioViewModel.limpiarMensajePago()
                        navController.navigate("detalle_pago/$idComp")
                    },
                    onActualizarAvisos = { act, dias -> usuarioViewModel.actualizarAvisos(act, dias) }
                )
            }

            composable(Rutas.DETALLE_PAGO) { entry ->
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

            composable(Rutas.REPORTAR) { entry ->
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
                                navController.popBackStack(Rutas.HOME_USUARIO, inclusive = false)
                            }
                        },
                        onVolver = { navController.popBackStack() }
                    )
                }
            }

            composable(Rutas.HISTORIAL) {
                HistorialScreen(
                    uiState = usuarioState,
                    onFiltroChange = usuarioViewModel::actualizarFiltroHistorial
                )
            }

            composable(Rutas.RECOMENDAR) {
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

            composable(Rutas.HOME_AGENTE) {
                AgenteScreen(
                    uiState = agenteState,
                    onFiltroReporteChange = agenteViewModel::cambiarFiltroReporte,
                    onTomarReporte = agenteViewModel::tomarReporte,
                    onRespuestaChange = agenteViewModel::onRespuestaChange,
                    onResolverReporte = { rep, mantener -> agenteViewModel.resolverReporte(rep, mantener) {} },
                    onMotivoDescarteChange = agenteViewModel::onMotivoDescarteChange,
                    onCambiarEstadoRecomendacion = agenteViewModel::actualizarEstadoRecomendacion,
                    onCerrarSesion = {
                        navController.navigate(Rutas.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Rutas.HOME_EJECUTIVO) {
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
                    onCerrarSesion = {
                        navController.navigate(Rutas.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

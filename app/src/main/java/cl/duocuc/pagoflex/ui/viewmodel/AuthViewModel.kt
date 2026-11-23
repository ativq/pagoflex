package cl.duocuc.pagoflex.ui.viewmodel

import androidx.lifecycle.ViewModel
import cl.duocuc.pagoflex.data.local.SesionPreferencias
import cl.duocuc.pagoflex.data.model.UsuarioSesion
import cl.duocuc.pagoflex.data.repository.PagoFlexRepository
import cl.duocuc.pagoflex.utils.Formatters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginFormState(
    val identificador: String = "", // RUT o Correo (Starts empty!)
    val errorIdentificador: String? = null,
    val password: String = "", // Starts empty!
    val errorPassword: String? = null,
    val errorLogin: String? = null,
    val cargando: Boolean = false
)

data class RegistroFormState(
    val rut: String = "",
    val errorRut: String? = null,
    val nombres: String = "",
    val errorNombres: String? = null,
    val apellidos: String = "",
    val errorApellidos: String? = null,
    val correo: String = "",
    val errorCorreo: String? = null,
    val password: String = "",
    val errorPassword: String? = null,
    val confirmarPassword: String = "",
    val errorConfirmarPassword: String? = null,
    val telefono: String = "",
    val comunaSeleccionada: String = "Maipú",
    val mensajeResultado: String? = null,
    val esErrorResultado: Boolean = false
)

data class RecuperarClaveState(
    val identificador: String = "",
    val errorIdentificador: String? = null,
    val mensajeResultado: String? = null,
    val esErrorResultado: Boolean = false
)

data class PerfilFormState(
    val nombres: String = "",
    val errorNombres: String? = null,
    val apellidos: String = "",
    val errorApellidos: String? = null,
    val correo: String = "",
    val errorCorreo: String? = null,
    val telefono: String = "",
    val comunaSeleccionada: String = "Maipú",
    val claveActual: String = "",
    val errorClaveActual: String? = null,
    val nuevaClave: String = "",
    val errorNuevaClave: String? = null,
    val confirmarNuevaClave: String = "",
    val errorConfirmarNuevaClave: String? = null,
    val mensajePerfil: String? = null,
    val esErrorPerfil: Boolean = false,
    val mensajeClave: String? = null,
    val esErrorClave: Boolean = false
)

class AuthViewModel(
    private val repository: PagoFlexRepository,
    private val preferencias: SesionPreferencias
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginFormState())
    val loginState: StateFlow<LoginFormState> = _loginState.asStateFlow()

    private val _registroState = MutableStateFlow(RegistroFormState())
    val registroState: StateFlow<RegistroFormState> = _registroState.asStateFlow()

    private val _recuperarState = MutableStateFlow(RecuperarClaveState())
    val recuperarState: StateFlow<RecuperarClaveState> = _recuperarState.asStateFlow()

    private val _perfilState = MutableStateFlow(PerfilFormState())
    val perfilState: StateFlow<PerfilFormState> = _perfilState.asStateFlow()

    // --- LOGIN ---

    fun onLoginIdentificadorChange(texto: String) {
        _loginState.update { it.copy(identificador = texto, errorIdentificador = null, errorLogin = null) }
    }

    fun onLoginPasswordChange(texto: String) {
        _loginState.update { it.copy(password = texto, errorPassword = null, errorLogin = null) }
    }

    fun iniciarSesion(onExito: (UsuarioSesion) -> Unit) {
        val state = _loginState.value
        var hayError = false
        var errId: String? = null
        var errPass: String? = null

        if (state.identificador.trim().isEmpty()) {
            errId = "Ingresa tu correo electrónico o RUT."
            hayError = true
        }

        if (state.password.trim().isEmpty()) {
            errPass = "Ingresa tu contraseña."
            hayError = true
        }

        if (hayError) {
            _loginState.update { it.copy(errorIdentificador = errId, errorPassword = errPass) }
            return
        }

        val usuario = repository.autenticar(state.identificador, state.password)
        if (usuario != null) {
            preferencias.guardarUsuarioActivo(usuario.codigo)
            _loginState.update { LoginFormState() } // Reset form
            onExito(usuario)
        } else {
            _loginState.update {
                it.copy(errorLogin = "Credenciales incorrectas. Verifica tu correo/RUT o clave.")
            }
        }
    }

    // --- ACCESO DIRECTO RÁPIDO PARA DEFENSA DE LOS 3 ROLES ---

    fun ingresarComoDemostracion(codigoUsuario: String, onExito: (UsuarioSesion) -> Unit) {
        val user = repository.obtenerUsuarioPorCodigo(codigoUsuario)
        if (user != null) {
            preferencias.guardarUsuarioActivo(user.codigo)
            onExito(user)
        }
    }

    // --- REGISTRO DE USUARIO ---

    fun onRegistroFormChange(
        rut: String = _registroState.value.rut,
        nombres: String = _registroState.value.nombres,
        apellidos: String = _registroState.value.apellidos,
        correo: String = _registroState.value.correo,
        password: String = _registroState.value.password,
        confirmarPassword: String = _registroState.value.confirmarPassword,
        telefono: String = _registroState.value.telefono,
        comuna: String = _registroState.value.comunaSeleccionada
    ) {
        val rutLimpio = rut.replace(".", "").replace("-", "")
        val rutFormateado = if (rutLimpio.length >= 8) Formatters.formatearRut(rut) else rut

        _registroState.update {
            it.copy(
                rut = rutFormateado, errorRut = null,
                nombres = nombres, errorNombres = null,
                apellidos = apellidos, errorApellidos = null,
                correo = correo, errorCorreo = null,
                password = password, errorPassword = null,
                confirmarPassword = confirmarPassword, errorConfirmarPassword = null,
                telefono = telefono, comunaSeleccionada = comuna,
                mensajeResultado = null
            )
        }
    }

    fun registrarUsuario(onExito: (UsuarioSesion) -> Unit) {
        val f = _registroState.value
        var hayError = false

        var errRut: String? = null
        var errNom: String? = null
        var errApe: String? = null
        var errCor: String? = null
        var errPass: String? = null
        var errConf: String? = null

        if (!Formatters.validarRut(f.rut)) {
            errRut = "RUT inválido. Verifica el número y dígito verificador."
            hayError = true
        }

        if (f.nombres.trim().length < 2) {
            errNom = "Ingresa tus nombres (mín. 2 caracteres)."
            hayError = true
        }

        if (f.apellidos.trim().length < 2) {
            errApe = "Ingresa tus apellidos."
            hayError = true
        }

        if (!f.correo.contains("@") || !f.correo.contains(".")) {
            errCor = "Ingresa un correo electrónico válido."
            hayError = true
        }

        if (f.password.length < 6) {
            errPass = "La contraseña debe tener al menos 6 caracteres."
            hayError = true
        }

        if (f.confirmarPassword != f.password) {
            errConf = "Las contraseñas no coinciden."
            hayError = true
        }

        if (hayError) {
            _registroState.update {
                it.copy(
                    errorRut = errRut, errorNombres = errNom, errorApellidos = errApe,
                    errorCorreo = errCor, errorPassword = errPass, errorConfirmarPassword = errConf
                )
            }
            return
        }

        val res = repository.registrarUsuario(
            rut = f.rut,
            nombres = f.nombres,
            apellidos = f.apellidos,
            correo = f.correo,
            password = f.password,
            telefono = f.telefono,
            comunaNombre = f.comunaSeleccionada
        )

        res.fold(
            onSuccess = { user ->
                preferencias.guardarUsuarioActivo(user.codigo)
                _registroState.update { RegistroFormState() }
                onExito(user)
            },
            onFailure = { error ->
                _registroState.update {
                    it.copy(mensajeResultado = error.message, esErrorResultado = true)
                }
            }
        )
    }

    // --- RECUPERAR CLAVE ---

    fun onRecuperarIdentificadorChange(texto: String) {
        _recuperarState.update { it.copy(identificador = texto, errorIdentificador = null, mensajeResultado = null) }
    }

    fun recuperarClave() {
        val text = _recuperarState.value.identificador.trim()
        if (text.isEmpty()) {
            _recuperarState.update { it.copy(errorIdentificador = "Ingresa tu correo o RUT para recuperar tu clave.") }
            return
        }

        val res = repository.recuperarClave(text)
        res.fold(
            onSuccess = { msg ->
                _recuperarState.update { it.copy(mensajeResultado = msg, esErrorResultado = false) }
            },
            onFailure = { err ->
                _recuperarState.update { it.copy(mensajeResultado = err.message, esErrorResultado = true) }
            }
        )
    }

    // --- PERFIL Y CAMBIO DE CLAVE ---

    fun cargarDatosPerfil(usuario: UsuarioSesion) {
        _perfilState.update {
            it.copy(
                nombres = usuario.nombres,
                apellidos = usuario.apellidos,
                correo = usuario.correo,
                telefono = usuario.telefono ?: "",
                comunaSeleccionada = usuario.comuna ?: "Maipú",
                mensajePerfil = null,
                mensajeClave = null
            )
        }
    }

    fun onPerfilFormChange(
        nombres: String = _perfilState.value.nombres,
        apellidos: String = _perfilState.value.apellidos,
        correo: String = _perfilState.value.correo,
        telefono: String = _perfilState.value.telefono,
        comuna: String = _perfilState.value.comunaSeleccionada,
        claveActual: String = _perfilState.value.claveActual,
        nuevaClave: String = _perfilState.value.nuevaClave,
        confirmarNuevaClave: String = _perfilState.value.confirmarNuevaClave
    ) {
        _perfilState.update {
            it.copy(
                nombres = nombres, errorNombres = null,
                apellidos = apellidos, errorApellidos = null,
                correo = correo, errorCorreo = null,
                telefono = telefono, comunaSeleccionada = comuna,
                claveActual = claveActual, errorClaveActual = null,
                nuevaClave = nuevaClave, errorNuevaClave = null,
                confirmarNuevaClave = confirmarNuevaClave, errorConfirmarNuevaClave = null,
                mensajePerfil = null, mensajeClave = null
            )
        }
    }

    fun guardarPerfil(idUsuario: Int, onActualizado: (UsuarioSesion) -> Unit) {
        val f = _perfilState.value
        var hayError = false
        var errNom: String? = null
        var errApe: String? = null
        var errCor: String? = null

        if (f.nombres.trim().length < 2) {
            errNom = "Ingresa tus nombres."
            hayError = true
        }
        if (f.apellidos.trim().length < 2) {
            errApe = "Ingresa tus apellidos."
            hayError = true
        }
        if (!f.correo.contains("@")) {
            errCor = "Correo inválido."
            hayError = true
        }

        if (hayError) {
            _perfilState.update { it.copy(errorNombres = errNom, errorApellidos = errApe, errorCorreo = errCor) }
            return
        }

        val res = repository.actualizarPerfil(
            idUsuario = idUsuario,
            nombres = f.nombres,
            apellidos = f.apellidos,
            correo = f.correo,
            telefono = f.telefono,
            comunaNombre = f.comunaSeleccionada
        )

        res.fold(
            onSuccess = { updated ->
                _perfilState.update { it.copy(mensajePerfil = "¡Perfil actualizado con éxito!", esErrorPerfil = false) }
                onActualizado(updated)
            },
            onFailure = { err ->
                _perfilState.update { it.copy(mensajePerfil = err.message, esErrorPerfil = true) }
            }
        )
    }

    fun cambiarPassword(idUsuario: Int) {
        val f = _perfilState.value
        var hayError = false
        var errAct: String? = null
        var errNue: String? = null
        var errConf: String? = null

        if (f.claveActual.isEmpty()) {
            errAct = "Ingresa tu clave actual."
            hayError = true
        }
        if (f.nuevaClave.length < 6) {
            errNue = "La nueva clave debe tener al menos 6 caracteres."
            hayError = true
        }
        if (f.confirmarNuevaClave != f.nuevaClave) {
            errConf = "Las contraseñas no coinciden."
            hayError = true
        }

        if (hayError) {
            _perfilState.update {
                it.copy(errorClaveActual = errAct, errorNuevaClave = errNue, errorConfirmarNuevaClave = errConf)
            }
            return
        }

        val res = repository.cambiarPassword(idUsuario, f.claveActual, f.nuevaClave)
        res.fold(
            onSuccess = {
                _perfilState.update {
                    it.copy(
                        claveActual = "", nuevaClave = "", confirmarNuevaClave = "",
                        mensajeClave = "¡Contraseña modificada correctamente!", esErrorClave = false
                    )
                }
            },
            onFailure = { err ->
                _perfilState.update { it.copy(mensajeClave = err.message, esErrorClave = true) }
            }
        )
    }
}

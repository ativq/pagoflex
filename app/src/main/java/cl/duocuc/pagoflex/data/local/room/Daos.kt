package cl.duocuc.pagoflex.data.local.room

import androidx.room.*

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuario WHERE activo = 1 ORDER BY id_rol ASC, id_usuario ASC")
    fun obtenerUsuariosActivos(): List<UsuarioEntity>

    @Query("SELECT * FROM usuario WHERE codigo = :codigo LIMIT 1")
    fun obtenerPorCodigo(codigo: String): UsuarioEntity?

    @Query("SELECT * FROM usuario WHERE rut = :rut LIMIT 1")
    fun obtenerPorRut(rut: String): UsuarioEntity?

    @Query("SELECT * FROM usuario WHERE correo = :correo LIMIT 1")
    fun obtenerPorCorreo(correo: String): UsuarioEntity?

    @Query("SELECT * FROM usuario WHERE (correo = :identificador OR rut = :identificador) AND password = :password LIMIT 1")
    fun autenticar(identificador: String, password: String): UsuarioEntity?

    @Query("SELECT * FROM usuario WHERE id_usuario = :idUsuario LIMIT 1")
    fun obtenerPorId(idUsuario: Int): UsuarioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertar(usuario: UsuarioEntity): Long

    @Update
    fun actualizar(usuario: UsuarioEntity)

    @Query("UPDATE usuario SET avisos_activos = :activos, dias_anticipacion = :dias WHERE id_usuario = :idUsuario")
    fun actualizarAvisos(idUsuario: Int, activos: Int, dias: Int)

    @Query("UPDATE usuario SET password = :nuevaPassword WHERE id_usuario = :idUsuario")
    fun cambiarPassword(idUsuario: Int, nuevaPassword: String)
}

@Dao
interface CompromisoDao {
    @Query("SELECT * FROM compromiso ORDER BY id_compromiso DESC")
    fun obtenerTodos(): List<CompromisoEntity>

    @Query("SELECT * FROM compromiso WHERE id_usuario_final = :idUsuario")
    fun obtenerPorUsuario(idUsuario: Int): List<CompromisoEntity>

    @Query("SELECT * FROM compromiso WHERE id_empresa = :idEmpresa")
    fun obtenerPorEmpresa(idEmpresa: Int): List<CompromisoEntity>

    @Query("SELECT * FROM compromiso WHERE id_compromiso = :idCompromiso LIMIT 1")
    fun obtenerPorId(idCompromiso: Int): CompromisoEntity?

    @Query("SELECT COUNT(*) FROM compromiso WHERE id_usuario_final = :idUsuario AND id_empresa = :idEmpresa AND LOWER(concepto) = LOWER(:concepto) AND COALESCE(numero_cuota, -1) = :numeroCuota")
    fun contarDuplicados(idUsuario: Int, idEmpresa: Int, concepto: String, numeroCuota: Int): Int

    @Query("SELECT COUNT(*) FROM compromiso")
    fun contarCompromisos(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertar(compromiso: CompromisoEntity): Long

    @Update
    fun actualizar(compromiso: CompromisoEntity)

    @Query("UPDATE compromiso SET id_estado_compromiso = :nuevoEstado, fecha_cambio_estado = :fecha WHERE id_compromiso = :idCompromiso")
    fun actualizarEstado(idCompromiso: Int, nuevoEstado: Int, fecha: String)
}

@Dao
interface PagoDao {
    @Query("SELECT * FROM pago WHERE id_compromiso = :idCompromiso LIMIT 1")
    fun obtenerPagoPorCompromiso(idCompromiso: Int): PagoEntity?

    @Query("SELECT COUNT(*) FROM pago")
    fun contarPagos(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertar(pago: PagoEntity): Long

    @Query("SELECT p.* FROM pago p JOIN compromiso c ON p.id_compromiso = c.id_compromiso WHERE c.id_usuario_final = :idUsuario ORDER BY p.id_pago DESC")
    fun obtenerPagosUsuario(idUsuario: Int): List<PagoEntity>
}

@Dao
interface ReporteDao {
    @Query("SELECT * FROM reporte ORDER BY id_estado_reporte ASC, id_reporte DESC")
    fun obtenerTodos(): List<ReporteEntity>

    @Query("SELECT * FROM reporte WHERE id_compromiso = :idCompromiso LIMIT 1")
    fun obtenerPorCompromiso(idCompromiso: Int): ReporteEntity?

    @Query("SELECT COUNT(*) FROM reporte WHERE id_compromiso = :idCompromiso AND id_estado_reporte IN (1, 2)")
    fun contarReportesAbiertosCompromiso(idCompromiso: Int): Int

    @Query("SELECT COUNT(*) FROM reporte")
    fun contarReportes(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertar(reporte: ReporteEntity): Long

    @Update
    fun actualizar(reporte: ReporteEntity)
}

@Dao
interface RecomendacionDao {
    @Query("SELECT * FROM recomendacion ORDER BY id_recomendacion DESC")
    fun obtenerTodas(): List<RecomendacionEntity>

    @Query("SELECT * FROM recomendacion WHERE id_usuario_final = :idUsuario ORDER BY id_recomendacion DESC")
    fun obtenerPorUsuario(idUsuario: Int): List<RecomendacionEntity>

    @Query("SELECT COUNT(*) FROM recomendacion WHERE id_usuario_final = :idUsuario AND LOWER(nombre_organizacion) = LOWER(:nombreOrg)")
    fun contarDuplicadas(idUsuario: Int, nombreOrg: String): Int

    @Query("SELECT COUNT(*) FROM recomendacion")
    fun contarRecomendaciones(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertar(recomendacion: RecomendacionEntity): Long

    @Update
    fun actualizar(recomendacion: RecomendacionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertarContacto(contacto: ContactoRecomendacionEntity)

    @Query("SELECT * FROM contacto_recomendacion WHERE id_recomendacion = :idRecomendacion LIMIT 1")
    fun obtenerContacto(idRecomendacion: Int): ContactoRecomendacionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertarHistorial(historial: HistorialRecomendacionEntity)

    @Query("SELECT * FROM historial_recomendacion WHERE id_recomendacion = :idRecomendacion ORDER BY id_historial DESC LIMIT 1")
    fun obtenerUltimoHistorial(idRecomendacion: Int): HistorialRecomendacionEntity?
}

@Dao
interface EmpresaDao {
    @Query("SELECT * FROM empresa_cliente WHERE activa = 1 ORDER BY id_empresa ASC")
    fun obtenerTodas(): List<EmpresaClienteEntity>

    @Query("SELECT * FROM empresa_cliente WHERE id_empresa = :idEmpresa LIMIT 1")
    fun obtenerPorId(idEmpresa: Int): EmpresaClienteEntity?
}

@Dao
interface CatalogosDao {
    @Query("SELECT nombre FROM comuna ORDER BY id_comuna ASC")
    fun obtenerComunas(): List<String>

    @Query("SELECT nombre FROM rubro ORDER BY id_rubro ASC")
    fun obtenerRubros(): List<String>

    @Query("SELECT nombre FROM rol ORDER BY id_rol ASC")
    fun obtenerRoles(): List<String>

    @Query("SELECT id_rubro FROM rubro WHERE nombre = :nombre LIMIT 1")
    fun obtenerIdRubroPorNombre(nombre: String): Int?

    @Query("SELECT id_comuna FROM comuna WHERE nombre = :nombre LIMIT 1")
    fun obtenerIdComunaPorNombre(nombre: String): Int?

    @Query("SELECT id_medio_pago FROM medio_pago WHERE nombre = :nombre LIMIT 1")
    fun obtenerIdMedioPagoPorNombre(nombre: String): Int?

    @Query("SELECT id_motivo_reporte FROM motivo_reporte WHERE nombre = :nombre LIMIT 1")
    fun obtenerIdMotivoReportePorNombre(nombre: String): Int?

    @Query("SELECT id_motivo_anulacion FROM motivo_anulacion WHERE nombre = :nombre LIMIT 1")
    fun obtenerIdMotivoAnulacionPorNombre(nombre: String): Int?

    @Query("SELECT * FROM estado_recomendacion WHERE nombre = :nombre LIMIT 1")
    fun obtenerEstadoRecomendacionPorNombre(nombre: String): EstadoRecomendacionEntity?

    @Query("SELECT nombre FROM estado_compromiso WHERE id_estado_compromiso = :id LIMIT 1")
    fun obtenerNombreEstadoCompromiso(id: Int): String?

    @Query("SELECT nombre FROM estado_reporte WHERE id_estado_reporte = :id LIMIT 1")
    fun obtenerNombreEstadoReporte(id: Int): String?

    @Query("SELECT nombre FROM estado_recomendacion WHERE id_estado_recomendacion = :id LIMIT 1")
    fun obtenerNombreEstadoRecomendacion(id: Int): String?

    @Query("SELECT nombre FROM medio_pago WHERE id_medio_pago = :id LIMIT 1")
    fun obtenerNombreMedioPago(id: Int): String?

    @Query("SELECT nombre FROM canal WHERE id_canal = :id LIMIT 1")
    fun obtenerNombreCanal(id: Int): String?

    @Query("SELECT nombre FROM area WHERE id_area = :id LIMIT 1")
    fun obtenerNombreArea(id: Int): String?

    @Query("SELECT codigo FROM rol WHERE id_rol = :id LIMIT 1")
    fun obtenerCodigoRol(id: Int): String?
}

@Dao
interface AnulacionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertar(anulacion: AnulacionEntity)

    @Query("SELECT * FROM anulacion WHERE id_compromiso = :idCompromiso LIMIT 1")
    fun obtenerPorCompromiso(idCompromiso: Int): AnulacionEntity?
}

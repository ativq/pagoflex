package cl.duocuc.pagoflex.data.local.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "rol")
data class RolEntity(
    @PrimaryKey
    @ColumnInfo(name = "id_rol") val idRol: Int,
    @ColumnInfo(name = "codigo") val codigo: String,
    @ColumnInfo(name = "nombre") val nombre: String
)

@Entity(tableName = "comuna")
data class ComunaEntity(
    @PrimaryKey
    @ColumnInfo(name = "id_comuna") val idComuna: Int,
    @ColumnInfo(name = "nombre") val nombre: String
)

@Entity(tableName = "area")
data class AreaEntity(
    @PrimaryKey
    @ColumnInfo(name = "id_area") val idArea: Int,
    @ColumnInfo(name = "nombre") val nombre: String
)

@Entity(tableName = "rubro")
data class RubroEntity(
    @PrimaryKey
    @ColumnInfo(name = "id_rubro") val idRubro: Int,
    @ColumnInfo(name = "nombre") val nombre: String
)

@Entity(tableName = "estado_compromiso")
data class EstadoCompromisoEntity(
    @PrimaryKey
    @ColumnInfo(name = "id_estado_compromiso") val idEstadoCompromiso: Int,
    @ColumnInfo(name = "nombre") val nombre: String
)

@Entity(tableName = "motivo_anulacion")
data class MotivoAnulacionEntity(
    @PrimaryKey
    @ColumnInfo(name = "id_motivo_anulacion") val idMotivoAnulacion: Int,
    @ColumnInfo(name = "nombre") val nombre: String
)

@Entity(tableName = "canal")
data class CanalEntity(
    @PrimaryKey
    @ColumnInfo(name = "id_canal") val idCanal: Int,
    @ColumnInfo(name = "nombre") val nombre: String
)

@Entity(tableName = "medio_pago")
data class MedioPagoEntity(
    @PrimaryKey
    @ColumnInfo(name = "id_medio_pago") val idMedioPago: Int,
    @ColumnInfo(name = "nombre") val nombre: String
)

@Entity(tableName = "motivo_reporte")
data class MotivoReporteEntity(
    @PrimaryKey
    @ColumnInfo(name = "id_motivo_reporte") val idMotivoReporte: Int,
    @ColumnInfo(name = "nombre") val nombre: String
)

@Entity(tableName = "estado_reporte")
data class EstadoReporteEntity(
    @PrimaryKey
    @ColumnInfo(name = "id_estado_reporte") val idEstadoReporte: Int,
    @ColumnInfo(name = "nombre") val nombre: String
)

@Entity(tableName = "estado_recomendacion")
data class EstadoRecomendacionEntity(
    @PrimaryKey
    @ColumnInfo(name = "id_estado_recomendacion") val idEstadoRecomendacion: Int,
    @ColumnInfo(name = "nombre") val nombre: String,
    @ColumnInfo(name = "orden") val orden: Int
)

@Entity(tableName = "empresa_cliente")
data class EmpresaClienteEntity(
    @PrimaryKey
    @ColumnInfo(name = "id_empresa") val idEmpresa: Int,
    @ColumnInfo(name = "codigo") val codigo: String,
    @ColumnInfo(name = "razon_social") val razonSocial: String,
    @ColumnInfo(name = "nombre_fantasia") val nombreFantasia: String,
    @ColumnInfo(name = "rut") val rut: String,
    @ColumnInfo(name = "id_rubro") val idRubro: Int,
    @ColumnInfo(name = "correo_atencion") val correoAtencion: String,
    @ColumnInfo(name = "telefono_atencion") val telefonoAtencion: String,
    @ColumnInfo(name = "recargo_atraso") val recargoAtraso: Int,
    @ColumnInfo(name = "activa") val activa: Int = 1
)

@Entity(tableName = "usuario")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_usuario") val idUsuario: Int = 0,
    @ColumnInfo(name = "codigo") val codigo: String,
    @ColumnInfo(name = "id_rol") val idRol: Int,
    @ColumnInfo(name = "rut") val rut: String,
    @ColumnInfo(name = "nombres") val nombres: String,
    @ColumnInfo(name = "apellidos") val apellidos: String,
    @ColumnInfo(name = "correo") val correo: String,
    @ColumnInfo(name = "password") val password: String = "123456",
    @ColumnInfo(name = "telefono") val telefono: String? = null,
    @ColumnInfo(name = "activo") val activo: Int = 1,
    @ColumnInfo(name = "id_comuna") val idComuna: Int? = null,
    @ColumnInfo(name = "fecha_incorporacion") val fechaIncorporacion: String? = null,
    @ColumnInfo(name = "avisos_activos") val avisosActivos: Int? = 1,
    @ColumnInfo(name = "dias_anticipacion") val diasAnticipacion: Int? = 3,
    @ColumnInfo(name = "id_area") val idArea: Int? = null,
    @ColumnInfo(name = "id_empresa") val idEmpresa: Int? = null,
    @ColumnInfo(name = "cargo") val cargo: String? = null,
    @ColumnInfo(name = "hist_considerados") val histConsiderados: Int = 0,
    @ColumnInfo(name = "hist_a_tiempo") val histATiempo: Int = 0
)

@Entity(tableName = "compromiso")
data class CompromisoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_compromiso") val idCompromiso: Int = 0,
    @ColumnInfo(name = "folio") val folio: String,
    @ColumnInfo(name = "id_usuario_final") val idUsuarioFinal: Int,
    @ColumnInfo(name = "id_empresa") val idEmpresa: Int,
    @ColumnInfo(name = "concepto") val concepto: String,
    @ColumnInfo(name = "numero_cuota") val numeroCuota: Int? = null,
    @ColumnInfo(name = "total_cuotas") val totalCuotas: Int? = null,
    @ColumnInfo(name = "monto") val monto: Int,
    @ColumnInfo(name = "fecha_vencimiento") val fechaVencimiento: String,
    @ColumnInfo(name = "id_estado_compromiso") val idEstadoCompromiso: Int,
    @ColumnInfo(name = "recargo_aplicado") val recargoAplicado: Int = 0,
    @ColumnInfo(name = "fecha_cambio_estado") val fechaCambioEstado: String,
    @ColumnInfo(name = "id_ejecutivo_registro") val idEjecutivoRegistro: Int? = null,
    @ColumnInfo(name = "fecha_registro") val fechaRegistro: String
)

@Entity(tableName = "anulacion")
data class AnulacionEntity(
    @PrimaryKey
    @ColumnInfo(name = "id_compromiso") val idCompromiso: Int,
    @ColumnInfo(name = "id_ejecutivo") val idEjecutivo: Int,
    @ColumnInfo(name = "id_motivo_anulacion") val idMotivoAnulacion: Int,
    @ColumnInfo(name = "comentario") val comentario: String? = null,
    @ColumnInfo(name = "fecha") val fecha: String
)

@Entity(tableName = "pago")
data class PagoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_pago") val idPago: Int = 0,
    @ColumnInfo(name = "numero_comprobante") val numeroComprobante: String,
    @ColumnInfo(name = "id_compromiso") val idCompromiso: Int,
    @ColumnInfo(name = "fecha_hora") val fechaHora: String,
    @ColumnInfo(name = "monto_pagado") val montoPagado: Int,
    @ColumnInfo(name = "id_canal") val idCanal: Int,
    @ColumnInfo(name = "id_medio_pago") val idMedioPago: Int
)

@Entity(tableName = "reporte")
data class ReporteEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_reporte") val idReporte: Int = 0,
    @ColumnInfo(name = "numero") val numero: String,
    @ColumnInfo(name = "id_compromiso") val idCompromiso: Int,
    @ColumnInfo(name = "id_motivo_reporte") val idMotivoReporte: Int,
    @ColumnInfo(name = "comentario") val comentario: String? = null,
    @ColumnInfo(name = "fecha_creacion") val fechaCreacion: String,
    @ColumnInfo(name = "id_estado_reporte") val idEstadoReporte: Int,
    @ColumnInfo(name = "id_agente") val idAgente: Int? = null,
    @ColumnInfo(name = "fecha_toma") val fechaToma: String? = null,
    @ColumnInfo(name = "respuesta") val respuesta: String? = null,
    @ColumnInfo(name = "fecha_resolucion") val fechaResolucion: String? = null
)

@Entity(tableName = "recomendacion")
data class RecomendacionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_recomendacion") val idRecomendacion: Int = 0,
    @ColumnInfo(name = "numero") val numero: String,
    @ColumnInfo(name = "id_usuario_final") val idUsuarioFinal: Int,
    @ColumnInfo(name = "nombre_organizacion") val nombreOrganizacion: String,
    @ColumnInfo(name = "rut_organizacion") val rutOrganizacion: String? = null,
    @ColumnInfo(name = "id_rubro") val idRubro: Int,
    @ColumnInfo(name = "id_comuna") val idComuna: Int,
    @ColumnInfo(name = "fecha") val fecha: String,
    @ColumnInfo(name = "id_estado_recomendacion") val idEstadoRecomendacion: Int
)

@Entity(tableName = "contacto_recomendacion")
data class ContactoRecomendacionEntity(
    @PrimaryKey
    @ColumnInfo(name = "id_recomendacion") val idRecomendacion: Int,
    @ColumnInfo(name = "nombre") val nombre: String,
    @ColumnInfo(name = "correo") val correo: String? = null,
    @ColumnInfo(name = "telefono") val telefono: String? = null,
    @ColumnInfo(name = "autoriza_contacto") val autorizaContacto: Int = 1
)

@Entity(tableName = "historial_recomendacion")
data class HistorialRecomendacionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_historial") val idHistorial: Int = 0,
    @ColumnInfo(name = "id_recomendacion") val idRecomendacion: Int,
    @ColumnInfo(name = "id_estado_recomendacion") val idEstadoRecomendacion: Int,
    @ColumnInfo(name = "id_agente") val idAgente: Int,
    @ColumnInfo(name = "fecha") val fecha: String,
    @ColumnInfo(name = "motivo") val motivo: String? = null
)

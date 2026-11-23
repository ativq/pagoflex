package cl.duocuc.pagoflex.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.concurrent.Executors

@Database(
    entities = [
        RolEntity::class,
        ComunaEntity::class,
        AreaEntity::class,
        RubroEntity::class,
        EstadoCompromisoEntity::class,
        MotivoAnulacionEntity::class,
        CanalEntity::class,
        MedioPagoEntity::class,
        MotivoReporteEntity::class,
        EstadoReporteEntity::class,
        EstadoRecomendacionEntity::class,
        EmpresaClienteEntity::class,
        UsuarioEntity::class,
        CompromisoEntity::class,
        AnulacionEntity::class,
        PagoEntity::class,
        ReporteEntity::class,
        RecomendacionEntity::class,
        ContactoRecomendacionEntity::class,
        HistorialRecomendacionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PagoFlexRoomDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun compromisoDao(): CompromisoDao
    abstract fun pagoDao(): PagoDao
    abstract fun reporteDao(): ReporteDao
    abstract fun recomendacionDao(): RecomendacionDao
    abstract fun empresaDao(): EmpresaDao
    abstract fun catalogosDao(): CatalogosDao
    abstract fun anulacionDao(): AnulacionDao

    companion object {
        @Volatile
        private var INSTANCE: PagoFlexRoomDatabase? = null

        fun getDatabase(context: Context): PagoFlexRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PagoFlexRoomDatabase::class.java,
                    "pagoflex_room_3fn.db"
                )
                    .allowMainThreadQueries()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Executors.newSingleThreadExecutor().execute {
                                INSTANCE?.let { database ->
                                    poblarDatosAnexo9(database)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private fun poblarDatosAnexo9(db: PagoFlexRoomDatabase) {
            val c = db.catalogosDao()
            val u = db.usuarioDao()
            val comp = db.compromisoDao()

            // Check if already populated
            if (c.obtenerRoles().isNotEmpty()) return

            // SQLite direct inserts via SupportSQLiteDatabase or DAOs
            val sdb = db.openHelper.writableDatabase

            sdb.execSQL("INSERT INTO rol VALUES (1, 'USUARIO_FINAL', 'Usuario final'), (2, 'AGENTE', 'Agente PagoFlex'), (3, 'EJECUTIVO', 'Ejecutivo de empresa cliente')")

            sdb.execSQL("""
                INSERT INTO comuna VALUES 
                (1, 'Maipú'), (2, 'Puente Alto'), (3, 'San Miguel'), (4, 'La Florida'),
                (5, 'Ñuñoa'), (6, 'Quilicura'), (7, 'San Bernardo'), (8, 'Estación Central')
            """)

            sdb.execSQL("INSERT INTO area VALUES (1, 'Soporte'), (2, 'Comercial')")

            sdb.execSQL("""
                INSERT INTO rubro VALUES 
                (1, 'Inmobiliaria'), (2, 'Educación escolar'), (3, 'Crédito de consumo'),
                (4, 'Seguros'), (5, 'ONG'), (6, 'Club deportivo'), (7, 'Educación superior'),
                (8, 'Administración de arriendos'), (9, 'Educación parvularia'),
                (10, 'Administración de condominio'), (11, 'Deporte'), (12, 'Educación artística')
            """)

            sdb.execSQL("INSERT INTO estado_compromiso VALUES (1, 'Pendiente'), (2, 'Vencido'), (3, 'Pagado'), (4, 'En revisión'), (5, 'Anulado')")
            sdb.execSQL("INSERT INTO motivo_anulacion VALUES (1, 'Repactación'), (2, 'Error de registro'), (3, 'Pagado directamente a la empresa'), (4, 'Otro')")
            sdb.execSQL("INSERT INTO canal VALUES (1, 'App'), (2, 'Portal de pagos'), (3, 'Agente conversacional')")
            sdb.execSQL("INSERT INTO medio_pago VALUES (1, 'Tarjeta de débito'), (2, 'Tarjeta de crédito'), (3, 'Transferencia'), (4, 'Pago simulado')")
            sdb.execSQL("INSERT INTO motivo_reporte VALUES (1, 'Ya pagué por otro medio'), (2, 'No reconozco este cobro'), (3, 'El monto no corresponde'), (4, 'Otro')")
            sdb.execSQL("INSERT INTO estado_reporte VALUES (1, 'Abierto'), (2, 'En gestión'), (3, 'Resuelto')")
            sdb.execSQL("INSERT INTO estado_recomendacion VALUES (1, 'Recibida', 1), (2, 'Contactada', 2), (3, 'En evaluación', 3), (4, 'Se unió', 4), (5, 'Descartada', 4)")

            sdb.execSQL("""
                INSERT INTO empresa_cliente VALUES
                (1, 'EC-01', 'Inmobiliaria Altos del Maipo SpA', 'Altos del Maipo', '76.845.213-K', 1, 'atencion@altosmaipo.ejemplo', '+56 2 0000 0201', 8500, 1),
                (2, 'EC-02', 'Corporación Educacional Bosque Nativo', 'Colegio Bosque Nativo', '65.132.874-8', 2, 'pagos@bosquenativo.ejemplo', '+56 2 0000 0202', 5000, 1),
                (3, 'EC-03', 'Crédito Andino S.A.', 'Crédito Andino', '96.571.438-3', 3, 'clientes@creditoandino.ejemplo', '+56 2 0000 0203', 12000, 1),
                (4, 'EC-04', 'Seguros Horizonte Sur S.A.', 'Horizonte Sur', '96.814.205-4', 4, 'contacto@horizontesur.ejemplo', '+56 2 0000 0204', 3000, 1),
                (5, 'EC-05', 'Fundación Manos Abiertas', 'Manos Abiertas', '65.247.190-0', 5, 'socios@manosabiertas.ejemplo', '+56 2 0000 0205', 0, 1),
                (6, 'EC-06', 'Club Deportivo Los Halcones', 'Club Los Halcones', '65.398.412-K', 6, 'tesoreria@loshalcones.ejemplo', '+56 2 0000 0206', 2000, 1),
                (7, 'EC-07', 'Instituto Técnico Cordillera SpA', 'Instituto Cordillera', '77.214.583-7', 7, 'finanzas@itcordillera.ejemplo', '+56 2 0000 0207', 6000, 1),
                (8, 'EC-08', 'Arriendos Plaza Oriente Ltda.', 'Plaza Oriente', '76.432.871-K', 8, 'cobranza@plazaoriente.ejemplo', '+56 2 0000 0208', 7500, 1)
            """)

            sdb.execSQL("""
                INSERT INTO usuario (id_usuario, codigo, id_rol, rut, nombres, apellidos, correo, password, telefono, activo, id_comuna, fecha_incorporacion, avisos_activos, dias_anticipacion, hist_considerados, hist_a_tiempo) VALUES
                (1, 'UF-01', 1, '15.834.207-3', 'Camila Andrea', 'Rojas Peña', 'crojas@correo.ejemplo', '123456', '+56 9 0000 0101', 1, 1, '14-03-2023', 1, 3, 36, 33),
                (2, 'UF-02', 1, '12.476.318-5', 'Luis Alberto', 'Muñoz Tapia', 'lmunoz@correo.ejemplo', '123456', '+56 9 0000 0102', 1, 2, '02-11-2023', 1, 1, 12, 8),
                (3, 'UF-03', 1, '6.893.541-5', 'Rosa Elena', 'Contreras Díaz', 'rcontreras@correo.ejemplo', '123456', '+56 9 0000 0103', 1, 3, '20-06-2024', 1, 5, 28, 28),
                (4, 'UF-04', 1, '21.045.873-5', 'Matías Ignacio', 'Soto Vergara', 'msoto@correo.ejemplo', '123456', '+56 9 0000 0104', 1, 4, '07-03-2025', 0, 3, 7, 6),
                (5, 'UF-05', 1, '17.562.904-1', 'Fernanda Paz', 'Godoy Lagos', 'fgodoy@correo.ejemplo', '123456', '+56 9 0000 0105', 1, 5, '15-01-2024', 1, 3, 20, 19),
                (6, 'UF-06', 1, '10.318.265-4', 'Héctor Manuel', 'Pizarro Campos', 'hpizarro@correo.ejemplo', '123456', '+56 9 0000 0106', 1, 6, '28-08-2023', 1, 3, 14, 12),
                (7, 'UF-07', 1, '19.287.430-0', 'Javiera Constanza', 'Reyes Molina', 'jreyes@correo.ejemplo', '123456', '+56 9 0000 0107', 1, 7, '03-05-2025', 1, 1, 5, 5),
                (8, 'UF-08', 1, '18.704.396-4', 'Benjamín Esteban', 'Araya Fuentes', 'baraya@correo.ejemplo', '123456', '+56 9 0000 0108', 1, 8, '11-09-2026', 1, 3, 0, 0)
            """)

            sdb.execSQL("""
                INSERT INTO usuario (id_usuario, codigo, id_rol, rut, nombres, apellidos, correo, password, activo, id_area) VALUES
                (9,  'AG-01', 2, '16.248.317-K', 'Andrés Felipe', 'Lagos Meza', 'alagos@pagoflex.ejemplo', '123456', 1, 1),
                (10, 'AG-02', 2, '18.352.091-1', 'Daniela Sofía', 'Ortiz Bravo', 'dortiz@pagoflex.ejemplo', '123456', 1, 1),
                (11, 'AG-03', 2, '13.907.264-2', 'Ricardo José', 'Salinas Cáceres', 'rsalinas@pagoflex.ejemplo', '123456', 0, 1),
                (12, 'AG-04', 2, '17.781.435-0', 'Valentina Ignacia', 'Paredes Silva', 'vparedes@pagoflex.ejemplo', '123456', 1, 2),
                (13, 'AG-05', 2, '19.564.028-9', 'Tomás Alejandro', 'Vidal Rojas', 'tvidal@pagoflex.ejemplo', '123456', 1, 2)
            """)

            sdb.execSQL("""
                INSERT INTO usuario (id_usuario, codigo, id_rol, rut, nombres, apellidos, correo, password, telefono, activo, id_empresa, cargo) VALUES
                (14, 'EJ-01', 3, '14.629.853-2', 'Paula Andrea', 'Saavedra Núñez', 'psaavedra@altosmaipo.ejemplo', '123456', '+56 9 0000 0401', 1, 1, 'Ejecutiva de cobranza'),
                (15, 'EJ-02', 3, '11.853.720-3', 'Marco Antonio', 'Fuentes Riquelme', 'mfuentes@bosquenativo.ejemplo', '123456', '+56 9 0000 0402', 1, 2, 'Encargado de finanzas'),
                (16, 'EJ-03', 3, '15.370.946-7', 'Claudia Beatriz', 'Henríquez Soto', 'chenriquez@creditoandino.ejemplo', '123456', '+56 9 0000 0403', 1, 3, 'Jefa de cobranza'),
                (17, 'EJ-04', 3, '17.095.482-3', 'Felipe Ignacio', 'Morales Tapia', 'fmorales@creditoandino.ejemplo', '123456', '+56 9 0000 0404', 1, 3, 'Ejecutivo de cobranza'),
                (18, 'EJ-05', 3, '9.846.217-1', 'Gloria Inés', 'Carrasco Vega', 'gcarrasco@loshalcones.ejemplo', '123456', '+56 9 0000 0405', 1, 6, 'Tesorera'),
                (19, 'EJ-06', 3, '18.930.571-0', 'Sebastián Andrés', 'Núñez Olivares', 'snunez@itcordillera.ejemplo', '123456', '+56 9 0000 0406', 1, 7, 'Analista de finanzas'),
                (20, 'EJ-07', 3, '12.584.396-4', 'Mónica Patricia', 'Leiva Castro', 'mleiva@plazaoriente.ejemplo', '123456', '+56 9 0000 0407', 1, 8, 'Administradora de cobranza')
            """)

            sdb.execSQL("""
                INSERT INTO compromiso (id_compromiso, folio, id_usuario_final, id_empresa, concepto, numero_cuota, total_cuotas, monto, fecha_vencimiento, id_estado_compromiso, recargo_aplicado, fecha_cambio_estado, id_ejecutivo_registro, fecha_registro) VALUES
                (1,  'CP-2026-0101', 1, 8, 'Arriendo departamento octubre', 10, 12, 520000, '10-10-2026', 1, 0, '01-10-2026', 20, '01-10-2026'),
                (2,  'CP-2026-0102', 1, 6, 'Cuota social octubre', 10, 12, 15000, '05-10-2026', 2, 2000, '06-10-2026', 18, '01-10-2026'),
                (3,  'CP-2026-0103', 1, 5, 'Aporte mensual octubre', NULL, NULL, 10000, '15-10-2026', 1, 0, '01-10-2026', NULL, '01-10-2026'),
                (4,  'CP-2026-0104', 1, 3, 'Crédito de consumo', 7, 24, 86400, '25-09-2026', 3, 0, '24-09-2026', 16, '01-09-2026'),
                (5,  'CP-2026-0105', 1, 3, 'Crédito de consumo', 8, 24, 86400, '25-10-2026', 1, 0, '01-10-2026', 16, '01-10-2026'),
                (6,  'CP-2026-0106', 2, 1, 'Cuota pie departamento', 14, 36, 310000, '30-09-2026', 2, 8500, '01-10-2026', 14, '01-09-2026'),
                (7,  'CP-2026-0107', 2, 1, 'Cuota pie departamento', 15, 36, 310000, '30-10-2026', 1, 0, '01-10-2026', 14, '01-10-2026'),
                (8,  'CP-2026-0108', 3, 4, 'Seguro de hogar octubre', 10, 12, 18900, '01-10-2026', 3, 0, '30-09-2026', NULL, '01-09-2026'),
                (9,  'CP-2026-0109', 4, 7, 'Arancel octubre', 8, 10, 142000, '05-10-2026', 4, 0, '03-10-2026', 19, '01-10-2026'),
                (10, 'CP-2026-0110', 5, 2, 'Mensualidad octubre', 8, 10, 185000, '10-10-2026', 1, 0, '01-10-2026', 15, '01-10-2026'),
                (11, 'CP-2026-0111', 6, 3, 'Crédito de consumo', 19, 36, 54200, '20-09-2026', 5, 0, '22-09-2026', 17, '01-09-2026'),
                (12, 'CP-2026-0112', 7, 6, 'Cuota social octubre', 10, 12, 15000, '05-10-2026', 3, 0, '04-10-2026', 18, '01-10-2026'),
                (13, 'CP-2026-0072', 2, 1, 'Cuota pie departamento', 13, 36, 310000, '31-08-2026', 3, 8500, '02-09-2026', 14, '01-08-2026'),
                (14, 'CP-2026-0089', 1, 8, 'Arriendo departamento septiembre', 9, 12, 520000, '10-09-2026', 3, 0, '09-09-2026', 20, '01-09-2026'),
                (15, 'CP-2026-0090', 1, 6, 'Cuota social septiembre', 9, 12, 15000, '05-09-2026', 3, 2000, '07-09-2026', 18, '01-09-2026'),
                (16, 'CP-2026-0091', 1, 5, 'Aporte mensual septiembre', NULL, NULL, 10000, '15-09-2026', 3, 0, '15-09-2026', NULL, '01-09-2026'),
                (17, 'CP-2026-0098', 5, 2, 'Mensualidad septiembre', 7, 10, 185000, '10-09-2026', 3, 0, '10-09-2026', 15, '01-09-2026'),
                (18, 'CP-2026-0095', 8, 6, 'Cuota social septiembre', 1, 12, 15000, '28-09-2026', 4, 0, '30-09-2026', 18, '01-09-2026')
            """)

            sdb.execSQL("INSERT INTO anulacion VALUES (11, 17, 1, 'Repactado en sucursal', '22-09-2026')")

            sdb.execSQL("""
                INSERT INTO pago (numero_comprobante, id_compromiso, fecha_hora, monto_pagado, id_canal, id_medio_pago) VALUES
                ('CPR-000377', 13, '02-09-2026 21:14', 318500, 3, 4),
                ('CPR-000398', 14, '09-09-2026 13:05', 520000, 2, 3),
                ('CPR-000402', 15, '07-09-2026 18:40', 17000,  2, 1),
                ('CPR-000405', 16, '15-09-2026 08:22', 10000,  3, 4),
                ('CPR-000451', 4,  '24-09-2026 10:42', 86400,  2, 1),
                ('CPR-000452', 8,  '30-09-2026 19:05', 18900,  3, 4),
                ('CPR-000453', 12, '04-10-2026 08:15', 15000,  1, 1),
                ('CPR-000447', 17, '10-09-2026 22:31', 185000, 2, 2)
            """)

            sdb.execSQL("""
                INSERT INTO reporte (numero, id_compromiso, id_motivo_reporte, comentario, fecha_creacion, id_estado_reporte, id_agente, fecha_toma, respuesta, fecha_resolucion) VALUES
                ('REP-0011', 9,  1, 'Pagué por transferencia directa al instituto el 03-10.', '03-10-2026', 2, 10, '04-10-2026', NULL, NULL),
                ('REP-0009', 11, 3, 'Repacté el crédito y me siguen cobrando la cuota antigua.', '18-09-2026', 3, 9, '19-09-2026', 'La empresa anuló la cuota por repactación (22-09-2026).', '22-09-2026'),
                ('REP-0010', 18, 2, 'Me llegó un cobro de un club al que no pertenezco.', '30-09-2026', 1, NULL, NULL, NULL, NULL)
            """)

            sdb.execSQL("""
                INSERT INTO recomendacion (id_recomendacion, numero, id_usuario_final, nombre_organizacion, rut_organizacion, id_rubro, id_comuna, fecha, id_estado_recomendacion) VALUES
                (1, 'REC-0001', 1, 'Jardín Infantil Pequeños Exploradores', NULL, 9, 1, '12-08-2026', 4),
                (2, 'REC-0002', 1, 'Comunidad Edificio Parque Los Olmos', NULL, 10, 1, '02-09-2026', 2),
                (3, 'REC-0003', 3, 'Gimnasio Fuerza Sur', NULL, 11, 3, '15-09-2026', 3),
                (4, 'REC-0004', 5, 'Academia de Música Clave de Sol', NULL, 12, 5, '20-09-2026', 1),
                (5, 'REC-0005', 4, 'Colegio Bosque Nativo', NULL, 2, 4, '22-09-2026', 5),
                (6, 'REC-0006', 7, 'Corporación Deportiva Cordillera', NULL, 6, 7, '01-10-2026', 1)
            """)

            sdb.execSQL("""
                INSERT INTO contacto_recomendacion VALUES
                (1, 'Paula Herrera', 'pherrera@pexploradores.ejemplo', NULL, 1),
                (3, 'Raúl Ibáñez', NULL, '+56 9 0000 0301', 1),
                (6, 'Ana Valdés', 'avaldes@cdcordillera.ejemplo', NULL, 1)
            """)

            sdb.execSQL("""
                INSERT INTO historial_recomendacion (id_recomendacion, id_estado_recomendacion, id_agente, fecha, motivo) VALUES
                (1, 4, 12, '25-09-2026', NULL),
                (2, 2, 13, '10-09-2026', NULL),
                (3, 3, 12, '28-09-2026', NULL),
                (5, 5, 13, '23-09-2026', 'La organización ya es cliente de PagoFlex')
            """)
        }
    }
}

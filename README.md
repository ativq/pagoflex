# PagoFlex - Aplicación Móvil (Android / Kotlin / Jetpack Compose)

Aplicación móvil desarrollada en **Kotlin** y **Jetpack Compose** para el proyecto **PagoFlex** (Evaluación Sumativa 3). Permite a los usuarios finales, ejecutivos de empresas y agentes de soporte gestionar, consultar y pagar compromisos financieros de forma centralizada, segura e intuitiva.

---

## Características Principales

### 1. Autenticación y Gestión de Usuarios
- **Inicio de sesión real**: Validación de credenciales conectada a base de datos local SQLite/Room (con 15 cuentas pre-sembradas con contraseña `admin123`).
- **Registro de usuarios**: Alta de nuevos usuarios finales con validaciones en ViewModel.
- **Recuperación de contraseña y Edición de Perfil**: Captura de foto de perfil con la **cámara nativa** del dispositivo y cambio de contraseña.

### 2. Gestión de Compromisos y Pagos
- **Listados paginados**: Visualización optimizada con límite de 5 elementos y botón "Mostrar más".
- **Flujo de pago con Funciones Nativas**: Notificaciones push de vencimiento, vibración háptica al completar pagos y compartir comprobantes mediante `Intent.ACTION_SEND`.
- **Formato automático de RUT**: Conversión inteligente al digitar.

### 3. Roles del Sistema
- **Usuario Final**: Consulta de compromisos, pagos, historial, reportes y recomendaciones.
- **Ejecutivo de Empresa**: Portal de cobranza, tasa de recaudación y registro de nuevas cuotas.
- **Agente PagoFlex**: Reportes consolidados y análisis de cumplimiento.

---

## Tecnologías y Arquitectura
- **Lenguaje**: Kotlin (100%).
- **UI Toolkit**: Jetpack Compose & Material 3 (Paleta Emerald Teal & Coral).
- **Arquitectura**: MVVM con StateFlow.
- **Persistencia**: Room / SQLite con seed data de Anexo 9.
- **Pruebas Unitarias**: 14 pruebas unitarias en `app/src/test` (pasando 100%).

---

## Pruebas Unitarias
Para ejecutar las pruebas unitarias:
```bash
./gradlew test
```

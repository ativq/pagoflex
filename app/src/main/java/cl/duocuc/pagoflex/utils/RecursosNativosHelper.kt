package cl.duocuc.pagoflex.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import cl.duocuc.pagoflex.data.model.PagoDetalle

object RecursosNativosHelper {

    private const val CHANNEL_ID = "pagoflex_avisos_channel"

    fun enviarNotificacionPositiva(context: Context, titulo: String, mensaje: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Avisos de Vencimiento PagoFlex",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Recordatorios amigables de compromisos de pago"
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), builder.build())
    }

    fun compartirComprobante(context: Context, pago: PagoDetalle) {
        val cuotaTexto = if (pago.numeroCuota != null && pago.totalCuotas != null) {
            " · Cuota ${pago.numeroCuota}/${pago.totalCuotas}"
        } else ""

        val textoComprobante = """
            ✅ COMPROBANTE DE PAGO PAGOFLEX
            N° Comprobante: ${pago.numeroComprobante}
            Fecha y hora: ${pago.fechaHora}
            Pagado por: ${pago.nombreUsuario} · RUT ${Formatters.ocultarRut(pago.rutUsuario)}
            Empresa: ${pago.nombreFantasiaEmpresa} (${pago.razonSocialEmpresa})
            Folio compromiso: ${pago.folioCompromiso}
            Concepto: ${pago.concepto}$cuotaTexto
            Vencimiento: ${pago.fechaVencimiento}
            Monto cuota: ${Formatters.formatearCLP(pago.montoOriginal)}
            Recargo por atraso: ${Formatters.formatearCLP(pago.recargoAplicado)}
            Total pagado: ${Formatters.formatearCLP(pago.montoPagado)}
            Medio de pago: ${pago.medioPago}
            Canal: ${pago.canal}
            Estado: Pago confirmado
        """.trimIndent()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, textoComprobante)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Enviar comprobante a...")
        context.startActivity(shareIntent)
    }

    fun vibrarExito(context: Context) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(150)
        }
    }
}
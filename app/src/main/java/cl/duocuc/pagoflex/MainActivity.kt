package cl.duocuc.pagoflex

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import cl.duocuc.pagoflex.data.local.PagoFlexDbHelper
import cl.duocuc.pagoflex.data.local.SesionPreferencias
import cl.duocuc.pagoflex.data.repository.PagoFlexRepository
import cl.duocuc.pagoflex.navigation.AppNavegacion
import cl.duocuc.pagoflex.ui.viewmodel.AgenteViewModel
import cl.duocuc.pagoflex.ui.viewmodel.EjecutivoViewModel
import cl.duocuc.pagoflex.ui.viewmodel.UsuarioViewModel

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        val dbHelper = PagoFlexDbHelper(applicationContext)
        val preferencias = SesionPreferencias(applicationContext)
        val repository = PagoFlexRepository(dbHelper)

        val usuarioViewModel = UsuarioViewModel(repository, preferencias)
        val agenteViewModel = AgenteViewModel(repository, preferencias)
        val ejecutivoViewModel = EjecutivoViewModel(repository, preferencias)

        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavegacion(
                        repository = repository,
                        usuarioViewModel = usuarioViewModel,
                        agenteViewModel = agenteViewModel,
                        ejecutivoViewModel = ejecutivoViewModel
                    )
                }
            }
        }
    }
}
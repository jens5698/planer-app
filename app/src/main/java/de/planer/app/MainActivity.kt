package de.planer.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import de.planer.app.ui.PlanerNavHost
import de.planer.app.ui.theme.PlanerTheme

class MainActivity : ComponentActivity() {
    private val app get() = application as PlanerApp

    private val permissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            app.calendar.refreshPermission()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val wanted = buildList {
            add(Manifest.permission.READ_CALENDAR)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (savedInstanceState == null) permissions.launch(wanted.toTypedArray())
        setContent {
            PlanerTheme {
                PlanerNavHost(app.repository, app.calendar)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Berechtigung kann in den Systemeinstellungen geändert worden sein.
        app.calendar.refreshPermission()
    }
}

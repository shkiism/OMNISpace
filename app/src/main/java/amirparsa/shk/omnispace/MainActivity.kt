package amirparsa.shk.omnispace

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import amirparsa.shk.omnispace.core.designsystem.OmniSpaceTheme
import amirparsa.shk.omnispace.core.designsystem.OmniThemeManager
import amirparsa.shk.omnispace.core.notification.OmniNotificationManager
import amirparsa.shk.omnispace.presentation.main.OmniMainScaffold as PresentationOmniMainScaffold
import amirparsa.shk.omnispace.presentation.main.PlaceholderScreen as PresentationPlaceholderScreen
import amirparsa.shk.omnispace.presentation.main.model.ChatNavArgs

import dagger.hilt.android.AndroidEntryPoint

// Re-exports for backward compatibility
typealias ChatNavArgs = ChatNavArgs

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var incomingDestination by mutableStateOf<String?>(null)
    private var incomingIssueKey by mutableStateOf<String?>(null)

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        OmniThemeManager.init(this)
        OmniNotificationManager.initChannels(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        handleIntent(intent)
        enableEdgeToEdge()
        setContent {
            OmniSpaceTheme(themeMode = OmniThemeManager.themeMode) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    PresentationOmniMainScaffold(
                        incomingDestination = incomingDestination,
                        incomingIssueKey = incomingIssueKey,
                        onClearIncoming = {
                            incomingDestination = null
                            incomingIssueKey = null
                        }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val dest = intent?.getStringExtra(OmniNotificationManager.EXTRA_DESTINATION)
        val issueKey = intent?.getStringExtra(OmniNotificationManager.EXTRA_ISSUE_KEY)
        if (dest != null) {
            incomingDestination = dest
            incomingIssueKey = issueKey
        }
    }
}

/**
 * Facade entry-point for OmniMainScaffold maintaining backward compatibility.
 */
@Composable
fun OmniMainScaffold(
    incomingDestination: String? = null,
    incomingIssueKey: String? = null,
    onClearIncoming: () -> Unit = {}
) {
    PresentationOmniMainScaffold(
        incomingDestination = incomingDestination,
        incomingIssueKey = incomingIssueKey,
        onClearIncoming = onClearIncoming
    )
}

/**
 * Re-export of PlaceholderScreen for backward compatibility.
 */
@Composable
fun PlaceholderScreen(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    PresentationPlaceholderScreen(
        title = title,
        subtitle = subtitle,
        modifier = modifier
    )
}

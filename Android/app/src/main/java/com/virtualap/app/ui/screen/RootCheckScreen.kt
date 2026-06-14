package com.virtualap.app.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.virtualap.app.R
import com.virtualap.app.util.RootStatus
import kotlinx.coroutines.delay

/**
 * Full-screen root gate. Only two visible states:
 *  - Checking: verifying access.
 *  - Denied:   "root required" - polls in the background and advances itself the
 *              moment the user grants root (no buttons).
 * The Granted state is never rendered: navigation moves on as soon as root is
 * confirmed, so this screen is only ever seen when root is missing.
 */
@Composable
fun RootCheckScreen(
    rootStatus: RootStatus = RootStatus.Checking,
    onPollRoot: () -> Unit = {}
) {
    var titleVisible by remember { mutableStateOf(false) }
    var cardVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(60); titleVisible = true
        delay(120); cardVisible = true
    }

    // While denied, keep re-checking so the screen advances itself once the user
    // grants root in their root manager.
    LaunchedEffect(rootStatus) {
        if (rootStatus == RootStatus.Denied) {
            while (true) {
                delay(2500)
                onPollRoot()
            }
        }
    }

    val titleAlpha by animateFloatAsState(
        if (titleVisible) 1f else 0f,
        tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "titleAlpha"
    )
    val cardAlpha by animateFloatAsState(
        if (cardVisible) 1f else 0f,
        tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "cardAlpha"
    )

    Scaffold(containerColor = Color.Transparent) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically)
        ) {
            Text(
                text = stringResource(R.string.root_check_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(titleAlpha)
            )

            val denied = rootStatus == RootStatus.Denied
            val accent = if (denied) MaterialTheme.colorScheme.error
                         else MaterialTheme.colorScheme.onSurfaceVariant

            Surface(
                modifier = Modifier.fillMaxWidth().alpha(cardAlpha),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceContainer,
                border = BorderStroke(1.dp, accent.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (denied) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                            tonalElevation = 0.dp
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                modifier = Modifier.padding(12.dp).size(32.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                        Text(
                            text = stringResource(R.string.root_denied),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = stringResource(R.string.root_check_body),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            strokeWidth = 4.dp
                        )
                        Text(
                            text = stringResource(R.string.checking_root),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

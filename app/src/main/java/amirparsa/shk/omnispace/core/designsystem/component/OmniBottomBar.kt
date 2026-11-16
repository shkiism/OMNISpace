package amirparsa.shk.omnispace.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.ViewKanban
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import amirparsa.shk.omnispace.core.designsystem.OmniTheme

enum class OmniNavigationTab(
    val titleFa: String,
    val titleEn: String,
    val icon: ImageVector
) {
    TODAY("روز من", "Today", Icons.Default.AutoAwesome),
    WORKSPACE("کارها", "Workspace", Icons.Default.ViewKanban),
    CONNECT("ارتباطات", "Connect", Icons.AutoMirrored.Filled.Chat),
    HR_GROWTH("سازمان من", "My HR", Icons.Default.Badge)
}

@Composable
fun OmniBottomBar(
    currentTab: OmniNavigationTab,
    onTabSelected: (OmniNavigationTab) -> Unit,
    modifier: Modifier = Modifier,
    usePersian: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(OmniTheme.colors.card.copy(alpha = 0.96f))
            .border(width = 1.dp, color = OmniTheme.colors.borderSubtle)
            .navigationBarsPadding()
            .height(64.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OmniNavigationTab.entries.forEach { tab ->
            val isSelected = tab == currentTab
            val contentColor = if (isSelected) OmniTheme.colors.primaryViolet else OmniTheme.colors.textMuted

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = tab.icon,
                    contentDescription = if (usePersian) tab.titleFa else tab.titleEn,
                    tint = contentColor,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = if (usePersian) tab.titleFa else tab.titleEn,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = contentColor,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}


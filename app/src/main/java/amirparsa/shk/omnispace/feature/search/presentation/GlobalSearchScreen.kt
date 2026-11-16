package amirparsa.shk.omnispace.feature.search.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import amirparsa.shk.omnispace.core.designsystem.DarkBorderSubtle
import amirparsa.shk.omnispace.core.designsystem.DarkCard
import amirparsa.shk.omnispace.core.designsystem.DarkElevated
import amirparsa.shk.omnispace.core.designsystem.DarkPrimaryViolet
import amirparsa.shk.omnispace.core.designsystem.DarkStatusDanger
import amirparsa.shk.omnispace.core.designsystem.DarkTextMuted
import amirparsa.shk.omnispace.core.designsystem.DarkTextSecondary
import amirparsa.shk.omnispace.core.designsystem.OmniTheme

/**
 * Stateful entry composable for Global Search screen following MVI pattern.
 */
@Composable
fun GlobalSearchScreen(
    onBackClick: () -> Unit = {},
    onOpenIssue: (String) -> Unit = {},
    onOpenChat: (String) -> Unit = {},
    onOpenWiki: (String) -> Unit = {},
    onOpenProfile: (String) -> Unit = {},
    viewModel: GlobalSearchViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                GlobalSearchEffect.NavigateBack -> onBackClick()
                is GlobalSearchEffect.OpenIssue -> onOpenIssue(effect.key)
                is GlobalSearchEffect.OpenChat -> onOpenChat(effect.key)
                is GlobalSearchEffect.OpenWiki -> onOpenWiki(effect.key)
                is GlobalSearchEffect.OpenProfile -> onOpenProfile(effect.key)
            }
        }
    }

    GlobalSearchContent(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier
    )
}

/**
 * Stateless UI content composable for Global Search screen.
 */
@Composable
fun GlobalSearchContent(
    state: GlobalSearchUiState,
    onIntent: (GlobalSearchIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(OmniTheme.colors.canvas)
        ) {
            // Search Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkCard)
                    .border(0.5.dp, DarkBorderSubtle)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = { onIntent(GlobalSearchIntent.BackClicked) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "بازگشت",
                        tint = Color.White
                    )
                }

                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { onIntent(GlobalSearchIntent.QueryChanged(it)) },
                    placeholder = {
                        Text(
                            text = "جستجوی معنایی هوش مصنوعی و کلیدواژه...",
                            fontSize = 13.sp,
                            color = DarkTextMuted
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = DarkPrimaryViolet,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (state.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onIntent(GlobalSearchIntent.ClearQuery) }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "پاک کردن",
                                    tint = DarkTextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkElevated,
                        unfocusedContainerColor = DarkElevated,
                        focusedBorderColor = DarkPrimaryViolet,
                        unfocusedBorderColor = DarkBorderSubtle,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )
            }

            // Category Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SearchCategory.entries.forEach { category ->
                    val isSelected = category == state.selectedCategory
                    Surface(
                        modifier = Modifier.clickable { onIntent(GlobalSearchIntent.SelectCategory(category)) },
                        color = if (isSelected) DarkPrimaryViolet else DarkCard,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) DarkPrimaryViolet else DarkBorderSubtle
                        )
                    ) {
                        Text(
                            text = category.titleFa,
                            color = if (isSelected) Color.Black else Color.White,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Quick Filter Toggles
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier.clickable { onIntent(GlobalSearchIntent.ToggleFilterMine) },
                    color = if (state.filterOnlyMine) DarkPrimaryViolet.copy(alpha = 0.2f) else DarkElevated,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (state.filterOnlyMine) DarkPrimaryViolet else DarkBorderSubtle
                    )
                ) {
                    Text(
                        text = "تخصیص‌یافته به من",
                        color = if (state.filterOnlyMine) DarkPrimaryViolet else DarkTextSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    modifier = Modifier.clickable { onIntent(GlobalSearchIntent.ToggleFilterHighPriority) },
                    color = if (state.filterOnlyHighPriority) DarkStatusDanger.copy(alpha = 0.2f) else DarkElevated,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (state.filterOnlyHighPriority) DarkStatusDanger else DarkBorderSubtle
                    )
                ) {
                    Text(
                        text = "فوری و بحرانی",
                        color = if (state.filterOnlyHighPriority) DarkStatusDanger else DarkTextSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Results count
            if (state.filteredResults.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "یافته‌ها (${state.filteredResults.size} مورد)",
                        fontSize = 12.sp,
                        color = DarkTextMuted,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "مبتنی بر موتور برداری هوش مصنوعی",
                        fontSize = 10.sp,
                        color = DarkPrimaryViolet
                    )
                }
            }

            // Main Content Area
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when {
                    state.isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = DarkPrimaryViolet,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(36.dp)
                                )
                                Text(
                                    text = "در حال جستجوی هوشمند در منابع سازمانی...",
                                    color = DarkTextSecondary,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    state.errorMessage != null -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(DarkStatusDanger.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = DarkStatusDanger,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Text(
                                    text = state.errorMessage ?: "خطا در دریافت نتایج جستجو",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Button(
                                    onClick = { onIntent(GlobalSearchIntent.Retry) },
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkPrimaryViolet),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = Color.Black
                                        )
                                        Text(text = "تلاش مجدد", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    state.searchQuery.isBlank() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(DarkElevated),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = DarkTextMuted,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Text(
                                    text = "جستجوی جامع OMNISpace",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "کلیدواژه یا عبارت مورد نظر را برای جستجو در تسک‌ها، پیام‌ها، اسناد و همکاران تایپ کنید.",
                                    color = DarkTextSecondary,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )
                            }
                        }
                    }

                    state.filteredResults.isEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "موردی با این عبارت یافت نشد.",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "می‌توانید فیلتر دسته‌بندی را تغییر دهید یا از کلمات کلیدی دیگری استفاده کنید.",
                                    color = DarkTextMuted,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    else -> {
                        state.filteredResults.forEach { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onIntent(GlobalSearchIntent.ResultItemClicked(item)) },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkCard),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(item.tagColor.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when (item.category) {
                                                SearchCategory.ISSUES -> Icons.AutoMirrored.Filled.Assignment
                                                SearchCategory.CHATS -> Icons.Default.ChatBubbleOutline
                                                SearchCategory.WIKI -> Icons.Default.Description
                                                SearchCategory.PEOPLE -> Icons.Default.Person
                                                SearchCategory.ALL -> Icons.Default.Search
                                            },
                                            contentDescription = null,
                                            tint = item.tagColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = item.title,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Surface(
                                                color = item.tagColor.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = item.metaTag,
                                                    color = item.tagColor,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Text(
                                            text = item.subtitle,
                                            fontSize = 11.sp,
                                            color = DarkTextSecondary,
                                            maxLines = 2
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

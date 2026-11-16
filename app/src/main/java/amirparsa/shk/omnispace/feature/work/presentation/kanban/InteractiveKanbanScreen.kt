package amirparsa.shk.omnispace.feature.work.presentation.kanban

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import amirparsa.shk.omnispace.core.designsystem.DarkBorderSubtle
import amirparsa.shk.omnispace.core.designsystem.DarkCard
import amirparsa.shk.omnispace.core.designsystem.DarkElevated
import amirparsa.shk.omnispace.core.designsystem.DarkPrimaryViolet
import amirparsa.shk.omnispace.core.designsystem.DarkTextMuted
import amirparsa.shk.omnispace.core.designsystem.DarkTextSecondary
import amirparsa.shk.omnispace.core.designsystem.OmniTheme
import amirparsa.shk.omnispace.feature.work.domain.model.KanbanCardItem
import amirparsa.shk.omnispace.feature.work.domain.model.KanbanColumnStatus

/**
 * Stateful entry composable for Interactive Kanban Board following MVI architecture.
 */
@Composable
fun InteractiveKanbanScreen(
    projectName: String = "تخته اسکرام و کانبان",
    viewModel: InteractiveKanbanViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onTaskClick: (String) -> Unit = {},
    onNewTaskClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                InteractiveKanbanEffect.NavigateBack -> onBackClick()
                InteractiveKanbanEffect.OpenNewTask -> onNewTaskClick()
                is InteractiveKanbanEffect.OpenTask -> onTaskClick(effect.key)
                is InteractiveKanbanEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    InteractiveKanbanContent(
        state = state.copy(projectName = projectName),
        onIntent = viewModel::onIntent
    )
}

/**
 * Pure Stateless Composable rendering the Interactive Kanban Board.
 */
@Composable
fun InteractiveKanbanContent(
    state: InteractiveKanbanUiState,
    onIntent: (InteractiveKanbanIntent) -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(OmniTheme.colors.canvas)
        ) {
            // Header Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkCard)
                    .border(0.5.dp, DarkBorderSubtle)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = { onIntent(InteractiveKanbanIntent.BackClicked) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "بازگشت",
                            tint = Color.White
                        )
                    }
                    Column {
                        Text(
                            text = "بُرد کانبان چابک (Kanban Board)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = state.projectName,
                            fontSize = 11.sp,
                            color = DarkTextSecondary
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { onIntent(InteractiveKanbanIntent.NewTaskClicked) },
                        modifier = Modifier
                            .size(34.dp)
                            .background(DarkPrimaryViolet, RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "تسک جدید",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            if (state.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp),
                    color = DarkPrimaryViolet,
                    trackColor = DarkElevated
                )
            }

            // Horizontally Scrollable Kanban Columns
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(rememberScrollState())
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KanbanColumnStatus.entries.forEach { colStatus ->
                    val columnCards = state.cards.filter { it.status == colStatus }
                    KanbanColumnView(
                        status = colStatus,
                        cards = columnCards,
                        onCardClick = { onIntent(InteractiveKanbanIntent.TaskClicked(it.key)) },
                        onMoveStatus = { card, target ->
                            onIntent(InteractiveKanbanIntent.MoveCard(card.id, target))
                        },
                        onAddClick = { onIntent(InteractiveKanbanIntent.NewTaskClicked) }
                    )
                }
            }
        }
    }
}

@Composable
private fun KanbanColumnView(
    status: KanbanColumnStatus,
    cards: List<KanbanCardItem>,
    onCardClick: (KanbanCardItem) -> Unit,
    onMoveStatus: (KanbanCardItem, KanbanColumnStatus) -> Unit,
    onAddClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard.copy(alpha = 0.9f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            // Column Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(status.color)
                    )
                    Text(
                        text = status.titleFa,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        color = DarkElevated,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "${cards.size}",
                            color = DarkTextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onAddClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "افزودن کارت",
                        tint = DarkTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Cards Vertical List
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (cards.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "بدون تسک",
                            color = DarkTextMuted,
                            fontSize = 11.sp
                        )
                    }
                } else {
                    cards.forEach { card ->
                        KanbanCard(
                            card = card,
                            onClick = { onCardClick(card) },
                            onMoveStatus = { newSt -> onMoveStatus(card, newSt) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KanbanCard(
    card: KanbanCardItem,
    onClick: () -> Unit,
    onMoveStatus: (KanbanColumnStatus) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = DarkElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Top Row: Key + Type + Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val tColor = Color(card.typeColor)
                    Surface(
                        color = tColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = card.key,
                            color = tColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = card.type,
                        color = DarkTextMuted,
                        fontSize = 10.sp
                    )
                }

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "عملیات",
                            tint = DarkTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        KanbanColumnStatus.entries.filter { it != card.status }.forEach { targetStatus ->
                            DropdownMenuItem(
                                text = { Text("انتقال به ${targetStatus.titleFa}", fontSize = 12.sp) },
                                onClick = {
                                    showMenu = false
                                    onMoveStatus(targetStatus)
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.SwapHoriz,
                                        contentDescription = null,
                                        tint = targetStatus.color,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = card.title,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(card.priorityColor))
                    )
                    Text(
                        text = card.priorityName,
                        color = DarkTextSecondary,
                        fontSize = 10.sp
                    )
                    Text(text = "•", color = DarkTextMuted, fontSize = 10.sp)
                    Text(
                        text = card.dueDate,
                        color = DarkTextMuted,
                        fontSize = 10.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(DarkPrimaryViolet.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = card.assigneeInitials,
                        color = DarkPrimaryViolet,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


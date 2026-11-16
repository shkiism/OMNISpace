package amirparsa.shk.omnispace.feature.chat.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import amirparsa.shk.omnispace.core.designsystem.OmniSpaceTheme
import amirparsa.shk.omnispace.core.designsystem.OmniTheme
import amirparsa.shk.omnispace.core.designsystem.OmniThemeMode
import amirparsa.shk.omnispace.core.designsystem.component.omniShimmer
import amirparsa.shk.omnispace.feature.chat.domain.model.ChatMessage
import amirparsa.shk.omnispace.feature.chat.domain.model.MessageType

/**
 * Stateful entry composable for Chat Room following MVI architecture.
 */
@Composable
fun ChatRoomScreen(
    roomId: String? = null,
    roomTitle: String = "گفتگو",
    roomSubtitle: String = "",
    linkedIssueKey: String? = null,
    linkedIssueTitle: String? = null,
    viewModel: ChatRoomViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onStartVideoCall: () -> Unit = {},
    onStartVoiceCall: () -> Unit = {},
    onOpenLinkedIssue: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    val effectiveRoomId = roomId ?: linkedIssueKey ?: "general"

    LaunchedEffect(effectiveRoomId) {
        viewModel.loadMessages(effectiveRoomId)
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ChatRoomEffect.NavigateBack -> onBackClick()
                ChatRoomEffect.StartVideoCall -> onStartVideoCall()
                ChatRoomEffect.StartVoiceCall -> onStartVoiceCall()
                is ChatRoomEffect.OpenLinkedIssue -> onOpenLinkedIssue(effect.key)
                is ChatRoomEffect.ShowToast -> { /* Handle toast */ }
                ChatRoomEffect.ScrollToBottom -> {
                    if (state.messages.isNotEmpty()) {
                        listState.animateScrollToItem(state.messages.size)
                    }
                }
            }
        }
    }

    ChatRoomContent(
        state = state.copy(
            roomId = effectiveRoomId,
            roomTitle = roomTitle,
            roomSubtitle = roomSubtitle,
            linkedIssueKey = linkedIssueKey,
            linkedIssueTitle = linkedIssueTitle
        ),
        onIntent = viewModel::onIntent,
        modifier = modifier
    )
}

/**
 * Pure Stateless Composable rendering Chat Room.
 */
@Composable
fun ChatRoomContent(
    state: ChatRoomUiState,
    onIntent: (ChatRoomIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OmniTheme.colors.canvas)
            .navigationBarsPadding()
            .imePadding()
    ) {
        // TOP APP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(OmniTheme.colors.card)
                .border(0.5.dp, OmniTheme.colors.borderSubtle)
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                IconButton(onClick = { onIntent(ChatRoomIntent.BackClicked) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "بازگشت",
                        tint = OmniTheme.colors.textPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF7C3AED).copy(alpha = 0.2f))
                        .border(1.dp, Color(0xFF7C3AED).copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Tag,
                        contentDescription = null,
                        tint = Color(0xFFC4B5FD),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = state.roomTitle,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = OmniTheme.colors.textPrimary
                    )
                    Text(
                        text = state.roomSubtitle,
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            // Quick Call Actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = { onIntent(ChatRoomIntent.StartVoiceCallClicked) }) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "تماس صوتی",
                        tint = Color(0xFF94A3B8)
                    )
                }
                IconButton(onClick = { onIntent(ChatRoomIntent.StartVideoCallClicked) }) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "تماس تصویری LiveKit",
                        tint = Color(0xFF9D7BF6)
                    )
                }
            }
        }

        // LINKED ISSUE BANNER
        if (state.linkedIssueKey != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF16152B))
                    .border(0.5.dp, Color(0xFF7C3AED).copy(alpha = 0.35f))
                    .clickable { onIntent(ChatRoomIntent.OpenLinkedIssueClicked(state.linkedIssueKey)) }
                    .padding(horizontal = 16.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF7C3AED))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = state.linkedIssueKey,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "گفتگوی مرتبط با تسک: ${state.linkedIssueTitle ?: ""}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFE2E8F0)
                    )
                }
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = null,
                    tint = Color(0xFF9D7BF6),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // MESSAGES STREAM
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (state.errorMessage != null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF2A1518))
                            .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = state.errorMessage,
                                fontSize = 12.sp,
                                color = Color(0xFFFDA4AF)
                            )
                        }
                    }
                }
            }

            if (state.isLoading) {
                items(3) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(60.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .omniShimmer(shape = RoundedCornerShape(14.dp))
                    )
                }
            } else if (state.messages.isEmpty()) {
                item {
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
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Chat,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = "هنوز پیامی در این گفتگو ارسال نشده است",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = "اولین نفری باشید که پیامی ارسال می‌کند.",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            } else {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1E2230))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "امروز",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }

                items(state.messages, key = { it.id }) { msg ->
                    ChatBubble(
                        message = msg,
                        onVoiceToggle = { onIntent(ChatRoomIntent.ToggleVoicePlayback(msg.id)) },
                        onDownload = { onIntent(ChatRoomIntent.DownloadAttachment(msg.id)) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        // BOTTOM INPUT BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(OmniTheme.colors.card)
                .border(0.5.dp, OmniTheme.colors.borderSubtle)
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = { /* File attachment */ },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = "پیوست فایل",
                    tint = Color(0xFF94A3B8)
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF161A24))
                    .border(1.dp, Color(0xFF262C3C), RoundedCornerShape(22.dp))
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                TextField(
                    value = state.inputText,
                    onValueChange = { onIntent(ChatRoomIntent.UpdateInput(it)) },
                    placeholder = {
                        Text(
                            text = "پیام خود را بنویسید...",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 4
                )
            }

            if (state.inputText.isBlank()) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E2433))
                        .clickable { /* Record voice */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "ضبط صدا",
                        tint = Color(0xFF9D7BF6),
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF7C3AED))
                        .clickable(enabled = !state.isSending) { onIntent(ChatRoomIntent.SendMessage) },
                    contentAlignment = Alignment.Center
                ) {
                    if (state.isSending) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "ارسال",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    onVoiceToggle: () -> Unit = {},
    onDownload: () -> Unit = {}
) {
    val isMe = message.isFromMe

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isMe) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF3B2E58)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = message.senderInitials,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD8B4FE)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Text(
                text = if (isMe) "شما" else message.senderName,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isMe) Color(0xFFC4B5FD) else Color(0xFF9D7BF6),
                modifier = Modifier.padding(bottom = 3.dp, start = 4.dp, end = 4.dp)
            )

            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isMe) 16.dp else 4.dp,
                            bottomEnd = if (isMe) 4.dp else 16.dp
                        )
                    )
                    .background(
                        if (isMe) Color(0xFF4C1D95) else Color(0xFF1E2433)
                    )
                    .border(
                        1.dp,
                        if (isMe) Color(0xFF6D28D9) else Color(0xFF2B3347),
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isMe) 16.dp else 4.dp,
                            bottomEnd = if (isMe) 4.dp else 16.dp
                        )
                    )
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                when (message.type) {
                    MessageType.TEXT -> {
                        Text(
                            text = message.text ?: "",
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            color = Color.White
                        )
                    }

                    MessageType.VOICE_NOTE -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF9D7BF6))
                                    .clickable { onVoiceToggle() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (message.isPlayingVoice) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val heights = listOf(8, 14, 20, 10, 16, 24, 18, 12, 22, 16, 8, 14, 20, 10)
                                heights.forEach { h ->
                                    Box(
                                        modifier = Modifier
                                            .width(3.dp)
                                            .height(h.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(Color(0xFF94A3B8))
                                    )
                                }
                            }

                            Text(
                                text = message.voiceDuration ?: "۰:۰۰",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEDE9FE)
                            )
                        }
                    }

                    MessageType.FILE_ATTACHMENT -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF381E72)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = Color(0xFFC4B5FD),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = message.fileName ?: "",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${message.fileSize} • ${message.fileExtension}",
                                    fontSize = 10.sp,
                                    color = Color(0xFFCBD5E1)
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "دانلود",
                                tint = Color(0xFFC4B5FD),
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { onDownload() }
                            )
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(top = 3.dp, start = 4.dp, end = 4.dp)
            ) {
                Text(
                    text = message.time,
                    fontSize = 10.sp,
                    color = Color(0xFF64748B)
                )
                if (isMe) {
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = "تحویل داده شد",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Preview(name = "Chat Room Preview", showBackground = true)
@Composable
fun ChatRoomPreview() {
    OmniSpaceTheme(themeMode = OmniThemeMode.DARK) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ChatRoomContent(
                state = ChatRoomUiState(),
                onIntent = {}
            )
        }
    }
}


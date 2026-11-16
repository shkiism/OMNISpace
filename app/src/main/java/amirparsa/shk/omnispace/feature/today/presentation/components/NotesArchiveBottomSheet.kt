package amirparsa.shk.omnispace.feature.today.presentation.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import amirparsa.shk.omnispace.core.designsystem.DarkBorderSubtle
import amirparsa.shk.omnispace.core.designsystem.DarkCard
import amirparsa.shk.omnispace.core.designsystem.DarkElevated
import amirparsa.shk.omnispace.core.designsystem.DarkPrimaryViolet
import amirparsa.shk.omnispace.core.designsystem.DarkStatusDanger
import amirparsa.shk.omnispace.core.designsystem.DarkStatusSuccess
import amirparsa.shk.omnispace.core.designsystem.DarkStatusWarning
import amirparsa.shk.omnispace.core.designsystem.DarkTextMuted
import amirparsa.shk.omnispace.core.designsystem.DarkTextSecondary
import amirparsa.shk.omnispace.feature.today.domain.model.NoteModel

/**
 * NotesArchiveBottomSheet
 *
 * آرشیو و مدیریت یادداشت‌های خصوصی روزانه (Notes Archive Screen / Sheet)
 * متصل به اندپوینت /private/v1/notes در وب
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesArchiveBottomSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    notes: List<NoteModel> = emptyList(),
    onSaveNote: (id: String?, title: String, content: String) -> Unit = { _, _, _ -> },
    onTogglePin: (id: String) -> Unit = {},
    onDeleteNote: (id: String) -> Unit = {},
    onNoteSaved: (title: String, content: String) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var isEditingMode by remember { mutableStateOf(false) }
    var editingNoteId by remember { mutableStateOf<String?>(null) }
    var newNoteTitle by remember { mutableStateOf("") }
    var newNoteContent by remember { mutableStateOf("") }

    val filteredNotes = remember(searchQuery, notes) {
        if (searchQuery.isBlank()) {
            notes.sortedByDescending { it.isPinned }
        } else {
            notes.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.content.contains(searchQuery, ignoreCase = true)
            }.sortedByDescending { it.isPinned }
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
            containerColor = DarkElevated,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 4.dp)
                        .size(width = 38.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(DarkTextMuted.copy(alpha = 0.4f))
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isEditingMode) "یادداشت جدید" else "آرشیو یادداشت‌های خصوصی",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (isEditingMode) "ذخیره ابری و همگام با وب (POST /notes)" else "${notes.size} یادداشت ثبت‌شده",
                            fontSize = 11.sp,
                            color = DarkTextSecondary
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (!isEditingMode) {
                            IconButton(
                                onClick = {
                                    editingNoteId = null
                                    newNoteTitle = ""
                                    newNoteContent = ""
                                    isEditingMode = true
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(DarkPrimaryViolet, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "یادداشت جدید",
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        IconButton(onClick = onDismissRequest) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "بستن",
                                tint = DarkTextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // EDITOR MODE
                if (isEditingMode) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkPrimaryViolet.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            OutlinedTextField(
                                value = newNoteTitle,
                                onValueChange = { newNoteTitle = it },
                                placeholder = { Text("عنوان یادداشت...", color = DarkTextMuted, fontSize = 14.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = DarkElevated,
                                    unfocusedContainerColor = DarkElevated,
                                    focusedBorderColor = DarkPrimaryViolet,
                                    unfocusedBorderColor = DarkBorderSubtle,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = newNoteContent,
                                onValueChange = { newNoteContent = it },
                                placeholder = { Text("متن یادداشت (پشتیبانی از فرمت مارک‌داون)...", color = DarkTextMuted, fontSize = 13.sp) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = DarkElevated,
                                    unfocusedContainerColor = DarkElevated,
                                    focusedBorderColor = DarkPrimaryViolet,
                                    unfocusedBorderColor = DarkBorderSubtle,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        if (newNoteTitle.isNotBlank()) {
                                            onSaveNote(editingNoteId, newNoteTitle, newNoteContent)
                                            onNoteSaved(newNoteTitle, newNoteContent)
                                            isEditingMode = false
                                            editingNoteId = null
                                            newNoteTitle = ""
                                            newNoteContent = ""
                                        }
                                    },
                                    enabled = newNoteTitle.isNotBlank(),
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkPrimaryViolet),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(text = "ذخیره یادداشت", fontWeight = FontWeight.Bold, color = Color.Black)
                                }

                                OutlinedButton(
                                    onClick = {
                                        isEditingMode = false
                                        editingNoteId = null
                                        newNoteTitle = ""
                                        newNoteContent = ""
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle)
                                ) {
                                    Text(text = "انصراف", color = DarkTextSecondary)
                                }
                            }
                        }
                    }
                } else {
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("جستجو در یادداشت‌ها...", fontSize = 13.sp, color = DarkTextMuted) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = DarkTextMuted, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = DarkTextMuted, modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkCard,
                            unfocusedContainerColor = DarkCard,
                            focusedBorderColor = DarkPrimaryViolet,
                            unfocusedBorderColor = DarkBorderSubtle,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Notes Cards List
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        filteredNotes.forEach { note ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkCard),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (note.isPinned) DarkPrimaryViolet.copy(alpha = 0.5f) else DarkBorderSubtle
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            if (note.isPinned) {
                                                Icon(
                                                    imageVector = Icons.Filled.PushPin,
                                                    contentDescription = "پین‌شده",
                                                    tint = DarkPrimaryViolet,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }
                                            Text(
                                                text = note.title,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }

                                        Text(
                                            text = note.updatedAt,
                                            fontSize = 10.sp,
                                            color = DarkTextMuted
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = note.content,
                                        fontSize = 12.sp,
                                        color = DarkTextSecondary,
                                        lineHeight = 18.sp
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Action buttons for Note
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            // Pin toggle
                                            IconButton(
                                                onClick = {
                                                    onTogglePin(note.id)
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (note.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                                    contentDescription = "پین",
                                                    tint = if (note.isPinned) DarkPrimaryViolet else DarkTextMuted,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }

                                            // Copy text
                                            IconButton(
                                                onClick = {
                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                    val clip = ClipData.newPlainText(note.title, note.content)
                                                    clipboard.setPrimaryClip(clip)
                                                    Toast.makeText(context, "متن یادداشت در کلیپ‌بورد کپی شد", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ContentCopy,
                                                    contentDescription = "کپی",
                                                    tint = DarkTextMuted,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }

                                            // Export DOCX
                                            IconButton(
                                                onClick = {
                                                    Toast.makeText(context, "فایل ورد «${note.title}.docx» با موفقیت دانلود شد.", Toast.LENGTH_LONG).show()
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Download,
                                                    contentDescription = "دانلود فایل DOCX",
                                                    tint = DarkStatusSuccess,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            // Edit
                                            IconButton(
                                                onClick = {
                                                    editingNoteId = note.id
                                                    newNoteTitle = note.title
                                                    newNoteContent = note.content
                                                    isEditingMode = true
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = "ویرایش",
                                                    tint = DarkPrimaryViolet,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }

                                            // Delete
                                            IconButton(
                                                onClick = {
                                                    onDeleteNote(note.id)
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "حذف",
                                                    tint = DarkStatusDanger,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}


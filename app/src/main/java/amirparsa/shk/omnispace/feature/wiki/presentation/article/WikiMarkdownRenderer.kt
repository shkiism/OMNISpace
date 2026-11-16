package amirparsa.shk.omnispace.feature.wiki.presentation.article

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

sealed interface MarkdownBlock {
    data class Header(val level: Int, val text: String) : MarkdownBlock
    data class Callout(val type: CalloutType, val content: String) : MarkdownBlock
    data class CodeBlock(val language: String, val code: String) : MarkdownBlock
    data class ListItem(val text: String, val isOrdered: Boolean = false, val index: Int = 1) : MarkdownBlock
    data class Paragraph(val text: String) : MarkdownBlock
    data object Divider : MarkdownBlock
}

enum class CalloutType(val titleFa: String, val accentColor: Color, val containerColor: Color) {
    NOTE("یادداشت مهم", Color(0xFF38BDF8), Color(0xFF0C243B)),
    TIP("نکته کلیدی", Color(0xFF10B981), Color(0xFF092920)),
    WARNING("هشدار", Color(0xFFF59E0B), Color(0xFF2E1C0A)),
    IMPORTANT("الزامی", Color(0xFFA855F7), Color(0xFF25113B))
}

@Composable
fun WikiMarkdownRenderer(
    markdown: String,
    onCopyCode: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val blocks = parseMarkdownBlocks(markdown)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        blocks.forEach { block ->
            when (block) {
                is MarkdownBlock.Header -> RenderHeader(block)
                is MarkdownBlock.Callout -> RenderCallout(block)
                is MarkdownBlock.CodeBlock -> RenderCodeBlock(block, onCopyCode)
                is MarkdownBlock.ListItem -> RenderListItem(block)
                is MarkdownBlock.Paragraph -> RenderParagraph(block)
                MarkdownBlock.Divider -> HorizontalDivider(
                    color = Color(0xFF232838),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun RenderHeader(header: MarkdownBlock.Header) {
    val (fontSize, fontWeight, color) = when (header.level) {
        1 -> Triple(18.sp, FontWeight.ExtraBold, Color.White)
        2 -> Triple(15.sp, FontWeight.Bold, Color(0xFFE2E8F0))
        else -> Triple(13.sp, FontWeight.SemiBold, Color(0xFFCBD5E1))
    }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = parseInlineMarkdown(header.text),
            fontSize = fontSize,
            fontWeight = fontWeight,
            color = color,
            lineHeight = (fontSize.value * 1.4).sp
        )
        if (header.level == 1) {
            Box(
                modifier = Modifier
                    .size(width = 36.dp, height = 3.dp)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(Color(0xFF7C3AED))
            )
        }
    }
}

@Composable
private fun RenderCallout(callout: MarkdownBlock.Callout) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(callout.type.containerColor)
            .border(1.dp, callout.type.accentColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val icon = when (callout.type) {
                CalloutType.NOTE -> Icons.Default.Info
                CalloutType.TIP -> Icons.Default.Lightbulb
                CalloutType.WARNING -> Icons.Default.Warning
                CalloutType.IMPORTANT -> Icons.Default.Check
            }

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(callout.type.accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = callout.type.accentColor,
                    modifier = Modifier.size(14.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = callout.type.titleFa,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = callout.type.accentColor
                )
                Text(
                    text = parseInlineMarkdown(callout.content),
                    fontSize = 12.sp,
                    color = Color(0xFFE2E8F0),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun RenderCodeBlock(
    codeBlock: MarkdownBlock.CodeBlock,
    onCopy: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F1117))
            .border(1.dp, Color(0xFF1E2433), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = codeBlock.language.ifBlank { "CODE" }.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9D7BF6)
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1E2433))
                        .clickable { onCopy(codeBlock.code) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "کپی",
                            fontSize = 10.sp,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }
            }

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Text(
                    text = codeBlock.code,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = Color(0xFFA5B4FC),
                    lineHeight = 17.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                )
            }
        }
    }
}

@Composable
private fun RenderListItem(item: MarkdownBlock.ListItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        if (item.isOrdered) {
            Text(
                text = "${item.index}.",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFA78BFA)
            )
        } else {
            Box(
                modifier = Modifier
                    .padding(top = 7.dp)
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFA78BFA))
            )
        }

        Text(
            text = parseInlineMarkdown(item.text),
            fontSize = 12.sp,
            color = Color(0xFFCBD5E1),
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun RenderParagraph(para: MarkdownBlock.Paragraph) {
    Text(
        text = parseInlineMarkdown(para.text),
        fontSize = 12.sp,
        color = Color(0xFFCBD5E1),
        lineHeight = 20.sp
    )
}

/**
 * Parses markdown into sequential typed blocks.
 */
fun parseMarkdownBlocks(markdown: String): List<MarkdownBlock> {
    val lines = markdown.lines()
    val blocks = mutableListOf<MarkdownBlock>()
    var i = 0

    while (i < lines.size) {
        val line = lines[i].trimEnd()

        when {
            line.isBlank() -> {
                i++
            }
            line.startsWith("```") -> {
                val lang = line.removePrefix("```").trim()
                val codeLines = mutableListOf<String>()
                i++
                while (i < lines.size && !lines[i].trim().startsWith("```")) {
                    codeLines.add(lines[i])
                    i++
                }
                if (i < lines.size) i++ // skip ending ```
                blocks.add(MarkdownBlock.CodeBlock(lang, codeLines.joinToString("\n")))
            }
            line.startsWith("#") -> {
                val level = line.takeWhile { it == '#' }.length
                val text = line.removePrefix("#".repeat(level)).trim()
                blocks.add(MarkdownBlock.Header(level, text))
                i++
            }
            line.startsWith("> [!") -> {
                // Callout like > [!NOTE] or > [!TIP]
                val closeBracket = line.indexOf(']')
                val tag = if (closeBracket != -1) line.substring(4, closeBracket).uppercase() else "NOTE"
                val type = when (tag) {
                    "TIP" -> CalloutType.TIP
                    "WARNING" -> CalloutType.WARNING
                    "IMPORTANT" -> CalloutType.IMPORTANT
                    else -> CalloutType.NOTE
                }
                val contentLines = mutableListOf<String>()
                i++
                while (i < lines.size && lines[i].trimStart().startsWith(">")) {
                    val contentLine = lines[i].trimStart().removePrefix(">").trim()
                    if (contentLine.isNotBlank()) contentLines.add(contentLine)
                    i++
                }
                blocks.add(MarkdownBlock.Callout(type, contentLines.joinToString("\n")))
            }
            line.startsWith("> ") -> {
                val contentLines = mutableListOf(line.removePrefix("> ").trim())
                i++
                while (i < lines.size && lines[i].trimStart().startsWith(">")) {
                    contentLines.add(lines[i].trimStart().removePrefix(">").trim())
                    i++
                }
                blocks.add(MarkdownBlock.Callout(CalloutType.NOTE, contentLines.joinToString("\n")))
            }
            line.startsWith("- ") || line.startsWith("* ") -> {
                val itemText = line.drop(2).trim()
                blocks.add(MarkdownBlock.ListItem(itemText, isOrdered = false))
                i++
            }
            line.matches(Regex("""^\d+\.\s.*""")) -> {
                val dotIndex = line.indexOf('.')
                val num = line.substring(0, dotIndex).toIntOrNull() ?: 1
                val itemText = line.substring(dotIndex + 1).trim()
                blocks.add(MarkdownBlock.ListItem(itemText, isOrdered = true, index = num))
                i++
            }
            line.startsWith("---") || line.startsWith("***") -> {
                blocks.add(MarkdownBlock.Divider)
                i++
            }
            else -> {
                // Regular paragraph
                val paraLines = mutableListOf(line)
                i++
                while (i < lines.size && lines[i].isNotBlank() && !lines[i].startsWith("#") && !lines[i].startsWith("```") && !lines[i].startsWith("> ") && !lines[i].startsWith("- ") && !lines[i].matches(Regex("""^\d+\.\s.*"""))) {
                    paraLines.add(lines[i].trimEnd())
                    i++
                }
                blocks.add(MarkdownBlock.Paragraph(paraLines.joinToString(" ")))
            }
        }
    }

    return blocks
}

/**
 * Parses inline bold **text** and inline `code` into an AnnotatedString.
 */
fun parseInlineMarkdown(text: String): AnnotatedString {
    return buildAnnotatedString {
        var cursor = 0
        while (cursor < text.length) {
            val nextBold = text.indexOf("**", cursor)
            val nextCode = text.indexOf('`', cursor)

            val nextSpecial = when {
                nextBold != -1 && nextCode != -1 -> minOf(nextBold, nextCode)
                nextBold != -1 -> nextBold
                nextCode != -1 -> nextCode
                else -> -1
            }

            if (nextSpecial == -1) {
                append(text.substring(cursor))
                break
            }

            if (nextSpecial > cursor) {
                append(text.substring(cursor, nextSpecial))
                cursor = nextSpecial
            }

            if (text.startsWith("**", cursor)) {
                val endBold = text.indexOf("**", cursor + 2)
                if (endBold != -1) {
                    val boldContent = text.substring(cursor + 2, endBold)
                    val start = length
                    append(boldContent)
                    addStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.White), start, length)
                    cursor = endBold + 2
                } else {
                    append("**")
                    cursor += 2
                }
            } else if (text.startsWith("`", cursor)) {
                val endCode = text.indexOf('`', cursor + 1)
                if (endCode != -1) {
                    val codeContent = text.substring(cursor + 1, endCode)
                    val start = length
                    append(codeContent)
                    addStyle(
                        SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFA5B4FC),
                            background = Color(0xFF1E2433)
                        ),
                        start,
                        length
                    )
                    cursor = endCode + 1
                } else {
                    append("`")
                    cursor += 1
                }
            }
        }
    }
}


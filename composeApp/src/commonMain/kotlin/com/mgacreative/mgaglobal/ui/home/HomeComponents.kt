package com.mgacreative.mgaglobal.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mgacreative.mgaglobal.openUrl

@Composable
fun AnnouncementCard(
    title: String,
    desc: String,
    accentColor: Color,
    isWeb: Boolean = false,
    link: String = ""
) {
    Card(
        modifier = Modifier
            .width(if (isWeb) 350.dp else 260.dp)
            .height(140.dp)
            .let {
                if (link.isNotBlank()) it.clickable { openUrl(link) } else it
            },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0).copy(alpha = 0.5f))
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxHeight().width(6.dp).background(accentColor))
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color(0xFF1A1C1C)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinkifyText(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 3
                )
            }
        }
    }
}

@Composable
fun LinkifyText(
    text: String,
    style: TextStyle,
    color: Color = Color.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis
) {
    val urlPattern = Regex("(https?://[\\w\\d.\\-/\\?&%#=]*)")
    val annotatedString = buildAnnotatedString {
        append(text)
        urlPattern.findAll(text).forEach { matchResult ->
            addStyle(
                style = SpanStyle(
                    color = Color(0xFF3B82F6),
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.SemiBold
                ),
                start = matchResult.range.first,
                end = matchResult.range.last + 1
            )
            addStringAnnotation(
                tag = "URL",
                annotation = matchResult.value,
                start = matchResult.range.first,
                end = matchResult.range.last + 1
            )
        }
    }

    ClickableText(
        text = annotatedString,
        style = style.copy(color = color),
        maxLines = maxLines,
        overflow = overflow,
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    openUrl(annotation.item)
                }
        }
    )
}


package com.mgacreative.globaltrade.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mgacreative.globaltrade.manager.changeAppLanguage

@Composable
fun LanguageSwitcher(
    currentLanguage: String,
    onLanguageChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val languages = listOf(
        LanguageOption("Türkçe", "tr"),
        LanguageOption("English", "en"),
        LanguageOption("Deutsch", "de"),
        LanguageOption("Русский", "ru"),
        LanguageOption("العربية", "ar"),
        LanguageOption("中文", "zh")
    )

    Box(modifier = modifier) {
        IconButton(
            onClick = { expanded = true },
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.1f))
        ) {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = "Language",
                tint = Color.White
            )
        }

        // Glassmorphism effect Dropdown
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(180.dp)
                .background(
                    color = Color(0xFF1D2335).copy(alpha = 0.85f), // PrimaryContainer semi-transparent
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(vertical = 4.dp)
        ) {
            languages.forEach { lang ->
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = lang.name,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = if (currentLanguage == lang.code) FontWeight.Bold else FontWeight.Normal
                            )
                            if (currentLanguage == lang.code) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    onClick = {
                        expanded = false
                        onLanguageChange(lang.code)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

data class LanguageOption(val name: String, val code: String)

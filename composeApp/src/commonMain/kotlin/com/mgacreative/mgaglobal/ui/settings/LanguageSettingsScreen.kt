package com.mgacreative.mgaglobal.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mgacreative.mgaglobal.manager.changeAppLanguage
import com.mgacreative.mgaglobal.manager.getCurrentAppLanguage
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import mgaglobal.composeapp.generated.resources.*

data class LanguageOption(val code: String, val displayName: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSettingsScreen(
    onNavigateBack: () -> Unit
) {
    val languages = listOf(
        LanguageOption("tr", "Türkçe"),
        LanguageOption("en", "English"),
        LanguageOption("ar", "العربية"),
        LanguageOption("ru", "Русский"),
        LanguageOption("de", "Deutsch"),
        LanguageOption("zh-rCN", "简体中文")
    )

    var currentLanguage by remember { mutableStateOf<String?>("en") }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        val savedLang = getCurrentAppLanguage()
        if (savedLang != null) {
            currentLanguage = savedLang
        }
    }

    val successMessage = stringResource(Res.string.language_updated_success)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Public, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(Res.string.language_settings), fontWeight = FontWeight.Bold) 
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Corporate navy theme
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA)), // Clean spacing & corporate background
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(languages) { language ->
                LanguageItem(
                    language = language,
                    isSelected = currentLanguage == language.code || (currentLanguage == "zh" && language.code == "zh-rCN"),
                    onClick = {
                        coroutineScope.launch {
                            currentLanguage = language.code
                            changeAppLanguage(language.code)
                            snackbarHostState.showSnackbar(successMessage)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun LanguageItem(
    language: LanguageOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = language.displayName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.DarkGray
            )

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}



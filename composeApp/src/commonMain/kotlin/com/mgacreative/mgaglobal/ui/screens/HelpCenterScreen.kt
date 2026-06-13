package com.mgacreative.mgaglobal.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mgacreative.mgaglobal.core.domain.auth.SettingsService
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import mgaglobal.composeapp.generated.resources.*

import com.mgacreative.mgaglobal.core.domain.auth.FAQItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterScreen(onBackClick: () -> Unit) {
    val scope = rememberCoroutineScope()
    val settingsService = remember { SettingsService() }
    var contactEmail by remember { mutableStateOf("Yükleniyor...") }
    var faqList by remember { mutableStateOf<List<FAQItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val emailResult = settingsService.getContactEmail()
        contactEmail = emailResult.getOrNull() ?: "destek@globaltrade.local"
        
        val faqResult = settingsService.getHelpCenterFAQs()
        faqList = faqResult.getOrNull() ?: emptyList()
        
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(Res.string.help_center), 
                        fontWeight = FontWeight.Bold 
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF8F9FA)),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.HelpOutline,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Merhaba, nasıl yardımcı olabiliriz?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (faqList.isEmpty()) {
                    item {
                        Text("Henüz SSS eklenmemiş.", color = Color.Gray, modifier = Modifier.padding(16.dp))
                    }
                } else {
                    items(faqList) { item ->
                        FAQListItem(item)
                    }
                }
                
                item {
                    Spacer(Modifier.height(24.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Hala sorunuz mu var?", fontWeight = FontWeight.Bold)
                            Text("Destek ekibimize e-posta göndererek ulaşabilirsiniz.", style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.height(8.dp))
                            Text(contactEmail, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FAQListItem(item: FAQItem) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.question,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = item.answer,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}



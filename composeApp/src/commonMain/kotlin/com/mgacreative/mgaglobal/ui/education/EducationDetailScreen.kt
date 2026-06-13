package com.mgacreative.mgaglobal.ui.education

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mgacreative.mgaglobal.core.domain.education.EducationService
import com.mgacreative.mgaglobal.ui.education.Education
import com.mgacreative.mgaglobal.openUrl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationDetailScreen(
    eduId: String,
    onBackClick: () -> Unit,
    onStartExam: () -> Unit
) {
    var education by remember { mutableStateOf<Education?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val educationService = remember { EducationService() }

    LaunchedEffect(eduId) {
        isLoading = true
        educationService.getEducationById(eduId)
            .onSuccess { education = it }
            .onFailure { /* Handle error */ }
        isLoading = false
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("EÄŸitim DetayÄ±", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri") } },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF0F172A), titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF0F172A)),
            contentAlignment = Alignment.TopCenter
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .widthIn(max = 1200.dp)
                    .fillMaxHeight()
                    .background(Color(0xFFF8F9FA))
            ) {
                if (isLoading || education == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    val edu = education!!
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Text(edu.topic, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(edu.title, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("EÄŸitmen: ${edu.instructor}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                                
                                HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))
                                
                                // EÄŸitim Ã–zeti (Her zaman gÃ¶sterilir)
                                Text("EÄŸitim Ã–zeti", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(edu.contentText, style = MaterialTheme.typography.bodyLarge, lineHeight = 28.sp, color = Color(0xFF334155))
                                
                                Spacer(modifier = Modifier.height(32.dp))

                                // EÄŸer video/iÃ§erik linki varsa buton olarak gÃ¶ster
                                if (!edu.contentUrl.isNullOrBlank()) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.PlayCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text("EÄŸitim Ä°Ã§eriÄŸi Mevcut", fontWeight = FontWeight.Bold)
                                                Text("Videoyu veya dÃ¶kÃ¼manÄ± harici olarak aÃ§Ä±n", fontSize = 12.sp, color = Color.Gray)
                                            }
                                            Button(
                                                onClick = { openUrl(edu.contentUrl!!) },
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text("Ä°Ã§eriÄŸi AÃ§")
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(24.dp))
                                }
                            }
                        }
                        
                        if (edu.examLink.isNotBlank()) {
                            item {
                                Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                                    Button(
                                        onClick = onStartExam, 
                                        modifier = Modifier.fillMaxWidth().height(56.dp), 
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
                                    ) {
                                        Text("SÄ±nava BaÅŸla", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                                    }
                                }
                                Spacer(modifier = Modifier.height(40.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}


package com.mgacreative.mgaglobal.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
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
import com.mgacreative.mgaglobal.core.domain.auth.FAQItem
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Delete

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHelpCenterScreen(
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val settingsService = remember { SettingsService() }
    val snackbarHostState = remember { SnackbarHostState() }
    
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var faqs by remember { mutableStateOf(listOf<FAQItem>()) }
    var newQuestion by remember { mutableStateOf("") }
    var newAnswer by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val result = settingsService.getHelpCenterFAQs()
        faqs = result.getOrNull() ?: emptyList()
        isLoading = false
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Yardım Merkezi Yönetimi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                isSaving = true
                                val result = settingsService.updateHelpCenterFAQs(faqs)
                                if (result is com.mgacreative.mgaglobal.core.error.AppResult.Success) {
                                    snackbarHostState.showSnackbar("İçerik başarıyla kaydedildi.")
                                } else {
                                    snackbarHostState.showSnackbar("Hata: Kayıt başarısız.")
                                }
                                isSaving = false
                            }
                        },
                        enabled = !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Icon(Icons.Default.Save, contentDescription = "Kaydet")
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF8FAFC))
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Yeni Soru Ekle",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        OutlinedTextField(
                            value = newQuestion,
                            onValueChange = { newQuestion = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Soru") },
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newAnswer,
                            onValueChange = { newAnswer = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Cevap") },
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Button(
                            onClick = {
                                if (newQuestion.isNotBlank() && newAnswer.isNotBlank()) {
                                    faqs = faqs + FAQItem(newQuestion, newAnswer)
                                    newQuestion = ""
                                    newAnswer = ""
                                }
                            },
                            modifier = Modifier.align(Alignment.End),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Ekle", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
                ) {
                    faqs.forEachIndexed { index, faq ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Soru: ${faq.question}", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                    Text("Cevap: ${faq.answer}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }
                                IconButton(
                                    onClick = { 
                                        faqs = faqs.toMutableList().apply { removeAt(index) } 
                                    }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Sil", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = {
                        scope.launch {
                            isSaving = true
                            val result = settingsService.updateHelpCenterFAQs(faqs)
                            if (result is com.mgacreative.mgaglobal.core.error.AppResult.Success) {
                                snackbarHostState.showSnackbar("İçerik başarıyla kaydedildi.")
                            } else {
                                snackbarHostState.showSnackbar("Hata: Kayıt başarısız.")
                            }
                            isSaving = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text("Değişiklikleri Yayınla", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


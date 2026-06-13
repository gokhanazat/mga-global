package com.mgacreative.mgaglobal.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mgacreative.mgaglobal.core.domain.consultancy.Consultant
import com.mgacreative.mgaglobal.core.domain.consultancy.ConsultantService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminConsultantScreen(paddingValues: PaddingValues, onBackClick: () -> Unit) {
    val scope = rememberCoroutineScope()
    val consultantService = remember { ConsultantService() }
    var consultants by remember { mutableStateOf<List<Consultant>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingConsultant by remember { mutableStateOf<Consultant?>(null) }
    
    val snackbarHostState = remember { SnackbarHostState() }

    fun refresh() {
        isLoading = true
        scope.launch {
            val result = consultantService.getConsultants()
            if (result is com.mgacreative.mgaglobal.core.error.AppResult.Success) {
                consultants = result.data
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { refresh() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Danışman Yönetimi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        editingConsultant = null
                        showAddDialog = true 
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Ekle")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding).background(Color(0xFFF5F7FA)),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(consultants) { consultant ->
                    ConsultantAdminCard(
                        consultant = consultant,
                        onEdit = {
                            editingConsultant = consultant
                            showAddDialog = true
                        },
                        onDelete = {
                            scope.launch {
                                val res = consultantService.deleteConsultant(consultant.id)
                                if (res is com.mgacreative.mgaglobal.core.error.AppResult.Success) {
                                    refresh()
                                    snackbarHostState.showSnackbar("Danışman silindi")
                                }
                            }
                        }
                    )
                }
                if (consultants.isEmpty()) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("Henüz danışman tanımlanmamış.", color = Color.Gray)
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            ConsultantEditDialog(
                consultant = editingConsultant,
                onDismiss = { showAddDialog = false },
                onSave = { consultant ->
                    scope.launch {
                        val res = if (editingConsultant == null) {
                            consultantService.addConsultant(consultant)
                        } else {
                            consultantService.updateConsultant(consultant)
                        }
                        if (res is com.mgacreative.mgaglobal.core.error.AppResult.Success) {
                            refresh()
                            showAddDialog = false
                            snackbarHostState.showSnackbar("Danışman kaydedildi")
                        } else if (res is com.mgacreative.mgaglobal.core.error.AppResult.Error) {
                            snackbarHostState.showSnackbar("Hata: ${res.error.message ?: "Danışman kaydedilemedi"}")
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun ConsultantAdminCard(consultant: Consultant, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(consultant.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(consultant.title, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                Text(consultant.expertise, color = Color.Gray, fontSize = 12.sp)
            }
            Row {
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Düzenle", tint = Color.Blue) }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Sil", tint = Color.Red) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultantEditDialog(consultant: Consultant?, onDismiss: () -> Unit, onSave: (Consultant) -> Unit) {
    var name by remember { mutableStateOf(consultant?.name ?: "") }
    var title by remember { mutableStateOf(consultant?.title ?: "") }
    var expertise by remember { mutableStateOf(consultant?.expertise ?: "") }
    var bio by remember { mutableStateOf(consultant?.bio ?: "") }
    var email by remember { mutableStateOf(consultant?.email ?: "") }
    var phone by remember { mutableStateOf(consultant?.phone ?: "") }
    var whatsapp by remember { mutableStateOf(consultant?.whatsapp ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (consultant == null) "Yeni Danışman" else "Danışman Düzenle") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Ad Soyad") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Unvan (Örn: Kıdemli İhracat Uzmanı)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = expertise, onValueChange = { expertise = it }, label = { Text("Uzmanlık Alanı") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = bio, onValueChange = { bio = it }, label = { Text("Kısa Özgeçmiş") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("E-posta") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Telefon") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = whatsapp, onValueChange = { whatsapp = it }, label = { Text("WhatsApp Numarası") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(
                    consultant?.copy(name = name, title = title, expertise = expertise, bio = bio, email = email, phone = phone, whatsapp = whatsapp)
                    ?: Consultant(name = name, title = title, expertise = expertise, bio = bio, email = email, phone = phone, whatsapp = whatsapp)
                )
            }) { Text("Kaydet") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("İptal") } }
    )
}


package com.mgacreative.mgaglobal.ui.admin.education

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mgacreative.mgaglobal.ui.education.Education
import com.mgacreative.mgaglobal.core.domain.education.EducationService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEducationScreen(
    paddingValues: PaddingValues = PaddingValues(),
    onBackClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val educationService = remember { EducationService() }
    var educations by remember { mutableStateOf<List<Education>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    var currentEducation by remember { mutableStateOf<Education?>(null) }
    
    var title by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf("") }
    var instructor by remember { mutableStateOf("") }
    var contentText by remember { mutableStateOf("") }
    var videoUrl by remember { mutableStateOf("") }
    var examLink by remember { mutableStateOf("") }
    var contentUrl by remember { mutableStateOf("") }
    
    val snackbarHostState = remember { SnackbarHostState() }

    fun fetchEducations() {
        scope.launch {
            isLoading = true
            educationService.getAllEducations().onSuccess {
                educations = it
            }.onFailure { 
                scope.launch { snackbarHostState.showSnackbar("Eğitimler yüklenemedi: ${it.message}") } 
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        fetchEducations()
    }

    Scaffold(
        modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Eğitim Yönetimi", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { 
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = Color.White) 
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    currentEducation = null
                    title = ""
                    topic = ""
                    instructor = ""
                    contentText = ""
                    videoUrl = ""
                    examLink = ""
                    contentUrl = ""
                    showDialog = true
                },
                containerColor = Color(0xFF0F172A)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Eğitim Ekle", tint = Color.White)
            }
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding).background(Color(0xFFF8F9FA)),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(educations) { education ->
                    EducationListItem(
                        education = education,
                        onEdit = {
                            currentEducation = education
                            title = education.title
                            topic = education.topic
                            instructor = education.instructor
                            contentText = education.contentText
                            videoUrl = education.videoUrl
                            examLink = education.examLink
                            contentUrl = education.contentUrl ?: ""
                            showDialog = true
                        },
                        onDelete = {
                            scope.launch {
                                val res = educationService.deleteEducation(education.id)
                                if (res.isSuccess) {
                                    snackbarHostState.showSnackbar("Eğitim silindi")
                                    fetchEducations()
                                }
                            }
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (currentEducation == null) "Yeni Eğitim Ekle" else "Eğitimi Düzenle", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Başlık") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = topic, onValueChange = { topic = it }, label = { Text("Konu") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = instructor, onValueChange = { instructor = it }, label = { Text("Eğitmen") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = videoUrl, onValueChange = { videoUrl = it }, label = { Text("Video URL") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = examLink, onValueChange = { examLink = it }, label = { Text("Sınav Linki") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = contentUrl, onValueChange = { contentUrl = it }, label = { Text("İçerik Notları URL") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = contentText, onValueChange = { contentText = it }, label = { Text("Kısa Açıklama") }, modifier = Modifier.fillMaxWidth(), minLines = 4)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            val education = (currentEducation ?: Education()).copy(
                                title = title.trim(),
                                topic = topic.trim(),
                                instructor = instructor.trim(),
                                contentText = contentText.trim(),
                                videoUrl = videoUrl.trim(),
                                examLink = examLink.trim(),
                                contentUrl = contentUrl.trim().ifEmpty { null }
                            )
                            educationService.saveEducation(education).onSuccess {
                                snackbarHostState.showSnackbar("Eğitim kaydedildi")
                                fetchEducations()
                                showDialog = false
                            }.onFailure {
                                snackbarHostState.showSnackbar("Kayıt Hatası: ${it.message}")
                            }
                        }
                    },
                    enabled = title.isNotBlank()
                ) {
                    Text("Kaydet")
                }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("İptal") } }
        )
    }
}

@Composable
fun EducationListItem(education: Education, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(education.title, fontWeight = FontWeight.Bold)
                Text(education.topic, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Düzenle") }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Sil") }
        }
    }
}


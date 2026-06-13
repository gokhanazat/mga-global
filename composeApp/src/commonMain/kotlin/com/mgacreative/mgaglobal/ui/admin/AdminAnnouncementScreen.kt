package com.mgacreative.mgaglobal.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.sp
import com.mgacreative.mgaglobal.core.domain.announcement.Announcement
import com.mgacreative.mgaglobal.core.domain.announcement.AnnouncementService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAnnouncementScreen(
    paddingValues: PaddingValues = PaddingValues(),
    onBackClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val announcementService = remember { AnnouncementService() }
    var announcements by remember { mutableStateOf<List<Announcement>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    var currentAnnouncement by remember { mutableStateOf<Announcement?>(null) }
    
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var titleEn by remember { mutableStateOf("") }
    var descriptionEn by remember { mutableStateOf("") }
    var titleAr by remember { mutableStateOf("") }
    var descriptionAr by remember { mutableStateOf("") }
    var titleZh by remember { mutableStateOf("") }
    var descriptionZh by remember { mutableStateOf("") }
    var titleDe by remember { mutableStateOf("") }
    var descriptionDe by remember { mutableStateOf("") }
    var titleRu by remember { mutableStateOf("") }
    var descriptionRu by remember { mutableStateOf("") }
    var colorHex by remember { mutableStateOf("#4361EE") }
    var isActive by remember { mutableStateOf(true) }
    
    val snackbarHostState = remember { SnackbarHostState() }

    val presetColors = listOf(
        "#4361EE", // Blue
        "#3A0CA3", // Deep Purple
        "#4CC9F0", // Cyan
        "#F72585", // Pink
        "#7209B7", // Purple
        "#4895EF", // Light Blue
        "#2DCC70", // Green
        "#E74C3C"  // Red
    )

    fun fetchAnnouncements() {
        scope.launch {
            isLoading = true
            val result = announcementService.getAllAnnouncements()
            announcements = result.getOrNull() ?: emptyList()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        fetchAnnouncements()
    }

    Scaffold(
        modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Duyuru YÃ¶netimi", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { 
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = Color.White) 
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A)
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    currentAnnouncement = null
                    title = ""; description = ""
                    titleEn = ""; descriptionEn = ""
                    titleAr = ""; descriptionAr = ""
                    titleZh = ""; descriptionZh = ""
                    titleDe = ""; descriptionDe = ""
                    titleRu = ""; descriptionRu = ""
                    colorHex = "#4361EE"
                    isActive = true
                    showDialog = true
                },
                containerColor = Color(0xFF4361EE),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Duyuru Ekle")
            }
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF4361EE))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding).background(Color(0xFFF5F7FA)),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        "TÃ¼m Duyurular", 
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                
                if (announcements.isEmpty()) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                            Text("HenÃ¼z duyuru eklenmemiÅŸ.", color = Color.Gray)
                        }
                    }
                }
                
                items(announcements) { announcement ->
                    AnnouncementListItem(
                        announcement = announcement,
                        onToggleActive = { newStatus ->
                            scope.launch {
                                val res = announcementService.toggleAnnouncementStatus(announcement.id, newStatus)
                                if (res.isSuccess) {
                                    snackbarHostState.showSnackbar(if (newStatus) "Duyuru aktifleÅŸtirildi" else "Duyuru gizlendi")
                                    fetchAnnouncements()
                                } else {
                                    snackbarHostState.showSnackbar("Durum gÃ¼ncellenirken hata oluÅŸtu")
                                }
                            }
                        },
                        onEdit = {
                            currentAnnouncement = announcement
                            title = announcement.title
                            description = announcement.description
                            titleEn = announcement.titleEn
                            descriptionEn = announcement.descriptionEn
                            titleAr = announcement.titleAr
                            descriptionAr = announcement.descriptionAr
                            titleZh = announcement.titleZh
                            descriptionZh = announcement.descriptionZh
                            titleDe = announcement.titleDe
                            descriptionDe = announcement.descriptionDe
                            titleRu = announcement.titleRu
                            descriptionRu = announcement.descriptionRu
                            colorHex = announcement.colorHex
                            isActive = announcement.isActive
                            showDialog = true
                        },
                        onDelete = {
                            scope.launch {
                                val res = announcementService.deleteAnnouncement(announcement.id)
                                if (res.isSuccess) {
                                    snackbarHostState.showSnackbar("Duyuru silindi")
                                    fetchAnnouncements()
                                } else {
                                    snackbarHostState.showSnackbar("Duyuru silinirken hata oluÅŸtu")
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (currentAnnouncement == null) "Yeni Duyuru Ekle" else "Duyuruyu DÃ¼zenle", fontWeight = FontWeight.Bold) },
            text = {
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp).verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("TÃ¼rkÃ§e (VarsayÄ±lan)", fontWeight = FontWeight.Bold, color = Color(0xFF4361EE), fontSize = 12.sp)
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("BaÅŸlÄ±k") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Ä°Ã§erik") }, modifier = Modifier.fillMaxWidth())

                    Text("English", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 12.sp)
                    OutlinedTextField(value = titleEn, onValueChange = { titleEn = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = descriptionEn, onValueChange = { descriptionEn = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())

                    Text("Arabic (Ø§Ù„Ø¹Ø±Ø¨ÙŠØ©)", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 12.sp)
                    OutlinedTextField(value = titleAr, onValueChange = { titleAr = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = descriptionAr, onValueChange = { descriptionAr = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())

                    Text("Chinese (ä¸­æ–‡)", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 12.sp)
                    OutlinedTextField(value = titleZh, onValueChange = { titleZh = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = descriptionZh, onValueChange = { descriptionZh = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Aktif", style = MaterialTheme.typography.labelMedium)
                        Switch(checked = isActive, onCheckedChange = { isActive = it })
                    }
                    
                    Text("Renk SeÃ§imi", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        presetColors.forEach { colorStr ->
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color(colorStringToInt(colorStr)), CircleShape)
                                    .clickable { colorHex = colorStr }
                                    .let { if (colorHex == colorStr) it.padding(2.dp).background(Color.White, CircleShape).padding(2.dp).background(Color(colorStringToInt(colorStr)), CircleShape) else it }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            val announcement = (currentAnnouncement ?: Announcement()).copy(
                                title = title, description = description,
                                titleEn = titleEn, descriptionEn = descriptionEn,
                                titleAr = titleAr, descriptionAr = descriptionAr,
                                titleZh = titleZh, descriptionZh = descriptionZh,
                                titleDe = titleDe, descriptionDe = descriptionDe,
                                titleRu = titleRu, descriptionRu = descriptionRu,
                                colorHex = colorHex, active = if (isActive) 1 else 0
                            )
                            val res = announcementService.saveAnnouncement(announcement)
                            if (res.isSuccess) {
                                snackbarHostState.showSnackbar("Kaydedildi")
                                fetchAnnouncements()
                                showDialog = false
                            }
                        }
                    },
                    enabled = title.isNotBlank(),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Kaydet") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Ä°ptal") } }
        )
    }
}

@Composable
fun AnnouncementListItem(
    announcement: Announcement,
    onToggleActive: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Visual Color Strip on the left
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(8.dp)
                    .background(if (announcement.isActive) Color(colorStringToInt(announcement.colorHex)) else Color.LightGray)
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = announcement.title, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 16.sp,
                        color = Color.DarkGray
                    )
                    if (!announcement.isActive) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(color = Color.LightGray.copy(alpha = 0.3f), shape = RoundedCornerShape(4.dp)) {
                            Text("Gizli", fontSize = 10.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), color = Color.Gray)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = announcement.description, 
                    fontSize = 13.sp, 
                    color = Color.Gray, 
                    lineHeight = 18.sp
                )
            }
            
            Row(modifier = Modifier.padding(end = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = announcement.isActive,
                    onCheckedChange = onToggleActive,
                    modifier = Modifier.padding(end = 8.dp)
                )
                IconButton(onClick = onEdit) { 
                    Icon(Icons.Default.Edit, "DÃ¼zenle", tint = Color(0xFF4361EE).copy(alpha = 0.7f)) 
                }
                IconButton(onClick = onDelete) { 
                    Icon(Icons.Default.Delete, "Sil", tint = Color(0xFFE74C3C).copy(alpha = 0.7f)) 
                }
            }
        }
    }
}

private fun colorStringToInt(hex: String): Int {
    return try {
        val cleanHex = hex.removePrefix("#")
        val longVal = cleanHex.toLong(16)
        if (cleanHex.length == 6) {
            (0xFF000000 or longVal).toInt()
        } else {
            longVal.toInt()
        }
    } catch (e: Exception) {
        0xFF4361EE.toInt() // Fallback blue
    }
}


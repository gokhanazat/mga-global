package com.mgacreative.globaltrade.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.mgacreative.globaltrade.core.domain.sector.Sector
import com.mgacreative.globaltrade.core.domain.sector.SectorService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSectorsScreen(
    paddingValues: PaddingValues = PaddingValues(),
    onBackClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val sectorService = remember { SectorService() }
    var sectors by remember { mutableStateOf<List<Sector>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var currentSector by remember { mutableStateOf<Sector?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var sectorName by remember { mutableStateOf("") }
    var groupNo by remember { mutableStateOf("") }
    
    val snackbarHostState = remember { SnackbarHostState() }

    fun fetchSectors() {
        scope.launch {
            isLoading = true
            val result = sectorService.getSectors()
            sectors = result.getOrNull()?.sortedWith(
                compareBy<Sector>(
                    { it.groupNo.filter { c -> c.isDigit() }.toIntOrNull() ?: Int.MAX_VALUE },
                    { it.groupNo },
                    { it.name }
                )
            ) ?: emptyList()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        fetchSectors()
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidth = maxWidth
        val isWeb = screenWidth > 800.dp
        
        // Web'de yan boşluklar Lacivert
        Box(modifier = Modifier.fillMaxSize().background(if (isWeb) Color(0xFF0F172A) else Color(0xFFF8F9FA)))

        Scaffold(
            modifier = Modifier
                .widthIn(max = if (isWeb) 800.dp else screenWidth)
                .align(Alignment.Center)
                .padding(bottom = paddingValues.calculateBottomPadding()),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                CenterAlignedTopAppBar(
                    title = { 
                        Text(
                            "Meslek Grupları (Sektörler)", 
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) { 
                            Icon(Icons.Default.ArrowBack, contentDescription = "Geri") 
                        }
                    },
                    actions = {
                        IconButton(onClick = { 
                            currentSector = null
                            sectorName = ""
                            groupNo = ""
                            showDialog = true 
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Ekle")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFF0F172A),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { 
                        currentSector = null
                        sectorName = ""
                        groupNo = ""
                        showDialog = true 
                    },
                    containerColor = Color(0xFF0F172A),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Ekle")
                }
            }
        ) { innerPadding ->
            if (isLoading) {
                Box(Modifier.fillMaxSize().padding(innerPadding).background(Color(0xFFF8F9FA)), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF0F172A))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(innerPadding).background(Color(0xFFF8F9FA)),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            "Bu listedeki sektörler, şirket profili oluştururken 'Sektör' seçimi olarak sunulacaktır.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    if (sectors.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                                Text("Henüz meslek grubu tanımlanmamış.", color = Color.LightGray)
                            }
                        }
                    }
                    items(sectors) { sector ->
                        SectorListItem(
                            sector = sector,
                            onEdit = {
                                currentSector = sector
                                sectorName = sector.name
                                groupNo = sector.groupNo
                                showDialog = true
                            },
                            onDelete = {
                                scope.launch {
                                    val res = sectorService.deleteSector(sector.id)
                                    if (res.isSuccess) {
                                        snackbarHostState.showSnackbar("Sektör silindi")
                                        fetchSectors()
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (currentSector == null) "Yeni Meslek Grubu Ekle" else "Meslek Grubunu Düzenle", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = groupNo,
                        onValueChange = { groupNo = it },
                        label = { Text("Grup No") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Örn: G1") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = sectorName,
                        onValueChange = { sectorName = it },
                        label = { Text("Sektör / Meslek Grubu Adı") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            val sector = (currentSector ?: Sector()).copy(
                                name = sectorName.trim(),
                                groupNo = groupNo.trim()
                            )
                            val res = sectorService.saveSector(sector)
                            if (res.isSuccess) {
                                snackbarHostState.showSnackbar(if (currentSector == null) "Sektör eklendi" else "Sektör güncellendi")
                                fetchSectors()
                                showDialog = false
                            }
                        }
                    },
                    enabled = sectorName.isNotBlank(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
                ) {
                    Text(if (currentSector == null) "Ekle" else "Güncelle")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("İptal", color = Color.Gray) }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
fun SectorListItem(
    sector: Sector,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Category, contentDescription = null, tint = Color(0xFF0F172A).copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                if (sector.groupNo.isNotBlank()) {
                    Text(
                        text = "Grup: ${sector.groupNo}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF3B82F6),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
                Text(sector.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF0F172A), lineHeight = 16.sp)
            }
            // Artık düzenleme kartın kendisine tıklanarak yapılıyor. Sadece silme ikonu bırakıldı.
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) { 
                Icon(Icons.Default.Delete, "Sil", tint = Color(0xFFE74C3C).copy(alpha = 0.8f), modifier = Modifier.size(20.dp)) 
            }
        }
    }
}

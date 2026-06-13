package com.mgacreative.mgaglobal.ui.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mgacreative.mgaglobal.core.audit.ActionType
import com.mgacreative.mgaglobal.core.audit.AuditEvent
import com.mgacreative.mgaglobal.core.audit.AuditLogger
import com.mgacreative.mgaglobal.core.error.AppResult
import com.mgacreative.mgaglobal.core.util.CommonFormatters

import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuditLogScreen(
    paddingValues: PaddingValues = PaddingValues(),
    onBackClick: () -> Unit
) {
    var logs by remember { mutableStateOf<List<AuditEvent>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    // Filter States
    var selectedAction by remember { mutableStateOf<ActionType?>(null) }
    var selectedRole by remember { mutableStateOf<String?>(null) }
    var showFilterSheet by remember { mutableStateOf(false) }

    // Detail Dialog State
    var selectedLog by remember { mutableStateOf<AuditEvent?>(null) }

    var isLoadingMore by remember { mutableStateOf(false) }
    var hasMore by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()

    suspend fun fetchLogs() {
        isLoading = true
        val res = AuditLogger.getLogs()
        if (res.isSuccess) {
            logs = res.getOrNull() ?: emptyList()
        }
        isLoading = false
    }

    LaunchedEffect(Unit) {
        fetchLogs()
    }

    LaunchedEffect(selectedAction, selectedRole) {
        // Since we are filtering locally for now to avoid cost/index complexity,
        // but if the list is long, pagination might hide filtered items.
        // Optimally, filtering should happen at Firestore level.
        // For "Cost-aware", we fetch initial page and if filter is active, 
        // local filtering is okay for small datasets. 
    }

    val filteredLogs = remember(logs, selectedAction, selectedRole) {
        logs.filter { log ->
            val matchAction = selectedAction == null || log.actionType == selectedAction
            val matchRole = selectedRole == null || log.userRole == selectedRole
            matchAction && matchRole
        }
    }

    Scaffold(
        modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()),
        topBar = {
            TopAppBar(
                title = { Text("Denetim Kayıtları (Audit Logs)", fontWeight = FontWeight.Bold, color = Color(0xFF1B263B)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, contentDescription = "Geri") }
                },
                actions = {
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filtrele", tint = Color(0xFF1B263B))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(Modifier.fillMaxSize().padding(innerPadding).background(Color(0xFFF8F9FA))) {
                
                // Active Filters Row
                if (selectedAction != null || selectedRole != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        selectedAction?.let { action ->
                            InputChip(
                                selected = true,
                                onClick = { selectedAction = null },
                                label = { Text(action.toString()) },
                                trailingIcon = { Icon(Icons.Default.Close, null, Modifier.size(16.dp)) }
                            )
                        }
                        selectedRole?.let { role ->
                            InputChip(
                                selected = true,
                                onClick = { selectedRole = null },
                                label = { Text(role) },
                                trailingIcon = { Icon(Icons.Default.Close, null, Modifier.size(16.dp)) }
                            )
                        }
                    }
                }

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredLogs) { log ->
                        AuditLogCard(log = log, onClick = { selectedLog = log })
                    }
                    
                    if (filteredLogs.isEmpty() && !isLoading) {
                        item {
                            Text("Kayıt bulunamadı.", modifier = Modifier.padding(32.dp), color = Color.Gray)
                        }
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        ModalBottomSheet(onDismissRequest = { showFilterSheet = false }) {
            Column(Modifier.padding(16.dp).padding(bottom = 32.dp)) {
                Text("Filtreleme", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                
                Text("İşlem Tipi (Action Type)", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                val actions = ActionType.entries.toList()
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 200.dp)
                ) {
                    items(actions) { action ->
                        FilterChip(
                            selected = selectedAction == action,
                            onClick = { selectedAction = if (selectedAction == action) null else action },
                            label = { Text(action.toString(), fontSize = 12.sp) }
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text("Kullanıcı Rolü (User Role)", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("ADMIN", "USER", "BUSINESS").forEach { role ->
                        FilterChip(
                            selected = selectedRole == role,
                            onClick = { selectedRole = if (selectedRole == role) null else role },
                            label = { Text(role) }
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { showFilterSheet = false },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Uygula")
                }
            }
        }
    }

    selectedLog?.let { log ->
        AlertDialog(
            onDismissRequest = { selectedLog = null },
            confirmButton = {
                TextButton(onClick = { selectedLog = null }) { Text("Kapat") }
            },
            title = { Text("Log Detayları") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailRow("ID", log.id)
                    DetailRow("Kullanıcı", log.userId)
                    DetailRow("Tarih", CommonFormatters.formatTimestamp(log.timestamp))
                    DetailRow("Açıklama", log.description)
                    DetailRow("Hedef Modül", log.targetModule)
                    DetailRow("Hedef ID", log.targetId ?: "-")
                    DetailRow("Cihaz", log.deviceInfo ?: "-")
                    DetailRow("Versiyon", log.appVersion)
                }
            },
            containerColor = Color.White
        )
    }
}

@Composable
fun AuditLogCard(log: AuditEvent, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Action Type Badge
                val badgeColor = when (log.actionType) {
                    ActionType.DELETE, ActionType.REJECTION -> Color(0xFFD32F2F)
                    ActionType.CREATE, ActionType.APPROVAL -> Color(0xFF388E3C)
                    ActionType.UPDATE -> Color(0xFF1976D2)
                    ActionType.LOGIN, ActionType.LOGOUT -> Color(0xFFF57C00)
                    else -> Color(0xFF607D8B)
                }
                Surface(
                    color = badgeColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        log.actionType.toString(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = badgeColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
                
                Text(
                    text = CommonFormatters.formatTimestamp(log.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "${log.userId} (${log.userRole})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Category, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(Modifier.width(4.dp))
                Text(
                    text = log.targetModule,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(label, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
        Text(value, fontSize = 14.sp, color = Color.DarkGray)
        Spacer(Modifier.height(4.dp))
        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
    }
}




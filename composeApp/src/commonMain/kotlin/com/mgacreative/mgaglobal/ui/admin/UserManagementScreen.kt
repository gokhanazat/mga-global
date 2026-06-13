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
import com.mgacreative.mgaglobal.core.auth.*
import com.mgacreative.mgaglobal.core.network.ApiConfig
import com.mgacreative.mgaglobal.core.domain.audit.AuditDomainService
import com.mgacreative.mgaglobal.core.error.AppResult
import com.mgacreative.mgaglobal.core.error.safeCall
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class RoleUpdateRequest(val id: String, val role: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    paddingValues: PaddingValues = PaddingValues(),
    onBackClick: () -> Unit,
    onRegistryClick: () -> Unit = {}
) {
    var users by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var showRolePicker by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    
    val client = remember {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; coerceInputValues = true })
            }
        }
    }

    fun fetchUsers() {
        scope.launch {
            isLoading = true
            val res = safeCall {
                val response = client.get("${ApiConfig.BASE_URL}/users")
                // Core'daki User modelini kullanmak için doğrudan listeyi çekiyoruz
                response.body<List<User>>()
            }
            users = res.getOrNull() ?: emptyList()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        fetchUsers()
    }

    Scaffold(
        modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()),
        topBar = {
            TopAppBar(
                title = { Text("Kullanıcı & Yetki Yönetimi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, contentDescription = "Geri") }
                },
                actions = {
                    IconButton(onClick = onRegistryClick) {
                        Icon(Icons.Default.AppRegistration, contentDescription = "Sicil Yönetimi", tint = MaterialTheme.colorScheme.primary)
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
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding).background(Color(0xFFF8F9FA)),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(users) { user ->
                    UserListItem(user = user, onClick = { 
                        selectedUser = user
                        showRolePicker = true 
                    })
                }
            }
        }
    }

    if (showRolePicker && selectedUser != null) {
        RoleAssignmentDialog(
            user = selectedUser!!,
            onDismiss = { showRolePicker = false },
            onRoleAssigned = { newRole ->
                scope.launch {
                    val oldRole = selectedUser!!.role
                    val res = safeCall {
                        client.post("${ApiConfig.BASE_URL}/users/update-role") {
                            contentType(ContentType.Application.Json)
                            setBody(RoleUpdateRequest(selectedUser!!.id, newRole.name))
                        }
                    }
                    if (res is AppResult.Success) {
                        AuditDomainService.logRoleAssignment(
                            adminId = SessionManager.getUserId(),
                            adminRole = SessionManager.userRole.value?.name ?: "ADMIN",
                            targetUserId = selectedUser!!.id,
                            oldRole = oldRole,
                            newRole = newRole
                        )
                        fetchUsers()
                    }
                }
                showRolePicker = false
            }
        )
    }
}

@Composable
fun UserListItem(user: User, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(user.id, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(user.status, fontSize = 11.sp, color = if (user.isApproved) Color(0xFF388E3C) else Color.Red, fontWeight = FontWeight.Medium)
                
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    val roleColor = when(user.role) {
                        Role.SUPER_ADMIN -> Color(0xFFD32F2F)
                        Role.ADMIN -> Color(0xFF1976D2)
                        Role.MODERATOR -> Color(0xFFF57C00)
                        else -> Color(0xFF388E3C)
                    }
                    Surface(color = roleColor.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp)) {
                        Text(
                            user.role.name, 
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp, 
                            color = roleColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleAssignmentDialog(user: User, onDismiss: () -> Unit, onRoleAssigned: (Role) -> Unit) {
    var selectedRole by remember { mutableStateOf(user.role) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rol Atama: ${user.id}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Yeni bir rol seçin:", style = MaterialTheme.typography.bodyMedium)
                
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    mainAxisSpacing = 8.dp,
                    crossAxisSpacing = 8.dp
                ) {
                    Role.entries.forEach { role ->
                        FilterChip(
                            selected = selectedRole == role,
                            onClick = { selectedRole = role },
                            label = { Text(role.name, fontSize = 11.sp) }
                        )
                    }
                }

                HorizontalDivider() // Compose Multiplatform standard Divider

                Text("Rol Yetkileri (Önizleme):", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                val permissions = RolePermissionMatrix.getPermissionsForRole(selectedRole)
                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                    items(permissions.toList()) { perm ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                            Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF388E3C), modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(perm.name, fontSize = 12.sp, color = Color.DarkGray)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onRoleAssigned(selectedRole) }) {
                Text("Onayla ve Kaydet")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        },
        containerColor = Color.White
    )
}

@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    mainAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    crossAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.layout.Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val placeholders = measurables.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }
        var rowWidth = 0
        var rowHeight = 0
        var totalHeight = 0
        var maxWidth = 0
        
        val rows = mutableListOf<List<androidx.compose.ui.layout.Placeable>>()
        var currentRow = mutableListOf<androidx.compose.ui.layout.Placeable>()
        
        placeholders.forEach { placeable ->
            if (rowWidth + placeable.width > constraints.maxWidth && currentRow.isNotEmpty()) {
                rows.add(currentRow)
                totalHeight += rowHeight + crossAxisSpacing.roundToPx()
                maxWidth = maxOf(maxWidth, rowWidth)
                currentRow = mutableListOf()
                rowWidth = 0
                rowHeight = 0
            }
            currentRow.add(placeable)
            rowWidth += placeable.width + mainAxisSpacing.roundToPx()
            rowHeight = maxOf(rowHeight, placeable.height)
        }
        rows.add(currentRow)
        totalHeight += rowHeight
        maxWidth = maxOf(maxWidth, rowWidth)
        
        layout(maxWidth, totalHeight) {
            var y = 0
            rows.forEach { row ->
                var x = 0
                var currentMaxHeight = 0
                row.forEach { placeable ->
                    placeable.place(x, y)
                    x += placeable.width + mainAxisSpacing.roundToPx()
                    currentMaxHeight = maxOf(currentMaxHeight, placeable.height)
                }
                y += currentMaxHeight + crossAxisSpacing.roundToPx()
            }
        }
    }
}


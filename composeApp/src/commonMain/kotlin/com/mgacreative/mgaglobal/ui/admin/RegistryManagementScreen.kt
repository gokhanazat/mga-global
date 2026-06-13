package com.mgacreative.mgaglobal.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mgacreative.mgaglobal.core.domain.auth.RegistryEntry
import com.mgacreative.mgaglobal.core.domain.auth.RegistryService
import com.mgacreative.mgaglobal.core.domain.auth.AuthService
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PickerMode
import mgaglobal.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistryManagementScreen(
    paddingValues: PaddingValues = PaddingValues(),
    onBackClick: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showPasteDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf<RegistryEntry?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }
    var entries by remember { mutableStateOf<List<RegistryEntry>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val registryService = remember { RegistryService() }
    val authService = remember { AuthService() }

    fun refreshData() {
        isLoading = true
        scope.launch {
            val res = registryService.getAllRegistryEntries()
            entries = res.getOrNull() ?: emptyList()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        refreshData()
    }
    
    // Common processing logic for both file and paste
    suspend fun processRawData(content: String): Int {
        val lines = content.lines()
        val dataLines = if (lines.firstOrNull()?.contains("sicil", ignoreCase = true) == true) lines.drop(1) else lines
        
        var count = 0
        for (line in dataLines) {
            if (line.isBlank()) continue
            // Handle Tab (Excel copy), Semicolon (Turkish CSV), and Comma
            val parts = when {
                line.contains("\t") -> line.split("\t")
                line.contains(";") -> line.split(";")
                line.contains(",") -> line.split(",")
                else -> listOf(line)
            }
            
            if (parts.size >= 2) {
                val number = parts[0].trim().filter { it.isDigit() }
                val name = parts[1].trim()
                if (number.isNotEmpty()) {
                    val res = registryService.addRegistryNumber(number, name)
                    if (res is com.mgacreative.mgaglobal.core.error.AppResult.Success) {
                        count++
                    }
                }
            }
        }
        return count
    }
    
    // Move processing to a separate function to keep the Composable clean and stable
    suspend fun processCsvFile(file: io.github.vinceglb.filekit.core.PlatformFile) {
        isUploading = true
        try {
            val result = withContext(Dispatchers.Default) {
                val bytes = try { file.readBytes() } catch (e: Exception) { null }
                if (bytes == null) return@withContext 0
                processRawData(bytes.decodeToString())
            }
            refreshData()
            snackbarHostState.showSnackbar("$result adet sicil başarıyla yüklendi.")
        } catch (e: Exception) {
            snackbarHostState.showSnackbar("Hata: ${e.message}")
        } finally {
            isUploading = false
        }
    }

    val launcher = rememberFilePickerLauncher(
        type = PickerType.File(extensions = listOf("csv")),
        mode = PickerMode.Single,
        title = "Dosya Seçin"
    ) { file ->
        if (file != null) {
            scope.launch { processCsvFile(file) }
        }
    }

    Scaffold(
        modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Sicil Numarası Yönetimi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Alternative location for upload
                    IconButton(onClick = { 
                        try { launcher.launch() } catch (e: Exception) {
                            scope.launch { snackbarHostState.showSnackbar("Hata: ${e.message}") }
                            showPasteDialog = true // Fallback to paste if launch fails
                        }
                    }, enabled = !isUploading) {
                        if (isUploading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Icon(Icons.Default.Upload, contentDescription = "Yükle", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (isUploading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Main stable button
                ElevatedButton(
                    onClick = { showPasteDialog = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Toplu Yapıştır ve Yükle", fontWeight = FontWeight.Bold)
                }
                
                // File picker as alternative
                OutlinedButton(
                    onClick = { try { launcher.launch() } catch(e: Exception) { scope.launch { snackbarHostState.showSnackbar("Hata: ${e.message}") }; showPasteDialog = true } },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Upload, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Dosya Seç")
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                placeholder = { Text("Sicil no veya şirket adı ara...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(entries.filter { it.number.contains(searchQuery) || it.ownerName.contains(searchQuery, ignoreCase = true) }) { entry ->
                    RegistryListItem(
                        entry = entry,
                        onResetPassword = { showResetDialog = entry },
                        onToggleStatus = {
                            scope.launch {
                                registryService.setRegistryStatus(entry.number, !entry.active)
                                refreshData()
                            }
                        },
                        onDelete = {
                            scope.launch {
                                val res = registryService.deleteRegistryEntry(entry.id)
                                if (res.isSuccess) {
                                    snackbarHostState.showSnackbar("Sicil silindi: ${entry.number}")
                                    refreshData()
                                } else {
                                    snackbarHostState.showSnackbar("Silme hatası!")
                                }
                            }
                        }
                    )
                }
            }
        }

        if (showResetDialog != null) {
            val targetEntry = showResetDialog!!
            ResetPasswordDialog(
                entry = targetEntry,
                onDismiss = { showResetDialog = null },
                onReset = { password ->
                    scope.launch {
                        val res = authService.adminResetPassword(targetEntry.number, password)
                        if (res.isSuccess) {
                            snackbarHostState.showSnackbar("Geçici şifre başarıyla atandı.")
                            showResetDialog = null
                        } else {
                            snackbarHostState.showSnackbar("Şifre atama hatası!")
                        }
                    }
                }
            )
        }

        if (showAddDialog) {
            AddRegistryDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { number, name ->
                    scope.launch {
                        val res = registryService.addRegistryNumber(number, name)
                        if (res is com.mgacreative.mgaglobal.core.error.AppResult.Success) {
                            refreshData()
                            showAddDialog = false
                            snackbarHostState.showSnackbar("Sicil eklendi: $number")
                        } else if (res is com.mgacreative.mgaglobal.core.error.AppResult.Error) {
                            snackbarHostState.showSnackbar("Hata: ${res.error.message ?: "Sicil eklenemedi"}")
                        }
                    }
                }
            )
        }

        if (showPasteDialog) {
            PasteRegistryDialog(
                onDismiss = { showPasteDialog = false },
                onUpload = { content ->
                    scope.launch {
                        isUploading = true
                        val count = processRawData(content)
                        refreshData()
                        showPasteDialog = false
                        isUploading = false
                        snackbarHostState.showSnackbar("$count adet sicil eklendi.")
                    }
                }
            )
        }
    }
}

@Composable
fun PasteRegistryDialog(onDismiss: () -> Unit, onUpload: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Toplu Veri Yapıştır") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Excel'den kopyaladığınız verileri buraya yapıştırın. " +
                    "Format: SicilNo [TAB/Virgül] ŞirketAdı",
                    style = MaterialTheme.typography.bodySmall
                )
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    placeholder = { Text("Verileri buraya yapıştırın...") },
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (text.isNotBlank()) onUpload(text) },
                enabled = text.isNotBlank()
            ) {
                Text("Yükle")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Vazgeç")
            }
        }
    )
}

@Composable
fun RegistryListItem(entry: RegistryEntry, onResetPassword: () -> Unit, onToggleStatus: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = entry.number, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = entry.ownerName, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            
            IconButton(onClick = onResetPassword) {
                Icon(Icons.Default.Lock, contentDescription = "Şifre Sıfırla", tint = Color.Gray)
            }
            
            Switch(
                checked = entry.active,
                onCheckedChange = { onToggleStatus() }
            )

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Sil", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
fun AddRegistryDialog(onDismiss: () -> Unit, onAdd: (String, String) -> Unit) {
    var number by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yeni Sicil No Ekle") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it },
                    label = { Text("Sicil Numarası") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Şirket Adı") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { if (number.isNotBlank() && name.isNotBlank()) onAdd(number, name) }) {
                Text("Ekle")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Vazgeç")
            }
        }
    )
}

@Composable
fun ResetPasswordDialog(entry: RegistryEntry, onDismiss: () -> Unit, onReset: (String) -> Unit) {
    var password by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Geçici Şifre Belirle") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "${entry.number} - ${entry.ownerName} için yeni bir geçici şifre belirleyin. Bu şifreyi işletme ile paylaşmanız gerekir.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Yeni Şifre") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(onClick = { if (password.isNotBlank()) onReset(password.trim()) }) {
                Text("Şifreyi Kaydet")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}



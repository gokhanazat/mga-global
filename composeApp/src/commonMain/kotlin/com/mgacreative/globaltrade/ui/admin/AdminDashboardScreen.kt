package com.mgacreative.globaltrade.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.*
import com.mgacreative.globaltrade.core.domain.auth.SettingsService
import com.mgacreative.globaltrade.core.domain.auth.RegistryService
import com.mgacreative.globaltrade.core.domain.b2b.CompanyService
import com.mgacreative.globaltrade.core.domain.sector.SectorService
import com.mgacreative.globaltrade.core.domain.education.EducationService
import com.mgacreative.globaltrade.core.util.CSVExportHelper
import com.mgacreative.globaltrade.getNowMillis
import com.mgacreative.globaltrade.saveFile
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    paddingValues: PaddingValues = PaddingValues(),
    onNavigateToUsers: () -> Unit,
    onNavigateToRegistry: () -> Unit,
    onNavigateToAuditLog: () -> Unit,
    onNavigateToEducations: () -> Unit,
    onNavigateToSectors: () -> Unit,
    onNavigateToAnnouncements: () -> Unit,
    onNavigateToConsultancy: () -> Unit,
    onNavigateToHelpCenter: () -> Unit,
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val settingsService = remember { SettingsService() }
    val snackbarHostState = remember { SnackbarHostState() }
    
    var showSettingsDialog by remember { mutableStateOf(false) }
    var contactEmail by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    
    val sectorService = remember { SectorService() }
    val educationService = remember { EducationService() }
    val companyService = remember { CompanyService() }
    val registryService = remember { RegistryService() }
    
    var totalCompanies by remember { mutableStateOf(0) }
    var activeRegistries by remember { mutableStateOf(0) }
    var totalSectors by remember { mutableStateOf(0) }
    var totalEducations by remember { mutableStateOf(0) }
    var isLoadingStats by remember { mutableStateOf(true) }
    var isExporting by remember { mutableStateOf(false) }

    val companiesList = remember { mutableStateListOf<com.mgacreative.globaltrade.core.domain.b2b.B2BCompany>() }
    val sectorsList = remember { mutableStateListOf<com.mgacreative.globaltrade.core.domain.sector.Sector>() }

    LaunchedEffect(Unit) {
        try {
            coroutineScope {
                val companiesDef = async { companyService.getAllCompanies() }
                val registryDef = async { registryService.getAllRegistryEntries() }
                val sectorsDef = async { sectorService.getSectors() }
                val educationDef = async { educationService.getAllEducations() }
                
                val companiesResult = companiesDef.await().getOrNull() ?: emptyList()
                totalCompanies = companiesResult.size
                companiesList.clear()
                companiesList.addAll(companiesResult)
                
                val registryRes = registryDef.await()
                if (registryRes is com.mgacreative.globaltrade.core.error.AppResult.Success) {
                    activeRegistries = registryRes.data.count { it.active }
                }

                val sectorsRes = sectorsDef.await()
                if (sectorsRes is com.mgacreative.globaltrade.core.error.AppResult.Success) {
                    totalSectors = sectorsRes.data.size
                    sectorsList.clear()
                    sectorsList.addAll(sectorsRes.data)
                }

                val eduRes = educationDef.await()
                if (eduRes.isSuccess) {
                    totalEducations = eduRes.getOrNull()?.size ?: 0
                }
            }
        } catch (e: Exception) {
            println("Admin Dashboard Stats Error: ${e.message}")
        } finally {
            isLoadingStats = false
        }
    }

    Scaffold(
        modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Yönetim Paneli", fontWeight = FontWeight.Bold, color = Color.White) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Çıkış", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF5F7FA))
        ) {
            val isWeb = maxWidth > 800.dp
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (isWeb) 24.dp else 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Text(
                    text = "Hoş Geldiniz, Sistem Yöneticisi",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Platform yetkilerini ve özet istatistikleri buradan takip edebilirsiniz.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                )

                if (isWeb) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        // Left Column: Stats Cards and Export
                        Column(modifier = Modifier.weight(1f)) {
                            StatsCardsSection(totalCompanies, activeRegistries, totalSectors, totalEducations, isLoadingStats)
                            Spacer(modifier = Modifier.height(24.dp))
                            ExportButton(isExporting, totalCompanies, scope, companyService, companiesList, snackbarHostState)
                            Spacer(modifier = Modifier.height(24.dp))
                            AdminModulesGrid(onNavigateToRegistry, onNavigateToUsers, onNavigateToAuditLog, onNavigateToEducations, onNavigateToSectors, onNavigateToAnnouncements, onNavigateToConsultancy, onNavigateToHelpCenter)
                        }
                        
                        Spacer(modifier = Modifier.width(24.dp))
                        
                        // Right Column: Sektörel Analiz Table
                        Card(
                            modifier = Modifier.weight(1.2f).fillMaxHeight(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            SectorStatsTable(sectorsList, companiesList, isLoadingStats)
                        }
                    }
                } else {
                    // Mobile Layout: Sequential
                    StatsCardsSection(totalCompanies, activeRegistries, totalSectors, totalEducations, isLoadingStats)
                    Spacer(modifier = Modifier.height(24.dp))
                    AdminModulesGrid(onNavigateToRegistry, onNavigateToUsers, onNavigateToAuditLog, onNavigateToEducations, onNavigateToSectors, onNavigateToAnnouncements, onNavigateToConsultancy, onNavigateToHelpCenter)
                    Spacer(modifier = Modifier.height(24.dp))
                    ExportButton(isExporting, totalCompanies, scope, companyService, companiesList, snackbarHostState)
                    Spacer(modifier = Modifier.height(24.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        SectorStatsTable(sectorsList, companiesList, isLoadingStats)
                    }
                }
            }
        }
    }
}

@Composable
fun StatsCardsSection(totalCompanies: Int, activeRegistries: Int, totalSectors: Int, totalEducations: Int, isLoading: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BigStatCard("Kayıtlı İşletme", totalCompanies.toString(), Icons.Default.Business, Color(0xFF4361EE), Modifier.weight(1f), isLoading)
        BigStatCard("Toplam Sektör", totalSectors.toString(), Icons.Default.Category, Color(0xFF7209B7), Modifier.weight(1f), isLoading)
    }
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BigStatCard("Aktif Sicil", activeRegistries.toString(), Icons.Default.AppRegistration, Color(0xFF4CC9F0), Modifier.weight(1f), isLoading)
        BigStatCard("Toplam Eğitim", totalEducations.toString(), Icons.Default.School, Color(0xFFF72585), Modifier.weight(1f), isLoading)
    }
}

@Composable
fun BigStatCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier, isLoading: Boolean) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                } else {
                    Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }
                Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}

@Composable
fun SectorStatsTable(sectors: List<com.mgacreative.globaltrade.core.domain.sector.Sector>, companies: List<com.mgacreative.globaltrade.core.domain.b2b.B2BCompany>, isLoading: Boolean) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Sektörel Analiz", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Text("Sektörlerdeki kayıtlı firma ve ürün dağılımı.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
            Text("Sektör Adı", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            Text("Firma", modifier = Modifier.weight(0.5f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            Text("Ürün", modifier = Modifier.weight(0.5f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        }
        HorizontalDivider()
        
        if (isLoading) {
            Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(modifier = Modifier.heightIn(max = 400.dp).verticalScroll(rememberScrollState())) {
                sectors.forEach { sector ->
                    val companyCount = companies.count { it.sector == sector.name }
                    // Ürün sayımı için her firmanın ürün listesinin boyutu gerekiyor, 
                    // Performans için şimdilik bunu bir placeholder veya 0 olarak bırakabiliriz (tüm ürünleri fetch etmedik)
                    val productCount = 0 
                    
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(sector.name, modifier = Modifier.weight(1.5f), fontSize = 12.sp)
                        Text("$companyCount", modifier = Modifier.weight(0.5f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("$productCount", modifier = Modifier.weight(0.5f), fontSize = 12.sp, color = Color.Gray)
                    }
                    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@Composable
fun AdminModulesGrid(
    onNavigateToRegistry: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToAuditLog: () -> Unit,
    onNavigateToEducations: () -> Unit,
    onNavigateToSectors: () -> Unit,
    onNavigateToAnnouncements: () -> Unit,
    onNavigateToConsultancy: () -> Unit,
    onNavigateToHelpCenter: () -> Unit
) {
    val adminModules = listOf(
        AdminModule("Sicil No", "Yeni üye sicil no ekle.", Icons.Default.AppRegistration, Color(0xFF4361EE), onNavigateToRegistry),
        AdminModule("Yetki", "Rolleri düzenle.", Icons.Default.People, Color(0xFF3A0CA3), onNavigateToUsers),
        AdminModule("Log", "Logları incele.", Icons.Default.History, Color(0xFF4CC9F0), onNavigateToAuditLog),
        AdminModule("Eğitim", "Modülleri tanımla.", Icons.Default.School, Color(0xFFF72585), onNavigateToEducations),
        AdminModule("Sektör", "Sektörleri yönet.", Icons.Default.Category, Color(0xFF7209B7), onNavigateToSectors),
        AdminModule("Duyuru", "Duyuruları yönet.", Icons.Default.Campaign, Color(0xFFF72585), onNavigateToAnnouncements),
        AdminModule("Uzman", "Uzmanları yönet.", Icons.Default.SupportAgent, Color(0xFF4361EE), onNavigateToConsultancy),
        AdminModule("Yardım", "SSS içerikleri.", Icons.Default.LiveHelp, Color(0xFF4CC9F0), onNavigateToHelpCenter)
    )

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 120.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp)
    ) {
        items(adminModules) { module ->
            AdminModuleCard(module)
        }
    }
}

@Composable
fun ExportButton(
    isExporting: Boolean, 
    totalCompanies: Int, 
    scope: CoroutineScope, 
    companyService: CompanyService,
    companiesList: List<com.mgacreative.globaltrade.core.domain.b2b.B2BCompany>,
    snackbarHostState: SnackbarHostState
) {
    Button(
        onClick = {
            scope.launch {
                // Export logic (same as before)
                try {
                    val csvContent = CSVExportHelper.companiesToCsv(companiesList)
                    saveFile(
                        fileName = "IsletmeProfilleri_${com.mgacreative.globaltrade.getNowMillis()}.csv",
                        content = csvContent,
                        mimeType = "text/csv; charset=utf-8"
                    )
                    snackbarHostState.showSnackbar("Dışa aktarma tamamlandı.")
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar("Hata: ${e.message}")
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        enabled = !isExporting && totalCompanies > 0,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("İşletme Profillerini Export Et (CSV)")
        }
    }
}

@Composable
fun AdminModuleCard(module: AdminModule) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp) // Web'de daha kompakt olması için yüksekliği düşürdük
            .clickable { module.onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = module.color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(module.icon, contentDescription = null, tint = module.color, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = module.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.DarkGray)
            Text(text = module.description, fontSize = 9.sp, color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center, maxLines = 1)
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
        Text(text = label, fontSize = 11.sp, color = Color.Gray)
    }
}

data class AdminModule(val title: String, val description: String, val icon: ImageVector, val color: Color, val onClick: () -> Unit)

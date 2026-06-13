package com.mgacreative.mgaglobal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mgacreative.mgaglobal.core.domain.b2b.B2BCompany
import com.mgacreative.mgaglobal.core.domain.b2b.CompanyService
import com.mgacreative.mgaglobal.core.domain.sector.Sector
import com.mgacreative.mgaglobal.core.domain.sector.SectorService
import com.mgacreative.mgaglobal.core.util.ImageResizer
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerType
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.launch
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import com.mgacreative.mgaglobal.core.network.ApiConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanySettingsScreen(paddingValues: PaddingValues, onBackClick: () -> Unit) {
    val scope = rememberCoroutineScope()
    val companyService = remember { CompanyService() }
    val sectorService = remember { SectorService() }
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    var sectors by remember { mutableStateOf<List<Sector>>(emptyList()) }
    var expanded by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var isExistingRecord by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var sector by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var yearsInMarket by remember { mutableStateOf("") }
    var exportVolume by remember { mutableStateOf("") }
    var logoUrl by remember { mutableStateOf<String?>(null) }
    var phone by remember { mutableStateOf("") }
    var gsm by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var authorizedPerson by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    @OptIn(ExperimentalEncodingApi::class)
    val logoPicker = rememberFilePickerLauncher(
        type = PickerType.Image,
        title = "Logo Sec"
    ) { file ->
        if (file != null) {
            scope.launch {
                try {
                    val bytes = file.readBytes()
                    if (bytes.size > 1048576) {
                        snackbarHostState.showSnackbar("Dosya 1MB altinda olmali.")
                        return@launch
                    }
                    val compressedBytes = ImageResizer.compressImage(bytes, 800, 800, 80)
                    val base64String = Base64.Default.encode(compressedBytes)
                    logoUrl = "data:image/webp;base64,$base64String"
                    snackbarHostState.showSnackbar("Logo hazir.")
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar("Resim isleme hatasi.")
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        val result = companyService.getOwnCompany()
        result.onSuccess { company ->
            if (company != null) {
                isExistingRecord = true
                name = company.name
                sector = company.sector
                country = company.country
                yearsInMarket = company.yearsInMarket.toString()
                exportVolume = company.exportVolume.toString()
                logoUrl = company.logoUrl
                phone = company.phone
                gsm = company.gsm
                email = company.email
                authorizedPerson = company.authorizedPerson
                description = company.description
            }
        }

        val sectorResult = sectorService.getSectors()
        sectors = sectorResult.getOrNull()?.sortedBy { it.groupNo } ?: emptyList()
        isLoading = false
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize().padding(paddingValues)
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF0F2F5))
                    .verticalScroll(scrollState)
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                            val uriHandler = LocalUriHandler.current
                            val logoData = remember(logoUrl) {
                                val raw = logoUrl ?: return@remember null
                                if (raw.startsWith("data:image/")) {
                                    val base64String = raw.substringAfter("base64,").trim()
                                    @OptIn(ExperimentalEncodingApi::class)
                                    try { Base64.Default.decode(base64String) } catch (e: Exception) { null }
                                } else if (raw.startsWith("http")) {
                                    raw.filter { it.code in 33..126 && it != '"' && it != '\'' }
                                } else {
                                    val cleaned = raw.filter { it.code in 33..126 && it != '"' && it != '\'' }
                                    if (cleaned.isNotBlank() && cleaned != "null") ApiConfig.getImageUrl(cleaned) else null
                                }
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFF1F5F9))
                                        .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), CircleShape)
                                        .clickable { logoPicker.launch() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (logoData != null) {
                                        coil3.compose.AsyncImage(
                                            model = logoData,
                                            contentDescription = "Logo",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(Icons.Default.AddAPhoto, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                                            Text("Logo Ekle", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                                
                                if (logoData is String && logoData.startsWith("http")) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Tarayıcıda Kontrol Et",
                                        fontSize = 8.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.clickable { try { uriHandler.openUri(logoData) } catch(e: Exception) {} }
                                    )
                                }
                            }
                        Text(text = if (name.isEmpty()) "Sirket Profili" else name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }

                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Genel Bilgiler", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Sirket Adi") }, modifier = Modifier.fillMaxWidth())

                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = sector, onValueChange = {}, label = { Text("Sektor") },
                                modifier = Modifier.fillMaxWidth(), readOnly = true,
                                trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, Modifier.clickable { expanded = true }) }
                            )
                            Box(modifier = Modifier.matchParentSize().clickable { expanded = true })
                            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                sectors.forEach { item ->
                                    DropdownMenuItem(text = { Text(item.name) }, onClick = { sector = item.name; expanded = false })
                                }
                            }
                        }

                        OutlinedTextField(value = country, onValueChange = { country = it }, label = { Text("Ulke") }, modifier = Modifier.fillMaxWidth())

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = yearsInMarket, onValueChange = { yearsInMarket = it }, label = { Text("Pazar Yili") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = exportVolume, onValueChange = { exportVolume = it }, label = { Text("Ihracat ($)") }, modifier = Modifier.weight(1f))
                        }

                        OutlinedTextField(value = authorizedPerson, onValueChange = { authorizedPerson = it }, label = { Text("Yetkili Kisi (Ad Soyad)") }, modifier = Modifier.fillMaxWidth())

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Sirket Aciklamasi") },
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            maxLines = 5,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Iletisim Bilgileri", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Sabit Telefon") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = gsm, onValueChange = { gsm = it }, label = { Text("GSM / Cep Telefonu") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("E-posta Adresi") }, modifier = Modifier.fillMaxWidth())
                    }
                }

                Button(
                    onClick = {
                        isSaving = true
                        scope.launch {
                            val company = B2BCompany(
                                name = name, sector = sector, country = country,
                                yearsInMarket = yearsInMarket.toIntOrNull() ?: 0,
                                exportVolume = exportVolume.toDoubleOrNull() ?: 0.0,
                                logoUrl = logoUrl, phone = phone, gsm = gsm, email = email,
                                authorizedPerson = authorizedPerson,
                                description = description
                            )
                            val result = companyService.saveCompany(company)
                            if (result.isSuccess) {
                                snackbarHostState.showSnackbar("Basariyla kaydedildi.")
                                isExistingRecord = true
                                
                                val updatedResult = companyService.getOwnCompany()
                                updatedResult.onSuccess { updated ->
                                    if (updated != null) {
                                        logoUrl = updated.logoUrl
                                    }
                                }
                            } else {
                                snackbarHostState.showSnackbar("Hata: ${result.exceptionOrNull()?.message}")
                            }
                            isSaving = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = !isSaving,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Icon(Icons.Default.Save, null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (isExistingRecord) "Bilgileri Guncelle" else "Sirketi Kaydet", fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}


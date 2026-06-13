package com.mgacreative.mgaglobal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import mgaglobal.composeapp.generated.resources.*
import com.mgacreative.mgaglobal.core.domain.auth.SettingsService
import kotlinx.coroutines.launch
import com.mgacreative.mgaglobal.core.domain.b2b.CompanyService
import com.mgacreative.mgaglobal.core.domain.b2b.B2BCompany
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerType
import com.mgacreative.mgaglobal.core.util.ImageResizer
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import com.mgacreative.mgaglobal.core.network.ApiConfig
import androidx.compose.ui.layout.ContentScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    paddingValues: PaddingValues = PaddingValues(),
    onCompanySettingsClick: () -> Unit,
    onSecurityClick: () -> Unit,
    onHelpCenterClick: () -> Unit,
    onNotificationSettingsClick: () -> Unit,
    onProductsClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val settingsService = remember { SettingsService() }
    val companyService = remember { CompanyService() }
    var contactEmail by remember { mutableStateOf("Yükleniyor...") }
    var companyData by remember { mutableStateOf<B2BCompany?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    val logoPicker = rememberFilePickerLauncher(
        type = PickerType.Image,
        title = "Logo Seç"
    ) { file ->
        if (file != null) {
            scope.launch {
                try {
                    val bytes = file.readBytes()
                    if (bytes.size > 2097152) { // 2MB limit
                        snackbarHostState.showSnackbar("Logo 2MB'dan büyük olamaz.")
                        return@launch
                    }
                    
                    val compressedBytes = ImageResizer.compressImage(bytes, 800, 800, 80)
                    
                    @OptIn(ExperimentalEncodingApi::class)
                    val base64 = Base64.Default.encode(compressedBytes)
                    val dataUrl = "data:image/webp;base64,$base64"
                    
                    companyData?.let { currentCompany ->
                        val updated = currentCompany.copy(logoUrl = dataUrl)
                        // saveCompany handles the Base64-to-URL conversion and upload
                        val saveResult = companyService.saveCompany(updated)
                        if (saveResult.isSuccess) {
                            // Re-fetch or update local state
                            val companyResult = companyService.getOwnCompany()
                            companyData = companyResult.getOrNull()
                            snackbarHostState.showSnackbar("Logo başarıyla güncellendi.")
                        } else {
                            snackbarHostState.showSnackbar("Hata: ${saveResult.exceptionOrNull()?.message}")
                        }
                    }
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar("Resim işlemi sırasında hata: ${e.message}")
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        try {
            val result = settingsService.getContactEmail()
            contactEmail = result.getOrNull() ?: "destek@globaltrade.local"
            
            val companyResult = companyService.getOwnCompany()
            companyData = companyResult.getOrNull()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ProfileHeader(
                    company = companyData, 
                    onSettingsClick = onCompanySettingsClick,
                    onLogoClick = { logoPicker.launch() }
                )
            }
            
            item {
                Text(
                    text = stringResource(Res.string.account_settings),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column {
                        ProfileOptionItem(stringResource(Res.string.company_info), Icons.Default.Business, onClick = onCompanySettingsClick)
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))
                        ProfileOptionItem("Ürün Yönetimi", Icons.Default.Category, onClick = onProductsClick)
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))
                        ProfileOptionItem(stringResource(Res.string.notification_prefs), Icons.Default.Notifications, onClick = onNotificationSettingsClick)
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))
                        ProfileOptionItem(stringResource(Res.string.security_password), Icons.Default.Lock, onClick = onSecurityClick)
                    }
                }
            }

            item {
                Text(
                    text = stringResource(Res.string.support),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column {
                        ProfileOptionItem(stringResource(Res.string.help_center), Icons.Default.Help, onClick = onHelpCenterClick)
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))
                        ProfileOptionItem(stringResource(Res.string.contact_us), Icons.Default.Email, onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Destek E-posta: $contactEmail")
                            }
                        })
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFC62828).copy(alpha = 0.1f),
                        contentColor = Color(0xFFC62828)
                    ),
                    elevation = null
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(Res.string.logout), fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun ProfileHeader(company: B2BCompany?, onSettingsClick: () -> Unit, onLogoClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSettingsClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { onLogoClick() },
                contentAlignment = Alignment.Center
            ) {
                val logoUrl = company?.logoUrl?.trim()?.removeSurrounding("\"")
                
                if (!logoUrl.isNullOrBlank() && logoUrl != "null") {
                    val rawUrl = logoUrl
                    val model: Any? = if (rawUrl.startsWith("data:image/")) {
                        try {
                            val base64String = rawUrl.substringAfter("base64,").trim()
                            @OptIn(ExperimentalEncodingApi::class)
                            Base64.Default.decode(base64String)
                        } catch (e: Exception) { null }
                    } else {
                        val cleaned = rawUrl.trim().filter { it.code in 33..1000 }
                        if (cleaned.isNotBlank() && cleaned != "null") {
                            if (cleaned.startsWith("http")) cleaned else ApiConfig.getImageUrl(cleaned)
                        } else null
                    }

                    if (model != null) {
                        coil3.compose.AsyncImage(
                            model = model,
                            contentDescription = "Logo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Default.Business, null, modifier = Modifier.size(40.dp), tint = Color.LightGray) 
                    }
                } else {
                    Icon(Icons.Default.Business, null, modifier = Modifier.size(40.dp), tint = Color.LightGray) 
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = company?.name?.ifBlank { "irket Adı Yok" } ?: "irket Adı Seçilmedi",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = company?.authorizedPerson?.ifBlank { "Yetkili Kişi Yok" } ?: "Yetkili Kişi Belirtilmedi",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.verified_account),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileOptionItem(text: String, icon: ImageVector, onClick: () -> Unit = {}) {
    Surface(
        onClick = onClick,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}



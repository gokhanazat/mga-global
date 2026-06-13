package com.mgacreative.mgaglobal.ui.b2b

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mgacreative.mgaglobal.core.domain.b2b.B2BCompany
import com.mgacreative.mgaglobal.core.domain.b2b.CompanyService
import com.mgacreative.mgaglobal.core.domain.education.EducationService
import com.mgacreative.mgaglobal.core.domain.education.UserCertificate
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import com.mgacreative.mgaglobal.core.network.ApiConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyProfileScreen(
    companyId: String,
    onBack: () -> Unit,
    onNavigateToShowroom: (String) -> Unit
) {
    var company by remember { mutableStateOf<B2BCompany?>(null) }
    var certificates by remember { mutableStateOf<List<UserCertificate>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val companyService = remember { CompanyService() }
    val educationService = remember { EducationService() }
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(companyId) {
        val result = companyService.getCompanyById(companyId)
        if (result.isSuccess) {
            val comp = result.getOrNull()
            company = comp
            if (comp != null && comp.email.isNotBlank()) {
                val certResult = educationService.getUserCertificates(comp.email)
                if (certResult.isSuccess) {
                    certificates = certResult.getOrNull() ?: emptyList()
                }
            }
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("irket Profili", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF0F172A))
            }
        } else if (company == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("irket bulunamadı.", color = Color.Gray)
            }
        } else {
            val comp = company!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF8FAFC))
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Profile Header Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF1F5F9)),
                                contentAlignment = Alignment.Center
                            ) {
                                val logoUrlRaw = comp.logoUrl ?: ""
                                val cleanedLogo = logoUrlRaw.trim().filter { it.code in 33..1000 } // Daha esnek temizlik
                                
                                if (cleanedLogo.isNotBlank() && cleanedLogo != "null") {
                                    val finalUrl = if (cleanedLogo.startsWith("http")) cleanedLogo else ApiConfig.getImageUrl(cleanedLogo)
                                    
                                    coil3.compose.AsyncImage(
                                        model = finalUrl,
                                        contentDescription = "Logo",
                                        modifier = Modifier.fillMaxSize().clickable { 
                                            try { uriHandler.openUri(finalUrl) } catch(e: Exception) {} 
                                        },
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(Icons.Default.Business, null, tint = Color.LightGray, modifier = Modifier.size(40.dp))
                                }
                            }
                            
                            // Resim gelirse tıkla gör kalksın (çünkü resim görünecek artık)
                            val logoUrlRaw = comp.logoUrl ?: ""
                            val cleanedLogo = logoUrlRaw.trim().filter { it.code in 33..1000 }
                            if (cleanedLogo.isBlank() || cleanedLogo == "null") {
                                // Boşken ikon gösteriyoruz, linke gerek yok
                            } else {
                                // Ekstra sigorta olarak link kalsın ama daha küçük olsun
                                val finalUrl = if (cleanedLogo.startsWith("http")) cleanedLogo else ApiConfig.getImageUrl(cleanedLogo)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Resmi Tarayıcıda Aç",
                                    fontSize = 8.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.clickable { try { uriHandler.openUri(finalUrl) } catch(e: Exception) {} }
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = comp.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Surface(
                                color = Color(0xFF0F172A).copy(alpha = 0.1f),
                                shape = CircleShape
                            ) {
                                Text(
                                    text = comp.sector,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFF0F172A),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    // Details Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                            Text("Firma Detayları", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            ProfileDetailRow(icon = Icons.Default.Public, label = "Ülke", value = comp.country.ifBlank { "Belirtilmemiş" })
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))
                            
                            ProfileDetailRow(icon = Icons.Default.Person, label = "Yetkili Kişi", value = comp.authorizedPerson.ifBlank { "Belirtilmemiş" })
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))

                            ProfileDetailRow(icon = Icons.Default.Phone, label = "Telefon", value = comp.phone.ifBlank { "Belirtilmemiş" })
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))

                            ProfileDetailRow(icon = Icons.Default.Email, label = "E-posta", value = comp.email.ifBlank { "Belirtilmemiş" })

                            if (comp.description.isNotBlank()) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text("irket Hakkında", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = comp.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF1E293B),
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }
                    }

                    // Certificates Section
                    if (certificates.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Başarı Sertifikaları", 
                            style = MaterialTheme.typography.titleMedium, 
                            fontWeight = FontWeight.Bold, 
                            color = Color(0xFF1E293B),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                        )
                        
                        certificates.forEach { cert ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        modifier = Modifier.size(40.dp),
                                        shape = CircleShape,
                                        color = Color(0xFFFFD700).copy(alpha = 0.1f)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.Verified, null, tint = Color(0xFFDAA520), modifier = Modifier.size(24.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text("Eğitim Başarı Sertifikası", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Kod: ${cert.certCode}", fontSize = 12.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom Actions
                Surface(
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .navigationBarsPadding(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { onNavigateToShowroom(comp.id) },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
                        ) {
                            Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Showroom", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val gsm = comp.gsm.ifBlank { comp.phone }
                                if (gsm.isNotBlank()) {
                                    val cleanGsm = gsm.replace(Regex("[^0-9]"), "")
                                    try { uriHandler.openUri("https://wa.me/$cleanGsm") } catch (e: Exception) { e.printStackTrace() }
                                }
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                        ) {
                            Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Mesaj", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                if (comp.email.isNotBlank()) {
                                    try { uriHandler.openUri("mailto:${comp.email}") } catch (e: Exception) { e.printStackTrace() }
                                }
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF475569))
                        ) {
                            Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("E-mail", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileDetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = CircleShape,
            color = Color(0xFF0F172A).copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = Color(0xFF0F172A), modifier = Modifier.size(18.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF1E293B), fontWeight = FontWeight.Medium)
        }
    }
}


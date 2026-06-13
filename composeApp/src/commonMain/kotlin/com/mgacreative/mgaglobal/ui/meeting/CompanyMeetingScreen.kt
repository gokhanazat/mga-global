package com.mgacreative.mgaglobal.ui.meeting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mgacreative.mgaglobal.core.domain.b2b.B2BCompany
import com.mgacreative.mgaglobal.core.domain.b2b.CompanyService
import com.mgacreative.mgaglobal.core.network.ApiConfig
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import androidx.compose.ui.layout.ContentScale
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyMeetingScreen(
    onBack: () -> Unit,
    onCardClick: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val companyService = remember { CompanyService() }
    var companies by remember { mutableStateOf<List<B2BCompany>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        scope.launch {
            isLoading = true
            val result = companyService.getAllCompanies()
            if (result.isSuccess) {
                companies = result.getOrNull() ?: emptyList()
            }
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("B2B Eşleştirme", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri") } },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(padding)) {
            val screenWidth = maxWidth
            val isWeb = screenWidth > 800.dp
            val listModifier = if (isWeb) Modifier.width(800.dp).align(Alignment.TopCenter) else Modifier.fillMaxWidth()

            // Web'de yan boşluklar Lacivert
            Box(modifier = Modifier.fillMaxSize().background(if (isWeb) Color(0xFF0F172A) else Color(0xFFF1F3F5)))

            Column(modifier = listModifier.background(Color(0xFFF1F3F5))) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    placeholder = { Text("Firma adıyla ara...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    shape = RoundedCornerShape(16.dp)
                )

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF0F172A))
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(companies.filter { it.name.contains(searchQuery, ignoreCase = true) }) { company ->
                            CompanyCard(company = company, onClick = { onCardClick(company.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompanyCard(company: B2BCompany, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.LightGray.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                val logoData = remember(company.logoUrl) {
                    val url = company.logoUrl ?: return@remember null
                    if (url.isBlank()) return@remember null
                    
                    if (url.startsWith("data:image/")) {
                        val base64String = url.substringAfter("base64,", "").trim()
                        if (base64String.isNotEmpty()) {
                            @OptIn(ExperimentalEncodingApi::class)
                            runCatching { Base64.Default.decode(base64String) }.getOrNull()
                        } else null
                    } else if (url.startsWith("http")) {
                        url
                    } else if (url.startsWith("/")) {
                        "${ApiConfig.BASE_URL}$url"
                    } else {
                        ApiConfig.getImageUrl(url)
                    }
                }

                if (logoData != null) {
                    KamelImage(
                        resource = asyncPainterResource(data = logoData),
                        contentDescription = "Logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        onLoading = { CircularProgressIndicator(modifier = Modifier.size(20.dp)) },
                        onFailure = { 
                            Icon(Icons.Default.Business, contentDescription = null, tint = Color.Gray)
                        }
                    )
                } else {
                    Icon(Icons.Default.Business, contentDescription = null, tint = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(company.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(company.sector, color = Color.Gray, fontSize = 12.sp)
            }
            Icon(Icons.Default.CheckCircle, null, tint = Color.Green.copy(alpha = 0.5f))
        }
    }
}


package com.mgacreative.mgaglobal.ui.showroom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import mgaglobal.composeapp.generated.resources.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import com.mgacreative.mgaglobal.core.network.ApiConfig
import com.mgacreative.mgaglobal.core.domain.showroom.ProductService
import com.mgacreative.mgaglobal.core.domain.showroom.ShowroomProduct

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productName: String,
    onBackClick: () -> Unit = {}
) {
    val decodedName = productName.replace("%20", " ")
    val scrollState = rememberScrollState()
    val productService = remember { ProductService() }
    var product by remember { mutableStateOf<ShowroomProduct?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(decodedName) {
        isLoading = true
        val result = productService.getAllProducts()
        if (result.isSuccess) {
            product = result.getOrNull()?.find { it.name == decodedName }
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.nav_product_detail), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(stringResource(Res.string.action_send_b2b_request), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (product == null) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("ÃœrÃ¼n bulunamadÄ±.", color = Color.Gray)
            }
        } else {
            val p = product!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(scrollState)
            ) {
                // Product Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color.LightGray.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!p.imageUrl.isNullOrBlank()) {
                        val rawUrl = p.imageUrl ?: ""
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
                                contentDescription = "ÃœrÃ¼n Resmi",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.Image, null, tint = Color.Gray, modifier = Modifier.size(80.dp))
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = Color.Gray.copy(alpha = 0.5f)
                        )
                    }
                }

                Column(modifier = Modifier.padding(24.dp)) {
                    // Title and Basic Info
                    Text(
                        text = p.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Business,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = p.companyName,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = p.country,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }

                    if (p.price.isNotBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "$${p.price}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Description
                    DetailSection(title = "ÃœrÃ¼n AÃ§Ä±klamasÄ±") {
                        Text(
                            text = p.description.ifEmpty { "AÃ§Ä±klama bulunmuyor." },
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 24.sp,
                            color = Color.DarkGray
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Technical Specifications (Category as fallback)
                    DetailSection(title = "Detaylar") {
                        Row(modifier = Modifier.padding(vertical = 4.dp)) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp).padding(top = 2.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Kategori: ${p.category}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun DetailSection(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun CertificationChip(label: String) {
    Surface(
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Verified,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}



package com.mgacreative.globaltrade.ui.screens

import com.mgacreative.globaltrade.getNowMillis
import com.mgacreative.globaltrade.core.auth.SessionManager

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mgacreative.globaltrade.core.domain.b2b.CompanyService
import com.mgacreative.globaltrade.core.domain.sector.Sector
import com.mgacreative.globaltrade.core.domain.sector.SectorService
import com.mgacreative.globaltrade.core.domain.showroom.ProductService
import com.mgacreative.globaltrade.core.domain.showroom.ShowroomProduct
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerType
import com.mgacreative.globaltrade.core.util.ImageResizer
import com.mgacreative.globaltrade.core.network.ApiConfig
import kotlinx.coroutines.launch
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import com.mgacreative.globaltrade.core.domain.audit.AuditDomainService
import com.mgacreative.globaltrade.core.audit.ActionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductManagementScreen(
    paddingValues: PaddingValues = PaddingValues(0.dp),
    productId: String? = null,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val productService = remember { ProductService() }
    val companyService = remember { CompanyService() }
    val sectorService = remember { SectorService() }

    var productName by remember { mutableStateOf("") }
    var productCategory by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var productDescription by remember { mutableStateOf("") }
    var productImageBase64 by remember { mutableStateOf<String?>(null) }
    var existingProduct by remember { mutableStateOf<ShowroomProduct?>(null) }
    
    var sectors by remember { mutableStateOf<List<Sector>>(emptyList()) }
    var sectorExpanded by remember { mutableStateOf(false) }
    
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var userProducts by remember { mutableStateOf<List<ShowroomProduct>>(emptyList()) }

    LaunchedEffect(productId) {
        // Fetch sectors first
        val sectorResult = sectorService.getSectors()
        sectors = sectorResult.getOrNull()?.sortedBy { it.groupNo } ?: emptyList()

        if (!productId.isNullOrEmpty() && productId != "new") {
            val result = productService.getProductById(productId)
            if (result.isSuccess) {
                val p = result.getOrThrow()
                existingProduct = p
                productName = p.name
                productCategory = p.category
                productPrice = p.price
                productDescription = p.description
                productImageBase64 = p.imageUrl
            }
        }
        
        // Load user's products
        val userProductsResult = productService.getProductsByOwnerId(SessionManager.getUserId())
        if (userProductsResult.isSuccess) {
            userProducts = userProductsResult.getOrNull() ?: emptyList()
        }
        
        isLoading = false
    }
    
    @OptIn(ExperimentalEncodingApi::class)
    val imagePicker = rememberFilePickerLauncher(
        type = PickerType.Image,
        title = "Ürün Resmi Seç"
    ) { file ->
        if (file != null) {
            scope.launch {
                try {
                    val bytes = file.readBytes()
                    
                    // 1. Dosya boyutu kontrolü (1MB sınırı)
                    if (bytes.size > 1048576) {
                        snackbarHostState.showSnackbar("Dosya çok büyük (${bytes.size / 1024f.toInt()} KB). Lütfen 1MB altında seçin.")
                        return@launch
                    }

                    // 2. Arka planda sıkıştırma
                    val compressedBytes = ImageResizer.compressImage(bytes, 800, 800, 85)
                    
                    // 3. WebP Base64 çevrimi
                    val base64 = Base64.Default.encode(compressedBytes)
                    productImageBase64 = "data:image/webp;base64,$base64"
                    
                    // snackbarHostState.showSnackbar("Resim hazır.")
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar("Resim eklenirken hata: ${e.message}")
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Product Image Picker
                        Box(
                            modifier = Modifier
                                .size(120.dp) // Even smaller to save space
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.LightGray.copy(alpha = 0.2f))
                                .clickable { imagePicker.launch() },
                            contentAlignment = Alignment.Center
                        ) {
                            if (productImageBase64 != null) {
                                val rawUrl = productImageBase64!!
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
                                        contentDescription = "Ürün Resmi",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(28.dp), tint = Color.Gray)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Resim Hatası", color = Color.Gray, fontSize = 11.sp)
                                    }
                                }
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(28.dp), tint = Color.Gray)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Resim Seç", color = Color.Gray, fontSize = 11.sp)
                                }
                            }
                        }

                        OutlinedTextField(
                            value = productName,
                            onValueChange = { productName = it },
                            label = { Text("Ürün Adı *") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.Inventory, null) },
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        // Sector Dropdown (Required)
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = productCategory,
                                onValueChange = { },
                                label = { Text("Sektör / Meslek Grubu *") },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = { Icon(Icons.Default.Category, null) },
                                trailingIcon = { 
                                    IconButton(onClick = { sectorExpanded = true }) {
                                        Icon(if (sectorExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown, null)
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                readOnly = true
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { sectorExpanded = true }
                            )
                            
                            DropdownMenu(
                                expanded = sectorExpanded,
                                onDismissRequest = { sectorExpanded = false },
                                modifier = Modifier.fillMaxWidth(0.9f)
                            ) {
                                if (sectors.isEmpty()) {
                                    DropdownMenuItem(
                                        text = { Text("Henüz sektör tanımlanmamış", color = Color.Gray) },
                                        onClick = { sectorExpanded = false }
                                    )
                                } else {
                                    sectors.forEach { item ->
                                        val displayName = if (item.groupNo.isNotBlank()) "(${item.groupNo}) ${item.name}" else item.name
                                        DropdownMenuItem(
                                            text = { Text(displayName) },
                                            onClick = {
                                                productCategory = item.name
                                                sectorExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = productPrice,
                            onValueChange = { productPrice = it },
                            label = { Text("Fiyat ($)") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.AttachMoney, null) },
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = productDescription,
                            onValueChange = { productDescription = it },
                            label = { Text("Ürün Açıklaması *") },
                            modifier = Modifier.fillMaxWidth().height(100.dp), // Slightly smaller
                            leadingIcon = { Icon(Icons.Default.Description, null) },
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 3
                        )
                    }
                }

                Button(
                    onClick = { 
                        if (isSaving) return@Button
                        scope.launch {
                            if (productName.isBlank() || productCategory.isBlank() || productDescription.isBlank()) {
                                snackbarHostState.showSnackbar("Lütfen Ürün Adı, Sektör ve Açıklama alanlarını doldurunuz.")
                                return@launch
                            }
                            // Image is no longer required for now
                            
                            isSaving = true
                            
                            val product = if (existingProduct != null) {
                                existingProduct!!.copy(
                                    name = productName,
                                    category = productCategory,
                                    price = productPrice,
                                    description = productDescription,
                                    imageUrl = productImageBase64
                                )
                            } else {
                                val companyResult = companyService.getOwnCompany()
                                val company = companyResult.getOrNull()
                                val companyName = company?.name ?: "Bilinmeyen Şirket"
                                val country = company?.country ?: "Bilinmiyor"

                                ShowroomProduct(
                                    id = "prod_${getNowMillis()}",
                                    name = productName,
                                    category = productCategory,
                                    price = productPrice,
                                    description = productDescription,
                                    imageUrl = productImageBase64,
                                    ownerId = SessionManager.getUserId(),
                                    companyName = companyName,
                                    country = country,
                                    isPremium = company?.isVerified == true,
                                    createdAt = getNowMillis()
                                )
                            }
                            
                            val saveResult = productService.saveProduct(product)
                            
                            if (saveResult.isSuccess) {
                                val successMsg = if (existingProduct != null) "Ürün başarıyla güncellendi!" else "Ürün başarıyla eklendi!"
                                snackbarHostState.showSnackbar(successMsg)
                                if (existingProduct == null) {
                                    AuditDomainService.logContentAction(product.ownerId, "MEMBER", product.id, "Showroom", ActionType.CREATE)
                                    productName = ""
                                    productCategory = ""
                                    productPrice = ""
                                    productDescription = ""
                                    productImageBase64 = null
                                } else {
                                    AuditDomainService.logContentAction(product.ownerId, "MEMBER", product.id, "Showroom", ActionType.UPDATE)
                                    onBackClick()
                                }
                            } else {
                                snackbarHostState.showSnackbar("Hata: ${saveResult.exceptionOrNull()?.message}")
                            }
                            isSaving = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isSaving,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (existingProduct != null) "Güncelle" else "Ürünü Kaydet", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                if (userProducts.isNotEmpty()) {
                    Text("Ürünleriniz", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    userProducts.forEach { product ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { 
                                // Reset form and set this product for editing
                                existingProduct = product
                                productName = product.name
                                productCategory = product.category
                                productPrice = product.price
                                productDescription = product.description
                                productImageBase64 = product.imageUrl
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Inventory, null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(product.name, fontWeight = FontWeight.Bold)
                                    Text(product.category, fontSize = 12.sp, color = Color.Gray)
                                }
                                IconButton(onClick = {
                                    scope.launch {
                                        productService.deleteProduct(product.id)
                                        userProducts = userProducts.filter { it.id != product.id }
                                    }
                                }) {
                                    Icon(Icons.Default.Delete, null, tint = Color.Red.copy(alpha = 0.7f))
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

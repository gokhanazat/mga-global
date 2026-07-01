package com.mgacreative.mgaglobal.ui.showroom

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.window.Dialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import com.mgacreative.mgaglobal.core.domain.showroom.ProductService
import com.mgacreative.mgaglobal.core.domain.showroom.ShowroomProduct
import com.mgacreative.mgaglobal.core.domain.showroom.CartManager
import com.mgacreative.mgaglobal.core.auth.SessionManager
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import mgaglobal.composeapp.generated.resources.*
import com.mgacreative.mgaglobal.ui.components.ProductCard
import com.mgacreative.mgaglobal.core.pdf.PdfGenerator
import com.mgacreative.mgaglobal.core.pdf.util.FileSaver
import com.mgacreative.mgaglobal.core.domain.b2b.B2BCompany
import com.mgacreative.mgaglobal.core.domain.b2b.CompanyService
import com.mgacreative.mgaglobal.core.network.ApiConfig
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import com.mgacreative.mgaglobal.core.domain.audit.AuditDomainService
import com.mgacreative.mgaglobal.core.audit.ActionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowroomScreen(
    paddingValues: PaddingValues = PaddingValues(0.dp),
    onMenuClick: () -> Unit = {},
    initialCategory: String? = null,
    initialOwnerId: String? = null,
    onBackClick: () -> Unit = {},
    onProductClick: (String) -> Unit = {},
    onEditClick: (String) -> Unit = {},
    onCompanyClick: (String) -> Unit = {},
    onCartClick: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val productService = remember { ProductService() }
    val companyService = remember { CompanyService() }
    val snackbarHostState = remember { SnackbarHostState() }
    
    var products by remember { mutableStateOf<List<ShowroomProduct>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedProductForDetail by remember { mutableStateOf<ShowroomProduct?>(null) }
    val currentUserId = SessionManager.getUserId()
    
    val cartItems by CartManager.cartState.collectAsState()
    
    var isSelectionMode by remember { mutableStateOf(false) }
    var selectedProductIds by remember { mutableStateOf(setOf<String>()) }
    var ownCompany by remember { mutableStateOf<B2BCompany?>(null) }

    LaunchedEffect(initialCategory, initialOwnerId) {
        isLoading = true
        val result = when {
            !initialCategory.isNullOrBlank() && initialCategory != "{category}" -> productService.getProductsByCategory(initialCategory)
            !initialOwnerId.isNullOrBlank() && initialOwnerId != "{ownerId}" -> productService.getProductsByOwnerId(initialOwnerId)
            else -> {
                // Eğer URL üzerinden bir filtreleme gelmediyse ve ana sayfadan gelinmiyorsa, 
                // kaza eseri tüm ürünleri dökmemesi için boş liste dönelim.
                if (initialCategory == null && initialOwnerId == null) {
                    productService.getAllProducts()
                } else {
                    Result.success(emptyList())
                }
            }
        }
        if (result.isSuccess) { products = result.getOrNull() ?: emptyList() }
        val companyResult = companyService.getOwnCompany()
        if (companyResult.isSuccess) { ownCompany = companyResult.getOrNull() }
        isLoading = false
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(if (isSelectionMode) "${selectedProductIds.size} Ürün" else stringResource(Res.string.nav_showroom), fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                    navigationIcon = {
                        if (initialCategory == null && initialOwnerId == null) {
                            IconButton(onClick = onMenuClick) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        } else {
                            IconButton(onClick = onBackClick) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
                    },
                    actions = {
                        // Cart Icon with Badge
                        BadgedBox(
                            badge = {
                                if (cartItems.isNotEmpty()) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = Color.White
                                    ) {
                                        Text(cartItems.sumOf { it.quantity }.toString(), fontSize = 10.sp)
                                    }
                                }
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            IconButton(onClick = onCartClick) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Sepetim",
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        // PDF Export Icon - NO IF CHECK, ALWAYS VISIBLE NEXT TO "URUN SEC"
                        IconButton(
                            onClick = {
                                scope.launch {
                                    val targetProducts = if (isSelectionMode && selectedProductIds.isNotEmpty()) {
                                        products.filter { it.id in selectedProductIds }
                                    } else {
                                        products
                                    }
                                    if (targetProducts.isEmpty()) {
                                        snackbarHostState.showSnackbar("İndirilecek ürün bulunamadı.")
                                        return@launch
                                    }
                                    val pdfBytes = PdfGenerator.generateShowroomCatalog(targetProducts, ownCompany)
                                    FileSaver.savePdf("Urun_Katalogu.pdf", pdfBytes)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = "PDF İndir",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        if (isSelectionMode) {
                            IconButton(onClick = { isSelectionMode = false; selectedProductIds = emptySet() }) { 
                                Icon(Icons.Default.Close, contentDescription = "Vazgeç", tint = MaterialTheme.colorScheme.onBackground) 
                            }
                        } else {
                            TextButton(onClick = { isSelectionMode = true }) { 
                                Text("Ürün Seç", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 14.sp) 
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background, 
                        titleContentColor = MaterialTheme.colorScheme.onBackground, 
                        navigationIconContentColor = MaterialTheme.colorScheme.onBackground, 
                        actionIconContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            },
            floatingActionButton = {
                if (currentUserId != "guest" && currentUserId == initialOwnerId) {
                    FloatingActionButton(onClick = { onEditClick("") }, containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.White) { Icon(Icons.Default.Add, contentDescription = "Ürün Ekle") }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF8FAFC)),
                contentAlignment = Alignment.TopCenter
            ) {
                BoxWithConstraints(modifier = Modifier.widthIn(max = 1200.dp).fillMaxHeight()) {
                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
                    } else if (products.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { 
                            Text("Henüz ürün bulunmuyor.", color = Color.Gray) 
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 160.dp),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(products) { product ->
                                ProductCard(
                                    productName = product.name,
                                    companyName = product.companyName,
                                    country = product.country,
                                    isPremium = product.isPremium,
                                    price = if (product.price.isNotBlank()) "$${product.price}" else null,
                                    description = product.description,
                                    imageUrl = product.imageUrl,
                                    isSelected = selectedProductIds.contains(product.id),
                                    onSelectionChange = if (isSelectionMode) { { checked -> if (checked) selectedProductIds += product.id else selectedProductIds -= product.id } } else null,
                                    onCompanyClick = { onCompanyClick(product.ownerId) },
                                    onClickDetail = { if (isSelectionMode) { if (selectedProductIds.contains(product.id)) selectedProductIds -= product.id else selectedProductIds += product.id } else { selectedProductForDetail = product } },
                                    onEditClick = if (!isSelectionMode && currentUserId != "guest" && (currentUserId == product.ownerId || currentUserId == initialOwnerId)) { { onEditClick(product.id) } } else null,
                                    onDeleteClick = if (!isSelectionMode && currentUserId != "guest" && (currentUserId == product.ownerId || currentUserId == initialOwnerId)) { { scope.launch { 
                                        productService.deleteProduct(product.id)
                                        AuditDomainService.logContentAction(currentUserId, "MEMBER", product.id, "Showroom", ActionType.DELETE)
                                        products = products.filter { it.id != product.id } 
                                    } } } else null
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    selectedProductForDetail?.let { product ->
        ProductDetailDialog(
            product = product,
            currentUserId = currentUserId,
            onDismissRequest = { selectedProductForDetail = null },
            onCompanyClick = onCompanyClick
        )
    }
}

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun ProductDetailDialog(
    product: ShowroomProduct,
    currentUserId: String,
    onDismissRequest: () -> Unit,
    onCompanyClick: ((String) -> Unit)? = null
) {
    Dialog(onDismissRequest = onDismissRequest) {
        val scrollState = rememberScrollState()
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
                .heightIn(max = 600.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(scrollState)) {
                // Image Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .background(Color.LightGray.copy(alpha = 0.2f))
                ) {
                    if (!product.imageUrl.isNullOrBlank()) {
                        val rawUrl = product.imageUrl ?: ""
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
                                contentDescription = product.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Image, null, modifier = Modifier.size(48.dp), tint = Color.Gray)
                                Text("Resim Hatası", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Image, null, modifier = Modifier.size(48.dp), tint = Color.Gray)
                            Text("No Image Available", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    // Top Actions
                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }

                // Content Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header: Name and Badge
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = product.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.weight(1f)
                            )
                            if (product.isPremium) {
                                Icon(Icons.Default.Verified, "Verified", tint = Color(0xFF3B82F6), modifier = Modifier.size(20.dp))
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = if (onCompanyClick != null) Modifier.clickable { onCompanyClick(product.ownerId) } else Modifier
                        ) {
                            Icon(Icons.Default.Business, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(product.companyName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                            Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(product.country, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        }
                    }

                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

                    // Description Section
                    Column {
                        Text(
                            text = "Açıklama & Teknik Detaylar", 
                            style = MaterialTheme.typography.titleMedium, 
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = product.description,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Justify
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                CartManager.addProduct(product, 1)
                                onDismissRequest()
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.AddShoppingCart, null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sepete Ekle", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onDismissRequest,
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Kapat", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}



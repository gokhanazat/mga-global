package com.mgacreative.mgaglobal.ui.showroom

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.HorizontalDivider
import com.mgacreative.mgaglobal.core.domain.b2b.B2BCompany
import com.mgacreative.mgaglobal.core.domain.b2b.CompanyService
import com.mgacreative.mgaglobal.core.domain.sector.Sector
import com.mgacreative.mgaglobal.core.domain.sector.SectorService
import com.mgacreative.mgaglobal.core.domain.showroom.ProductService
import com.mgacreative.mgaglobal.core.domain.showroom.ShowroomProduct
import com.mgacreative.mgaglobal.ui.components.ProductCard
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import mgaglobal.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDigitalShowroomScreen(
    onBackClick: () -> Unit,
    onProductClick: (String) -> Unit,
    onCompanyClick: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val productService = remember { ProductService() }
    val companyService = remember { CompanyService() }
    val sectorService = remember { SectorService() }
    val snackbarHostState = remember { SnackbarHostState() }

    var products by remember { mutableStateOf<List<ShowroomProduct>>(emptyList()) }
    var sectors by remember { mutableStateOf<List<Sector>>(emptyList()) }
    var companies by remember { mutableStateOf<List<B2BCompany>>(emptyList()) }
    
    var isLoading by remember { mutableStateOf(true) }
    var selectedProductForDetail by remember { mutableStateOf<ShowroomProduct?>(null) }

    // Filter states
    var selectedSector by remember { mutableStateOf<Sector?>(null) }
    var selectedCompany by remember { mutableStateOf<B2BCompany?>(null) }
    var sectorExpanded by remember { mutableStateOf(false) }
    var companyExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(selectedSector, selectedCompany) {
        isLoading = true
        val result = try {
            when {
                selectedCompany != null -> productService.getProductsByOwnerId(selectedCompany!!.id)
                selectedSector != null -> productService.getProductsByCategory(selectedSector!!.name)
                else -> productService.getAllProducts()
            }
        } catch (e: Exception) {
            null
        }
        
        if (result != null && result.isSuccess) {
            products = result.getOrNull() ?: emptyList()
        } else {
            products = emptyList()
        }
        isLoading = false
    }

    // Load initial data for filters
    LaunchedEffect(Unit) {
        val sectorsResult = sectorService.getSectors()
        if (sectorsResult.isSuccess) {
            sectors = sectorsResult.getOrNull()?.sortedBy { it.groupNo } ?: emptyList()
        }

        val companiesResult = companyService.getAllCompanies()
        if (companiesResult.isSuccess) {
            companies = companiesResult.getOrNull() ?: emptyList()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.background(Color(0xFF0F172A))) {
                CenterAlignedTopAppBar(
                    title = { Text("Products", fontWeight = FontWeight.Bold, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White
                    )
                )
                
                // Filters Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Sector Filter
                    Box(modifier = Modifier.weight(1f)) {
                        FilterDropdown(
                            label = selectedSector?.name ?: "All Sectors",
                            expanded = sectorExpanded,
                            onExpandChange = { sectorExpanded = it },
                            onSelectAll = {
                                selectedSector = null
                                sectorExpanded = false
                            },
                            items = sectors.map { it.name },
                            onSelectItem = { name ->
                                selectedSector = sectors.find { it.name == name }
                                sectorExpanded = false
                            }
                        )
                    }

                    // Company Filter
                    Box(modifier = Modifier.weight(1f)) {
                        FilterDropdown(
                            label = selectedCompany?.name ?: "All Companies",
                            expanded = companyExpanded,
                            onExpandChange = { companyExpanded = it },
                            onSelectAll = {
                                selectedCompany = null
                                companyExpanded = false
                            },
                            items = companies.map { it.name },
                            onSelectItem = { name ->
                                selectedCompany = companies.find { it.name == name }
                                companyExpanded = false
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (products.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Inventory, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Spacer(Modifier.height(16.dp))
                    Text("No products found for selected filters.", color = Color.Gray)
                    if (selectedSector != null || selectedCompany != null) {
                        TextButton(onClick = {
                            selectedSector = null
                            selectedCompany = null
                        }) {
                            Text("Clear Filters")
                        }
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF8F9FA)),
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
                        onCompanyClick = { onCompanyClick(product.ownerId) },
                        onClickDetail = {
                            selectedProductForDetail = product
                        }
                    )
                }
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    selectedProductForDetail?.let { product ->
        ProductDetailDialog(
            product = product,
            onDismissRequest = { selectedProductForDetail = null },
            onCompanyClick = onCompanyClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropdown(
    label: String,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    onSelectAll: () -> Unit,
    items: List<String>,
    onSelectItem: (String) -> Unit
) {
    Surface(
        onClick = { onExpandChange(!expanded) },
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.15f),
        contentColor = Color.White,
        modifier = Modifier.fillMaxWidth().height(44.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Icon(
                if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                null,
                modifier = Modifier.size(20.dp)
            )
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onExpandChange(false) },
        modifier = Modifier.width(IntrinsicSize.Min).heightIn(max = 300.dp)
    ) {
        DropdownMenuItem(
            text = { Text("View All", fontWeight = FontWeight.Bold) },
            onClick = onSelectAll
        )
        HorizontalDivider()
        items.forEach { item ->
            DropdownMenuItem(
                text = { Text(item) },
                onClick = { onSelectItem(item) }
            )
        }
    }
}



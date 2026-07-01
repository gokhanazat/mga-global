package com.mgacreative.mgaglobal.ui.showroom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mgacreative.mgaglobal.core.domain.showroom.CartManager
import com.mgacreative.mgaglobal.core.domain.showroom.CartItem
import com.mgacreative.mgaglobal.core.domain.showroom.OrderService
import com.mgacreative.mgaglobal.core.domain.showroom.OrderItem
import com.mgacreative.mgaglobal.core.domain.audit.AuditDomainService
import com.mgacreative.mgaglobal.core.audit.ActionType
import com.mgacreative.mgaglobal.core.auth.SessionManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onBack: () -> Unit,
    onCheckoutSuccess: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val cartItems by CartManager.cartState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val orderService = remember { OrderService() }
    var isSubmitting by remember { mutableStateOf(false) }

    var showGuestDialog by remember { mutableStateOf(false) }
    var guestName by remember { mutableStateOf("") }
    var guestCompany by remember { mutableStateOf("") }
    var guestPhone by remember { mutableStateOf("") }
    var pendingCheckoutCompanyId by remember { mutableStateOf<String?>(null) }
    var pendingCheckoutItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }
    var pendingCheckoutTotal by remember { mutableStateOf(0.0) }

    // Group cart items by vendor company
    val groupedItems = remember(cartItems) {
        cartItems.groupBy { it.product.ownerId }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Sepetim", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = MaterialTheme.colorScheme.onBackground)
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8FAFC)),
            contentAlignment = Alignment.TopCenter
        ) {
            if (cartItems.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Sepetiniz henüz boş.",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Dijital Showroom'ları gezerek sepetinize ürün ekleyebilirsiniz.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Showroom'a Git", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 800.dp)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    groupedItems.forEach { (companyId, items) ->
                        val companyName = items.firstOrNull()?.product?.companyName ?: "Bilinmeyen Firma"
                        
                        item {
                            Text(
                                text = companyName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        items(items) { cartItem ->
                            CartProductCard(
                                cartItem = cartItem,
                                onQuantityChange = { qty ->
                                    CartManager.updateQuantity(cartItem.product.id, qty)
                                },
                                onDelete = {
                                    CartManager.removeProduct(cartItem.product.id)
                                }
                            )
                        }

                        item {
                            val groupTotal = items.sumOf {
                                val priceVal = it.product.price.toDoubleOrNull() ?: 0.0
                                priceVal * it.quantity
                            }
                            
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Toplam Tutar:", fontWeight = FontWeight.SemiBold)
                                        Text("$${groupTotal}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    }

                                    Button(
                                        onClick = {
                                            if (isSubmitting) return@Button
                                            
                                            if (SessionManager.getUserId() == "guest") {
                                                pendingCheckoutCompanyId = companyId
                                                pendingCheckoutItems = items
                                                pendingCheckoutTotal = groupTotal
                                                showGuestDialog = true
                                                return@Button
                                            }

                                            isSubmitting = true
                                            scope.launch {
                                                val orderItems = items.map {
                                                    OrderItem(
                                                        productId = it.product.id,
                                                        productName = it.product.name,
                                                        quantity = it.quantity,
                                                        price = it.product.price
                                                    )
                                                }
                                                val result = orderService.createOrder(companyId, orderItems, groupTotal)
                                                if (result.isSuccess) {
                                                    val createdOrder = result.getOrThrow()
                                                    val buyerId = SessionManager.getUserId()
                                                    AuditDomainService.logContentAction(
                                                        userId = buyerId,
                                                        userRole = "MEMBER",
                                                        targetId = createdOrder.id,
                                                        module = "Order",
                                                        action = ActionType.CREATE
                                                    )
                                                    
                                                    // Clean purchased products from cart
                                                    items.forEach { CartManager.removeProduct(it.product.id) }
                                                    snackbarHostState.showSnackbar("Sipariş başarıyla firmaya gönderildi.")
                                                    
                                                    if (cartItems.isEmpty()) {
                                                        onCheckoutSuccess()
                                                    }
                                                } else {
                                                    snackbarHostState.showSnackbar("Sipariş gönderilirken hata oluştu: ${result.exceptionOrNull()?.message}")
                                                }
                                                isSubmitting = false
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth().height(48.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                        enabled = !isSubmitting
                                    ) {
                                        if (isSubmitting) {
                                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                        } else {
                                            Text("Siparişi Firmaya Gönder", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showGuestDialog) {
        AlertDialog(
            onDismissRequest = { showGuestDialog = false },
            title = { Text("Misafir Sipariş Formu", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Siparişi satıcı firmaya iletebilmemiz için lütfen iletişim bilgilerinizi giriniz. Bu bilgiler satıcı firmaya ve sistem yöneticisine bildirilecektir.",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    OutlinedTextField(
                        value = guestName,
                        onValueChange = { guestName = it },
                        label = { Text("Ad Soyad") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = guestCompany,
                        onValueChange = { guestCompany = it },
                        label = { Text("Firma Adı") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = guestPhone,
                        onValueChange = { guestPhone = it },
                        label = { Text("Telefon Numarası") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (guestName.isBlank() || guestCompany.isBlank() || guestPhone.isBlank()) {
                            return@Button
                        }
                        showGuestDialog = false
                        isSubmitting = true
                        
                        val buyerId = "MISAFIR: $guestName | $guestCompany | $guestPhone"
                        val targetCompanyId = pendingCheckoutCompanyId ?: return@Button
                        
                        scope.launch {
                            val orderItems = pendingCheckoutItems.map {
                                OrderItem(
                                    productId = it.product.id,
                                    productName = it.product.name,
                                    quantity = it.quantity,
                                    price = it.product.price
                                )
                            }
                            val result = orderService.createOrder(
                                companyId = targetCompanyId,
                                items = orderItems,
                                totalPrice = pendingCheckoutTotal,
                                customBuyerId = buyerId
                            )
                            if (result.isSuccess) {
                                val createdOrder = result.getOrThrow()
                                AuditDomainService.logContentAction(
                                    userId = "guest",
                                    userRole = "MEMBER",
                                    targetId = createdOrder.id,
                                    module = "Order",
                                    action = ActionType.CREATE
                                )
                                
                                // Clean purchased products from cart
                                pendingCheckoutItems.forEach { CartManager.removeProduct(it.product.id) }
                                snackbarHostState.showSnackbar("Sipariş başarıyla firmaya gönderildi.")
                                
                                if (cartItems.isEmpty()) {
                                    onCheckoutSuccess()
                                }
                            } else {
                                snackbarHostState.showSnackbar("Sipariş gönderilirken hata oluştu: ${result.exceptionOrNull()?.message}")
                            }
                            isSubmitting = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Siparişi Gönder", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showGuestDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }
}

@Composable
fun CartProductCard(
    cartItem: CartItem,
    onQuantityChange: (Int) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cartItem.product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Birim Fiyat: $${cartItem.product.price}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(2.dp))
                val totalItemPrice = (cartItem.product.price.toDoubleOrNull() ?: 0.0) * cartItem.quantity
                Text(
                    text = "Toplam: $${totalItemPrice}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { onQuantityChange(cartItem.quantity - 1) },
                    modifier = Modifier.size(32.dp).background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Azalt", modifier = Modifier.size(16.dp))
                }
                
                Text(
                    text = cartItem.quantity.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.widthIn(min = 24.dp),
                    textAlign = TextAlign.Center
                )

                IconButton(
                    onClick = { onQuantityChange(cartItem.quantity + 1) },
                    modifier = Modifier.size(32.dp).background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Arttır", modifier = Modifier.size(16.dp))
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onDelete,
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color(0xFFEF4444))
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Sil")
                }
            }
        }
    }
}

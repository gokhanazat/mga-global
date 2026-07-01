package com.mgacreative.mgaglobal.ui.showroom

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mgacreative.mgaglobal.core.domain.showroom.Order
import com.mgacreative.mgaglobal.core.domain.showroom.OrderItem
import com.mgacreative.mgaglobal.core.domain.showroom.OrderService
import com.mgacreative.mgaglobal.core.domain.b2b.CompanyService
import com.mgacreative.mgaglobal.core.domain.b2b.B2BCompany
import com.mgacreative.mgaglobal.core.auth.SessionManager
import com.mgacreative.mgaglobal.core.domain.audit.AuditDomainService
import com.mgacreative.mgaglobal.core.audit.ActionType
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersReportScreen(
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val orderService = remember { OrderService() }
    val companyService = remember { CompanyService() }
    val currentUserId = SessionManager.getUserId()
    val isAdmin = remember { SessionManager.userRole.value == com.mgacreative.mgaglobal.core.auth.Role.ADMIN }
    
    var ownCompany by remember { mutableStateOf<B2BCompany?>(null) }
    var sentOrders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var receivedOrders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var allOrders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    val tabs = remember(ownCompany, isAdmin) {
        val list = mutableListOf<String>()
        if (isAdmin) {
            list.add("Tüm Şirket Siparişleri")
        }
        list.add("Gönderdiğim Siparişler")
        if (ownCompany != null) {
            list.add("Gelen Siparişler")
        }
        list
    }

    val fetchOrders = {
        scope.launch {
            isLoading = true
            
            if (isAdmin) {
                val allResult = orderService.getAllOrders()
                if (allResult.isSuccess) {
                    allOrders = allResult.getOrNull() ?: emptyList()
                }
            }

            val sentResult = orderService.getSentOrders(currentUserId)
            if (sentResult.isSuccess) {
                sentOrders = sentResult.getOrNull() ?: emptyList()
            }
            
            val companyResult = companyService.getOwnCompany()
            if (companyResult.isSuccess) {
                ownCompany = companyResult.getOrNull()
                ownCompany?.let { company ->
                     val receivedResult = orderService.getReceivedOrders(company.id)
                     if (receivedResult.isSuccess) {
                         receivedOrders = receivedResult.getOrNull() ?: emptyList()
                     }
                }
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        fetchOrders()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Sipariş Raporları", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8FAFC)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (tabs.size > 1) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, fontWeight = FontWeight.Bold) }
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.TopCenter
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFF0F172A))
                } else {
                    val currentTabName = tabs.getOrNull(selectedTab)
                    val ordersToDisplay = when (currentTabName) {
                        "Tüm Şirket Siparişleri" -> allOrders
                        "Gelen Siparişler" -> receivedOrders
                        else -> sentOrders
                    }
                    
                    if (ordersToDisplay.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color.LightGray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Gösterilecek sipariş bulunamadı.",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Gray
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .widthIn(max = 800.dp)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(ordersToDisplay) { order ->
                                OrderCard(
                                    order = order,
                                    isReceivedMode = currentTabName == "Gelen Siparişler",
                                    isAdminMode = currentTabName == "Tüm Şirket Siparişleri",
                                    orderService = orderService,
                                    onStatusUpdated = {
                                        fetchOrders()
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Sipariş durumu güncellendi.")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(
    order: Order,
    isReceivedMode: Boolean,
    isAdminMode: Boolean = false,
    orderService: OrderService,
    onStatusUpdated: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var isExpanded by remember { mutableStateOf(false) }
    var orderItems by remember { mutableStateOf<List<OrderItem>>(emptyList()) }
    var isLoadingItems by remember { mutableStateOf(false) }

    LaunchedEffect(isExpanded) {
        if (isExpanded && orderItems.isEmpty()) {
            isLoadingItems = true
            val result = orderService.getOrderItems(order.id)
            if (result.isSuccess) {
                orderItems = result.getOrNull() ?: emptyList()
            }
            isLoadingItems = false
        }
    }

    val statusColor = when (order.status) {
        "APPROVED" -> Color(0xFF10B981)
        "REJECTED" -> Color(0xFFEF4444)
        "COMPLETED" -> Color(0xFF3B82F6)
        else -> Color(0xFFF59E0B) // PENDING
    }

    val statusText = when (order.status) {
        "APPROVED" -> "Onaylandı"
        "REJECTED" -> "Reddedildi"
        "COMPLETED" -> "Tamamlandı"
        else -> "Beklemede"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Sipariş: #${order.id.substringAfter("ord_")}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (isAdminMode || isReceivedMode) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isAdminMode) "Alıcı: ${order.buyerId} -> Satıcı: ${order.companyId}" else "Alıcı: ${order.buyerId}",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tutar: $${order.totalPrice}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        color = statusColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = statusText,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = statusColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Divider(color = Color.LightGray.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Ürünler", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (isLoadingItems) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp).align(Alignment.CenterHorizontally))
                    } else {
                        orderItems.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${item.productName} x${item.quantity}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.DarkGray
                                )
                                Text(
                                    text = "$${(item.price.toDoubleOrNull() ?: 0.0) * item.quantity}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    if (isReceivedMode && order.status == "PENDING") {
                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = Color.LightGray.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        val result = orderService.updateOrderStatus(order.id, "APPROVED")
                                        if (result.isSuccess) {
                                            AuditDomainService.logContentAction(
                                                userId = SessionManager.getUserId(),
                                                userRole = "MEMBER",
                                                targetId = order.id,
                                                module = "Order",
                                                action = ActionType.UPDATE
                                            )
                                            onStatusUpdated()
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                            ) {
                                Text("Onayla", fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            Button(
                                onClick = {
                                    scope.launch {
                                        val result = orderService.updateOrderStatus(order.id, "REJECTED")
                                        if (result.isSuccess) {
                                            AuditDomainService.logContentAction(
                                                userId = SessionManager.getUserId(),
                                                userRole = "MEMBER",
                                                targetId = order.id,
                                                module = "Order",
                                                action = ActionType.UPDATE
                                            )
                                            onStatusUpdated()
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                            ) {
                                Text("Reddet", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

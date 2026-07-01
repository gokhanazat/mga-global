package com.mgacreative.mgaglobal

import androidx.compose.material3.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mgacreative.mgaglobal.ui.navigation.Screen
import com.mgacreative.mgaglobal.ui.navigation.TradeBridgeNavGraph
import com.mgacreative.mgaglobal.ui.theme.TradeBridgeTheme
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import org.jetbrains.compose.resources.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import mgaglobal.composeapp.generated.resources.*
import com.mgacreative.mgaglobal.core.presentation.SnackbarManager
import com.mgacreative.mgaglobal.core.presentation.SnackbarEvent
import com.mgacreative.mgaglobal.core.presentation.AppSnackbarVisuals
import com.mgacreative.mgaglobal.core.auth.PermissionManager
import com.mgacreative.mgaglobal.ui.theme.DarkNavy
import com.mgacreative.mgaglobal.ui.theme.Background
import com.mgacreative.mgaglobal.manager.getCurrentAppLanguage
import com.mgacreative.mgaglobal.manager.changeAppLanguage
import com.mgacreative.mgaglobal.manager.syncPlatformLocale
import com.mgacreative.mgaglobal.ui.components.GlobalSidebar
import com.mgacreative.mgaglobal.ui.components.LanguageSwitcher
import androidx.compose.foundation.Image
import com.mgacreative.mgaglobal.openUrl
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.ShoppingCart
import com.mgacreative.mgaglobal.core.domain.showroom.CartManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(initialLanguage: String = "tr") {
    var currentLanguage by remember { mutableStateOf(initialLanguage) }
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val onMenuClick: () -> Unit = { scope.launch { drawerState.open() } }

    var companyQuery by remember { mutableStateOf("") }
    var sectorQuery by remember { mutableStateOf("") }

    val cartItems by CartManager.cartState.collectAsState()
    val cartItemCount = remember(cartItems) { cartItems.sumOf { it.quantity } }

    LaunchedEffect(Unit) {
        val saved = getCurrentAppLanguage() ?: initialLanguage
        if (saved != currentLanguage) {
            currentLanguage = saved
        }
    }

    // Platform seviyesindeki locale'i (WasmJs Navigator) senkronize tut
    LaunchedEffect(currentLanguage) {
        syncPlatformLocale(currentLanguage)
    }

    // Note: Snackbar collection is moved inside the key(currentLanguage) block to ensure correct locale

    // Dynamic Status Bar & Toolbar Management
    val isHome = currentDestination?.route == Screen.Home.route
    val isLogin = currentDestination?.route == Screen.Login.route
    val isHelpCenter = currentDestination?.route == Screen.HelpCenter.route
    val isCompanyMeeting = currentDestination?.route == Screen.CompanyMeeting.route
    val isCompanyProfile = currentDestination?.route?.startsWith("company_profile/") == true
    val isCompanySettings = currentDestination?.route == Screen.CompanySettings.route
    val isProductManagement = currentDestination?.route == Screen.ProductManagement.route
    val isEditProduct = currentDestination?.route?.startsWith("edit_product/") == true
    val isShowroom = currentDestination?.route?.startsWith("showroom?") == true
    val isMainShowroom = currentDestination?.route == Screen.MainDigitalShowroom.route
    val isProductDetail = currentDestination?.route?.startsWith("product_detail/") == true
    val isEconomicNews = currentDestination?.route?.startsWith("economic_news") == true
    val isCart = currentDestination?.route == Screen.Cart.route
    val isOrdersReport = currentDestination?.route == Screen.OrdersReport.route
    val userRole by PermissionManager.currentUserRole.collectAsState()
    
    // Dynamic Status Bar
    val statusBarColor = if (isHome || isLogin) Color.Transparent else DarkNavy
    val navigationBarColor = if (isHome || isLogin) Color.Transparent else Background
    
    SetStatusBarAndNavigationBarColor(
        statusBarColor = statusBarColor,
        navigationBarColor = navigationBarColor,
        darkIcons = false
    )

    val layoutDirection = if (currentLanguage == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr

    key(currentLanguage) {
        LaunchedEffect(currentLanguage) {
            println("App currentLanguage changed to: $currentLanguage (Initial: $initialLanguage)")
        }

        LaunchedEffect(currentLanguage) {
            SnackbarManager.events.collect { event ->
                val result = when(event) {
                    is SnackbarEvent.Message -> {
                        snackbarHostState.showSnackbar(
                            AppSnackbarVisuals(
                                message = getString(event.messageRes),
                                actionLabel = event.actionLabelRes?.let { getString(it) },
                                duration = event.duration,
                                isError = false
                            )
                        )
                    }
                    is SnackbarEvent.Error -> {
                        snackbarHostState.showSnackbar(
                            AppSnackbarVisuals(
                                message = getString(event.error.userMessage),
                                actionLabel = event.actionLabelRes?.let { getString(it) },
                                duration = SnackbarManager.getDurationForError(event.error),
                                isError = true
                            )
                        )
                    }
                }

                if (result == SnackbarResult.ActionPerformed) {
                    when (event) {
                        is SnackbarEvent.Message -> event.action?.invoke()
                        is SnackbarEvent.Error -> event.action?.invoke()
                    }
                }
            }
        }

        CompositionLocalProvider(
            LocalLayoutDirection provides layoutDirection
        ) {
            TradeBridgeTheme {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val screenWidth = maxWidth
                    val isWeb = screenWidth > 900.dp

                    val isAdminScreen = currentDestination?.route?.startsWith("admin_") == true || 
                                        currentDestination?.route == Screen.UserManagement.route || 
                                        currentDestination?.route == Screen.RegistryManagement.route || 
                                        currentDestination?.route == Screen.AuditLog.route ||
                                        currentDestination?.route == Screen.AdminDashboard.route
                    
                    val noGlobalSidebarScreens = isLogin || isAdminScreen

                    if (isWeb) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            if (!noGlobalSidebarScreens) {
                                GlobalSidebar(
                                    companyQuery = companyQuery,
                                    onCompanyQueryChange = { companyQuery = it },
                                    sectorQuery = sectorQuery,
                                    onSectorQueryChange = { sectorQuery = it },
                                    cartItemCount = cartItemCount,
                                    onNavItemClick = { item ->
                                        when(item) {
                                            "Consultancy", "Education" -> navController.navigate(Screen.Login.route)
                                            "Companies" -> navController.navigate(Screen.CompanyMeeting.route)
                                            "Sectors" -> navController.navigate(Screen.Home.route)
                                            "Cart" -> navController.navigate(Screen.Cart.route)
                                            else -> { /* Handle other items */ }
                                        }
                                    },
                                    onItsoWebsiteClick = { openUrl("https://www.iskenderuntso.org.tr/") }
                                )
                            }
                            
                            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                                AppScaffoldContent(
                                    navController = navController,
                                    currentDestination = currentDestination,
                                    snackbarHostState = snackbarHostState,
                                    companyQuery = companyQuery,
                                    sectorQuery = sectorQuery,
                                    isWeb = true,
                                    isHome = isHome,
                                    isLogin = isLogin,
                                    isHelpCenter = isHelpCenter,
                                    isCompanyMeeting = isCompanyMeeting,
                                    isCompanyProfile = isCompanyProfile,
                                    isCompanySettings = isCompanySettings,
                                    isProductManagement = isProductManagement,
                                    isEditProduct = isEditProduct,
                                    isShowroom = isShowroom,
                                    isMainShowroom = isMainShowroom,
                                    isProductDetail = isProductDetail,
                                    isEconomicNews = isEconomicNews,
                                    isAdminScreen = isAdminScreen,
                                    userRole = userRole,
                                    currentLanguage = currentLanguage,
                                    onLanguageChange = { currentLanguage = it },
                                    onMenuClick = onMenuClick
                                )
                            }
                        }
                    } else {
                        ModalNavigationDrawer(
                            drawerState = drawerState,
                            gesturesEnabled = !noGlobalSidebarScreens,
                            drawerContent = {
                                if (!noGlobalSidebarScreens) {
                                    ModalDrawerSheet(
                                        drawerContainerColor = DarkNavy,
                                        drawerTonalElevation = 0.dp
                                    ) {
                                        GlobalSidebar(
                                            companyQuery = companyQuery,
                                            onCompanyQueryChange = { companyQuery = it },
                                            sectorQuery = sectorQuery,
                                            onSectorQueryChange = { sectorQuery = it },
                                            cartItemCount = cartItemCount,
                                            onNavItemClick = { item ->
                                                scope.launch { drawerState.close() }
                                                when(item) {
                                                    "Consultancy", "Education" -> navController.navigate(Screen.Login.route)
                                                    "Companies" -> navController.navigate(Screen.CompanyMeeting.route)
                                                    "Sectors" -> navController.navigate(Screen.Home.route)
                                                    "Cart" -> navController.navigate(Screen.Cart.route)
                                                    else -> { /* Handle others */ }
                                                }
                                            },
                                            onItsoWebsiteClick = { 
                                                scope.launch { drawerState.close() }
                                                openUrl("https://www.iskenderuntso.org.tr/") 
                                            }
                                        )
                                    }
                                }
                            }
                        ) {
                            AppScaffoldContent(
                                navController = navController,
                                currentDestination = currentDestination,
                                snackbarHostState = snackbarHostState,
                                companyQuery = companyQuery,
                                sectorQuery = sectorQuery,
                                isWeb = false,
                                isHome = isHome,
                                isLogin = isLogin,
                                isHelpCenter = isHelpCenter,
                                isCompanyMeeting = isCompanyMeeting,
                                isCompanyProfile = isCompanyProfile,
                                isCompanySettings = isCompanySettings,
                                isProductManagement = isProductManagement,
                                isEditProduct = isEditProduct,
                                isShowroom = isShowroom,
                                isMainShowroom = isMainShowroom,
                                isProductDetail = isProductDetail,
                                isEconomicNews = isEconomicNews,
                                isAdminScreen = isAdminScreen,
                                userRole = userRole,
                                currentLanguage = currentLanguage,
                                onLanguageChange = { currentLanguage = it },
                                onMenuClick = { scope.launch { drawerState.open() } }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffoldContent(
    navController: androidx.navigation.NavHostController,
    currentDestination: androidx.navigation.NavDestination?,
    snackbarHostState: SnackbarHostState,
    companyQuery: String,
    sectorQuery: String,
    isWeb: Boolean,
    isHome: Boolean,
    isLogin: Boolean,
    isHelpCenter: Boolean,
    isCompanyMeeting: Boolean,
    isCompanyProfile: Boolean,
    isCompanySettings: Boolean,
    isProductManagement: Boolean,
    isEditProduct: Boolean,
    isShowroom: Boolean,
    isMainShowroom: Boolean,
    isProductDetail: Boolean,
    isEconomicNews: Boolean,
    isAdminScreen: Boolean,
    userRole: com.mgacreative.mgaglobal.core.auth.Role?,
    currentLanguage: String,
    onLanguageChange: (String) -> Unit,
    onMenuClick: (() -> Unit)? = null
) {
    val scope = rememberCoroutineScope()
    val isCart = currentDestination?.route == Screen.Cart.route
    val isOrdersReport = currentDestination?.route == Screen.OrdersReport.route
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            val noGlobalTopBarScreens = isLogin || isAdminScreen || isShowroom || isMainShowroom || isProductDetail || isCart || isOrdersReport

            if (!noGlobalTopBarScreens || isWeb || isHome) {
                CenterAlignedTopAppBar(
                    title = { 
                        if (!isHome) {
                            Text(
                                stringResource(currentDestination?.labelResId() ?: Res.string.app_name),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        } else if (!isWeb) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(Res.drawable.itso_global_logo),
                                    contentDescription = "Logo",
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("GLOBAL TRADE", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    },
                    navigationIcon = {
                        if (!isWeb && onMenuClick != null && isHome) {
                            IconButton(onClick = onMenuClick) {
                                Icon(Icons.Default.Menu, null)
                            }
                        } else if (navController.previousBackStackEntry != null) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.back))
                            }
                        }
                    },
                    actions = {
                        if (!isLogin && !isAdminScreen) {
                            if (isWeb && isHome) {
                                LanguageSwitcher(
                                    currentLanguage = currentLanguage,
                                    onLanguageChange = { lang ->
                                        scope.launch {
                                            changeAppLanguage(lang)
                                            onLanguageChange(lang)
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                            }
                            
                            val cartItemsCountFlow by CartManager.cartState.collectAsState()
                            IconButton(onClick = { navController.navigate(Screen.Cart.route) }) {
                                BadgedBox(
                                    badge = {
                                        if (cartItemsCountFlow.isNotEmpty()) {
                                            Badge(
                                                containerColor = Color.Red,
                                                contentColor = Color.White
                                            ) {
                                                Text(cartItemsCountFlow.sumOf { it.quantity }.toString(), fontSize = 10.sp)
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = "Sepetim",
                                        tint = Color.White
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            if (userRole != null) {
                                IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                                    Icon(Icons.Default.Person, contentDescription = "Profile")
                                }
                            } else {
                                IconButton(onClick = { navController.navigate(Screen.Login.route) }) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Login",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = DarkNavy,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            }
        },
        bottomBar = {
            val noBottomBarScreens = isLogin || isCompanyMeeting || 
                                   isCompanyProfile || isCompanySettings || 
                                   isProductManagement || isEditProduct || 
                                   isShowroom || isMainShowroom || 
                                   isProductDetail || isEconomicNews ||
                                   isCart || isOrdersReport
            
            // Web'de sidebar olduğu için bottom bar'ı sadece mobilde gösteriyoruz
            if (!isWeb && !noBottomBarScreens && userRole != null) {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.primary,
                    tonalElevation = 8.dp
                ) {
                    Screen.items.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = stringResource(screen.title)) },
                            label = null,
                            alwaysShowLabel = false,
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().route ?: "") {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        TradeBridgeNavGraph(
            navController = navController, 
            paddingValues = paddingValues,
            onMenuClick = onMenuClick ?: {},
            companyQuery = companyQuery,
            sectorQuery = sectorQuery
        )
    }
}

@Composable
fun androidx.navigation.NavDestination.labelResId(): StringResource {
    return when (route) {
        Screen.Showroom.route -> Res.string.nav_showroom
        Screen.B2BMatch.route -> Res.string.nav_b2b
        Screen.Marketplace.route -> Res.string.nav_marketplace
        Screen.Education.route -> Res.string.nav_education
        Screen.Profile.route -> Res.string.nav_profile
        else -> Res.string.app_name
    }
}




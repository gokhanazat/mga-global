package com.mgacreative.globaltrade.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mgacreative.globaltrade.ui.auth.LoginScreen
import com.mgacreative.globaltrade.ui.showroom.MainDigitalShowroomScreen
import com.mgacreative.globaltrade.ui.showroom.ShowroomScreen
import com.mgacreative.globaltrade.ui.showroom.ProductDetailScreen
import com.mgacreative.globaltrade.ui.b2b.B2BMatchScreen
import com.mgacreative.globaltrade.ui.home.HomeDashboardScreen
import com.mgacreative.globaltrade.ui.home.MainHomeScreen
import com.mgacreative.globaltrade.ui.marketplace.MarketplaceScreen
import com.mgacreative.globaltrade.ui.education.EducationScreen
import com.mgacreative.globaltrade.ui.education.EducationDetailScreen
import com.mgacreative.globaltrade.ui.admin.AdminDashboardScreen
import com.mgacreative.globaltrade.ui.admin.AdminSectorsScreen
import com.mgacreative.globaltrade.ui.admin.AuditLogScreen
import com.mgacreative.globaltrade.ui.admin.RegistryManagementScreen
import com.mgacreative.globaltrade.ui.admin.UserManagementScreen
import com.mgacreative.globaltrade.ui.admin.AdminAnnouncementScreen
import com.mgacreative.globaltrade.ui.admin.education.AdminEducationScreen
import com.mgacreative.globaltrade.ui.settings.NotificationSettingsScreen
import com.mgacreative.globaltrade.ui.screens.CompanySettingsScreen
import com.mgacreative.globaltrade.ui.screens.HelpCenterScreen
import com.mgacreative.globaltrade.ui.screens.ProductManagementScreen
import com.mgacreative.globaltrade.ui.screens.ProfileScreen
import com.mgacreative.globaltrade.ui.screens.SecuritySettingsScreen
import com.mgacreative.globaltrade.core.auth.Permission
import com.mgacreative.globaltrade.core.auth.PermissionManager
import com.mgacreative.globaltrade.core.auth.SessionManager
import com.mgacreative.globaltrade.core.auth.Role
import com.mgacreative.globaltrade.ui.news.EconomicNewsScreen
import com.mgacreative.globaltrade.ui.meeting.CompanyMeetingScreen
import com.mgacreative.globaltrade.ui.b2b.CompanyProfileScreen
import com.mgacreative.globaltrade.ui.education.ExamWebView
import com.mgacreative.globaltrade.ui.education.CertificateScreen
import com.mgacreative.globaltrade.ui.education.Education
import com.mgacreative.globaltrade.ui.admin.AdminConsultantScreen
import com.mgacreative.globaltrade.ui.admin.AdminHelpCenterScreen
import com.mgacreative.globaltrade.ui.consultancy.ConsultantScreen
import com.mgacreative.globaltrade.core.domain.education.EducationService
import com.mgacreative.globaltrade.core.domain.audit.AuditDomainService
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment

@Composable
fun TradeBridgeNavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues,
    onMenuClick: () -> Unit,
    companyQuery: String,
    sectorQuery: String
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    val targetRoute = if (PermissionManager.currentUserRole.value == Role.ADMIN) {
                        Screen.AdminDashboard.route
                    } else {
                        Screen.Home.route
                    }
                    navController.navigate(targetRoute) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.NotificationSettings.route) {
            NotificationSettingsScreen(paddingValues = paddingValues, onBack = { navController.popBackStack() })
        }
        composable(Screen.Home.route) {
            val userRole by PermissionManager.currentUserRole.collectAsState()
            
            if (userRole == null) {
                MainHomeScreen(
                    paddingValues = paddingValues,
                    companyQuery = companyQuery,
                    sectorQuery = sectorQuery,
                    onModuleClick = { module, category, ownerId ->
                        when (module) {
                            "Login" -> navController.navigate(Screen.Login.route)
                            "Showroom", "MainDigitalShowroom", "Products" -> {
                                if (category != null || ownerId != null) {
                                    val route = buildString {
                                         append("showroom?")
                                         if (category != null) append("category=$category")
                                         if (category != null && ownerId != null) append("&")
                                         if (ownerId != null) append("ownerId=$ownerId")
                                    }
                                    navController.navigate(route)
                                } else {
                                    navController.navigate(Screen.MainDigitalShowroom.route)
                                }
                            }
                            "EconomicNews" -> {
                                navController.navigate(Screen.EconomicNews.route)
                            }
                            "Appointments", "CompanyMeeting" -> {
                                navController.navigate(Screen.CompanyMeeting.route)
                            }
                            "Consultancy" -> navController.navigate(Screen.Consultancy.route)
                            "CompanyProfile" -> {
                                if (ownerId != null) {
                                    navController.navigate(Screen.CompanyProfile.route.replace("{companyName}", ownerId))
                                }
                            }
                        }
                    },
                    onProfileClick = {
                        navController.navigate(Screen.Login.route)
                    }
                )
            } else if (userRole == Role.ADMIN) {
                navController.navigate(Screen.AdminDashboard.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            } else {
                HomeDashboardScreen(
                    paddingValues = paddingValues,
                    companyQuery = companyQuery,
                    sectorQuery = sectorQuery,
                    onModuleClick = { module, category, ownerId ->
                        when (module) {
                            "Showroom" -> {
                                if (category != null || ownerId != null) {
                                    val route = buildString {
                                         append("showroom?")
                                         if (category != null) append("category=$category")
                                         if (category != null && ownerId != null) append("&")
                                         if (ownerId != null) append("ownerId=$ownerId")
                                    }
                                    navController.navigate(route)
                                } else {
                                    navController.navigate(Screen.MainDigitalShowroom.route)
                                }
                            }
                            "B2B" -> navController.navigate(Screen.B2BMatch.route)
                            "Marketplace" -> navController.navigate(Screen.Marketplace.route)
                            "Education" -> navController.navigate(Screen.Education.route)
                            "EconomicNews" -> navController.navigate(Screen.EconomicNews.route)
                            "Appointments", "CompanyMeeting" -> navController.navigate(Screen.CompanyMeeting.route)
                            "Consultancy" -> navController.navigate(Screen.Consultancy.route)
                            "CompanyProfile" -> {
                                if (ownerId != null) {
                                    navController.navigate(Screen.CompanyProfile.route.replace("{companyName}", ownerId))
                                }
                            }
                        }
                    },
                    onLogout = {
                        val userId = SessionManager.getUserId()
                        val currentRole = PermissionManager.currentUserRole.value?.name ?: "UNKNOWN"
                        AuditDomainService.logLoginAction(userId, currentRole, false)
                        PermissionManager.updateRole(null)
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0)
                        }
                    }
                )
            }
        }
        
        composable(
            route = Screen.Showroom.route,
            arguments = listOf(
                androidx.navigation.navArgument("category") { 
                    type = androidx.navigation.NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                androidx.navigation.navArgument("ownerId") { 
                    type = androidx.navigation.NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category")
            val ownerId = backStackEntry.arguments?.getString("ownerId")
            ShowroomScreen(
                paddingValues = paddingValues,
                onMenuClick = onMenuClick,
                initialCategory = category,
                initialOwnerId = ownerId,
                onBackClick = { navController.popBackStack() },
                onProductClick = { name -> 
                    val encodedName = name.replace(" ", "%20")
                    navController.navigate("product_detail/$encodedName")
                },
                onEditClick = { id ->
                    navController.navigate("edit_product/$id")
                },
                onCompanyClick = { companyId ->
                    navController.navigate(Screen.CompanyProfile.route.replace("{companyName}", companyId))
                }
            )
        }
        composable(Screen.B2BMatch.route) {
            B2BMatchScreen(paddingValues = paddingValues)
        }
        composable(Screen.Marketplace.route) {
            MarketplaceScreen(paddingValues = paddingValues)
        }
        composable(Screen.Education.route) {
            EducationScreen(
                paddingValues = paddingValues,
                onEducationClick = { eduId ->
                    navController.navigate("education_detail/$eduId")
                },
                onExamClick = { eduId ->
                    navController.navigate(Screen.EducationExam.route.replace("{eduId}", eduId))
                }
            )
        }
        composable(Screen.EducationDetail.route) { backStackEntry ->
            val eduId = backStackEntry.arguments?.getString("eduId") ?: ""
            EducationDetailScreen(
                eduId = eduId,
                onBackClick = { navController.popBackStack() },
                onStartExam = { 
                    navController.navigate(Screen.EducationExam.route.replace("{eduId}", eduId))
                }
            )
        }
        
        composable(Screen.EducationExam.route) { backStackEntry ->
            val eduId = backStackEntry.arguments?.getString("eduId") ?: ""
            val educationService = remember { EducationService() }
            var education by remember { mutableStateOf<Education?>(null) }
            
            LaunchedEffect(eduId) {
                education = educationService.getEducationById(eduId).getOrNull()
            }
            
            if (education != null) {
                ExamWebView(
                    url = education!!.examLink,
                    onCertificateRequested = { returnedEduId ->
                        navController.navigate(Screen.EducationCertificate.route.replace("{eduId}", returnedEduId)) {
                            popUpTo(Screen.EducationExam.route) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            } else {
                Box(androidx.compose.ui.Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
        
        composable(Screen.EducationCertificate.route) { backStackEntry ->
            val eduId = backStackEntry.arguments?.getString("eduId") ?: ""
            CertificateScreen(
                eduId = eduId,
                onBack = { navController.popBackStack() },
                onNavigateToShowroom = {
                    navController.navigate(Screen.MainDigitalShowroom.route) {
                        popUpTo(Screen.Education.route)
                    }
                }
            )
        }


        composable(Screen.Profile.route) {
            ProfileScreen(
                paddingValues = paddingValues,
                onCompanySettingsClick = { navController.navigate(Screen.CompanySettings.route) },
                onSecurityClick = { navController.navigate(Screen.SecuritySettings.route) },
                onHelpCenterClick = { navController.navigate(Screen.HelpCenter.route) },
                onNotificationSettingsClick = { navController.navigate(Screen.NotificationSettings.route) },
                onProductsClick = { 
                    navController.navigate(Screen.ProductManagement.route)
                }
            )
        }
        composable(Screen.ProductDetail.route) { backStackEntry ->
            val productName = backStackEntry.arguments?.getString("productName") ?: "Product"
            ProductDetailScreen(
                productName = productName,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.CompanySettings.route) {
            CompanySettingsScreen(paddingValues = paddingValues, onBackClick = { navController.popBackStack() })
        }
        composable(Screen.SecuritySettings.route) {
            SecuritySettingsScreen(paddingValues = paddingValues, onBackClick = { navController.popBackStack() })
        }
        composable(Screen.HelpCenter.route) {
            HelpCenterScreen(onBackClick = { navController.popBackStack() })
        }
        composable(Screen.ProductManagement.route) {
            ProductManagementScreen(onBackClick = { navController.popBackStack() })
        }
        composable(Screen.EditProduct.route) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductManagementScreen(
                paddingValues = paddingValues,
                productId = productId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                paddingValues = paddingValues,
                onNavigateToUsers = { navController.navigate(Screen.UserManagement.route) },
                onNavigateToRegistry = { navController.navigate(Screen.RegistryManagement.route) },
                onNavigateToAuditLog = { navController.navigate(Screen.AuditLog.route) },
                onNavigateToEducations = { navController.navigate(Screen.AdminEducation.route) },
                onNavigateToSectors = { navController.navigate(Screen.AdminSectors.route) },
                onNavigateToAnnouncements = { navController.navigate(Screen.AdminAnnouncement.route) },
                onNavigateToConsultancy = { navController.navigate(Screen.AdminConsultancy.route) },
                onNavigateToHelpCenter = { navController.navigate(Screen.AdminHelpCenter.route) },
                onLogout = {
                    val userId = SessionManager.getUserId()
                    val currentRole = PermissionManager.currentUserRole.value?.name ?: "ADMIN"
                    AuditDomainService.logLoginAction(userId, currentRole, false)
                    PermissionManager.updateRole(null)
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.AdminDashboard.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.AuditLog.route) {
            AuditLogScreen(onBackClick = { navController.popBackStack() })
        }
        composable(Screen.UserManagement.route) {
            UserManagementScreen(
                paddingValues = paddingValues,
                onBackClick = { navController.popBackStack() },
                onRegistryClick = { navController.navigate(Screen.RegistryManagement.route) }
            )
        }
        composable(Screen.RegistryManagement.route) {
            RegistryManagementScreen(paddingValues = paddingValues, onBackClick = { navController.popBackStack() })
        }
        composable(Screen.AdminEducation.route) {
            AdminEducationScreen(paddingValues = paddingValues, onBackClick = { navController.popBackStack() })
        }
        composable(Screen.AdminSectors.route) {
            AdminSectorsScreen(paddingValues = paddingValues, onBackClick = { navController.popBackStack() })
        }
        composable(Screen.AdminAnnouncement.route) {
            AdminAnnouncementScreen(paddingValues = paddingValues, onBackClick = { navController.popBackStack() })
        }
        composable(Screen.AdminConsultancy.route) {
            AdminConsultantScreen(paddingValues = paddingValues, onBackClick = { navController.popBackStack() })
        }
        composable(Screen.AdminHelpCenter.route) {
            AdminHelpCenterScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Consultancy.route) {
            ConsultantScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.EconomicNews.route) {
            EconomicNewsScreen(
                paddingValues = paddingValues,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.CompanyMeeting.route) {
            CompanyMeetingScreen(
                onBack = { navController.popBackStack() },
                onCardClick = { companyId ->
                    navController.navigate(Screen.CompanyProfile.route.replace("{companyName}", companyId))
                }
            )
        }
        composable(Screen.CompanyProfile.route) { backStackEntry ->
            val companyId = backStackEntry.arguments?.getString("companyName") ?: ""
            CompanyProfileScreen(
                companyId = companyId,
                onBack = { navController.popBackStack() },
                onNavigateToShowroom = { id ->
                    val route = "showroom?category=&ownerId=$id"
                    navController.navigate(route)
                }
            )
        }
        composable(Screen.MainDigitalShowroom.route) {
            MainDigitalShowroomScreen(
                onBackClick = { navController.popBackStack() },
                onProductClick = { name ->
                    val encodedName = name.replace(" ", "%20")
                    navController.navigate("product_detail/$encodedName")
                },
                onCompanyClick = { companyId ->
                    navController.navigate(Screen.CompanyProfile.route.replace("{companyName}", companyId))
                }
            )
        }
    }
}

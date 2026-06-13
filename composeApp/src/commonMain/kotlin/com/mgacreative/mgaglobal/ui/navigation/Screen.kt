package com.mgacreative.mgaglobal.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource
import mgaglobal.composeapp.generated.resources.*

import com.mgacreative.mgaglobal.core.auth.Permission

sealed class Screen(
    val route: String, 
    val title: StringResource, 
    val icon: ImageVector,
    val requiredPermission: Permission? = null
) {
    object Login : Screen("login", Res.string.action_login, Icons.Default.Login)
    object Home : Screen("home", Res.string.nav_home, Icons.Default.Home)
    object Showroom : Screen("showroom?category={category}&ownerId={ownerId}", Res.string.nav_showroom, Icons.Default.Business)
    object B2BMatch : Screen("b2b_match", Res.string.nav_b2b, Icons.Default.Handshake, Permission.B2B_VIEW)
    object Marketplace : Screen("marketplace", Res.string.nav_marketplace, Icons.Default.Storefront, Permission.MARKETPLACE_VIEW)
    object Education : Screen("education", Res.string.nav_education, Icons.Default.School, Permission.TRAINING_VIEW)
    object Profile : Screen("profile", Res.string.nav_profile, Icons.Default.Person)
    object ProductDetail : Screen("product_detail/{productName}", Res.string.nav_product_detail, Icons.Default.Business)
    object EducationDetail : Screen("education_detail/{eduId}", Res.string.nav_education_detail, Icons.Default.School, Permission.TRAINING_VIEW)
    object CompanyProfile : Screen("company_profile/{companyName}", Res.string.nav_company_profile, Icons.Default.Business)
    object MarketplaceConnect : Screen("marketplace_connect/{platformName}", Res.string.nav_marketplace_connect, Icons.Default.Hub, Permission.MARKETPLACE_MANAGE)
    object NotificationSettings : Screen("notification_settings", Res.string.nav_profile, Icons.Default.Notifications)
    object CompanySettings : Screen("company_settings", Res.string.company_info, Icons.Default.Business)
    object SecuritySettings : Screen("security_settings", Res.string.security_password, Icons.Default.Lock)
    object HelpCenter : Screen("help_center", Res.string.help_center, Icons.Default.Help)
    
    object ProductManagement : Screen("product_management", Res.string.nav_showroom, Icons.Default.Category)
    object EditProduct : Screen("edit_product/{productId}", Res.string.nav_showroom, Icons.Default.Edit)
    object EconomicNews : Screen("economic_news", Res.string.economic_news, Icons.Default.Article)
    object CompanyMeeting : Screen("company_meeting", Res.string.nav_home, Icons.Default.Groups)
    object MainDigitalShowroom : Screen("main_digital_showroom", Res.string.nav_showroom, Icons.Default.Collections)
    
    // Updated Consultancy name to match current nav structure
    object Consultancy : Screen("consultancy", Res.string.nav_appointments, Icons.Default.SupportAgent)
    
    // New Education related screens
    object EducationExam : Screen("education_exam/{eduId}", Res.string.nav_education, Icons.Default.Quiz)
    object EducationCertificate : Screen("education_certificate/{eduId}", Res.string.nav_education, Icons.Default.Badge)

    // Admin Screens
    object AdminDashboard : Screen("admin_dashboard", Res.string.app_name, Icons.Default.AdminPanelSettings, Permission.ROLE_ASSIGN)
    object AuditLog : Screen("audit_log", Res.string.app_name, Icons.Default.History, Permission.AUDIT_LOG_VIEW)
    object UserManagement : Screen("user_management", Res.string.app_name, Icons.Default.People, Permission.ROLE_ASSIGN)
    object RegistryManagement : Screen("registry_management", Res.string.app_name, Icons.Default.AppRegistration, Permission.ROLE_ASSIGN)
    object AdminEducation : Screen("admin_education", Res.string.nav_home, Icons.Default.School, Permission.ROLE_ASSIGN)
    object AdminSectors : Screen("admin_sectors", Res.string.nav_home, Icons.Default.Category)
    object AdminAnnouncement : Screen("admin_announcement", Res.string.platform_announcements, Icons.Default.Campaign, Permission.ROLE_ASSIGN)
    object AdminConsultancy : Screen("admin_consultancy", Res.string.nav_appointments, Icons.Default.SupportAgent, Permission.ROLE_ASSIGN)
    object AdminHelpCenter : Screen("admin_help_center", Res.string.help_center, Icons.Default.LiveHelp, Permission.ROLE_ASSIGN)

    companion object {
        // Safe list of screens for Bottom Navigation
        val items: List<Screen> get() = listOf(Home, MainDigitalShowroom, B2BMatch, Marketplace, Education, Consultancy, Profile)
        val authScreens get() = listOf(Login)
    }
}



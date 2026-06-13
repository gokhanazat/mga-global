package com.mgacreative.mgaglobal.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mgacreative.mgaglobal.core.auth.PermissionManager
import com.mgacreative.mgaglobal.core.auth.Role
import com.mgacreative.mgaglobal.core.auth.SessionManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import mgaglobal.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeDashboardScreen(
    paddingValues: PaddingValues = PaddingValues(0.dp),
    companyQuery: String = "",
    sectorQuery: String = "",
    onModuleClick: (String, String?, String?) -> Unit = { _, _, _ -> },
    onLogout: () -> Unit = {},
    onPremiumPreviewToggle: (Boolean) -> Unit = {}
) {
    val companyService = remember { com.mgacreative.mgaglobal.core.domain.b2b.CompanyService() }
    var companyName by remember { mutableStateOf<String?>(null) }
    var showPremiumHome by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(showPremiumHome) {
        onPremiumPreviewToggle(showPremiumHome)
    }
    
    LaunchedEffect(Unit) {
        val result = companyService.getOwnCompany()
        companyName = result.getOrNull()?.name
    }

    if (showPremiumHome) {
        MainHomeScreen(
            paddingValues = paddingValues,
            companyQuery = companyQuery,
            sectorQuery = sectorQuery,
            onModuleClick = { module, category, ownerId -> 
                showPremiumHome = false
                onModuleClick(module, category, ownerId)
            },
            onProfileClick = { 
                showPremiumHome = false
            },
            onNotificationsClick = { 
                showPremiumHome = false
            }
        )
        Box(modifier = Modifier.fillMaxSize()) {
            IconButton(
                onClick = { showPremiumHome = false },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 48.dp, end = 16.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.Default.Close, "Kapat", tint = Color.White)
            }
        }
    } else {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            val screenWidth = maxWidth
            val columns = when {
                screenWidth < 600.dp -> 1
                screenWidth < 1100.dp -> 2
                else -> 3
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item(span = { GridItemSpan(columns) }) {
                    WelcomeSection(companyName = companyName ?: stringResource(Res.string.welcome_member))
                }

                    item(span = { GridItemSpan(columns) }) {
                        Text(
                            text = stringResource(Res.string.platform_modules),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                    }
                    
                    if (PermissionManager.currentUserRole.value == Role.ADMIN) {
                        item {
                            ModuleCard(
                                title = stringResource(Res.string.admin_panel_title),
                                description = stringResource(Res.string.admin_panel_desc),
                                icon = Icons.Default.AdminPanelSettings,
                                onClick = { onModuleClick("Admin", null, null) }
                            )
                        }
                    }

                    item {
                        val currentUserId = SessionManager.getUserId()
                        ModuleCard(
                            title = stringResource(Res.string.nav_showroom),
                            description = stringResource(Res.string.showroom_desc),
                            icon = Icons.Default.Storefront,
                            onClick = { onModuleClick("Showroom", null, currentUserId) }
                        )
                    }
                    item {
                        ModuleCard(
                            title = stringResource(Res.string.nav_education),
                            description = stringResource(Res.string.nav_education_desc),
                            icon = Icons.Default.School,
                            onClick = { onModuleClick("Education", null, null) }
                        )
                    }
                    item {
                        ModuleCard(
                            title = stringResource(Res.string.nav_b2b),
                            description = stringResource(Res.string.b2b_center_desc),
                            icon = Icons.Default.Groups,
                            onClick = { onModuleClick("B2B", null, null) }
                        )
                    }
                    item {
                        ModuleCard(
                            title = stringResource(Res.string.nav_marketplace),
                            description = stringResource(Res.string.marketplace_desc),
                            icon = Icons.Default.Hub,
                            onClick = { onModuleClick("Marketplace", null, null) }
                        )
                    }

                    item {
                        ModuleCard(
                            title = stringResource(Res.string.nav_appointments),
                            description = stringResource(Res.string.appointments_desc),
                            icon = Icons.Default.SupportAgent,
                            onClick = { onModuleClick("Consultancy", null, null) }
                        )
                    }
                }
            }
        }
    }

@Composable
fun WelcomeSection(companyName: String) {
    Column {
        Text(
            text = if (companyName.isNotBlank() && companyName != stringResource(Res.string.welcome_member)) 
                "${stringResource(Res.string.welcome)}, $companyName" 
                else stringResource(Res.string.welcome),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = stringResource(Res.string.export_management_dashboard),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun ModuleCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    lineHeight = 16.sp
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}



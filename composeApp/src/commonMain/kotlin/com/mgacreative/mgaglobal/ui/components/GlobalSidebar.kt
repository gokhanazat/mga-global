package com.mgacreative.mgaglobal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mgacreative.mgaglobal.ui.theme.*
import mgaglobal.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import androidx.compose.foundation.Image

@Composable
fun GlobalSidebar(
    modifier: Modifier = Modifier,
    companyQuery: String,
    onCompanyQueryChange: (String) -> Unit,
    sectorQuery: String,
    onSectorQueryChange: (String) -> Unit,
    cartItemCount: Int = 0,
    onNavItemClick: (String) -> Unit,
    onItsoWebsiteClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 32.dp)
            .verticalScroll(scrollState)
    ) {
        // Logo Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.itso_global_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(44.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "GLOBAL TRADE",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "THE SOVEREIGN LEDGER",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }
        }

        // Navigation Items
        SidebarNavItem(
            title = "FİRMALAR",
            icon = Icons.Default.Business,
            hasSearch = true,
            searchValue = companyQuery,
            onSearchValueChange = onCompanyQueryChange,
            onClick = { onNavItemClick("Companies") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SidebarNavItem(
            title = "SEKTÖRLER",
            icon = Icons.Default.Dashboard,
            hasSearch = true,
            searchValue = sectorQuery,
            onSearchValueChange = onSectorQueryChange,
            onClick = { onNavItemClick("Sectors") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SidebarNavItem(
            title = "DANIMANLIK",
            icon = Icons.Default.SupportAgent,
            onClick = { onNavItemClick("Consultancy") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SidebarNavItem(
            title = "EİTİMLER",
            icon = Icons.Default.School,
            onClick = { onNavItemClick("Education") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SidebarNavItem(
            title = "SEPETİM",
            icon = Icons.Default.ShoppingCart,
            badgeCount = cartItemCount,
            onClick = { onNavItemClick("Cart") }
        )

        // İTSO Link removed as requested

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Branding
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = RoundedCornerShape(8.dp),
                color = PrimaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("IT", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Admin", color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("GLOBAL PORTAL", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun SidebarNavItem(
    title: String,
    icon: ImageVector,
    hasSearch: Boolean = false,
    searchValue: String = "",
    onSearchValueChange: (String) -> Unit = {},
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { onClick() }
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.weight(1f)
            )
            if (badgeCount > 0) {
                Badge(
                    containerColor = Color.Red,
                    contentColor = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(badgeCount.toString(), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (hasSearch) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth()
                    .height(36.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Search,
                        null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (searchValue.isEmpty()) {
                            Text(
                                "Ara...",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                fontSize = 11.sp
                            )
                        }
                        BasicTextField(
                            value = searchValue,
                            onValueChange = onSearchValueChange,
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}



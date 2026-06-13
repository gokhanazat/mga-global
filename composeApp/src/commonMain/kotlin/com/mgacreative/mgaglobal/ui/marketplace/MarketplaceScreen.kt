package com.mgacreative.mgaglobal.ui.marketplace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import mgaglobal.composeapp.generated.resources.*

// —— Design Tokens ——————————————————————————————————————————————
private val Navy        = Color(0xFF1B263B)
private val NavyLight   = Color(0xFF415A77)
private val PageBg      = Color(0xFFF1F3F5)
private val CardBg      = Color.White
private val TextPrimary = Color(0xFF212529)
private val TextSecondary = Color(0xFF495057)

data class MarketplacePortal(
    val name: String,
    val url: String,
    val logoColor: Color
)

private val marketplaceList = listOf(
    MarketplacePortal("Amazon", "https://sellercentral.amazon.com/", Color(0xFFFF9900)),
    MarketplacePortal("Ebay", "https://sellerhub.ebay.com/", Color(0xFFE53238)),
    MarketplacePortal("AliExpress", "https://seller.aliexpress.com/", Color(0xFFFF4747)),
    MarketplacePortal("Walmart Marketplace", "https://seller.walmart.com/", Color(0xFF0071CE)),
    MarketplacePortal("Trendyol", "https://partner.trendyol.com/", Color(0xFFF27A1A)),
    MarketplacePortal("Hepsi Burada", "https://merchant.hepsiburada.com/", Color(0xFFFF6000)),
    MarketplacePortal("n11", "https://so.n11.com/", Color(0xFF211E55)),
    MarketplacePortal("Pazarama", "https://satici.pazarama.com/", Color(0xFFF03C7A))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    paddingValues: PaddingValues,
    onConnectClick: (String) -> Unit = {}
) {
    val uriHandler = LocalUriHandler.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(PageBg)
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
                Text(
                    "Mevcut Platformlar",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6C757D),
                    letterSpacing = 1.2.sp
                )
            }

            items(marketplaceList) { portal ->
                PortalCard(
                    portal = portal,
                    onOpenPortal = {
                        try {
                            uriHandler.openUri(portal.url)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                )
            }

            item(span = { GridItemSpan(columns) }) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun PortalCard(
    portal: MarketplacePortal,
    onOpenPortal: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Logo circle
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(portal.logoColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = portal.name.take(1),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = portal.logoColor
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = portal.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Satıcı Paneli",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            Button(
                onClick = onOpenPortal,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Navy),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text("Bağlan", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}



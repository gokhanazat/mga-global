package com.mgacreative.mgaglobal.ui.b2b

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Navy        = Color(0xFF1B263B)
private val PageBg      = Color(0xFFF1F3F5)
private val CardBg      = Color.White
private val TextPrimary = Color(0xFF212529)
private val TextSecondary = Color(0xFF495057)

data class B2BPortal(val name: String, val url: String, val logoColor: Color)

private val b2bList = listOf(
    B2BPortal("Amazon Business", "https://business.amazon.com/", Color(0xFF0071CE)),
    B2BPortal("Global Sources", "https://www.globalsources.com/", Color(0xFFE50012)),
    B2BPortal("Thomas Net", "https://www.thomasnet.com/", Color(0xFF005A9C)),
    B2BPortal("Europages", "https://www.europages.com/", Color(0xFF0033A0)),
    B2BPortal("Made in China", "https://www.made-in-china.com/", Color(0xFFE50012)),
    B2BPortal("Turkish Exporter", "https://www.turkishexporter.net/", Color(0xFFE30A17)),
    B2BPortal("Trade Atlas", "https://www.tradeatlas.com/", Color(0xFF0055A4))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun B2BMatchScreen(paddingValues: PaddingValues) {
    val uriHandler = LocalUriHandler.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(Color(0xFF0F172A)),
        contentAlignment = Alignment.TopCenter
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .widthIn(max = 1200.dp)
                .fillMaxHeight()
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
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(b2bList) { portal ->
                    B2BPortalCard(portal = portal, onOpenPortal = { uriHandler.openUri(portal.url) })
                }
            }
        }
    }
}

@Composable
fun B2BPortalCard(portal: B2BPortal, onOpenPortal: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).background(portal.logoColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = portal.name.take(1), fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = portal.logoColor)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = portal.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = "B2B Portalı", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Button(onClick = onOpenPortal, shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = Navy)) {
                Text("Bağlan", fontSize = 13.sp)
            }
        }
    }
}


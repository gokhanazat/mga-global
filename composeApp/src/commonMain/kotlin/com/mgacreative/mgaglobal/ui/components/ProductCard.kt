package com.mgacreative.mgaglobal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import mgaglobal.composeapp.generated.resources.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import com.mgacreative.mgaglobal.core.network.ApiConfig

@Composable
fun ProductCard(
    productName: String,
    companyName: String,
    country: String,
    isPremium: Boolean,
    price: String? = null,
    description: String? = null,
    imageUrl: String? = null,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onSelectionChange: ((Boolean) -> Unit)? = null,
    onCompanyClick: (() -> Unit)? = null,
    onClickDetail: () -> Unit = {},
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null
) {
    var showMenu by remember { mutableStateOf(false) }
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClickDetail,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(Color.LightGray.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                if (!imageUrl.isNullOrBlank()) {
                    val rawUrl = imageUrl ?: ""
                    val model: Any? = if (rawUrl.startsWith("data:image/")) {
                        try {
                            val base64String = rawUrl.substringAfter("base64,").trim()
                            @OptIn(ExperimentalEncodingApi::class)
                            Base64.Default.decode(base64String)
                        } catch (e: Exception) { null }
                    } else {
                        val cleaned = rawUrl.trim().filter { it.code in 33..1000 }
                        if (cleaned.isNotBlank() && cleaned != "null") {
                            if (cleaned.startsWith("http")) cleaned else ApiConfig.getImageUrl(cleaned)
                        } else null
                    }

                    if (model != null) {
                        coil3.compose.AsyncImage(
                            model = model,
                            contentDescription = "Ürün Resmi",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Default.BrokenImage, null, tint = Color.Gray)
                    }
                } else {
                    Text(
                        text = stringResource(Res.string.product_image),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
                
                // Overlay Badges (Premium, Menu, etc.)
                if (isPremium) {
                    Surface(
                        color = Color(0xFFFFD700),
                        modifier = Modifier.padding(8.dp).align(Alignment.TopEnd).clip(RoundedCornerShape(6.dp)),
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, null, modifier = Modifier.size(10.dp), tint = Color.Black)
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(stringResource(Res.string.premium_badge), fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                        }
                    }
                }
                
                if (onEditClick != null || onDeleteClick != null) {
                    Box(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) {
                        IconButton(onClick = { showMenu = true }, modifier = Modifier.background(Color.White.copy(alpha=0.7f), RoundedCornerShape(20.dp)).size(28.dp)) {
                            Icon(Icons.Default.MoreVert, "More", modifier=Modifier.size(16.dp))
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            if (onEditClick != null) {
                                DropdownMenuItem(
                                    text = { Text("Düzenle") },
                                    onClick = { showMenu = false; onEditClick() },
                                    leadingIcon = { Icon(Icons.Default.Edit, "Edit") }
                                )
                            }
                            if (onDeleteClick != null) {
                                DropdownMenuItem(
                                    text = { Text("Sil", color = Color.Red) },
                                    onClick = { showMenu = false; onDeleteClick() },
                                    leadingIcon = { Icon(Icons.Default.Delete, "Delete", tint = Color.Red) }
                                )
                            }
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(productName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    text = companyName,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (onCompanyClick != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = if (onCompanyClick != null) {
                        Modifier.clickable { onCompanyClick() }
                    } else {
                        Modifier
                    }
                )
                
                if (!description.isNullOrBlank()) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onClickDetail,
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Text(stringResource(Res.string.view_details), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}



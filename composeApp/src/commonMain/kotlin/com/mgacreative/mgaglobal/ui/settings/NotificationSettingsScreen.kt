package com.mgacreative.mgaglobal.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mgacreative.mgaglobal.manager.NotificationPreferenceManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(paddingValues: PaddingValues, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    
    val appointmentEnabled by NotificationPreferenceManager.isAppointmentEnabled().collectAsState(initial = true)
    val remindersEnabled by NotificationPreferenceManager.isRemindersEnabled().collectAsState(initial = true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
            Text(
                text = "Tercihlerinizi Yönetin",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.SemiBold
            )

            NotificationToggleItem(
                title = "Randevu Bildirimleri",
                subtitle = "Yeni randevu oluşturulduğunda veya güncellendiğinde bildirim alırsınız.",
                icon = Icons.Default.Notifications,
                checked = appointmentEnabled,
                onCheckedChange = { 
                    scope.launch { NotificationPreferenceManager.setAppointmentEnabled(it) }
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)

            NotificationToggleItem(
                title = "Hatırlatıcı Bildirimleri",
                subtitle = "Randevu vaktinden 24 saat ve 1 saat önce hatırlatıcı alırsınız.",
                icon = Icons.Default.Timer,
                checked = remindersEnabled,
                onCheckedChange = { 
                    scope.launch { NotificationPreferenceManager.setRemindersEnabled(it) }
                }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = "Not: Ayarlar anında kaydedilir ve bir sonraki bildirim planlamasında geçerli olur.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
    }
}

@Composable
fun NotificationToggleItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}


package com.mgacreative.mgaglobal.ui.consultancy

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mgacreative.mgaglobal.core.domain.consultancy.Consultant
import com.mgacreative.mgaglobal.core.domain.consultancy.ConsultantService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultantScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    val consultantService = remember { ConsultantService() }
    var consultants by remember { mutableStateOf<List<Consultant>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val result = consultantService.getConsultants()
        if (result is com.mgacreative.mgaglobal.core.error.AppResult.Success) {
            consultants = result.data
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Danışmanlık", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color(0xFFF1F3F5)),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        "Uzman Danışmanlarımız",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        "İhracat ve küresel ticaret süreçlerinizde uzmanlarımızdan destek alın.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                items(consultants) { consultant ->
                    ConsultantCard(
                        consultant = consultant,
                        onEmailClick = {
                            if (consultant.email.isNotBlank()) {
                                uriHandler.openUri("mailto:${consultant.email}")
                            }
                        },
                        onWhatsappClick = {
                            if (consultant.whatsapp.isNotBlank()) {
                                val cleanNum = consultant.whatsapp.replace(Regex("[^0-9]"), "")
                                uriHandler.openUri("https://wa.me/$cleanNum")
                            }
                        }
                    )
                }

                if (consultants.isEmpty()) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("Şu an aktif danışman bulunmuyor.", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConsultantCard(consultant: Consultant, onEmailClick: () -> Unit, onWhatsappClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(60.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(30.dp))
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(consultant.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(consultant.title, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
                }
            }
            
            Spacer(Modifier.height(12.dp))
            Text("Uzmanlık: ${consultant.expertise}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            if (consultant.bio.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(consultant.bio, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
            }
            
            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onWhatsappClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Message, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("WhatsApp")
                }
                OutlinedButton(
                    onClick = onEmailClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Email, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("E-posta")
                }
            }
        }
    }
}


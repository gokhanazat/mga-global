package com.mgacreative.mgaglobal.ui.education

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mgacreative.mgaglobal.core.domain.education.EducationService
import com.mgacreative.mgaglobal.core.domain.b2b.CompanyService
import com.mgacreative.mgaglobal.core.domain.audit.AuditDomainService
import com.mgacreative.mgaglobal.core.auth.SessionManager
import com.mgacreative.mgaglobal.core.auth.PermissionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificateScreen(
    eduId: String,
    onBack: () -> Unit,
    onNavigateToShowroom: () -> Unit
) {
    val educationService = remember { EducationService() }
    val companyService = remember { CompanyService() }
    
    var education by remember { mutableStateOf<Education?>(null) }
    var companyName by remember { mutableStateOf("Ä°ÅŸletme") }
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(eduId) {
        val eduResult = educationService.getEducationById(eduId)
        if (eduResult.isSuccess) {
            education = eduResult.getOrNull()
            // Log certificate generation/view
            val userId = SessionManager.getUserId()
            val userRole = PermissionManager.currentUserRole.value?.name ?: "MEMBER"
            AuditDomainService.logCertificateGeneration(userId, userRole, eduId)
        }
        
        val companyResult = companyService.getOwnCompany()
        companyResult.onSuccess { company ->
            if (company != null) {
                companyName = company.name
            }
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BaÅŸarÄ± SertifikasÄ±", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Brush.verticalGradient(listOf(Color(0xFFF8FAFC), Color(0xFFF1F5F9)))),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(80.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            "BAÅARI SERTÄ°FÄ°KASI",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        
                        // Small Divider
                        Box(Modifier.width(100.dp).height(2.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)))
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            "SayÄ±n KatÄ±lÄ±mcÄ±,",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            education?.title ?: "EÄŸitim ProgramÄ±",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            "baÅŸarÄ±yla tamamlandÄ±ÄŸÄ± ve gerekli yeterlilik saÄŸlandÄ±ÄŸÄ± iÃ§in bu sertifikaya hak kazanÄ±lmÄ±ÅŸtÄ±r.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = Color.DarkGray,
                            lineHeight = 22.sp
                        )
                        
                        Spacer(modifier = Modifier.height(40.dp))
                        
                        // Showroom Label (The "Tag")
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.Collections,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "$companyName Dijital Showroom",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "OnaylÄ± EÄŸitim Ãœyesidir",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Button(
                            onClick = onNavigateToShowroom,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Business, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Showroom'u Ziyaret Et", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}


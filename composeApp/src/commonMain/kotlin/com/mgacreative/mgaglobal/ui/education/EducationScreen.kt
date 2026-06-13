package com.mgacreative.mgaglobal.ui.education

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mgacreative.mgaglobal.core.domain.education.EducationService

@Composable
fun EducationScreen(
    paddingValues: PaddingValues, 
    onEducationClick: (String) -> Unit,
    onExamClick: (String) -> Unit = {}
) {
    val educationService = remember { EducationService() }
    var educations by remember { mutableStateOf<List<Education>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val result = educationService.getAllEducations()
        if (result.isSuccess) {
            educations = result.getOrNull() ?: emptyList()
        }
        isLoading = false
    }

    Surface(
        modifier = Modifier.fillMaxSize().padding(paddingValues),
        color = Color(0xFFF8FAFC)
    ) {
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF0F172A))
            }
        } else if (educations.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Henüz eğitim tanımlanmamış.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(educations) { education ->
                    EducationCard(
                        education = education, 
                        onClick = { onEducationClick(education.id) },
                        onExamClick = { onExamClick(education.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun EducationCard(
    education: Education, 
    onClick: () -> Unit,
    onExamClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = education.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0F172A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            if (education.topic.isNotBlank()) {
                Text(
                    text = education.topic,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = education.contentText,
                fontSize = 13.sp,
                color = Color(0xFF64748B),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onClick,
                    modifier = Modifier.weight(1f).height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        "Eğitime Başla", 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 12.sp, 
                        color = Color.White
                    )
                }
                
                OutlinedButton(
                    onClick = onExamClick,
                    modifier = Modifier.weight(1f).height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0F172A)),
                    contentPadding = PaddingValues(0.dp),
                    enabled = education.examLink.isNotBlank()
                ) {
                    Text(
                        "Sınava Gir", 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}


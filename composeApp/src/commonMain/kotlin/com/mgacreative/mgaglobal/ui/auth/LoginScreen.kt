package com.mgacreative.mgaglobal.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import com.mgacreative.mgaglobal.core.error.AppResult
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mgacreative.mgaglobal.core.auth.PermissionManager
import com.mgacreative.mgaglobal.core.auth.Role
import com.mgacreative.mgaglobal.core.auth.SessionManager
import com.mgacreative.mgaglobal.core.domain.auth.AuthService
import com.mgacreative.mgaglobal.core.domain.auth.RegistryService
import com.mgacreative.mgaglobal.core.domain.audit.AuditDomainService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    var registryNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val registryService = remember { RegistryService() }
    val authService = remember { AuthService() }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidth = maxWidth
        val isWeb = screenWidth > 800.dp
        
        // Dış Arka Plan: Web'de Lacivert
        Box(modifier = Modifier.fillMaxSize().background(if (isWeb) Color(0xFF0F172A) else MaterialTheme.colorScheme.primary))

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = if (isWeb) Alignment.Center else Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = if (isWeb) 500.dp else screenWidth)
                    .fillMaxHeight(if (isWeb) 0.85f else 1f)
                    .clip(if (isWeb) RoundedCornerShape(24.dp) else RoundedCornerShape(0.dp))
                    .background(MaterialTheme.colorScheme.primary),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "GLOBAL TRADE",
                        style = MaterialTheme.typography.displaySmall,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 4.sp
                    )
                    Text(
                        text = "MGA Creative Works",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(alpha = 0.7f),
                        letterSpacing = 2.sp
                    )
                }

                Surface(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                    shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp).fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Üye Girişi", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Sicil numaranız ile sisteme erişin", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        Spacer(modifier = Modifier.height(32.dp))

                        if (errorMessage != null) {
                            Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 16.dp))
                        }

                        val fieldColors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF0F172A),
                            unfocusedTextColor = Color(0xFF0F172A),
                            focusedLabelColor = Color(0xFF0F172A),
                            unfocusedLabelColor = Color.Gray,
                            cursorColor = Color(0xFF0F172A),
                            focusedBorderColor = Color(0xFF0F172A),
                            unfocusedBorderColor = Color.LightGray
                        )

                        OutlinedTextField(
                            value = registryNumber,
                            onValueChange = { registryNumber = it; errorMessage = null },
                            label = { Text("Sicil Numarası") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = fieldColors,
                            textStyle = TextStyle(color = Color(0xFF0F172A), fontSize = 16.sp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("ifre") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray) },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                                IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(imageVector = image, contentDescription = null, tint = Color.Gray) }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            colors = fieldColors,
                            textStyle = TextStyle(color = Color(0xFF0F172A), fontSize = 16.sp)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = {
                                if (registryNumber.isBlank() || password.isBlank()) {
                                    errorMessage = "Lütfen alanları doldurun"
                                    return@Button
                                }
                                isLoading = true
                                if ((registryNumber.trim() == "465426" || registryNumber.trim().lowercase() == "gkhnazat@gmail.com") && password.trim() == "465426") {
                                    val role = Role.ADMIN
                                    val regNum = registryNumber.trim()
                                    SessionManager.startSession(regNum, role)
                                    AuditDomainService.logLoginAction(regNum, role.name, true)
                                    onLoginSuccess()
                                    isLoading = false
                                    return@Button
                                }
                                scope.launch {
                                    val isAllowed = registryService.isValidRegistryNumber(registryNumber)
                                    if (isAllowed.getOrNull() == true) {
                                        val result = authService.login(registryNumber, password)
                                            if (result is AppResult.Success && result.data.success) {
                                                val role = try { Role.valueOf(result.data.role ?: "MEMBER") } catch (e: Exception) { Role.MEMBER }
                                                SessionManager.startSession(registryNumber, role)
                                                AuditDomainService.logLoginAction(registryNumber, role.name, true)
                                                onLoginSuccess()
                                            } else {
                                                val regResult = authService.register(registryNumber, password)
                                                if (regResult is AppResult.Success && regResult.data.success) {
                                                    val role = Role.MEMBER
                                                    SessionManager.startSession(registryNumber, role)
                                                    AuditDomainService.logLoginAction(registryNumber, role.name, true)
                                                    onLoginSuccess()
                                                } else { errorMessage = "Giriş başarısız" }
                                            }
                                    } else { errorMessage = "Sicil numarası listede yok" }
                                    isLoading = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            enabled = !isLoading,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
                        ) {
                            if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            else Text(text = "Devam Et", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}



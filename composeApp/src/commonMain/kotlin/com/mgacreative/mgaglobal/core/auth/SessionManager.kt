package com.mgacreative.mgaglobal.core.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Kullanıcı oturum bilgilerini Firebase'den bağımsız olarak yönetir.
 */
object SessionManager {
    private val _currentRegistryNumber = MutableStateFlow<String?>(null)
    /**
     * Mevcut oturum açmış kullanıcının sicil numarası.
     */
    val currentRegistryNumber: StateFlow<String?> = _currentRegistryNumber.asStateFlow()

    private val _userRole = MutableStateFlow<Role?>(null)
    val userRole: StateFlow<Role?> = _userRole.asStateFlow()

    /**
     * Giriş yapıldığında oturumu başlatır.
     */
    fun startSession(registryNumber: String, role: Role) {
        _currentRegistryNumber.value = registryNumber
        _userRole.value = role
        // PermissionManager'ı da eşzamanlı güncelleyelim
        PermissionManager.updateRole(role)
    }

    /**
     * Çıkış yapıldığında oturumu sonlandırır.
     */
    fun endSession() {
        _currentRegistryNumber.value = null
        _userRole.value = null
        PermissionManager.updateRole(null)
    }

    /**
     * Firebase'deki 'uid' yerine artık 'registryNumber' kullanacağız.
     */
    fun getUserId(): String = _currentRegistryNumber.value ?: "guest"
    
    fun isLoggedIn(): Boolean = _currentRegistryNumber.value != null
}


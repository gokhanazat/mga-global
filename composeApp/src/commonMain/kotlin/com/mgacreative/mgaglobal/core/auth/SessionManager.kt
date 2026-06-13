package com.mgacreative.mgaglobal.core.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * KullanÄ±cÄ± oturum bilgilerini Firebase'den baÄŸÄ±msÄ±z olarak yÃ¶netir.
 */
object SessionManager {
    private val _currentRegistryNumber = MutableStateFlow<String?>(null)
    /**
     * Mevcut oturum aÃ§mÄ±ÅŸ kullanÄ±cÄ±nÄ±n sicil numarasÄ±.
     */
    val currentRegistryNumber: StateFlow<String?> = _currentRegistryNumber.asStateFlow()

    private val _userRole = MutableStateFlow<Role?>(null)
    val userRole: StateFlow<Role?> = _userRole.asStateFlow()

    /**
     * GiriÅŸ yapÄ±ldÄ±ÄŸÄ±nda oturumu baÅŸlatÄ±r.
     */
    fun startSession(registryNumber: String, role: Role) {
        _currentRegistryNumber.value = registryNumber
        _userRole.value = role
        // PermissionManager'Ä± da eÅŸzamanlÄ± gÃ¼ncelleyelim
        PermissionManager.updateRole(role)
    }

    /**
     * Ã‡Ä±kÄ±ÅŸ yapÄ±ldÄ±ÄŸÄ±nda oturumu sonlandÄ±rÄ±r.
     */
    fun endSession() {
        _currentRegistryNumber.value = null
        _userRole.value = null
        PermissionManager.updateRole(null)
    }

    /**
     * Firebase'deki 'uid' yerine artÄ±k 'registryNumber' kullanacaÄŸÄ±z.
     */
    fun getUserId(): String = _currentRegistryNumber.value ?: "guest"
    
    fun isLoggedIn(): Boolean = _currentRegistryNumber.value != null
}


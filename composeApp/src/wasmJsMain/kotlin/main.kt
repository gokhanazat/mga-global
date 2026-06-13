package com.mgacreative.mgaglobal

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.mgacreative.mgaglobal.App
import kotlinx.browser.window
import kotlinx.browser.localStorage

@JsFun("(lang) => { try { Object.defineProperty(window.navigator, 'language', { value: lang, configurable: true }); Object.defineProperty(window.navigator, 'languages', { value: [lang], configurable: true }); console.log('Navigator language overridden to:', lang); } catch (e) { console.error('Failed to override language:', e); } }")
external fun overrideNavigatorLanguage(lang: String)

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val initialLanguage = window.localStorage.getItem("app_language") ?: "tr"
    
    // TarayÄ±cÄ± dilini uygulama diliyle senkron et (Skiko iÃ§in kritik)
    overrideNavigatorLanguage(initialLanguage)
    
    println("!!! VERSION 03-26-16-30 !!!")
    println("Initializing App with language: $initialLanguage")
    
    CanvasBasedWindow(
        title = "GLOBAL TRADE",
        canvasElementId = "compose-target"
    ) {
        App(initialLanguage = initialLanguage)
    }
}


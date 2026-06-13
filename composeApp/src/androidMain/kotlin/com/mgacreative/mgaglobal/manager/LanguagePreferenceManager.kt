package com.mgacreative.mgaglobal.manager

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.util.Locale

private val Context.languageDataStore: DataStore<Preferences> by preferencesDataStore(name = "language_prefs_v2")

object LanguagePreferenceManager {
    private val KEY_LANGUAGE_INITIALIZED = booleanPreferencesKey("language_initialized")
    private val KEY_SELECTED_LANGUAGE = stringPreferencesKey("selected_language")

    private val supportedLanguages = setOf("tr", "en", "ar", "ru", "de")
    private const val CHINESE_CODE = "zh-rCN"
    private const val DEFAULT_LANGUAGE = "en"

    var appContext: Context? = null
        private set

    /**
     * Initializes language on app startup.
     * Uses saved language if available, otherwise detects device system language.
     */
    suspend fun init(context: Context) {
        appContext = context.applicationContext

        val isInitialized = getIsInitialized(context)
        
        if (!isInitialized) {
            val defaultLang = detectSystemLanguage()
            setLanguage(context, defaultLang)
        } else {
            val savedLanguage = getLanguage(context)
            if (savedLanguage != null) {
                applyLocale(savedLanguage)
            }
        }
    }

    /**
     * Safe Get Language Function
     */
    suspend fun getLanguage(context: Context): String? {
        return getLanguageFlow(context).first()
    }

    /**
     * Expose a Flow for Compose runtime observation
     */
    fun getLanguageFlow(context: Context) = context.languageDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs -> prefs[KEY_SELECTED_LANGUAGE] }

    /**
     * Safe Set Language Function
     */
    suspend fun setLanguage(context: Context, languageCode: String) {
        try {
            context.languageDataStore.edit { prefs ->
                prefs[KEY_SELECTED_LANGUAGE] = languageCode
                prefs[KEY_LANGUAGE_INITIALIZED] = true
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        applyLocale(languageCode)
    }

    private suspend fun getIsInitialized(context: Context): Boolean {
        return context.languageDataStore.data
            .catch { exception ->
                if (exception is IOException) emit(emptyPreferences()) else throw exception
            }
            .map { prefs -> prefs[KEY_LANGUAGE_INITIALIZED] ?: false }
            .first()
    }

    private fun detectSystemLanguage(): String {
        val sysLocale = android.content.res.Resources.getSystem().configuration.locales.get(0)
        val systemLocale = sysLocale.language
        return if (supportedLanguages.contains(systemLocale)) {
            systemLocale
        } else if (systemLocale == "zh") {
            val country = sysLocale.country
            if (country == "CN") CHINESE_CODE else DEFAULT_LANGUAGE
        } else {
            DEFAULT_LANGUAGE
        }
    }

    private fun applyLocale(languageCode: String) {
        val tag = if (languageCode == CHINESE_CODE) "zh-Hans-CN" else languageCode
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(tag)
        
        // Only apply if different to prevent infinite restart loop
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        if (currentLocales.toLanguageTags() != appLocale.toLanguageTags()) {
            AppCompatDelegate.setApplicationLocales(appLocale)
        }
    }
}


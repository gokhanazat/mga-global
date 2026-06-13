package com.mgacreative.mgaglobal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.mgacreative.mgaglobal.manager.LanguagePreferenceManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            enableEdgeToEdge()
            super.onCreate(savedInstanceState)

            initManagers()
            lifecycleScope.launch {
                try {
                    LanguagePreferenceManager.init(applicationContext)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            
            setContent {
                val languageCode by LanguagePreferenceManager.getLanguageFlow(applicationContext)
                    .collectAsState(initial = null)
                
                if (languageCode != null) {
                    App()
                } else {
                    Box(modifier = androidx.compose.ui.Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Even if it fails, try to set content to avoid complete white screen
            setContent {
                App()
            }
        }
    }

    private fun initManagers() {
        try {
            com.mgacreative.mgaglobal.manager.NotificationPreferenceManager.init(applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}


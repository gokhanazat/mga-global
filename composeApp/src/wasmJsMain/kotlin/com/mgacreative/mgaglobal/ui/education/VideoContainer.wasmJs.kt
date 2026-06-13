package com.mgacreative.mgaglobal.ui.education

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
actual fun VideoContainer(url: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Video iÃ§eriÄŸi Web sÃ¼rÃ¼mÃ¼nde yakÄ±nda desteklenecektir.")
    }
}


package com.mgacreative.mgaglobal.ui.news

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
actual fun NewsWebView(
    url: String,
    modifier: Modifier
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Haber iÃ§eriÄŸi Web sÃ¼rÃ¼mÃ¼nde yakÄ±nda desteklenecektir.")
    }
}


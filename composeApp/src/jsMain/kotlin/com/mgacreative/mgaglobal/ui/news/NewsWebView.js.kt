package com.mgacreative.mgaglobal.ui.news

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text

@Composable
actual fun NewsWebView(
    url: String,
    modifier: Modifier
) {
    Box(modifier = modifier) {
        Text("News WebView not supported on JS yet")
    }
}


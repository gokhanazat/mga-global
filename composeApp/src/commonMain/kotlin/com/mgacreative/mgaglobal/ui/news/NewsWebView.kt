package com.mgacreative.mgaglobal.ui.news

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun NewsWebView(
    url: String,
    modifier: Modifier = Modifier
)


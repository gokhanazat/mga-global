package com.mgacreative.mgaglobal

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
actual fun SetStatusBarAndNavigationBarColor(
    statusBarColor: Color,
    navigationBarColor: Color,
    darkIcons: Boolean
) {
    // No-op for JS/Web as it doesn't have a status bar in the same way.
}


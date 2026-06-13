package com.mgacreative.mgaglobal

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
expect fun SetStatusBarAndNavigationBarColor(
    statusBarColor: Color,
    navigationBarColor: Color,
    darkIcons: Boolean
)


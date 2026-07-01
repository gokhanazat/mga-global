package com.mgacreative.mgaglobal.ui.theme

import androidx.compose.ui.graphics.Color

// Sovereign Ledger Palette - Rebranded to Professional Light Corporate Navy Theme
val PrimaryAnchor = Color(0xFF0F294A) // Deep Corporate Navy Blue
val PrimaryContainer = Color(0xFF1E40AF) // Royal Corporate Blue
val SovereignSurface = Color(0xFFF8FAFC) // Clean modern slate background
val SovereignContainerLow = Color(0xFFF1F5F9) // Slate 100 card outlines / background blocks
val SovereignContainerLowest = Color(0xFFFFFFFF) // White card surface
val SovereignText = Color(0xFF0F172A) // Professional Slate 900 text color

// Legacy Colors (kept for compatibility)
val DarkNavy = Color(0xFF0F294A) // Matching Deep Corporate Navy instead of almost black
val DeepNavy = Color(0xFF1E3A8A)
val LightGray = Color(0xFFF8FAFC)

// Material 3 Color Scheme Mapping
val Primary = PrimaryAnchor
val OnPrimary = Color.White
val Secondary = PrimaryContainer
val OnSecondary = Color.White
val Background = SovereignSurface
val OnBackground = SovereignText
val Surface = SovereignContainerLowest
val OnSurface = SovereignText


package com.mgacreative.mgaglobal.ui.theme

import androidx.compose.ui.graphics.Color

// Sovereign Ledger Palette - Modern Slate/Charcoal & Royal Blue Hybrid B2B Theme
val PrimaryAnchor = Color(0xFF0F172A) // Slate 900 (Dark Charcoal for primary headers & text)
val PrimaryContainer = Color(0xFF2563EB) // Blue 600 (Vibrant Royal Blue accent for actions/buttons)
val SovereignSurface = Color(0xFFF8FAFC) // Slate 50 background (modern cool light gray)
val SovereignContainerLow = Color(0xFFF1F5F9) // Slate 100 card outlines / backgrounds
val SovereignContainerLowest = Color(0xFFFFFFFF) // White card surface
val SovereignText = Color(0xFF0F172A) // Slate 900 text color

// Legacy Colors (kept for compatibility)
val DarkNavy = Color(0xFFF8FAFC) // Light slate gray background for topbars/statusbars
val DeepNavy = Color(0xFFE2E8F0) // Slate 200 light gray
val LightGray = Color(0xFF0F172A) // Dark charcoal text for legacy elements

// Material 3 Color Scheme Mapping
val Primary = PrimaryAnchor
val OnPrimary = Color.White
val Secondary = PrimaryContainer
val OnSecondary = Color.White
val Background = SovereignSurface
val OnBackground = SovereignText
val Surface = SovereignContainerLowest
val OnSurface = SovereignText


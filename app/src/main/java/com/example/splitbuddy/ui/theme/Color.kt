package com.example.splitbuddy.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ── Brand ─────────────────────────────────────────────────────────────────────
val Primary     = Color(0xFF3D5AFE)   // Midnight Blue
val PrimaryDark = Color(0xFF2541E8)
val Accent      = Color(0xFF7C4DFF)   // Purple accent

// ── Neutrals ──────────────────────────────────────────────────────────────────
val White   = Color(0xFFFFFFFF)
val Black   = Color(0xFF0D0D1A)
val Gray    = Color(0xFF6B6B6B)
val DarkGray= Color(0xFFB0B0B0)

// ── Light theme surfaces ───────────────────────────────────────────────────────
val LightBackground = Color(0xFFF5F7FF)
val LightSurface    = Color(0xFFFFFFFF)
val LightSubtitle   = Color(0xFF6B6B6B)

// ── Dark theme surfaces ────────────────────────────────────────────────────────
val DarkBackground  = Color(0xFF0D0D1A)
val DarkSurface     = Color(0xFF151525)
val DarkSubtitle    = Color(0xFFB0B0B0)

// ── Legacy purple kept for gradient2 (GroupScreen card) ───────────────────────
val Purple80 = Color(0xFFD0BCFF)
val Purple40 = Color(0xFF6650A4)

// ── Gradients ─────────────────────────────────────────────────────────────────
val gradient = Brush.horizontalGradient(
    listOf(Primary, Accent)
)

val gradient2 = Brush.linearGradient(
    listOf(Primary, Accent)
)
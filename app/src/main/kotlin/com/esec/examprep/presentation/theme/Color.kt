package com.esec.examprep.presentation.theme

import androidx.compose.ui.graphics.Color

// ── Brand palette (refined, professional) ─────────────────────────
val PrimaryBlue       = Color(0xFF2563EB)   // confident, modern indigo-blue
val PrimaryBlueDark   = Color(0xFF1D4ED8)
val PrimaryBlueSoft   = Color(0xFFDBE7FF)   // light tinted container
val SecondaryGold     = Color(0xFFF59E0B)
val SecondaryGoldDark = Color(0xFFB45309)
val AccentTeal        = Color(0xFF0EA5A4)

// ── Surface & background (paper-cool tones) ───────────────────────
val SurfaceLight       = Color(0xFFFFFFFF)
val SurfaceVariantLight = Color(0xFFF1F4F9)
val BackgroundLight    = Color(0xFFF6F8FB)

val SurfaceDark        = Color(0xFF161A22)
val SurfaceVariantDark = Color(0xFF1F2530)
val BackgroundDark     = Color(0xFF0E1117)
val OutlineDark        = Color(0xFF2A323F)

// ── Semantic ──────────────────────────────────────────────────────
val CorrectGreen      = Color(0xFF16A34A)
val CorrectGreenLight = Color(0xFFDCFCE7)
val WrongRed          = Color(0xFFDC2626)
val WrongRedLight     = Color(0xFFFEE2E2)
val SkippedAmber      = Color(0xFFD97706)
val SkippedAmberLight = Color(0xFFFEF3C7)

// ── Neutral scale ─────────────────────────────────────────────────
val Neutral50         = Color(0xFFF9FAFB)
val Neutral100        = Color(0xFFF3F4F6)
val Neutral200        = Color(0xFFE5E7EB)
val Neutral300        = Color(0xFFD1D5DB)
val Neutral500        = Color(0xFF6B7280)
val Neutral600        = Color(0xFF4B5563)
val Neutral700        = Color(0xFF374151)
val Neutral800        = Color(0xFF1F2937)
val Neutral900        = Color(0xFF111827)

// ── Timer bar ─────────────────────────────────────────────────────
val TimerSafe         = Color(0xFF16A34A)
val TimerWarning      = Color(0xFFD97706)
val TimerCritical     = Color(0xFFDC2626)

// ── Score-band tinting ───────────────────────────────────────────
// 0–49 % → red, 50–74 % → orange, 75–100 % → green.
fun scoreColor(percent: Float): Color = when {
    percent >= 75f -> CorrectGreen
    percent >= 50f -> SkippedAmber
    else           -> WrongRed
}

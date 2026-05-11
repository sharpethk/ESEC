package com.esec.examprep.presentation.theme

import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────────────────────────
// EriXam design tokens — Eritrean Blue / Green / Red identity.
// Use these in NEW UI code. Bound into MaterialTheme.colorScheme
// via Theme.kt, so prefer `MaterialTheme.colorScheme.primary` etc.
// in components and screens instead of reaching for these directly.
// ─────────────────────────────────────────────────────────────────

// Primary — Eritrean Blue
val Primary               = Color(0xFF00658D)
val OnPrimary             = Color(0xFFFFFFFF)
val PrimaryContainer      = Color(0xFF00AEEF) // vibrant CTAs
val OnPrimaryContainer    = Color(0xFF003E58)
val PrimaryFixed          = Color(0xFFC6E7FF) // soft tint
val InversePrimary        = Color(0xFF82CFFF)

// Secondary — Vibrant Green (success / streaks / completion)
val Secondary             = Color(0xFF006D2F)
val OnSecondary           = Color(0xFFFFFFFF)
val SecondaryContainer    = Color(0xFF71FA92)
val OnSecondaryContainer  = Color(0xFF007232)

// Tertiary — Bold Red (streak fire, alerts, "wrong")
val Tertiary              = Color(0xFFC00015)
val OnTertiary            = Color(0xFFFFFFFF)
val TertiaryContainer     = Color(0xFFFF7B70)
val OnTertiaryContainer   = Color(0xFF790009)

// Error
val ErrorRed              = Color(0xFFBA1A1A)
val OnErrorRed            = Color(0xFFFFFFFF)
val ErrorContainer        = Color(0xFFFFDAD6)
val OnErrorContainer      = Color(0xFF93000A)

// Neutral / surfaces (paper-cool)
val Background            = Color(0xFFF5FAFF)
val OnBackground          = Color(0xFF171C20)
val Surface               = Color(0xFFF5FAFF)
val OnSurface             = Color(0xFF171C20)
val OnSurfaceVariant      = Color(0xFF3E4850)
val SurfaceContainerLowest  = Color(0xFFFFFFFF)
val SurfaceContainerLow     = Color(0xFFEFF4FA)
val SurfaceContainer        = Color(0xFFEAEEF4)
val SurfaceContainerHigh    = Color(0xFFE4E9EE)
val SurfaceContainerHighest = Color(0xFFDEE3E8)
val OutlineColor          = Color(0xFF6E7881)
val OutlineVariantColor   = Color(0xFFBDC8D1)

// Dark scheme bindings
val PrimaryDark           = Color(0xFF82CFFF)
val OnPrimaryDark         = Color(0xFF003549)
val PrimaryContainerDark  = Color(0xFF004C6B)
val OnPrimaryContainerDark = Color(0xFFC6E7FF)
val SecondaryDark         = Color(0xFF53DD75)
val OnSecondaryDark       = Color(0xFF003917)
val SecondaryContainerDark = Color(0xFF005323)
val OnSecondaryContainerDark = Color(0xFF71FA92)
val TertiaryDark          = Color(0xFFFFB3AC)
val OnTertiaryDark        = Color(0xFF690007)
val TertiaryContainerDark = Color(0xFF93000A)
val OnTertiaryContainerDark = Color(0xFFFFDAD6)
val BackgroundDarkScheme  = Color(0xFF0E1417)
val OnBackgroundDarkScheme = Color(0xFFDEE3E8)
val SurfaceDarkScheme     = Color(0xFF0E1417)
val OnSurfaceDarkScheme   = Color(0xFFDEE3E8)
val OnSurfaceVariantDark  = Color(0xFFBDC8D1)
val SurfaceContainerLowestDark  = Color(0xFF080D10)
val SurfaceContainerLowDark     = Color(0xFF161C20)
val SurfaceContainerDarkScheme  = Color(0xFF1A2024)
val SurfaceContainerHighDark    = Color(0xFF252B30)
val SurfaceContainerHighestDark = Color(0xFF30373B)
val OutlineDarkScheme     = Color(0xFF889299)
val OutlineVariantDarkScheme = Color(0xFF3E4850)

// ─────────────────────────────────────────────────────────────────
// Legacy semantic tokens — retained so existing screens
// (OptionItem, ScoreRing, TimerBar, ResultScreen, DashboardScreen,
//  ExamScreen, QuestionDetailScreen, SubjectScreen, …) keep
// compiling without sweeping import changes. New code should not
// use these — read from `MaterialTheme.colorScheme` instead.
// ─────────────────────────────────────────────────────────────────

// Legacy brand palette (re-pointed to Eritrean values)
val PrimaryBlue       = Color(0xFF00658D)
val PrimaryBlueDark   = Color(0xFF003E58)
val PrimaryBlueSoft   = Color(0xFFC6E7FF)
val SecondaryGold     = Color(0xFFF59E0B)
val SecondaryGoldDark = Color(0xFFB45309)
val AccentTeal        = Color(0xFF0EA5A4)

// Legacy surfaces (kept for direct refs; scheme uses new tokens)
val SurfaceLight        = Color(0xFFFFFFFF)
val SurfaceVariantLight = Color(0xFFEAEEF4)
val BackgroundLight     = Color(0xFFF5FAFF)
val SurfaceDark         = Color(0xFF1A2024)
val SurfaceVariantDark  = Color(0xFF252B30)
val BackgroundDark      = Color(0xFF0E1417)
val OutlineDark         = Color(0xFF3E4850)

// Semantic answer-state colors — mapped to Eritrean palette
val CorrectGreen      = Color(0xFF006D2F) // = Secondary
val CorrectGreenLight = Color(0xFFC8F7D2)
val WrongRed          = Color(0xFFC00015) // = Tertiary
val WrongRedLight     = Color(0xFFFFDAD6)
val SkippedAmber      = Color(0xFFD97706)
val SkippedAmberLight = Color(0xFFFEF3C7)

// Neutral scale (legacy — kept verbatim)
val Neutral50         = Color(0xFFF9FAFB)
val Neutral100        = Color(0xFFF3F4F6)
val Neutral200        = Color(0xFFE5E7EB)
val Neutral300        = Color(0xFFD1D5DB)
val Neutral500        = Color(0xFF6B7280)
val Neutral600        = Color(0xFF4B5563)
val Neutral700        = Color(0xFF374151)
val Neutral800        = Color(0xFF1F2937)
val Neutral900        = Color(0xFF111827)

// Timer bar — semantic green / amber / red mapped to EriXam palette
val TimerSafe         = Color(0xFF006D2F)
val TimerWarning      = Color(0xFFD97706)
val TimerCritical     = Color(0xFFC00015)

// ─────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────

/** 0–49 % → red, 50–74 % → amber, 75–100 % → green. */
fun scoreColor(percent: Float): Color = when {
    percent >= 75f -> CorrectGreen
    percent >= 50f -> SkippedAmber
    else           -> WrongRed
}

fun gradeColor(grade: com.esec.examprep.domain.model.Grade): Color = when {
    grade.gpa >= 3.5f -> CorrectGreen
    grade.gpa >= 2.0f -> SkippedAmber
    else              -> WrongRed
}

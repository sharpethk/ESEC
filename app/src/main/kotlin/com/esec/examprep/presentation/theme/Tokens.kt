package com.esec.examprep.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/** 4-pt grid spacing scale. Use these instead of hard-coded dp values. */
object Spacing {
    val xxs = 2.dp
    val xs  = 4.dp
    val sm  = 8.dp
    val md  = 12.dp
    val lg  = 16.dp
    val xl  = 20.dp
    val xxl = 24.dp
    val xxxl = 32.dp
    val huge = 48.dp
}

/** Corner radius scale used across surfaces. */
object Radius {
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val pill = 999.dp
}

/** Elevation scale (subtle, professional, layered). */
object Elevation {
    val none = 0.dp
    val xs   = 1.dp
    val sm   = 2.dp
    val md   = 4.dp
    val lg   = 8.dp
}

val ESECShapes = Shapes(
    extraSmall = RoundedCornerShape(Radius.sm),
    small      = RoundedCornerShape(Radius.md),
    medium     = RoundedCornerShape(Radius.lg),
    large      = RoundedCornerShape(Radius.xl),
    extraLarge = RoundedCornerShape(28.dp),
)

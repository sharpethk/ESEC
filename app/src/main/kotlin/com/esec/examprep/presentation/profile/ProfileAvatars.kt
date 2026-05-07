package com.esec.examprep.presentation.profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

object ProfileAvatars {
    private val map: Map<String, ImageVector> = linkedMapOf(
        "avatar_owl" to Icons.Default.Pets,
        "avatar_star" to Icons.Default.Star,
        "avatar_smile" to Icons.Default.EmojiEmotions,
        "avatar_school" to Icons.Default.School,
        "avatar_face" to Icons.Default.Face,
        "avatar_bolt" to Icons.Default.Bolt,
        "avatar_snow" to Icons.Default.AcUnit,
        "avatar_fire" to Icons.Default.LocalFireDepartment,
    )

    val keys: List<String> = map.keys.toList()

    fun iconFor(key: String): ImageVector = map[key] ?: Icons.Default.Person
}

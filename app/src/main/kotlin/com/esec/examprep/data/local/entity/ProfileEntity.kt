package com.esec.examprep.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val avatarKey: String,
    val gradeLevel: Int,
    val examCategory: String,
    val pinHash: String?,
    val createdAt: Long,
    val lastActiveAt: Long,
)

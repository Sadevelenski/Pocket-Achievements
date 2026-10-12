package com.juan.achievements.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val sortOrder: Int = 0
)

@Entity(
    tableName = "achievements",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId")]
)
data class AchievementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryId: Long,
    val name: String,
    val description: String = "",
    val rarity: String = Rarity.BRONZE.name,
    val isObtained: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val sortOrder: Int = 0,
    val customImage: ByteArray? = null
)

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = AchievementEntity::class,
            parentColumns = ["id"],
            childColumns = ["achievementId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("achievementId")]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val achievementId: Long,
    val name: String,
    val type: String = TaskType.CHECKBOX.name,
    val isCompleted: Boolean = false,
    val currentValue: Int = 0,
    val targetValue: Int = 0,
    val sortOrder: Int = 0
)

enum class Rarity(val displayName: String, val prestige: Int, val emoji: String) {
    PLATINUM("Platino", 0, "🏆"),
    GOLD("Oro", 1, "🥇"),
    SILVER("Plata", 2, "🥈"),
    BRONZE("Bronce", 3, "🥉")
}

enum class TaskType(val displayName: String) {
    CHECKBOX("Casilla"),
    COUNTER("Contador"),
    TARGET_VALUE("Valor objetivo")
}

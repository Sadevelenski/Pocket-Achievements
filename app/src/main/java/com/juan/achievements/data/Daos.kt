package com.juan.achievements.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY sortOrder ASC, name COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(category: CategoryEntity): Long

    @Update
    suspend fun update(category: CategoryEntity)

    @Delete
    suspend fun delete(category: CategoryEntity)
}

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements WHERE categoryId = :categoryId ORDER BY sortOrder ASC, name COLLATE NOCASE ASC")
    fun observeForCategory(categoryId: Long): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(achievement: AchievementEntity): Long

    @Update
    suspend fun update(achievement: AchievementEntity)

    @Delete
    suspend fun delete(achievement: AchievementEntity)
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE achievementId = :achievementId ORDER BY sortOrder ASC, id ASC")
    fun observeForAchievement(achievementId: Long): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)
}

package com.done.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.done.core.database.entity.HabitEntity
import com.done.core.database.model.DayCheckCount
import com.done.core.database.model.HabitWithCheckCount
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Query(
        """
        SELECT h.*, COUNT(c.id) as checksToday
        FROM habits h
        LEFT JOIN habit_checks c ON c.habitId = h.id AND c.dateDay = :day
        WHERE h.isArchived = 0
        GROUP BY h.id
        ORDER BY h.sortOrder ASC
        """
    )
    fun getHabitsWithChecksForDay(day: String): Flow<List<HabitWithCheckCount>>

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getHabitById(id: Long): HabitEntity?

    @Query("SELECT * FROM habits WHERE id = :id")
    fun observeHabitById(id: Long): Flow<HabitEntity?>

    @Query("SELECT * FROM habits WHERE isArchived = 0 ORDER BY sortOrder ASC")
    fun getAllActiveHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits ORDER BY sortOrder ASC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Query("UPDATE habits SET isArchived = :archived WHERE id = :id")
    suspend fun setArchived(id: Long, archived: Boolean)

    @Query("SELECT DISTINCT dateDay FROM habit_checks WHERE habitId = :habitId ORDER BY dateDay DESC")
    suspend fun getCheckDays(habitId: Long): List<String>

    @Query(
        """
        SELECT dateDay, COUNT(id) as count
        FROM habit_checks
        WHERE habitId = :habitId AND dateDay >= :fromDay
        GROUP BY dateDay
        """
    )
    fun getCheckHistoryFrom(habitId: Long, fromDay: String): Flow<List<DayCheckCount>>

    @Query(
        """
        SELECT dateDay, COUNT(id) as count
        FROM habit_checks
        WHERE habitId = :habitId
        GROUP BY dateDay
        ORDER BY dateDay DESC
        """
    )
    suspend fun getAllCheckDayCounts(habitId: Long): List<DayCheckCount>

    @Query("SELECT MAX(sortOrder) FROM habits")
    suspend fun getMaxSortOrder(): Int?
}

package com.done.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.done.core.database.entity.HabitCheckEntity

@Dao
interface HabitCheckDao {

    @Insert
    suspend fun insertCheck(check: HabitCheckEntity): Long

    @Query("SELECT COUNT(*) FROM habit_checks WHERE habitId = :habitId AND dateDay = :day")
    suspend fun getCheckCountForDay(habitId: Long, day: String): Int

    @Query(
        """
        SELECT * FROM habit_checks 
        WHERE habitId = :habitId AND dateDay = :day 
        ORDER BY checkedAt DESC 
        LIMIT 1
        """
    )
    suspend fun getLastCheckForDay(habitId: Long, day: String): HabitCheckEntity?

    @Query("DELETE FROM habit_checks WHERE id = :checkId")
    suspend fun deleteCheck(checkId: Long)

    @Query("SELECT COUNT(*) FROM habit_checks WHERE habitId = :habitId")
    suspend fun getTotalChecksForHabit(habitId: Long): Int

    @Query(
        """
        SELECT COUNT(DISTINCT dateDay) FROM habit_checks 
        WHERE habitId = :habitId AND dateDay >= :fromDay
        """
    )
    suspend fun getDaysWithChecksFrom(habitId: Long, fromDay: String): Int
}

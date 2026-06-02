package com.done.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.done.core.database.dao.HabitCheckDao
import com.done.core.database.dao.HabitDao
import com.done.core.database.entity.HabitCheckEntity
import com.done.core.database.entity.HabitEntity

@Database(
    entities = [HabitEntity::class, HabitCheckEntity::class],
    version = 1,
    exportSchema = true
)
abstract class DoneDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun habitCheckDao(): HabitCheckDao
}

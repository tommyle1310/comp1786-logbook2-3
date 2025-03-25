package com.example.logbook2.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.logbook2.data.dao.TodoDao
import com.example.logbook2.data.entity.Todo

@Database(entities = [Todo::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}
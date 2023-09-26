package com.loki.opt.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Schedule::class],
    version = 1,
    exportSchema = false
)
abstract class OptDatabase: RoomDatabase() {

    abstract val optDao: OptDao

    companion object {
        const val DATABASE_NAME = "opt_db"
    }
}
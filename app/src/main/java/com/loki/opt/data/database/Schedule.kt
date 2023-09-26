package com.loki.opt.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Schedule(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val offTime: String,
    val isEnabled: Boolean
)
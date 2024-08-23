package com.example.esp_p2p.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drawing")
data class Drawing(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val title: String,
    @ColumnInfo(name = "field_size") val fieldSize: Int,
    @ColumnInfo(name = "field_scale") val fieldScale: Float,
    @ColumnInfo(name = "color_array") val colorArray: List<Int>
)
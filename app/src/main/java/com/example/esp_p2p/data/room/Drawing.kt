package com.example.esp_p2p.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "drawing")
data class Drawing(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,

    val title: String = "",

    @ColumnInfo(name = "field_size")
    @SerializedName("field_size")
    val fieldSize: Int = 16,

    @ColumnInfo(name = "field_scale")
    @SerializedName("field_scale")
    val fieldScale: Float = 50f,

    @ColumnInfo(name = "color_array")
    @SerializedName("color_array")
    val colorArray: List<Int> = emptyList()
)
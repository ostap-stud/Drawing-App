package com.example.esp_p2p.data.room

import androidx.room.TypeConverter

object Converters {
    @TypeConverter
    fun fromListToString(colorList: List<Int>): String{
        return colorList.joinToString()
    }

    @TypeConverter
    fun fromStringToList(colorString: String): List<Int>{
        return colorString.split(", ").map { it.ifBlank { "0" }.toInt() }
    }
}
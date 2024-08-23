package com.example.esp_p2p.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DrawingDAO {

    @Insert
    suspend fun insert(drawing: Drawing): Long

    @Update
    suspend fun update(drawing: Drawing)

    @Delete
    suspend fun delete(drawing: Drawing)

    @Query("SELECT * FROM drawing")
    fun getAllDrawings(): Flow<List<Drawing>>

    @Query("SELECT * FROM drawing WHERE id == :drawingId")
    suspend fun getDrawingById(drawingId: Long): Drawing

}
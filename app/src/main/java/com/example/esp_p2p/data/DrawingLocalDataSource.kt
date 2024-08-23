package com.example.esp_p2p.data

import com.example.esp_p2p.data.room.Drawing
import com.example.esp_p2p.data.room.DrawingDAO
import javax.inject.Inject

class DrawingLocalDataSource @Inject constructor(
    private val drawingDAO: DrawingDAO
) {
    suspend fun insertDrawing(drawing: Drawing) = drawingDAO.insert(drawing)

    suspend fun updateDrawing(drawing: Drawing) = drawingDAO.update(drawing)

    suspend fun deleteDrawing(drawing: Drawing) = drawingDAO.delete(drawing)

    fun getAllDrawings() = drawingDAO.getAllDrawings()

    suspend fun getDrawingById(drawingId: Long) = drawingDAO.getDrawingById(drawingId)
}
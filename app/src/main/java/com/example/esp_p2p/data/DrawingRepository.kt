package com.example.esp_p2p.data

import com.example.esp_p2p.IoDispatcher
import com.example.esp_p2p.data.room.Drawing
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DrawingRepository @Inject constructor(
    private val drawingLocal: DrawingLocalDataSource,
    @IoDispatcher private val dispatcher: CoroutineContext
) {
    suspend fun insertDrawing(drawing: Drawing) = withContext(dispatcher){
        drawingLocal.insertDrawing(drawing)
    }

    suspend fun updateDrawing(drawing: Drawing) = withContext(dispatcher){
        drawingLocal.updateDrawing(drawing)
    }

    suspend fun deleteDrawing(drawing: Drawing) = withContext(dispatcher){
        drawingLocal.deleteDrawing(drawing)
    }

    fun getAllDrawings() = drawingLocal.getAllDrawings()

    suspend fun getDrawingById(drawingId: Long) = withContext(dispatcher){
        drawingLocal.getDrawingById(drawingId)
    }
}
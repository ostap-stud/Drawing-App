package com.example.esp_p2p.data

import android.util.Log
import com.example.esp_p2p.IoDispatcher
import com.example.esp_p2p.data.room.Drawing
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DrawingRepository @Inject constructor(
    private val drawingLocal: DrawingLocalDataSource,
    private val drawingRemote: DrawingRemoteDataSource,
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

    suspend fun getAllDrawings(): Flow<List<Drawing>>{
        withContext(dispatcher){
            try {
                val drawings = drawingRemote.getAllDrawings()
                drawingLocal.insertAllDrawings(drawings)
            }catch (ex: Exception){
                Log.e("GET_DRAWINGS_EXCEPTION", "Something went wrong trying get list of drawings remotely.")
            }
        }
        return drawingLocal.getAllDrawings()
    }

    suspend fun getDrawingById(drawingId: Long) = withContext(dispatcher){
        drawingLocal.getDrawingById(drawingId)
    }
}
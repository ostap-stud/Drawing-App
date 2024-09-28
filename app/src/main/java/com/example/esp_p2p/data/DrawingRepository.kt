package com.example.esp_p2p.data

import com.example.esp_p2p.IoDispatcher
import com.example.esp_p2p.data.firestore.DrawingFirestore
import com.example.esp_p2p.data.retrofit.DrawingRequest
import com.example.esp_p2p.data.room.Drawing
import com.example.esp_p2p.data.firestore.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DrawingRepository @Inject constructor(
    private val drawingLocal: DrawingLocalDataSource,
    private val drawingRemote: DrawingRemoteDataSource,
    private val drawingFirestore: DrawingFirestoreDataSource,
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

    fun getAllDrawings(): Flow<List<Drawing>> = drawingLocal.getAllDrawings()
    /*suspend fun getAllDrawings(): Flow<List<Drawing>>{
        withContext(dispatcher){
            try {
                val drawings = drawingRemote.getAllDrawings()
                drawingLocal.insertAllDrawings(drawings)
            }catch (ex: Exception){
                Log.e("GET_DRAWINGS_EXCEPTION", "Something went wrong trying get list of drawings remotely.")
            }
        }
        return drawingLocal.getAllDrawings()
    }*/

    suspend fun getDrawingById(drawingId: Long) = withContext(dispatcher){
        drawingLocal.getDrawingById(drawingId)
    }

    suspend fun postDrawing(hostname: String, colors: List<Int>) = withContext(dispatcher){
        drawingRemote.postDrawing(
            hostname,
            DrawingRequest(colors)
        )
    }

    fun getAllDrawingsFirestore(searchTitle: String) = drawingFirestore.getAllDrawings(searchTitle)
    fun getDrawingFirestore(id: String) = drawingFirestore.getDrawing(id)
    fun insertDrawingFirestore(drawing: DrawingFirestore) = drawingFirestore.insertDrawing(drawing)

    fun getUserFirestore(uid: String) = drawingFirestore.getUser(uid)
    fun insertUserFirestore(user: UserModel) = drawingFirestore.insertUser(user)
}
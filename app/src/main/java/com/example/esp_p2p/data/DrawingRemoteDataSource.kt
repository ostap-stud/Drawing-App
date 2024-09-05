package com.example.esp_p2p.data

import com.example.esp_p2p.data.retrofit.DrawingAPI
import javax.inject.Inject

class DrawingRemoteDataSource @Inject constructor(
    private val drawingAPI: DrawingAPI
) {
    suspend fun getAllDrawings() = drawingAPI.getAllDrawings()
}
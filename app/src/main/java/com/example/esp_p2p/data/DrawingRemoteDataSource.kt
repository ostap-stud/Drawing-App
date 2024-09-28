package com.example.esp_p2p.data

import com.example.esp_p2p.data.retrofit.DrawingAPI
import com.example.esp_p2p.data.retrofit.DrawingRequest
import com.example.esp_p2p.data.retrofit.DynamicHostInterceptor
import retrofit2.Response
import retrofit2.http.Body
import javax.inject.Inject

class DrawingRemoteDataSource @Inject constructor(
    private val drawingAPI: DrawingAPI,
    private val dynamicHostInterceptor: DynamicHostInterceptor
) {
//    suspend fun getAllDrawings() = drawingAPI.getAllDrawings()
    suspend fun postDrawing(hostname: String, body: DrawingRequest): Response<Map<String, String>>{
        dynamicHostInterceptor.hostname = hostname
        return drawingAPI.postDrawing(body)
    }
}
package com.example.esp_p2p.data.retrofit

import com.example.esp_p2p.data.room.Drawing
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface DrawingAPI {
//    @GET("api/Drawings")
//    suspend fun getAllDrawings(): List<Drawing>

    @POST("/setLED")
    suspend fun postDrawing(@Body body: DrawingRequest): Response<Map<String, String>>
}

data class DrawingRequest(
    val colors: List<Int>
)
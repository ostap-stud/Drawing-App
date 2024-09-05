package com.example.esp_p2p.data.retrofit

import com.example.esp_p2p.data.room.Drawing
import retrofit2.http.GET

interface DrawingAPI {
    @GET("api/Drawings")
    suspend fun getAllDrawings(): List<Drawing>
}
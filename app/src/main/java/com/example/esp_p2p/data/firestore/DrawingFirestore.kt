package com.example.esp_p2p.data.firestore

data class DrawingFirestore(
    var id: String = "",
    val uid: String = "",
    val title: String = "",
    val fieldSize: Int = 16,
    val fieldScale: Float = 50f,
    val colorArray: List<Int> = emptyList()
)

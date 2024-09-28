package com.example.esp_p2p.data

import com.example.esp_p2p.data.firestore.DrawingFirestore
import com.example.esp_p2p.data.room.Drawing
import com.example.esp_p2p.data.firestore.UserModel
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class DrawingFirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    // Drawings
    fun getAllDrawings(searchTitle: String) = firestore.collection("drawings").orderBy("title").startAt(searchTitle).endAt("$searchTitle\uf8ff")
    fun getDrawing(id: String) = firestore.collection("drawings").document(id)
    fun insertDrawing(drawing: DrawingFirestore) = firestore.collection("drawings").add(drawing)

    // Users
    fun getUser(uid: String) = firestore.collection("users").document(uid)
    fun insertUser(user: UserModel) = firestore.collection("users").document(user.uid).set(user)
}
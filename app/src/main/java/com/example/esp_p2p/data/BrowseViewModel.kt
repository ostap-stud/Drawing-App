package com.example.esp_p2p.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.esp_p2p.data.firestore.DrawingFirestore
import com.example.esp_p2p.data.firestore.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val drawingRepository: DrawingRepository
) : ViewModel() {

//    private var snapshotListener: ListenerRegistration

    var drawings by mutableStateOf(emptyList<DrawingFirestore>())

    var searchQuery by mutableStateOf("")
        private set

    /*var isSearching by mutableStateOf(false )
        private set*/

    init {
        searchByTitle("")
    }

    suspend fun getUser(uid: String): UserModel{
        return viewModelScope.async {
            drawingRepository.getUserFirestore(uid).get().await()
        }.await().toObject(UserModel::class.java) ?: UserModel()
    }

    private fun searchByTitle(searchTitle: String){
        drawingRepository.getAllDrawingsFirestore(searchTitle).get().addOnCompleteListener { task ->
            if (task.isSuccessful){
                val snapshot = task.result
                drawings = snapshot.toObjects(DrawingFirestore::class.java)
                snapshot?.documents?.forEachIndexed { index, documentSnapshot ->
                    drawings[index].id = documentSnapshot.id
                }
            }
        }
    }

    fun onSearchQueryChange(newSearchQuery: String){
        searchQuery = newSearchQuery
        searchByTitle(searchQuery)
    }

    /*fun onToggleSearch(activeState: Boolean){
        isSearching = activeState
    }*/

    fun onDispose(){
//        snapshotListener.remove()
        onSearchQueryChange("")
    }

}
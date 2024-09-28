package com.example.esp_p2p.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.esp_p2p.data.room.Drawing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class SavedDrawingsViewModel @Inject constructor(
    private val drawingRepository: DrawingRepository
) : ViewModel() {

    /*val uiState: StateFlow<SavedDrawingsUIState> = runBlocking {
        async {
            drawingRepository.getAllDrawings().map { SavedDrawingsUIState(it) }
                .stateIn(viewModelScope, SharingStarted.Eagerly, SavedDrawingsUIState())
        }.await()
    }*/
    val uiState: StateFlow<SavedDrawingsUIState> =
        drawingRepository.getAllDrawings().map { SavedDrawingsUIState(it) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, SavedDrawingsUIState())

    fun deleteDrawing(drawing: Drawing){
        viewModelScope.launch {
            drawingRepository.deleteDrawing(drawing)
        }
    }
}

data class SavedDrawingsUIState(
    val drawingList: List<Drawing> = listOf()
)
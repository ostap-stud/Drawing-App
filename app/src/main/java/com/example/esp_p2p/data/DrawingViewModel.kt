package com.example.esp_p2p.data

import android.util.Log
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.esp_p2p.R
import com.example.esp_p2p.data.room.Drawing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.KParameter

@HiltViewModel
class DrawingViewModel @Inject constructor(
    private val drawingRepository: DrawingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DrawingUIState())
    val uiState = _uiState.asStateFlow()

    var currentColor by mutableStateOf(Color(_uiState.value.paintColor))
        private set

    var isColorPicking = MutableTransitionState(false)
        private set

    var currentTitle by mutableStateOf("")
        private set

    private var drawingId: Long? = null

    var isEditing by mutableStateOf(false)
        private set

    /*init {
        fillDrawing(Color.Black.toArgb())
    }*/

    fun updateDialogState(){
        isColorPicking.targetState = !isColorPicking.targetState
    }

    fun updateCurrentColor(kParam: KParameter, value: Float){
        currentColor = currentColor::copy.callBy(mapOf(kParam to value))
    }

    fun updateCurrentTitle(title: String){
        currentTitle = title
    }

    fun resetCurrentColor() {
        if(currentColor.toArgb() != _uiState.value.paintColor){
            currentColor = Color(_uiState.value.paintColor)
        }
    }

    fun changePaintColor(){
        _uiState.update { currentState ->
            currentState.copy(paintColor = currentColor.toArgb())
        }
        updateDialogState()
    }

    fun fillDrawing(fillColor: Int){
        _uiState.value.bitmapColors.replaceAll { fillColor }
    }

    private fun updateEditingState(){
        isEditing = !isEditing
    }

    fun resetUIState(){
        _uiState.update { DrawingUIState() }
        updateCurrentTitle("")
        updateEditingState()
    }

    fun insertDrawing(){
        viewModelScope.launch {
            drawingId = drawingRepository.insertDrawing(
                Drawing(
                    title = currentTitle,
                    fieldSize = _uiState.value.fieldSize,
                    fieldScale = _uiState.value.fieldScale,
                    colorArray = _uiState.value.bitmapColors
                )
            )
            updateEditingState()
        }
    }

    fun updateDrawing(){
        viewModelScope.launch {
            drawingRepository.updateDrawing(
                Drawing(
                    id = drawingId!!,
                    title = currentTitle,
                    fieldSize = _uiState.value.fieldSize,
                    fieldScale = _uiState.value.fieldScale,
                    colorArray = _uiState.value.bitmapColors
                )
            )
        }
    }

    fun applyDrawing(drawingId: Long){
        viewModelScope.launch {

            val drawing = drawingRepository.getDrawingById(drawingId)

            _uiState.update { currentState ->
                currentState.copy(
                    fieldSize = drawing.fieldSize,
                    fieldScale = drawing.fieldScale,
                    bitmapColors = drawing.colorArray.toMutableStateList()
                )
            }

            updateCurrentTitle(drawing.title)
            updateEditingState()

            this@DrawingViewModel.drawingId = drawingId

            /*for (y in 0 until _uiState.value.fieldSize){
                for (x in 0 until _uiState.value.fieldSize){
                    _uiState.value.bitmapColors[x + y * _uiState.value.fieldSize] = drawing.colorArray[x + y * _uiState.value.fieldSize]
                }
            }*/

            /*for (y in 0 until _uiState.value.fieldSize){
                for (x in 0 until _uiState.value.fieldSize){
                    _uiState.value.fieldPoints[Offset(x + 0.5f, y + 0.5f)] = drawing.colorArray[x + y * _uiState.value.fieldSize]
                }
            }*/
        }
    }

}

data class DrawingUIState(
    val fieldSize: Int = 16,
    val fieldScale: Float = 50f,
    val paintColor: Int = Color.White.toArgb(),
//    val fieldPoints: SnapshotStateMap<Offset, Int> = mutableStateMapOf(),
    /*val bitmapColors: MutableList<Int> = mutableListOf<Int>().apply {
        addAll(IntArray(fieldSize * fieldSize) { Color.Black.toArgb()}.toList())
    }*/
    val bitmapColors: SnapshotStateList<Int> = mutableStateListOf<Int>().apply {
        addAll(IntArray(fieldSize * fieldSize) { Color.Black.toArgb() }.toList())
    }
)
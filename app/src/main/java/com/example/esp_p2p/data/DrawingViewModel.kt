package com.example.esp_p2p.data

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.esp_p2p.data.firestore.DrawingFirestore
import com.example.esp_p2p.data.room.Drawing
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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

    var hasConnectionError = MutableTransitionState(false)
        private set

    var currentTitle by mutableStateOf("")
        private set

//    var espHostname by mutableStateOf("esp32.local")
//        private set

    var connectionErrorMessage = ""
        private set

    private var drawingId: Long? = null

    var isEditing by mutableStateOf(false)
        private set

    var isSending by mutableStateOf(false)
        private set

    /*init {
        fillDrawing(Color.Black.toArgb())
    }*/

    fun updateDialogState(){
        isColorPicking.targetState = !isColorPicking.targetState
    }

    fun onConnectionError(message: String){
        hasConnectionError.targetState = !hasConnectionError.targetState
        connectionErrorMessage = message
    }

    fun updateCurrentColor(kParam: KParameter, value: Float){
        currentColor = currentColor::copy.callBy(mapOf(kParam to value))
    }

    fun updateCurrentTitle(title: String){
        currentTitle = title
    }

//    fun updateEspHostname(hostname: String){
//        espHostname = hostname
//    }

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

    private fun updateSendingState(){
        isSending = !isSending
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

    fun postDrawing(){
        updateSendingState()
        viewModelScope.launch {
            try {
                val response = drawingRepository.postDrawing(
                    espHostname,
                    _uiState.value.bitmapColors
                )
                if (!response.isSuccessful) onConnectionError(response.message())
            }catch (ex: Exception){
                onConnectionError(ex.message!!)
            }
            updateSendingState()
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
        }
    }

    fun applyFirestoreDrawing(drawingFsId: String){
        viewModelScope.launch {
            val drawing = drawingRepository.getDrawingFirestore(drawingFsId)
                .get().await().toObject(DrawingFirestore::class.java) ?: DrawingFirestore()
            _uiState.update { currentState ->
                currentState.copy(
                    fieldSize = drawing.fieldSize,
                    fieldScale = drawing.fieldScale,
                    bitmapColors = drawing.colorArray.toMutableStateList()
                )
            }
            updateCurrentTitle(drawing.title)
        }
    }

    // Publish to Firestore
    fun sendToFirestore(uid: String){
        drawingRepository.insertDrawingFirestore(
            DrawingFirestore(
                title = currentTitle,
                uid = uid,
                fieldSize = _uiState.value.fieldSize,
                fieldScale = _uiState.value.fieldScale,
                colorArray = _uiState.value.bitmapColors
            )
        )
    }

    companion object EspHostnameHolder{
        var espHostname by mutableStateOf("esp32.local")

        fun updateEspHostname(hostname: String){
            espHostname = hostname
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
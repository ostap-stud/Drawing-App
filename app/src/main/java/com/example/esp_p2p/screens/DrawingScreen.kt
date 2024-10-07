package com.example.esp_p2p.screens

import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import com.example.esp_p2p.data.DrawingViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.esp_p2p.R
import com.example.esp_p2p.components.ColorDialog
import com.example.esp_p2p.components.ConnectionErrorDialog
import com.example.esp_p2p.data.DrawingUIState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.StateFlow

/*@Preview(showBackground = true)
@Composable
private fun DrawingScreenPrev() {
    DrawingScreen(Modifier.fillMaxSize())
}*/

@Composable
fun DrawingScreen(
    modifier: Modifier = Modifier,
    viewModel: DrawingViewModel = viewModel(),
    drawingId: Any? = null,
    navigateToSignIn: () -> Unit
) {
    if (drawingId != null){
        LaunchedEffect(key1 = drawingId) {
            if (drawingId is Long){
                viewModel.applyDrawing(drawingId)
            } else if (drawingId is String){
                viewModel.applyFirestoreDrawing(drawingId)
            }
        }
    }
    DrawingScreenMain(
        modifier = modifier,
        isEditing = { viewModel.isEditing },
        getDrawState = { viewModel.uiState },
        getTitle = { viewModel.currentTitle },
        onTitleChange = { title -> viewModel.updateCurrentTitle(title) },
        getEspHostname = { DrawingViewModel.espHostname },
        onEspHostnameChange = { hostname -> DrawingViewModel.updateEspHostname(hostname) },
        onColorClick = { viewModel.updateDialogState() },
        onSaveClick = { viewModel.insertDrawing() },
        onUpdateClick = { viewModel.updateDrawing() },
        onNewDrawingClick = { viewModel.resetUIState() },
        onSendClick = { viewModel.postDrawing() },
        isSending = { viewModel.isSending },
        onFillClick = { color -> viewModel.fillDrawing(color) },
        onPublishClick = {
            val user = Firebase.auth.currentUser
            if (user != null) viewModel.sendToFirestore(user.uid)
            else navigateToSignIn()
        }
    )
    ColorDialog(
        isEnabled = { viewModel.isColorPicking },
        onDismissRequest = { viewModel.updateDialogState() },
        onConfirmation = { viewModel.changePaintColor() },
        getPaintColor = { viewModel.currentColor },
        afterVisibleChange = { viewModel.resetCurrentColor() },
        onUpdateCurrentColor = { kParam, value -> viewModel.updateCurrentColor(kParam, value) }
    )
    ConnectionErrorDialog(
        isEnabled = { viewModel.hasConnectionError },
        onDismissRequest = { viewModel.onConnectionError("") },
        errorMessage = { viewModel.connectionErrorMessage },
    )
}

@Composable
private fun DrawingScreenMain(
    modifier: Modifier = Modifier,
    isEditing: () -> Boolean,
    getDrawState: () -> StateFlow<DrawingUIState>,
    getTitle: () -> String,
    onTitleChange: (String) -> Unit,
    getEspHostname: () -> String,
    onEspHostnameChange: (String) -> Unit,
    onColorClick: () -> Unit,
    onSaveClick: () -> Unit,
    onUpdateClick: () -> Unit,
    onNewDrawingClick: () -> Unit,
    onSendClick: () -> Unit,
    isSending: () -> Boolean,
    onFillClick: (Int) -> Unit,
    onPublishClick: () -> Unit
){
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        val mainUIState = getDrawState().collectAsState().value
        Text(
            text = "${if (isEditing()) "Editing" else "Drawing"} Mode",
            style = MaterialTheme.typography.headlineMedium
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val availableSize = mainUIState.fieldSize.pxToDp() * mainUIState.fieldScale
            Box(
                modifier = Modifier.size(availableSize),
                contentAlignment = Alignment.Center
            ) {
                DrawingCard(
                    getDrawState = { mainUIState }
                )
            }
            Row(
                modifier = Modifier
                    .width(availableSize)
                    .padding(top = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = getTitle(),
                    onValueChange = onTitleChange,
                    modifier = Modifier.weight(0.6f),
                    singleLine = true,
                    label = { Text(text = stringResource(R.string.input_title)) }
                )
                IconButton(
                    onClick = if (isEditing()) onUpdateClick else onSaveClick,
                ) {
                    Icon(
                        painter = painterResource(id = if (isEditing()) R.drawable.update_24 else R.drawable.star_outline_24),
                        contentDescription = "Save/Update Icon Button",
                        modifier = Modifier.size(27.dp)
                    )
                }
                IconButton(onClick = onColorClick) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .border(3.dp, MaterialTheme.colorScheme.primaryContainer, CircleShape)
                            .padding(3.dp)
                            .clip(CircleShape)
                            .background(Color(mainUIState.paintColor))
                            .clearAndSetSemantics { contentDescription = "Color Dialog Button" }
                    )
                }
                IconButton(onClick = { onFillClick(mainUIState.paintColor) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.bucket_fill_24),
                        contentDescription = "Fill Icon Button",
                        modifier = Modifier.size(27.dp)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .width(availableSize)
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                if (isEditing()){
                    Button(onClick = onNewDrawingClick) {
                        Text(text = "New Drawing")
                    }
                }

                Button(onClick = onPublishClick) {
                    Text(text = "Publish")
                }
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ESP domain / IP",
                style = MaterialTheme.typography.headlineSmall
            )
            TextField(
                modifier = Modifier.padding(vertical = 10.dp),
                value = getEspHostname(),
                onValueChange = onEspHostnameChange,
                singleLine = true
            )
            if (isSending()){
                CircularProgressIndicator()
            }else{
                Button(onClick = onSendClick) {
                    Text(text = "Send")
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun DrawingCard(
    getDrawState: () -> DrawingUIState
) {
    val fieldSize = getDrawState().fieldSize
    val fieldSizeDp = (fieldSize).pxToDp()
    val fieldScale = getDrawState().fieldScale
    val paintColor = getDrawState().paintColor
//    val fieldPoints = getDrawState().fieldPoints
    val bitmapColors = getDrawState().bitmapColors

    val paintSize = 1f
    Canvas(
        modifier = Modifier
            .clearAndSetSemantics { contentDescription = "Drawing Canvas" }
            .size(fieldSizeDp)
            .scale(fieldScale)
            .clipToBounds()
            .pointerInteropFilter { event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        val xmod = event.x.mod(fieldScale)
                        val ymod = event.y.mod(fieldScale)
                        val x = (event.x.dp.value - xmod) / fieldScale
                        val y = (event.y.dp.value - ymod) / fieldScale
                        if (x.toInt() in 0 until fieldSize && y.toInt() in 0 until fieldSize) {
                            bitmapColors[(x + y * fieldSize).toInt()] = paintColor
                            val xDraw = x + 0.5f
                            val yDraw = y + 0.5f
//                            fieldPoints[Offset(xDraw, yDraw)] = paintColor
                            true
                        } else {
                            false
                        }
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val xmod = event.x.mod(fieldScale)
                        val ymod = event.y.mod(fieldScale)
                        val x = (event.x.dp.value - xmod) / fieldScale
                        val y = (event.y.dp.value - ymod) / fieldScale
                        if (x.toInt() in 0 until fieldSize && y.toInt() in 0 until fieldSize) {
                            bitmapColors[(x + y * fieldSize).toInt()] = paintColor
                            val xDraw = x + 0.5f
                            val yDraw = y + 0.5f
//                            fieldPoints[Offset(xDraw, yDraw)] = paintColor
                            true
                        } else {
                            false
                        }
                    }

                    else -> false
                }
            }
    ){
//        Log.d("CanvasSize", "Width: ${size.width} || Height: ${size.height}")

        /*fieldPoints.forEach { pointColor ->
            drawPoints(listOf(pointColor.key), PointMode.Points, Color(pointColor.value), paintSize)
        }*/

        for (y in 0 until fieldSize){
            for (x in 0 until fieldSize){
                drawPoints(
                    listOf(Offset(x + 0.5f, y + 0.5f)),
                    PointMode.Points,
                    Color(bitmapColors[x + y * fieldSize]),
                    paintSize
                )
            }
        }

    }
}

@Composable
fun Int.pxToDp() = with(LocalDensity.current) {this@pxToDp.toDp()}




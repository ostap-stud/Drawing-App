package com.example.esp_p2p.screens

import android.content.res.Resources
import android.graphics.drawable.shapes.Shape
import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.sharp.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Shapes
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.esp_p2p.data.DrawingViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.esp_p2p.R
import com.example.esp_p2p.components.ColorDialog
import com.example.esp_p2p.data.DrawingUIState
import kotlinx.coroutines.flow.StateFlow

@Preview(showBackground = true)
@Composable
private fun DrawingScreenPrev() {
    DrawingScreen(Modifier.fillMaxSize())
}

@Composable
fun DrawingScreen(
    modifier: Modifier = Modifier,
    viewModel: DrawingViewModel = viewModel(),
    drawingId: Long? = null
) {
    if (drawingId != null){
        LaunchedEffect(key1 = drawingId) {
            viewModel.applyDrawing(drawingId)
        }
    }
    DrawingScreenMain(
        modifier = modifier,
        isEditing = { viewModel.isEditing },
        getDrawState = { viewModel.uiState },
        getTitle = { viewModel.currentTitle },
        onTitleChange = { title -> viewModel.updateCurrentTitle(title) },
        onColorClick = { viewModel.updateDialogState() },
        onSaveClick = { viewModel.insertDrawing() },
        onUpdateClick = { viewModel.updateDrawing() },
        onNewDrawingClick = { viewModel.resetUIState() },
        onFillClick = { color -> viewModel.fillDrawing(color) }
    )
    ColorDialog(
        isEnabled = { viewModel.isColorPicking },
        onDismissRequest = { viewModel.updateDialogState() },
        onConfirmation = { viewModel.changePaintColor() },
        getPaintColor = { viewModel.currentColor },
        afterVisibleChange = { viewModel.resetCurrentColor() },
        onUpdateCurrentColor = { kParam, value -> viewModel.updateCurrentColor(kParam, value) }
    )
}

@Composable
private fun DrawingScreenMain(
    modifier: Modifier = Modifier,
    isEditing: () -> Boolean,
    getDrawState: () -> StateFlow<DrawingUIState>,
    getTitle: () -> String,
    onTitleChange: (String) -> Unit,
    onColorClick: () -> Unit,
    onSaveClick: () -> Unit,
    onUpdateClick: () -> Unit,
    onNewDrawingClick: () -> Unit,
    onFillClick: (Int) -> Unit
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
            if (isEditing()){
                Button(modifier = Modifier.padding(top = 10.dp), onClick = onNewDrawingClick) {
                    Text(text = "New Drawing")
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




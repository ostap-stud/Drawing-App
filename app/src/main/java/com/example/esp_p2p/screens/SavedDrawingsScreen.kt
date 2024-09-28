package com.example.esp_p2p.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.esp_p2p.data.SavedDrawingsViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.esp_p2p.R
import com.example.esp_p2p.data.room.Drawing

@Composable
fun SavedDrawingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SavedDrawingsViewModel = viewModel(),
    onEditItem: (Long) -> Unit
){
    val savedUIState = viewModel.uiState.collectAsState()
    LazyColumn(
        modifier = modifier
    ) {
        items(savedUIState.value.drawingList, { it.id!! }){ drawing ->
            SavedDrawingsItem(
                drawing = drawing,
                onDeleteItem = { viewModel.deleteDrawing(it) },
                onEditItem = onEditItem
            )
        }
    }
}

@Composable
fun SavedDrawingsItem(
    drawing: Drawing,
    modifier: Modifier = Modifier,
    onDeleteItem: (Drawing) -> Unit,
    onEditItem: (Long) -> Unit
){
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp)
    ) {
        val availableSize = drawing.fieldSize.pxToDp() * (drawing.fieldScale / 2)
        Row(
            modifier = Modifier.fillMaxWidth().height(availableSize),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.size(availableSize),
                contentAlignment = Alignment.Center
            ){
                Canvas(
                    modifier = Modifier
                        .size(drawing.fieldSize.pxToDp())
                        .scale(drawing.fieldScale / 2)
                ) {
                    for (y in 0 until drawing.fieldSize){
                        for (x in 0 until drawing.fieldSize){
                            drawPoints(
                                points =  listOf(Offset(x + 0.5f, y + 0.5f)),
                                pointMode =  PointMode.Points,
                                color =  Color(drawing.colorArray[x + y * drawing.fieldSize]),
                                strokeWidth = 1f
                            )
                        }
                    }
                }
            }
            Text(
                text = drawing.title.ifBlank {
                    stringResource(id = R.string.no_title)
                },
                modifier = Modifier.padding(start = 10.dp),
                style = MaterialTheme.typography.headlineSmall
            )
            Column(
                modifier = Modifier.fillMaxHeight().wrapContentWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { onEditItem(drawing.id!!) }) {
                    Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit Drawing Icon Button")
                }
                IconButton(onClick = { onDeleteItem(drawing) }) {
                    Icon(imageVector = Icons.Filled.Delete, tint = MaterialTheme.colorScheme.error, contentDescription = "Delete Drawing Icon Button")
                }
            }
        }
    }
}
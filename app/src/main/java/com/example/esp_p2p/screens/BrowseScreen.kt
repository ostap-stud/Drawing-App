package com.example.esp_p2p.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.esp_p2p.R
import com.example.esp_p2p.data.BrowseViewModel
import com.example.esp_p2p.data.firestore.DrawingFirestore
import com.example.esp_p2p.data.firestore.UserModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(
    modifier: Modifier = Modifier,
    viewModel: BrowseViewModel = viewModel(),
    onItemClick: (String) -> Unit
) {
    LazyVerticalStaggeredGrid(
        modifier = modifier,
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 30.dp,
        contentPadding = PaddingValues(top = 30.dp, bottom = 30.dp, start = 15.dp, end = 15.dp)
    ){
        itemsIndexed(viewModel.drawings){ index, drawing ->
            BrowseGridItem(
                drawing = drawing,
                getUser = { viewModel.getUser(drawing.uid) },
                onItemClick = onItemClick,
                modifier = Modifier
                    .padding( top =
                        if (index == 0 || index == 1) 100.dp
                        else 0.dp
                    )
            )
        }
    }
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ){
        SearchBar(
            modifier = Modifier,
            query = viewModel.searchQuery,
            onQueryChange = { newSearchQuery -> viewModel.onSearchQueryChange(newSearchQuery) },
            onSearch = { searchQuery -> viewModel.onSearchQueryChange(searchQuery) },
            active = false,
            onActiveChange = {  },
            placeholder = { Text(text = "Search") },
            leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = "Search Bar Icon") }
        ) { }
    }
    DisposableEffect(viewModel) {
        onDispose {
            viewModel.onDispose()
        }
    }
}

@Composable
fun BrowseGridItem(
    modifier: Modifier = Modifier,
    drawing: DrawingFirestore,
    onItemClick: (String) -> Unit,
    getUser: suspend () -> UserModel
){
    var user by remember {
        mutableStateOf(UserModel())
    }
    var isLoading by remember {
        mutableStateOf(true)
    }
    LaunchedEffect(key1 = drawing) {
        user = getUser()
        isLoading = false
    }
    Card(
        modifier = modifier.wrapContentWidth().clickable { onItemClick(drawing.id) }
    ) {
        val availableSize = drawing.fieldSize.pxToDp() * (drawing.fieldScale / 2)
        Column(
            modifier = Modifier.width(availableSize)
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
            AnimatedVisibility(visible = !isLoading) {
                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = drawing.title.ifBlank {
                            stringResource(id = R.string.no_title)
                        },
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "by ${user.name}", modifier = Modifier.fillMaxWidth(0.8f).padding(end = 5.dp))
                        AsyncImage(
                            model = user.photoUrl,
                            modifier = Modifier
                                .size(25.dp)
                                .clip(CircleShape),
                            contentDescription = "User's Avatar"
                        )
                    }
                }
            }
        }
    }
}
package com.example.esp_p2p

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class ESPDestinations(val icon: ImageVector, val route: String){
    data object SavedDrawingsDestination : ESPDestinations (Icons.Filled.Star, "saved")
    data object DrawingDestination : ESPDestinations (Icons.Filled.Edit, "drawing")
    data object BrowseDrawingDestination : ESPDestinations (Icons.Filled.Add, "browse")
    data object SignInDrawingDestination : ESPDestinations (Icons.Filled.AccountCircle, "account")
    data object EditDrawingDestination : ESPDestinations(Icons.Filled.Build, "editing"){
        const val drawingId = "id"
        val routeWithArgs = "$route/{$drawingId}"
        val args = listOf(
            navArgument(drawingId) { type = NavType.LongType }
        )
    }
    data object LoadedDrawingDestination : ESPDestinations(Icons.Filled.Build, "loaded"){
        const val drawingFsId = "id"
        val routeWithArgs = "$route/{$drawingFsId}"
        val args = listOf(
            navArgument(drawingFsId) { type = NavType.StringType }
        )
    }
}

val appDestinations = listOf(
    ESPDestinations.SavedDrawingsDestination,
    ESPDestinations.DrawingDestination,
    ESPDestinations.BrowseDrawingDestination,
    ESPDestinations.SignInDrawingDestination
)
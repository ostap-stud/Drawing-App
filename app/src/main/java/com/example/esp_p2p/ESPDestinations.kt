package com.example.esp_p2p

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class ESPDestinations(val icon: ImageVector, val route: String){
    data object SavedDrawingsDestination : ESPDestinations (Icons.Filled.Star, "saved")
    data object DrawingDestination : ESPDestinations (Icons.Filled.Edit, "drawing")
    data object PickImageDestination : ESPDestinations (Icons.Filled.Add, "picking")
    data object EditDrawingDestination : ESPDestinations(Icons.Filled.Build, "editing"){
        const val drawingId = "id"
        val routeWithArgs = "$route/{$drawingId}"
        val args = listOf(
            navArgument(drawingId) { type = NavType.LongType }
        )
    }
}

val appDestinations = listOf(
    ESPDestinations.SavedDrawingsDestination,
    ESPDestinations.DrawingDestination,
    ESPDestinations.PickImageDestination
)
package com.example.esp_p2p

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.esp_p2p.screens.DrawingScreen
import com.example.esp_p2p.screens.PickingScreen
import com.example.esp_p2p.screens.SavedDrawingsScreen

@Composable
fun ESPNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = ESPDestinations.DrawingDestination.route
    ) {
        composable(
            route = ESPDestinations.DrawingDestination.route
        ){
            DrawingScreen(modifier = Modifier.fillMaxSize(), viewModel = hiltViewModel())
        }
        composable(
            route = ESPDestinations.SavedDrawingsDestination.route
        ){
            SavedDrawingsScreen(
                modifier = Modifier.fillMaxSize(),
                viewModel = hiltViewModel(),
                onEditItem = { drawingId -> navController.navigateToEditDrawing(drawingId) }
            )
        }
        composable(
            route = ESPDestinations.PickImageDestination.route
        ){
            PickingScreen(modifier = Modifier.fillMaxSize())
        }
        composable(
            route = ESPDestinations.EditDrawingDestination.routeWithArgs,
            arguments = ESPDestinations.EditDrawingDestination.args
        ){ currentBackStackEntry ->
            val drawingId = currentBackStackEntry.arguments?.getLong("id")
            DrawingScreen(modifier = Modifier.fillMaxSize(), viewModel = hiltViewModel(), drawingId = drawingId)
        }
    }
}

fun NavHostController.navigateToSingleTop(route: String) =
    this.navigate(route) {
        val isNotInEdit = this@navigateToSingleTop.currentBackStackEntry?.destination?.route != ESPDestinations.EditDrawingDestination.routeWithArgs
        if (isNotInEdit || route == ESPDestinations.DrawingDestination.route){
            popUpTo(route = this@navigateToSingleTop.graph.startDestinationRoute!!) {
                if (isNotInEdit)
                    saveState = true
            }
        } else {
            popUpTo(route = this@navigateToSingleTop.previousBackStackEntry?.destination?.route!!)
        }
        launchSingleTop = true
        restoreState = true
    }

fun NavHostController.navigateToEditDrawing(drawingId: Long) =
    this.navigate("${ESPDestinations.EditDrawingDestination.route}/$drawingId"){
        launchSingleTop = true
    }
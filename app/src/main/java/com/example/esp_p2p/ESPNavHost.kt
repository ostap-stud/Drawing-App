package com.example.esp_p2p

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.esp_p2p.screens.DrawingScreen
import com.example.esp_p2p.screens.BrowseScreen
import com.example.esp_p2p.screens.SavedDrawingsScreen
import com.example.esp_p2p.screens.SignInScreen

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
            DrawingScreen(
                modifier = Modifier.fillMaxSize(),
                viewModel = hiltViewModel(),
                navigateToSignIn = { navController.navigateToSingleTop(ESPDestinations.SignInDrawingDestination.route) }
            )
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
            route = ESPDestinations.BrowseDrawingDestination.route
        ){
            BrowseScreen(
                modifier = Modifier.fillMaxSize(),
                viewModel = hiltViewModel(),
                onItemClick = { id -> navController.navigateToLoadedDrawing(id) }
            )
        }
        composable(
            route = ESPDestinations.SignInDrawingDestination.route
        ){
            SignInScreen(
                modifier = Modifier.fillMaxSize(),
                viewmodel = hiltViewModel()
            )
        }
        composable(
            route = ESPDestinations.EditDrawingDestination.routeWithArgs,
            arguments = ESPDestinations.EditDrawingDestination.args
        ){ currentBackStackEntry ->
            val drawingId = currentBackStackEntry.arguments?.getLong("id")
            DrawingScreen(
                modifier = Modifier.fillMaxSize(),
                viewModel = hiltViewModel(),
                drawingId = drawingId,
                navigateToSignIn = { navController.navigateToSingleTop(ESPDestinations.SignInDrawingDestination.route) }
            )
        }
        composable(
            route = ESPDestinations.LoadedDrawingDestination.routeWithArgs,
            arguments = ESPDestinations.LoadedDrawingDestination.args
        ){ currentBackStackEntry ->
            val drawingFsId = currentBackStackEntry.arguments?.getString("id")
            DrawingScreen(
                modifier = Modifier.fillMaxSize(),
                viewModel = hiltViewModel(),
                drawingId = drawingFsId,
                navigateToSignIn = { navController.navigateToSingleTop(ESPDestinations.SignInDrawingDestination.route) }
            )
        }
    }
}

fun NavHostController.navigateToSingleTop(route: String) =
    this.navigate(route) {
        val currentRoute = this@navigateToSingleTop.currentBackStackEntry?.destination?.route
        val previousRoute = this@navigateToSingleTop.previousBackStackEntry?.destination?.route
        val isNotInDrawingId = currentRoute != ESPDestinations.EditDrawingDestination.routeWithArgs
                && currentRoute != ESPDestinations.LoadedDrawingDestination.routeWithArgs
        val isNotToPrevious = route != previousRoute
        if (isNotInDrawingId || isNotToPrevious){
            popUpTo(route = this@navigateToSingleTop.graph.startDestinationRoute!!) {
                if (isNotInDrawingId)
                    saveState = true
            }
        } else {
            popUpTo(route = previousRoute!!)
        }
        launchSingleTop = true
        restoreState = true
    }

fun NavHostController.navigateToEditDrawing(drawingId: Long) =
    this.navigate("${ESPDestinations.EditDrawingDestination.route}/$drawingId"){
        launchSingleTop = true
    }

fun NavHostController.navigateToLoadedDrawing(drawingId: String) =
    this.navigate("${ESPDestinations.LoadedDrawingDestination.route}/$drawingId"){
        launchSingleTop = true
    }
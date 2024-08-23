package com.example.esp_p2p

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun ESPAppScaffold(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val currentEntryDestination = currentBackStackEntry?.destination
                appDestinations.forEach { destination ->
                    NavigationBarItem(
                        selected = destination.route == currentEntryDestination?.route,
                        onClick = { navController.navigateToSingleTop(destination.route) },
                        icon = { Icon(imageVector = destination.icon, contentDescription = "Destination Icon") },
                        label = { Text(text = destination.route.replaceFirstChar { it.uppercase() }) }
                    )
                }
            }
        }
    ) { innerPadding ->
        ESPNavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController
        )
    }
}
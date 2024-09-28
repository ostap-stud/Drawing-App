package com.example.esp_p2p.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.unit.dp

@Composable
fun ConnectionErrorDialog(
    isEnabled: () -> MutableTransitionState<Boolean>,
    onDismissRequest: () -> Unit,
    errorMessage: () -> String
){
    AnimatedDialog(isEnabled = isEnabled, onDismissRequest = onDismissRequest) {
        var errorMessageVisible by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(modifier = Modifier.height(100.dp), contentAlignment = Alignment.Center){
                Text(text = "Error occurred", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.error)
            }
            Text(modifier = Modifier.padding(start = 10.dp, end = 10.dp), text = "Could not connect to specified server.\nCheck your connection settings and domain name / IP-address of the server.")
            TextButton(modifier = Modifier.align(Alignment.Start), onClick = { errorMessageVisible = !errorMessageVisible }) {
                Text(text = "${if(!errorMessageVisible) "Show" else "Hide"} message")
            }
            AnimatedVisibility(visible = errorMessageVisible) {
                Box(modifier = Modifier.background(color = MaterialTheme.colorScheme.errorContainer)){
                    Text(modifier = Modifier.padding(10.dp), text = errorMessage(), color = MaterialTheme.colorScheme.error)
                }
            }
            Button(modifier = Modifier.padding(top = 10.dp, bottom = 10.dp), onClick = onDismissRequest) {
                Text(text = "OK")
            }
        }
    }
}
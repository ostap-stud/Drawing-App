package com.example.esp_p2p.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseInElastic
import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.EaseOutElastic
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun AnimatedDialog(
    isEnabled: () -> MutableTransitionState<Boolean>,
    onDismissRequest: () -> Unit,
    enterTransition: EnterTransition =
        slideInHorizontally(
            animationSpec = tween(durationMillis = 300)
        ) + fadeIn(),
    exitTransition: ExitTransition =
        slideOutHorizontally(
            animationSpec = tween(durationMillis = 300)
        ) + fadeOut(),
    content: @Composable () -> Unit
){
    var showAnimatedDialog by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = isEnabled().targetState) {
        if (isEnabled().targetState) showAnimatedDialog = true
    }
    if (showAnimatedDialog) {
        Dialog(onDismissRequest = onDismissRequest) {
            AnimatedVisibility(
                visibleState = isEnabled(),
                enter = enterTransition,
                exit = exitTransition
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    content()
                }
                DisposableEffect(Unit) {
                    onDispose {
                        showAnimatedDialog = false
                    }
                }
            }
        }
    }
}
package com.example.esp_p2p.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.TransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlin.reflect.KParameter
import kotlin.reflect.full.declaredMemberProperties

@Composable
private fun ColorDialogItem(
    parameter: Char,
    sliderValue: () -> Float,
    onSliderValueChange: (Float) -> Unit,
    onSliderChangeFinished: () -> Unit = { }
){
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(text = "$parameter: ${(sliderValue() * 255).toInt()}", modifier = Modifier.weight(0.2f))
        Slider(
            value = sliderValue(),
            onValueChange = onSliderValueChange,
            onValueChangeFinished = onSliderChangeFinished,
            valueRange = 0f..1f,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ColorDialogList(
    modifier: Modifier = Modifier,
    getPaintColor: () -> Color,
    onUpdateCurrentColor: (KParameter, Float) -> Unit
){
    val currentColor = getPaintColor()
    val colorCopyProps = currentColor::copy::parameters.get()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        colorCopyProps.forEach{ kParam ->
            ColorDialogItem(
                parameter = kParam.name?.first()?.uppercaseChar() ?: '?',
                sliderValue = {
                    currentColor::class.declaredMemberProperties.first {
                        it.name == kParam.name!!
                    }.getter.call(currentColor) as Float
                },
                onSliderValueChange = {
                    onUpdateCurrentColor(kParam, it)
                }
            )
        }
    }
}

@Composable
fun ColorDialog(
    isEnabled: () -> MutableTransitionState<Boolean>,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    getPaintColor: () -> Color,
    afterVisibleChange: () -> Unit,
    onUpdateCurrentColor: (KParameter, Float) -> Unit
) {
    AnimatedDialog(
        isEnabled = isEnabled,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(getPaintColor()),
                contentAlignment = Alignment.Center
            ) {
                val isSystemInDark = isSystemInDarkTheme()
//                val current = getPaintColor().toArgb() xor 0x00ffffff
                val animatedTextColor by animateColorAsState(
                    targetValue = /*Color(current)*/if(getPaintColor().luminance() >= 0.5f){
                        if (isSystemInDark) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                    }else{
                        if (isSystemInDark) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary
                    },
                    animationSpec = tween(400),
                    label = "Animated Text Color"
                )
                Text(
                    text = "Painting color",
                    style = MaterialTheme.typography.headlineSmall,
                    color = animatedTextColor
                )
            }
            ColorDialogList(
                getPaintColor = getPaintColor,
                onUpdateCurrentColor = onUpdateCurrentColor
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(onClick = { onDismissRequest() }) {
                    Text(text = "Cancel")
                }
                TextButton(onClick = { onConfirmation() }) {
                    Text(text = "Confirm")
                }
            }
        }
    }
    if (!isEnabled().currentState && !isEnabled().targetState){
        afterVisibleChange()
    }
}
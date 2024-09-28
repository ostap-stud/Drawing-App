package com.example.esp_p2p.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.FixedScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.example.esp_p2p.R
import com.example.esp_p2p.data.SignInViewModel
import com.example.esp_p2p.ui.theme.SuccessDark
import com.example.esp_p2p.ui.theme.SuccessLight
import com.example.esp_p2p.ui.theme.WarningDark
import com.example.esp_p2p.ui.theme.WarningLight

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    viewmodel: SignInViewModel = viewModel()
){
    val context = LocalContext.current

    if (!viewmodel.isSignedIn){
        SignInContent(
            modifier = modifier,
            isSignedIn = false,
            button = {
                SignInWithGoogleButton(
                    onClick = { viewmodel.signInWithGoogle(context) }
                )
            }
        )
    }else{
        SignInContent(
            modifier = modifier,
            isSignedIn = true,
            userName = "Hi, ${viewmodel.auth.currentUser?.displayName ?: "Error"}!" ,
            userEmail = viewmodel.auth.currentUser?.email ?: "Error",
            image = {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(viewmodel.auth.currentUser?.photoUrl.toString().replace("s96", "s384"))
                        .size(Size.ORIGINAL)
                        .crossfade(true)
                        .build(),
                    contentDescription = "User's Avatar",
                    modifier = Modifier.clip(CircleShape)
                )
            },
            button = {
                Button(onClick = { viewmodel.signOut(context) }, modifier = Modifier.padding(top = 10.dp)) {
                    Text(text = "Sign Out")
                }
            }
        )
    }
}

@Composable
private fun SignInContent(
    modifier: Modifier = Modifier,
    isSignedIn: Boolean,
    userName: String = "User's name",
    userEmail: String = "User's Email",
    image: @Composable () -> Unit = {
        Icon(
            modifier = Modifier.size(384.pxToDp()),
            imageVector = Icons.Filled.Face,
            contentDescription = "User's Avatar",
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    },
    button: @Composable () -> Unit = {
        Button(onClick = {  }) {
            Text(text = "nothing")
        }
    }
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.3f),
        contentAlignment = Alignment.Center
    ){
        InfoRow(isWarning = !isSignedIn, modifier = Modifier
            .padding(10.dp)
            .size(300.dp, 100.dp)
        )
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        image()
        Text(text = userName, modifier = Modifier.padding(top = 10.dp))
        Text(text = userEmail, modifier = Modifier.padding(top = 10.dp))
        button()
    }
}

@Composable
private fun InfoRow(
    modifier: Modifier = Modifier,
    isWarning: Boolean
){
    val isDark = isSystemInDarkTheme()
    val containerColor = when{
        isWarning && isDark -> WarningDark
        isWarning -> WarningLight
        isDark -> SuccessDark
        else -> SuccessLight
    }
    val onContainerColor = when{
        isWarning && isDark -> WarningLight
        isWarning -> WarningDark
        isDark -> SuccessLight
        else -> SuccessDark
    }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(15.dp))
            .background(color = containerColor),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ){
        Icon(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp),
            imageVector = if (isWarning) Icons.Filled.Warning else Icons.Filled.CheckCircle,
            contentDescription = "Warning",
            tint = onContainerColor
        )
        Text(
            text = if (isWarning) "You must sign in to be able to publish your drawings"
            else "Now you can publish your drawings",
            color = onContainerColor
        )
    }
}

@Composable
private fun SignInWithGoogleButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
){
    val isDarkTheme = isSystemInDarkTheme()
    Surface(
        shape = CircleShape,
        color = when (isDarkTheme) {
            true -> Color(0xFF131314)
            false -> Color(0xFFFFFFFF)
        },
        modifier = Modifier
            .padding(top = 15.dp)
            .height(50.dp)
            .width(260.dp)
            .clip(CircleShape)
            .border(
                BorderStroke(
                    width = 1.dp,
                    color = when (isDarkTheme) {
                        true -> Color(0xFF8E918F)
                        false -> Color.Transparent
                    }
                ),
                shape = CircleShape
            )
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 5.dp)
        ) {
            Spacer(modifier = Modifier.width(14.dp))
            Image(
                painterResource(id = R.drawable.icons8_google),
                contentDescription = null,
                modifier = Modifier.padding(vertical = 5.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "Continue with Google")
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
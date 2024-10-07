package com.example.esp_p2p

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UITest {
    @get:Rule
    val composeRule = createAndroidComposeRule(MainActivity::class.java)

    @Test
    fun mainScreenTest_True(){
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Drawing Mode").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Drawing Canvas").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Drawing Canvas").performTouchInput {
            var x = -150f
            var y = -150f
            down(Offset(x, y))
            for (i in 0..400){
                moveTo(Offset(x++, y))
            }
            up()
            x -= 200f
            down(Offset(x, y))
            for (i in 0..400){
                moveTo(Offset(x, y++))
            }
            up()
        }

        composeRule.waitForIdle()
        Thread.sleep(1000)

        val titleTesting = "...Testing title input..."
        composeRule.onNodeWithText("Input title", useUnmergedTree = true).onParent().performTextInput(titleTesting)
        composeRule.onNodeWithText("Input title", useUnmergedTree = true).onParent().assert(hasText(titleTesting))

        composeRule.waitForIdle()
        Thread.sleep(1000)

        composeRule.onNodeWithContentDescription("Color Dialog Button").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Color Dialog Button").performClick()

        composeRule.waitForIdle()
        Thread.sleep(5000)
    }

    @Test
    fun navigateToSavedAndBack_True(){
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Saved", useUnmergedTree = true).assertIsDisplayed().performClick()

        composeRule.waitForIdle()
        Thread.sleep(2000)

        composeRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }

        composeRule.waitForIdle()
        Thread.sleep(5000)
    }
}
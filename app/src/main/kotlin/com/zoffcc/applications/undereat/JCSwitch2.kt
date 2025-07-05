@file:Suppress("RemoveRedundantQualifierName")

package com.zoffcc.applications.undereat

import android.annotation.SuppressLint
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple


/**
 * @author Isaac Akakpo
 * Created on 12/6/2021 3:57 PM
 */

/*
* Custom Switch - Functionalities
*
* 1. Change Enabled  Color (Probably the disable color too) ..
* 2. Change box to card for clickable experience ..
* 3. Add vibration feature ..x
* 4. Add Switch Type (Square Switch, Cut Corner, Rounded)
* 5. Add Switch Text/Icon Functionality
* 6. elevate the round inner circle
*
*
* */

private enum class SwitchState {
    IS_ENABLED,
    NOT_ENABLED
}


val SwitchBgDisabled  = Color(0x75A4AAA2)

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun JCSwitch(
    modifier: Modifier = Modifier,
    enabledColor: Color = MaterialTheme.colorScheme.primary,
    disabledColor: Color = SwitchBgDisabled,
    switchWidth: Dp = 50.dp,
    switchHeight: Dp = 50.dp,
    isChecked: Boolean = false,
    thumbContent: (@Composable () -> Unit)? = null,
    onCheckChanged: (Boolean) -> Unit
) {


    val switchSizeW by remember {
        mutableStateOf(switchWidth)
    }
    val switchSizeH by remember {
        mutableStateOf(switchHeight)
    }
    var currentState by remember { if (!isChecked) mutableStateOf(SwitchState.NOT_ENABLED) else  mutableStateOf(SwitchState.IS_ENABLED)}

    val bgDisabledColour by remember {
        mutableStateOf(disabledColor)
    }

    val bgEnabledColour by remember {
        mutableStateOf(enabledColor)
    }

    val transition = updateTransition(currentState, label = "SwitchState")
    val color by transition.animateColor(transitionSpec = {
        tween(200, easing = FastOutLinearInEasing)
    }, label = "") {
        when (it) {
            SwitchState.IS_ENABLED ->
                bgEnabledColour
            else -> bgDisabledColour
        }
    }
    val interactionSource = remember { MutableInteractionSource() }

    val clickable = Modifier.clickable(
        interactionSource = interactionSource,
        indication = null
    ) {
        currentState = if (currentState == SwitchState.NOT_ENABLED) {
            onCheckChanged(true)
            SwitchState.IS_ENABLED
        } else {
            onCheckChanged(false)
            SwitchState.NOT_ENABLED
        }
    }

    Box(
        modifier = Modifier
            .then(clickable)
            .indication(
                interactionSource = MutableInteractionSource(),
                indication = ripple()
            )
    ) {
        BoxWithConstraints(
            modifier = modifier
                .width(switchSizeW - 3.dp)
                .height(switchSizeH / 2)
                .indication(MutableInteractionSource(), null)
                .background(color = color, shape = RoundedCornerShape(100)),
            contentAlignment = Alignment.CenterStart
        ) {

            val roundCardSize = this.maxWidth / 2

            val xOffset by transition.animateDp(
                transitionSpec = {
                    tween(150, easing = LinearOutSlowInEasing)
                }, label = "xOffset"
            ) { state ->
                when (state) {
                    SwitchState.NOT_ENABLED -> 0.dp
                    SwitchState.IS_ENABLED -> this.maxWidth - roundCardSize
                }
            }
            Card(
                modifier = Modifier
                    .size(this.maxWidth / 2)
                    .offset(x = xOffset, y = 0.dp)
                    .padding(3.dp),
                shape = RoundedCornerShape(100),
                border = BorderStroke(
                    if (currentState == SwitchState.NOT_ENABLED) 0.5.dp else 0.dp,
                    color = Color.Transparent
                )
            ) {
                if (thumbContent != null) {
                    CompositionLocalProvider(
                        content = thumbContent
                    )
                }
            }
        }
    }


}
package com.abtasty.qa_assistant_android.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.abtasty.qa_assistant_android.R

@Composable
fun ResetAll(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    normalColor: Int = R.color.reset_all_normal,
    pressedColor: Int = R.color.reset_all_pressed
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val color by animateColorAsState(
//        targetValue = if (isPressed) colorResource(R.color.reset_all_pressed) else colorResource(R.color.reset_all_normal),
        targetValue = colorResource(if (isPressed) pressedColor else normalColor),
        label = ""
    )

    Row(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            color = color
        )
    }
}

package com.abtasty.qa_assistant_android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.abtasty.qa_assistant_android.R
import com.abtasty.qa_assistant_android.ui.screens.TargetingOR

@Composable
fun TargetingOrItem(targeting: TargetingOR) {
    var orIndex = 0
    for (targetingAND in targeting.targetingList) {
        //or
        Column(

        ) {
            var andIndex = 0

            val (backgroundColor, borderColor) = when (targetingAND.isTargetingValid()) {
                true -> (R.color.light_green to R.color.accepted)
                else -> (R.color.light_red to R.color.rejected)
            }
            Box() {
                Column(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(start = 48.dp, end = 12.dp)
                        .align(Alignment.CenterEnd)
                        .background(
                            color = colorResource(backgroundColor),
                            shape = RoundedCornerShape(20f)
                        )
                        .border(
                            width = 1.dp,
                            color = colorResource(borderColor),
                            shape = RoundedCornerShape(20f),
                        ),
                ) {

                }
                Column() {
                    for (targetingElem in targetingAND.targetingList) {
                        TargetingAndItem(targetingElem)
                        if (andIndex < (targetingAND.targetingList.size - 1)) {
                            TargetingBadge(
                                modifier = Modifier
                                    .padding(start = 60.dp),
                                text = "AND"
                            )
                        }
                        andIndex++
                    }
                }
            }

        }

        if (orIndex < (targeting.targetingList.size - 1)) {
            Row(
                Modifier
                    .fillMaxWidth(),
                verticalAlignment = CenterVertically
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.check4), //todo
                    contentDescription = null,
                    tint = colorResource(R.color.gold), //todo
                    modifier = Modifier
                        .padding(all = 12.dp)
                        .width(24.dp)
                        .height(24.dp)
                        .alpha(0f)
                )
                TargetingBadge(text = "OR")
            }

        }
        orIndex++
    }
}
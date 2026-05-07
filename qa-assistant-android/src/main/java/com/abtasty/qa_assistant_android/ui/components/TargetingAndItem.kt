package com.abtasty.qa_assistant_android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abtasty.qa_assistant_android.R
import com.abtasty.qa_assistant_android.ui.screens.Targeting

@Composable
fun TargetingAndItem(targetingElem: Targeting) {


    val isTargetingValid = targetingElem.isTargetingValid
    val color =
        if (isTargetingValid) colorResource(R.color.event_now) else colorResource(
            R.color.event_clear
        )
    val icon = if (isTargetingValid) R.drawable.check4 else R.drawable.close

    Row(
        Modifier
            .fillMaxWidth()
            .padding(end = 12.dp),
        verticalAlignment = CenterVertically
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(icon),
            contentDescription = null,
            tint = color,
            modifier = Modifier
                .padding(all = 12.dp)
                .width(24.dp)
                .height(24.dp)
                .alpha(1f),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            if (targetingElem.key == "fs_all_users") {
                Text(
                    modifier = Modifier,
                    text = "All users",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(R.color.text_bold),
                    fontSize = 14.sp,
                )
            } else {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                        8.dp
                    ),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        text = targetingElem.key,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        color = colorResource(R.color.text_bold),
                        fontSize = 14.sp,
                    )
                    Text(
                        modifier = Modifier
                            .background(
                                color = colorResource(R.color.white_bg),
                                shape = RoundedCornerShape(50)
                            )
                            .border(
                                width = 1.dp,
                                color = colorResource(R.color.cell_border),
                                shape = RoundedCornerShape(50)
                            )
                            .padding(all = 8.dp),
                        text = targetingElem.operator.replace("_", " "),
                        fontWeight = FontWeight.SemiBold,
                        color = colorResource(R.color.text_bold),
                        fontSize = 14.sp,
                    )
                    for (value in targetingElem.values) {
                        Text(
                            modifier = Modifier
                                .background(
                                    color = colorResource(R.color.primary_background),
                                    shape = RoundedCornerShape(50)
                                )
                                .padding(all = 8.dp),
                            text = value.toString(),
                            fontWeight = FontWeight.SemiBold,
                            color = colorResource(R.color.selected_tab),
                            fontSize = 14.sp,
                        )
                    }
                }
            }
        }
    }
}

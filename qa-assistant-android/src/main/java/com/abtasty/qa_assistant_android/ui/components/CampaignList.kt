package com.abtasty.qa_assistant_android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abtasty.qa_assistant_android.R
import com.abtasty.qa_assistant_android.ui.screens.ChildItem
import com.abtasty.qa_assistant_android.ui.screens.ParentItem
import com.abtasty.qa_assistant_android.ui.screens.Status


@Composable
fun CampaignListHeader(
    parentItem: ParentItem,
    expanded: Boolean,
    onClick: () -> Unit,
    isLast: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .height(1.dp)
                .background(Color.LightGray)
                .fillMaxWidth()
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(top = 12.dp, bottom = 12.dp)
               ,

            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,

        ) {
            val iconResource =
                if (expanded) ImageVector.vectorResource(R.drawable.icon_caret_top) else ImageVector.vectorResource(
                    R.drawable.icon_caret_down
                )
            val color = when (parentItem.status) {
                Status.Accepted -> colorResource(R.color.accepted)
                else -> colorResource(R.color.rejected)
            }
            val statusTitle = when (parentItem.status) {
                Status.Accepted -> "Accepted"
                else -> "Rejected"
            }
            Icon(
                imageVector = iconResource,
                contentDescription = null,
                modifier = Modifier
                    .padding(
                        start = 12.dp,
                        end = 8.dp
                    )
                    .weight(0.11f)
            )
            PillBadge(
                label = statusTitle,
                modifier = Modifier
                    .weight(0.22f)
                ,
                backgroundColor = color
            )
            BasicText(
                "${parentItem.children.size} campaigns",
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(0.67f)
                ,
                style = TextStyle(
                    color = colorResource(R.color.text_light_grey)
                )
            )
        }
        Box(
            modifier = Modifier
                .height(if (expanded || isLast) 1.dp else 0.dp)
                .background(Color.LightGray)
                .fillMaxWidth()
        )
    }
}



@Composable
fun CampaignListItem(child: ChildItem, onClick: () -> Unit) {
    val color = when (child.status) {
        Status.Accepted -> colorResource(R.color.accepted)
        Status.Forced -> colorResource(R.color.forced)
        Status.Hidden -> colorResource(R.color.forced)
        else -> colorResource(R.color.rejected)
    }
    val statusTitle = when (child.status) {
        Status.AllocationRejected -> Status.Rejected.title
        Status.TargetingRejected -> Status.Rejected.title
        else -> child.status.title
    }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isPressed) colorResource(R.color.item_pressed) else Color.Transparent
            )
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(
                top = 8.dp,
                bottom = 8.dp,
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Column(
            modifier = Modifier
                .padding(start = 12.dp, end = 8.dp)
                .weight(0.67f)
        ) {
            BasicText(
                modifier = Modifier
                    .basicMarquee(
                        iterations = Int.MAX_VALUE,
                        initialDelayMillis = 1000,
                        repeatDelayMillis = 3000,
                        velocity = 40.dp
                    ),
                text = child.label,
                style = TextStyle(
                    color = colorResource(R.color.text_bold),
                    fontSize = 18.sp,
                ),
                maxLines = 1
            )
            BasicText(
                text = child.details,
                style = TextStyle(
                    color = colorResource(R.color.text_light_grey),
                    fontSize = 16.sp
                ),
                maxLines = 1
            )
        }
        PillBadge(
            label = statusTitle,
            modifier = Modifier
                .weight(0.22f)
            ,
            backgroundColor = color
        )
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.icon_caret_right),
            contentDescription = null,
            modifier = Modifier
                .padding(
                    start = 8.dp
                )
                .weight(0.11f)

        )

    }
}
package com.abtasty.qa_assistant_android.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abtasty.flagship.hits.Hit
import com.abtasty.qa_assistant_android.R
import com.abtasty.qa_assistant_android.ui.screens.timeAgo
import com.google.android.material.bottomsheet.BottomSheetBehavior

@Composable
fun EventExpandableList(hits: List<Hit.HitDTO>, behavior: BottomSheetBehavior<*>) {

    val lazyListState = rememberLazyListState()

    val listNotAtTop by remember {
        derivedStateOf { lazyListState.canScrollBackward }
    }

    DisposableEffect(listNotAtTop) {
        behavior.isDraggable = !listNotAtTop
        onDispose {
            behavior.isDraggable = true
        }
    }


    LazyColumn(
        state = lazyListState,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        items(
            count = hits.size,
            key = { index -> "${hits[index].id}_$index" }
        ) { item ->
            EventExpandableRow(hit = hits[item])
        }
    }
}

@Composable
fun EventExpandableRow(hit: Hit.HitDTO) {

    var expanded by rememberSaveable(hit.id) { mutableStateOf(false) }

    val timeString = timeAgo(hit.timestamp)
    val timeColor =
        colorResource(if (timeString.contains("Just now")) R.color.event_now else R.color.text_light_grey)
    val iconResource =
        if (expanded) ImageVector.vectorResource(R.drawable.icon_caret_top) else ImageVector.vectorResource(
            R.drawable.icon_caret_down
        )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = { expanded = !expanded }
            )
    ) {
        Box(
            modifier = Modifier
                .height(1.dp)
                .background(Color.LightGray)
                .fillMaxWidth()
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            PillBadge(
                label = hit.type.toString(),
                modifier = Modifier
                    .weight(0.45f)
                ,
                backgroundColor = colorResource(R.color.event_type_background)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.weight(1f)
            ) {
                BasicText(
                    text = timeString,
                    style = TextStyle(
                        color = timeColor,
                        fontSize = 16.sp
                    ),
                    maxLines = 1
                )
                Icon(
                    imageVector = iconResource,
                    contentDescription = null,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                )
            }
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(animationSpec = tween(180)),
            exit = shrinkVertically(animationSpec = tween(180))
        ) {
            Column(
            ) {
                JsonBox(hit.data.toString(4))
            }
        }
    }
}
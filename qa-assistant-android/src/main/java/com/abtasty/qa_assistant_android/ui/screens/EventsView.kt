package com.abtasty.qa_assistant_android.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abtasty.qa_assistant_android.R
import com.abtasty.qa_assistant_android.ui.components.JsonBox
import com.abtasty.qa_assistant_android.ui.components.ResetAll
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.json.JSONObject


data class Event(
    val id: String,
    val type: String,
    val timestamp: Long,
    val content: JSONObject
)

fun getEventList(): List<Event> {
    val list = mutableListOf<Event>()
    for (i in 0..20) {
        list.add(
            Event(
                "$i",
                listOf("ScreenView", "Event", "Item", "Consent", "Transaction").random(),
                System.currentTimeMillis() - ((i * (60000..((i+1)*100000)).random())),
                JSONObject()
                    .put("dl", "EventView.kt")
            )
        )
    }
    return list
}

@Composable
fun EventsView(behavior: BottomSheetBehavior<*>,
               eventList: List<Event> = getEventList()
//    eventList: List<Event> = listOf()
) {

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

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicText("${eventList.size} events recorded", modifier = Modifier.weight(1f))
            ResetAll(
                "Clear all",
                ImageVector.vectorResource(R.drawable.icon_clear),
                {

                },
                normalColor = if (eventList.isEmpty()) R.color.text_disabled else R.color.event_clear
            )

        }
        if (eventList.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    ImageVector.vectorResource(R.drawable.event_empty_background),
                    contentDescription = "My icon",
                    tint = Color.Unspecified
                )
                BasicText(
                    modifier = Modifier.padding(top = 16.dp),
                    text = "No event have been recorded so far",
                    style = TextStyle(
                        color = colorResource(R.color.text_light_grey),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                BasicText(
                    modifier = Modifier.padding(top = 16.dp),
                    text = "Interact with the page to see events here.",
                    style = TextStyle(
                        color = colorResource(R.color.text_light_grey),
                        fontSize = 16.sp,
                    )
                )
            }
        } else {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(eventList.size, key = { eventList[it].id }) { item ->
                    ExpandableRow(event = eventList[item])
                }
            }
        }
    }
}

@Composable
private fun ExpandableRow(event: Event) {

    var expanded by rememberSaveable(event.id) { mutableStateOf(false) }

    val timeString = timeAgo(event.timestamp)
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
            BasicText(
                event.type,
                modifier = Modifier
                    .background(
                        colorResource(R.color.event_type_background),
                        shape = RoundedCornerShape(50)
                    )
                    .padding(all = 8.dp)
                    .fillMaxWidth(0.25f),
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(R.color.text_bold),
                    textAlign = TextAlign.Center
                ),
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
                JsonBox(event.content.toString(4))
            }
        }
    }
}

fun timeAgo(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diffMillis = now - timestamp

    val seconds = diffMillis / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 30 -> "Just now"

        seconds < 60 -> "$seconds sec. ago"

        minutes < 60 -> "$minutes min. ago"

        hours < 24 -> {
            val remainingMinutes = minutes % 60
            if (remainingMinutes == 0L) {
                "$hours h. ago"
            } else {
                "$hours h. $remainingMinutes min. ago"
            }
        }

        else -> {
            val remainingHours = hours % 24
            if (remainingHours == 0L) {
                "$days d. ago"
            } else {
                "$days d. $remainingHours h. ago"
            }
        }
    }
}
package com.abtasty.qa_assistant_android.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abtasty.qa_assistant_android.QAAssistant2.hitManager
import com.abtasty.qa_assistant_android.R
import com.abtasty.qa_assistant_android.ui.components.EventExpandableList
import com.abtasty.qa_assistant_android.ui.components.ResetAll
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch


//data class Event(
//    val id: String,
//    val type: String,
//    val timestamp: Long,
//    val content: JSONObject
//)
//
//fun getEventList(): List<Event> {
//    val list = mutableListOf<Event>()
//    for (i in 0..20) {
//        list.add(
//            Event(
//                "$i",
//                listOf("ScreenView", "Event", "Item", "Consent", "Transaction").random(),
//                System.currentTimeMillis() - ((i * (60000..((i+1)*100000)).random())),
//                JSONObject()
//                    .put("dl", "EventView.kt")
//            )
//        )
//    }
//    return list
//}

@Composable
fun EventsView(behavior: BottomSheetBehavior<*>, query: String? = null) {

    val hits by hitManager.hits.collectAsState()
    val scope = rememberCoroutineScope()

    val filteredHits = remember(hits, query) {
        if (query.isNullOrEmpty()) {
            hits
        } else {
            hits.filter { hit ->
                hit.type.toString().contains(query, ignoreCase = true) ||
                hit.data.toString().contains(query, ignoreCase = true)
            }
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
            BasicText("${filteredHits.size} events recorded", modifier = Modifier.weight(1f))
            ResetAll(
                "Clear all",
                ImageVector.vectorResource(R.drawable.icon_clear),
                {
                    scope.launch {
                        hitManager.clearHits()
                    }
                },
                normalColor = if (hits.isEmpty()) R.color.text_disabled else R.color.event_clear
            )

        }
        if (filteredHits.isEmpty()) {
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
                    text = if (query.isNullOrEmpty()) {
                        "No event have been recorded so far"
                    } else {
                        "No events match your search"
                    },
                    style = TextStyle(
                        color = colorResource(R.color.text_light_grey),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                BasicText(
                    modifier = Modifier.padding(top = 16.dp),
                    text = if (query.isNullOrEmpty()) {
                        "Interact with the page to see events here."
                    } else {
                        "Try a different search term."
                    },
                    style = TextStyle(
                        color = colorResource(R.color.text_light_grey),
                        fontSize = 16.sp,
                    )
                )
            }
        } else {
            EventExpandableList(filteredHits, behavior)
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
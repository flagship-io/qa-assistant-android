package com.abtasty.qa_assistant_android.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abtasty.qa_assistant_android.R
import com.abtasty.qa_assistant_android.ui.components.ResetAll
import com.google.android.material.bottomsheet.BottomSheetBehavior

sealed class Status(val title: String) {
    object Accepted : Status("Accepted")
    object Hidden : Status("Hidden")
    object Rejected : Status("Rejected")
    object TargetingRejected : Status("Targeting rejected")
    object AllocationRejected : Status("Allocation rejected")
    object Forced : Status("Forced")
}

data class ChildItem(val id: String, val label: String, val details: String, val status: Status)
data class ParentItem(
    val id: String,
    val title: String,
    val children: List<ChildItem>,
    val status: Status
)

@Composable
fun CampaignsView(
    behavior: BottomSheetBehavior<*>,
    parentItems: List<ParentItem> = listOf(
        ParentItem(
            "1", "Parent 1", listOf(
                ChildItem("1", "Campaign 1", "Live 1", status = Status.Accepted),
                ChildItem("2", "Campaign 2", "Live 2", status = Status.Hidden),
                ChildItem("3", "Campaign 3", "Live 3", status = Status.Hidden),
                ChildItem("4", "Campaign 3", "Live 4", status = Status.Accepted),
                ChildItem("5", "Campaign 5", "Live 5", status = Status.Accepted),
                ChildItem("6", "Campaign 6", "Live 6", status = Status.Forced),
            ),
            status = Status.Accepted
        ),
        ParentItem(
            "2", "Parent 2", listOf(

                ChildItem("7", "Campaign 7", "Live 7", status = Status.Rejected),
                ChildItem("8", "Campaign 8", "Live 8", status = Status.Rejected),
                ChildItem("9", "Campaign 9", "Live 9", status = Status.Rejected),
                ChildItem(
                    "10",
                    "Home Page spring discount for NEW Visitors",
                    "Live 10",
                    status = Status.Forced
                ),
                ChildItem(
                    "11",
                    "Home Page spring discount for VIP Visitors",
                    "Live 11",
                    status = Status.AllocationRejected
                ),
                ChildItem("12", "Campaign 12", "Live 12", status = Status.TargetingRejected)
            ),
            status = Status.Rejected
        )
    ),
    onChildClick: (parent: ParentItem, child: ChildItem) -> Unit,
    modifier: Modifier = Modifier
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
            .fillMaxWidth()
        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicText("X live campaigns", modifier = Modifier.weight(1f))
            ResetAll(
                "Reset all",
                ImageVector.vectorResource(R.drawable.icon_reset),
                { }
            )
        }
        var expandedIdsList by rememberSaveable { mutableStateOf(emptyList<String>()) }
        val expandedIds = remember(expandedIdsList) { expandedIdsList.toSet() }

        fun toggle(parentId: String) {
            expandedIdsList =
                if (parentId in expandedIds) expandedIdsList.filterNot { it == parentId }
                else expandedIdsList + parentId
        }



        LazyColumn(
            state = lazyListState,
            modifier = modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            parentItems.forEach { parent ->
                item(key = "parent_${parent.id}") {
                    ParentRow(
                        parent,
                        expanded = parent.id in expandedIds,
                        onClick = { toggle(parent.id) },
                        isLast = parentItems.indexOf(parent) == parentItems.lastIndex
                    )
                }

                item(key = "children_${parent.id}") {
                    AnimatedVisibility(
                        visible = parent.id in expandedIds,
                        enter = expandVertically(
                            animationSpec = tween(
                                180
                            )
                        ),
                        exit = shrinkVertically(animationSpec = tween(180))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            parent.children.forEach { child ->
                                ChildRow(
                                    child = child,
                                    onClick = { onChildClick(parent, child) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ParentRow(
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
                .padding(12.dp),

            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
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
                modifier = Modifier.padding(end = 8.dp)
            )
            BasicText(
                statusTitle,
                modifier = Modifier
                    .background(
                        color,
                        shape = RoundedCornerShape(50)
                    )
                    .padding(all = 8.dp),
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(R.color.text_bold)
                )
            )
            BasicText(
                "${parentItem.children.size} campaigns",
                modifier = Modifier.padding(start = 8.dp),
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
private fun ChildRow(child: ChildItem, onClick: () -> Unit) {
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
            .padding(start = 12.dp, top = 8.dp, bottom = 8.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(end = 8.dp)
                .weight(1f)
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
        BasicText(
            text = statusTitle,
            modifier = Modifier
                .background(
                    color,
                    shape = RoundedCornerShape(50)
                )
                .padding(all = 8.dp)
                .fillMaxWidth(0.2f),
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                color = colorResource(R.color.text_bold),
                textAlign = TextAlign.Center
            ),
        )
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.icon_caret_right),
            contentDescription = null,
            modifier = Modifier.padding(start = 8.dp)
        )

    }
}
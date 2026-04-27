package com.abtasty.qa_assistant_android.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.abtasty.flagship.model.Campaign
import com.abtasty.flagship.model.CampaignStatus
import com.abtasty.qa_assistant_android.R
import com.abtasty.qa_assistant_android.ui.components.CampaignListHeader
import com.abtasty.qa_assistant_android.ui.components.CampaignListItem
import com.abtasty.qa_assistant_android.ui.components.ResetAll
import com.google.android.material.bottomsheet.BottomSheetBehavior

//sealed class CampaignStatus(val title: String) {
//    object Accepted : CampaignStatus("Accepted")
//    object Hidden : CampaignStatus("Hidden")
//    object Rejected : CampaignStatus("Rejected")
//    object TargetingRejected : CampaignStatus("Targeting rejected")
//    object AllocationRejected : CampaignStatus("Allocation rejected")
//    object Forced : CampaignStatus("Forced")
//}

//data class ChildItem(val id: String, val label: String, val details: String, val status: Status)
data class ParentItem(
    val id: String,
    val title: String,
    val campaigns: List<Campaign>,
    val status: CampaignStatus
)

@Composable
fun CampaignsView(
    behavior: BottomSheetBehavior<*>,
//    parentItems: List<ParentItem> = listOf(
//        ParentItem(
//            "1", "Parent 1", listOf(
//                ChildItem("1", "Campaign 1", "Live 1", status = Status.Accepted),
//                ChildItem("2", "Campaign 2", "Live 2", status = Status.Hidden),
//                ChildItem("3", "Campaign 3", "Live 3", status = Status.Hidden),
//                ChildItem("4", "Campaign 3", "Live 4", status = Status.Accepted),
//                ChildItem("5", "Campaign 5", "Live 5", status = Status.Accepted),
//                ChildItem("6", "Campaign 6", "Live 6", status = Status.Forced),
//            ),
//            status = Status.Accepted
//        ),
//        ParentItem(
//            "2", "Parent 2", listOf(
//
//                ChildItem("7", "Campaign 7", "Live 7", status = Status.Rejected),
//                ChildItem("8", "Campaign 8", "Live 8", status = Status.Rejected),
//                ChildItem("9", "Campaign 9", "Live 9", status = Status.Rejected),
//                ChildItem(
//                    "10",
//                    "Home Page spring discount for NEW Visitors",
//                    "Live 10",
//                    status = Status.Forced
//                ),
//                ChildItem(
//                    "11",
//                    "Home Page spring discount for VIP Visitors",
//                    "Live 11",
//                    status = Status.AllocationRejected
//                ),
//                ChildItem("12", "Campaign 12", "Live 12", status = Status.TargetingRejected)
//            ),
//            status = Status.Rejected
//        )
//    ),
    parentItems: List<ParentItem> = emptyList(),
    onChildClick: (parent: ParentItem, campaign: Campaign) -> Unit,
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
            val totalCampaigns = parentItems.sumOf { it.campaigns.size }
            BasicText("$totalCampaigns live campaigns", modifier = Modifier.weight(1f))
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
                println("parent ${parent.id + " " + parent.status.title}")
                item(key = "parent_${parent.id}") {
                    CampaignListHeader(
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
                            parent.campaigns.forEach { campaign: Campaign ->
                                CampaignListItem(
                                    campaign = campaign,
                                    onClick = { onChildClick(parent, campaign) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

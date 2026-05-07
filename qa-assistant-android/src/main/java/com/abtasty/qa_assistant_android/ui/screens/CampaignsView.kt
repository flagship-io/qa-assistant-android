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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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

data class ParentItem(
    val id: String,
    val title: String,
    val campaigns: List<Campaign>,
    val status: CampaignStatus
)

@Composable
fun CampaignsView(
    behavior: BottomSheetBehavior<*>,
    parentItems: List<ParentItem> = emptyList(),
    onChildClick: (parent: ParentItem, campaign: Campaign) -> Unit,
    modifier: Modifier = Modifier,
    expandedIds: Set<String> = emptySet(),
    onExpandedChange: (String) -> Unit = {}
) {

    val lazyListState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState()
    }

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
                        onClick = { onExpandedChange(parent.id) },
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

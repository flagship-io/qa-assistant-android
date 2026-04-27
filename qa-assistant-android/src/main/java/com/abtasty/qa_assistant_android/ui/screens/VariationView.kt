package com.abtasty.qa_assistant_android.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.abtasty.flagship.model.Campaign
import com.abtasty.flagship.model.Variation
import com.abtasty.qa_assistant_android.ui.components.CampaignListHeader
import com.abtasty.qa_assistant_android.ui.components.CampaignListItem
import com.abtasty.qa_assistant_android.ui.components.VariationListHeader
import com.abtasty.qa_assistant_android.ui.components.VariationListItem
import com.google.android.material.bottomsheet.BottomSheetBehavior

@Composable
fun VariationView(
    behavior: BottomSheetBehavior<*>,
    campaign: Campaign,
    onVariationSwitched: (Campaign, Variation) -> Unit,
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
            for (variationGroup in campaign.variationGroups) {
                println("#Var " + variationGroup?.variationGroupMetadata?.variationGroupName)
                val variations = variationGroup?.variations
                variations?.let {
                    for ((key, variation) in variations) {
                        val variationId = variation.variationMetadata.variationId
                        item(key = "header_${variationId}") {
                            VariationListHeader(
                                campaign = campaign,
                                variation = variation,
                                expanded =  variationId in expandedIds,
                                onClick = { toggle(variationId) },
                                isLast = (key == variations.keys.last())
                            )
                        }

                        item(key = "expended_${variationId}") {
                            AnimatedVisibility(
                                visible = variationId in expandedIds,
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
                                    variation.flags?.let { flags ->
                                        for ((key, flag) in flags) {
                                            VariationListItem(
                                                campaign = campaign,
                                                variationGroup = variationGroup,
                                                variation = variation,
                                                flag = flag
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}
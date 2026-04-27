package com.abtasty.qa_assistant_android.ui.screens

import android.R.id.toggle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abtasty.flagship.model.Campaign
import com.abtasty.flagship.model.CampaignStatus
import com.abtasty.qa_assistant_android.R
import com.abtasty.qa_assistant_android.ui.components.TargetingListHeader
import com.abtasty.qa_assistant_android.ui.components.TargetingListItem
import com.abtasty.qa_assistant_android.ui.components.VariationListHeader
import com.abtasty.qa_assistant_android.ui.components.VariationListItem
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator

@Composable
fun TargetingView(
    behavior: BottomSheetBehavior<*>,
    campaign: Campaign,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    data class AllocationStyleResources(
        val backgroundColor: Int,
        val borderColor: Int,
        val iconResource: Int,
        val iconTint: Int,
        val warning: String? = null
    )


    val style = when (campaign.status()) {
        CampaignStatus.Accepted ->
            AllocationStyleResources(
                R.color.light_green,
                R.color.accepted,
                R.drawable.check,
                R.color.event_now
            )

        CampaignStatus.Hidden ->
            AllocationStyleResources(
                R.color.light_green,
                R.color.accepted,
                R.drawable.check,
                R.color.event_now
            )

        CampaignStatus.Forced ->
            AllocationStyleResources(
                R.color.light_yellow,
                R.color.forced,
                R.drawable.check,
                R.color.gold,
                "Allocation has been bypassed"
            )

        CampaignStatus.AllocationRejected ->
            AllocationStyleResources(
                R.color.light_red,
                R.color.rejected,
                R.drawable.close,
                R.color.event_clear,
                "You are part of the untracked traffic"
            )

        CampaignStatus.Rejected,
        CampaignStatus.TargetingRejected,
        null ->
            AllocationStyleResources(
                R.color.light_red,
                R.color.rejected,
                R.drawable.close,
                R.color.event_clear
            )
    }
    Column(
        modifier = modifier
            .fillMaxHeight()
    ) {
        var expandedIdsList by rememberSaveable { mutableStateOf(emptyList<String>()) }
        val expandedIds = remember(expandedIdsList) { expandedIdsList.toSet() }

        fun toggle(parentId: String) {
            expandedIdsList =
                if (parentId in expandedIds) expandedIdsList.filterNot { it == parentId }
                else expandedIdsList + parentId
        }
        val lazyListState = rememberLazyListState()
        LazyColumn(
            state = lazyListState,
            modifier = modifier
                .fillMaxWidth()
                .weight(1f)
        ) {


            if (campaign.campaignMetadata.campaignType != "ab") {
                for (variationGroup in campaign.variationGroups) {
                    variationGroup?.let {
                        val variationGroupId = variationGroup.variationGroupMetadata.variationGroupId
                        item(key = "header_${variationGroupId}") {
                            TargetingListHeader(
                                variationGroup = variationGroup,
                                expanded = variationGroupId in expandedIds,
                                onClick = { toggle(variationGroupId) },
                                isLast = (variationGroup == campaign.variationGroups.last())
                            )
                        }

                        item(key = "expended_${variationGroupId}") {
                            AnimatedVisibility(
                                visible = variationGroupId in expandedIds,
                                enter = expandVertically(
                                    animationSpec = tween(
                                        180
                                    )
                                ),
                                exit = shrinkVertically(animationSpec = tween(180))
                            ) {
//                                Column(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//
//                                ) {
                                    TargetingListItem(
                                        behavior = behavior,
                                        variationGroup = variationGroup,
                                        onBack = {

                                        }
                                    )
//                                    variation.flags?.let { flags ->
//                                        for ((key, flag) in flags) {
//                                            VariationListItem(
//                                                campaign = campaign,
//                                                variationGroup = variationGroup,
//                                                variation = variation,
//                                                flag = flag
//                                            )
//                                        }
//                                    }
//                                }
                            }
                        }
                    }
                }
            } else {

            }
//
        }

//        Row(
//            modifier = modifier
//                .fillMaxWidth()
//                .height(IntrinsicSize.Min)
//                .padding(12.dp),
//        ) {
//
//            Column(
//                modifier = Modifier
//                    .fillMaxHeight(),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center,
//            ) {
//
//
//                Icon(
//                    imageVector = ImageVector.vectorResource(style.iconResource),
//                    contentDescription = null,
//                    tint = colorResource(style.iconTint),
//                    modifier = Modifier
//                        .width(24.dp)
//                        .height(24.dp)
//                )
//            }
//
//            val backgroundColor = colorResource(style.backgroundColor)
//
//            val borderColor = colorResource(style.borderColor)
//
//            Spacer(
//                modifier = Modifier
//                    .width(12.dp)
//            )
//
//            Column(
//                modifier = Modifier
//                    .weight(1f)
////                .fillMaxHeight()
//                    .border(
//                        width = 1.dp,
//                        color = borderColor,
//                        shape = RoundedCornerShape(4),
//                    )
//                    .background(
//                        color = backgroundColor,
//                        shape = RoundedCornerShape(4),
//                    ),
//            ) {
//
//                for (variationGroups in campaign.variationGroups) {
//                    variationGroups?.variations?.forEach { (variationId, variation) ->
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth(),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Text(
//                                modifier = Modifier
//                                    .padding(all = 8.dp)
//                                    .weight(1f),
//                                text = variation.variationMetadata.variationName,
//                                fontWeight = FontWeight.SemiBold,
//                                color = colorResource(R.color.text_bold),
//                                fontSize = 14.sp,
//                            )
//                            Text(
//                                modifier = Modifier
//                                    .padding(all = 8.dp)
//                                    .background(
//                                        color = colorResource(R.color.event_type_background),
//                                        shape = RoundedCornerShape(50)
//                                    )
//                                    .padding(all = 8.dp),
//                                text = variation.variationMetadata.allocation.toString() + " %",
//                                fontWeight = FontWeight.SemiBold,
//                                color = colorResource(R.color.text_bold),
//                                fontSize = 14.sp,
//                            )
//                        }
//
//                    }
//                }
//            }
//        }
//        if (style.warning != null) {
//            Text(
//                modifier = Modifier
//                    .padding(start = 48.dp)
//                    .background(
//                        color = colorResource(R.color.forced),
//                        shape = RoundedCornerShape(8)
//                    )
//                    .padding(all = 8.dp),
//                text = style.warning,
//                fontWeight = FontWeight.Normal,
//                color = colorResource(R.color.text_bold),
//                fontSize = 12.sp,
//            )
//        }
    }
}
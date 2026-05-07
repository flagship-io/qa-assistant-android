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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.abtasty.flagship.model.VariationGroup
import com.abtasty.qa_assistant_android.CampaignManager
import com.abtasty.qa_assistant_android.QAAssistant2
import com.abtasty.qa_assistant_android.R
import com.abtasty.qa_assistant_android.ui.components.TargetingListHeader
import com.abtasty.qa_assistant_android.ui.components.TargetingListItem
import com.abtasty.qa_assistant_android.ui.components.VariationListHeader
import com.abtasty.qa_assistant_android.ui.components.VariationListItem
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.json.JSONArray
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator


data class Targeting(
    val key: String,
    val operator: String,
    val values: ArrayList<Any?>
) {
    var isTargetingValid: Boolean = false
    var matchingValue: Any? = null
}

data class TargetingAND(val targetingList: ArrayList<Targeting>) {
    var isAllTargetingValid: Boolean = false

    fun isTargetingValid(): Boolean {
//        for (targeting in targetingList) {
//            if (!targeting.isTargetingValid) return false
//        }
//        return true
        return isAllTargetingValid
    }
}

data class TargetingOR(
    val variationGroupId: String,
    val variationGroupName: String,
    val targetingList: ArrayList<TargetingAND>
) {

    fun isTargetingValid(): Boolean {
        for (targetingAND in targetingList) {
            if (targetingAND.isTargetingValid()) return true
        }
        return false
    }

    fun findInvalidTargeting(): List<Pair<String, Any?>> {
        val invalidTargeting = mutableListOf<Pair<String, Any?>>()
        for (targetingAND in targetingList) {
            for (targeting in targetingAND.targetingList) {
                if (!targeting.isTargetingValid) {
                    for (value in targeting.values) {
                        invalidTargeting.add(Pair(targeting.key, value))
                    }
                }
            }
        }
        return invalidTargeting
    }

    companion object {
        fun getAllInvalidTargeting(targetingORList: List<TargetingOR>): Map<String, List<Any?>> {
            val invalidTargetingMap = mutableMapOf<String, MutableList<Any?>>()

            for (targetingOR in targetingORList) {
                for ((key, value) in targetingOR.findInvalidTargeting()) {
                    if (!invalidTargetingMap.containsKey(key))
                        invalidTargetingMap[key] = mutableListOf()
                    invalidTargetingMap[key]?.add(value)
                }
            }
            return invalidTargetingMap
        }
    }


}

fun extractTargetingValues(value: Any): ArrayList<Any?> {
    val result = ArrayList<Any?>()
    if (value is JSONArray) {
        for (i in 0 until value.length()) {
            result.add(value.get(i))
        }
    } else {
        result.add(value)
    }
    return result
}

fun getTargetingFromVariationGroup(variationGroup: VariationGroup): TargetingOR {
    val targetingOR = ArrayList<TargetingAND>()
    variationGroup.targetingGroups?.targetingGroups?.let { targetingGroups ->

        for (targeting in targetingGroups) {
            var allTargetingValid = true
            val targetingAND = ArrayList<Targeting>()
            targeting.targetingList?.let { targetingList ->
                for (targeting in targetingList) {
                    val targetingTrans = Targeting(
                        targeting.key,
                        targeting.operator,
                        extractTargetingValues(targeting.value),
                    )
                    targetingTrans.isTargetingValid = targeting.isTargetingValid
                    targetingTrans.isTargetingValid = targeting.isTargetingValid
                    if (!targeting.isTargetingValid) {
                        allTargetingValid = false
                    }
                    targetingAND.add(targetingTrans)
                }
            }
            val targetingAnd = TargetingAND(targetingList = targetingAND)
            targetingAnd.isAllTargetingValid = allTargetingValid
            targetingOR.add(targetingAnd)
        }
    }
    return TargetingOR(
        variationGroupId = variationGroup.variationGroupMetadata.variationGroupId,
        variationGroupName = variationGroup.variationGroupMetadata.variationGroupName,
        targetingList = targetingOR
    )
}


@Composable
fun TargetingView(
    behavior: BottomSheetBehavior<*>,
    campaign: Campaign,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    val targetingOR = ArrayList<TargetingOR>()

    for (variationGroup in campaign.variationGroups) {
        variationGroup?.let {
            targetingOR.add(getTargetingFromVariationGroup(it))
        }
    }

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
        modifier = modifier.fillMaxHeight()
    ) {
        // Invalid targeting banner
        val invalidTargeting = TargetingOR.getAllInvalidTargeting(targetingOR)
        if (campaign.status() == CampaignStatus.TargetingRejected || campaign.status() == CampaignStatus.Forced) {
            item {
                Column(
                    modifier = Modifier
                        .padding(all = 18.dp)
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(
                            color = colorResource(R.color.forced),
                            shape = RoundedCornerShape(8f)
                        )
                        .padding(8.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        text = "Targeting does not match current values: ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = colorResource(R.color.text_bold)
                    )

                    for ((key, value) in invalidTargeting) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(start = 4.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                                text = "-",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorResource(R.color.text_bold)
                            )
                            Text(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .weight(0.5f, fill = true),
                                text = "$key: ",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorResource(R.color.text_bold)
                            )
                            Text(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .weight(0.5f, fill = false),
                                text = QAAssistant2.campaignManager.currentVisitor?.context?.get(key).toString(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = colorResource(R.color.text_bold)
                            )
                        }
                    }
                }
            }
        }

        // Targeting groups
        if (campaign.campaignMetadata.campaignType != "ab") {
            for (targetingOR in targetingOR) {
                val variationGroupId = targetingOR.variationGroupId
                item(key = "header_${variationGroupId}") {
                    TargetingListHeader(
                        variationGroupName = targetingOR.variationGroupName,
                        expanded = variationGroupId in expandedIds,
                        onClick = { toggle(variationGroupId) }
                    )
                }

                item(key = "expended_${variationGroupId}") {
                    AnimatedVisibility(
                        visible = variationGroupId in expandedIds,
                        enter = expandVertically(animationSpec = tween(180)),
                        exit = shrinkVertically(animationSpec = tween(180))
                    ) {
                        TargetingListItem(
                            behavior = behavior,
                            targetingOR = targetingOR,
                            onBack = { }
                        )
                    }
                }
            }
        } else {
            items(targetingOR.size) { index ->
                TargetingListItem(
                    behavior = behavior,
                    targetingOR = targetingOR[index],
                    onBack = { },
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}
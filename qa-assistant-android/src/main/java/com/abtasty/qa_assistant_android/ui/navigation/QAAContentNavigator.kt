package com.abtasty.qa_assistant_android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.abtasty.flagship.model.Campaign
import com.abtasty.qa_assistant_android.QAAssistant2
import com.abtasty.qa_assistant_android.ui.screens.CampaignDetailScreen
import com.abtasty.qa_assistant_android.ui.screens.HomeScreen
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.flow.first

internal var savedExpandedIds: List<String> = emptyList()
internal var savedCampaignDetailId : String? = null
@Composable
fun QAAContentNavigator(
    behavior: BottomSheetBehavior<*>,
    onClose: () -> Unit
) {
    var expandedIdsList by rememberSaveable { mutableStateOf(savedExpandedIds) }
    val campaignDetailId = rememberSaveable { mutableStateOf<String?>(savedCampaignDetailId) }

    savedExpandedIds = expandedIdsList
    savedCampaignDetailId = campaignDetailId.value

    androidx.activity.compose.BackHandler {
        when (campaignDetailId.value) {
            null -> {
                onClose()
            }
            else -> {
                campaignDetailId.value = null
            }
        }
    }

    when  {
        (campaignDetailId.value != null) -> CampaignDetailScreen(
            behavior = behavior,
            campaign = campaignDetailId.let { id ->
                QAAssistant2.campaignManager.campaigns.value?.find { it.campaignMetadata.campaignId == id.value }
            }!!,
            onCampaignStatusChanged = { campaign, status -> },
            onBack = {
                campaignDetailId.value = null
            },
            onClose = onClose
        )
        else -> HomeScreen(
            behavior = behavior,
            onCampaignClick = { campaign ->
                campaignDetailId.value = campaign.campaignMetadata.campaignId
            },
            onClose = onClose,
            expandedIds = expandedIdsList,
            onExpandedChange = { parentId ->
                expandedIdsList = if (parentId in expandedIdsList) {
                    expandedIdsList.filterNot { it == parentId }
                } else {
                    expandedIdsList + parentId
                }
            }
        )
    }
}
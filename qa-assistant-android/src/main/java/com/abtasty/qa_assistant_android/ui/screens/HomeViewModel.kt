//package com.abtasty.qa_assistant_android.ui.screens
//
//import android.content.Context
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.abtasty.flagship.model.Campaign
//import com.abtasty.qa_assistant_android.CampaignManager
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//
//class HomeViewModel(
//    private val campaignManager: CampaignManager = CampaignManager()
//) : ViewModel() {
//
//    private val _parentItems = MutableStateFlow<List<ParentItem>>(emptyList())
//    val parentItems: StateFlow<List<ParentItem>> = _parentItems.asStateFlow()
//
//    init {
//        observeCampaigns()
//    }
//
//    private fun observeCampaigns() {
//        viewModelScope.launch {
//            campaignManager.campaigns.collect { campaigns ->
//                campaigns?.let {
//                    _parentItems.value = convertCampaignsToParentItems(it)
//                }
//            }
//        }
//    }
//
//    fun updateCampaigns(context: Context, envId: String) {
//        viewModelScope.launch {
//            campaignManager.updateCampaigns(context, envId)
//        }
//    }
//
//    private fun convertCampaignsToParentItems(campaigns: ArrayList<Campaign>): List<ParentItem> {
//        // Regrouper les campaigns acceptées et rejetées
//        val acceptedCampaigns = mutableListOf<ChildItem>()
//        val rejectedCampaigns = mutableListOf<ChildItem>()
//
//        campaigns.forEach { campaign ->
//            val childItem = ChildItem(
//                id = campaign.campaignMetadata.campaignId,
//                label = campaign.campaignMetadata.campaignName,
//                details = campaign.campaignMetadata.campaignType,
//                status = Status.Accepted // À adapter selon votre logique métier
//            )
//
//            // Logique à adapter selon vos critères d'acceptation/rejet
//            if (campaign.campaignMetadata.campaignId == null) {
//                acceptedCampaigns.add(childItem)
//            } else {
//                rejectedCampaigns.add(childItem)
//            }
//        }
//
//        val parentItems = mutableListOf<ParentItem>()
//
//        if (acceptedCampaigns.isNotEmpty()) {
//            parentItems.add(
//                ParentItem(
//                    id = "accepted",
//                    title = "Accepted",
//                    children = acceptedCampaigns,
//                    status = Status.Accepted
//                )
//            )
//        }
//
//        if (rejectedCampaigns.isNotEmpty()) {
//            parentItems.add(
//                ParentItem(
//                    id = "rejected",
//                    title = "Rejected",
//                    children = rejectedCampaigns,
//                    status = Status.Rejected
//                )
//            )
//        }
//
//        return parentItems
//    }
//}
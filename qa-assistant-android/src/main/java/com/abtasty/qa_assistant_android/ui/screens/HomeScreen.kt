package com.abtasty.qa_assistant_android.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.IconButton
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abtasty.flagship.model.Campaign
import com.abtasty.flagship.model.CampaignStatus
import com.abtasty.qa_assistant_android.CampaignManager
import com.abtasty.qa_assistant_android.QAAssistant2
import com.abtasty.qa_assistant_android.R
import com.abtasty.qa_assistant_android.ui.components.Header
import com.abtasty.qa_assistant_android.ui.navigation.savedExpandedIds
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


private enum class HomeTab(val label: String) {
    Campaigns("Campaigns"),
    Events("Events"),
    Context("Context");

    companion object {
//        fun fromString(tab: String): HomeTab = entries.find { it.label == tab } ?: Campaigns

        fun fromInt(tab: Int): HomeTab = entries.find { it.ordinal == tab } ?: Campaigns
    }
}

internal var savedTab : Int = 0


@Composable
fun HomeScreen(
    behavior: BottomSheetBehavior<*>,
    onCampaignClick: (Campaign) -> Unit,
    onClose: () -> Unit,
    campaignManager: CampaignManager = QAAssistant2.campaignManager,
    expandedIds: List<String> = emptyList(),
    onExpandedChange: (String) -> Unit = {}

) {

    var homeTab by rememberSaveable { mutableStateOf(savedTab) }

    savedTab = homeTab

    val campaigns by campaignManager.campaigns.collectAsState()

    val parentItems = remember(campaigns) {
        campaigns?.let { convertCampaignsToParentItems(it) } ?: emptyList()
    }

    println("[QA ASSISTANT] HomeScreen recomposing with ${parentItems.size} parent items")

    var query by rememberSaveable { mutableStateOf("") }

    Column (

        modifier = Modifier
        ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(8.dp) //Padding so we can see app on the sides
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(
                        RoundedCornerShape(
                            topStart = 24.dp,
                            topEnd = 24.dp,
                            bottomStart = 24.dp,
                            bottomEnd = 24.dp
                        )
                    )
                    .background(color = colorResource(R.color.primary_background))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp)
                ) {
                    Spacer(Modifier.height(8.dp))
                    Header(onClose = onClose)
                    Spacer(Modifier.height(4.dp))
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        BasicTextField(
                            value = query,
                            onValueChange = { query = it },
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .clip(RoundedCornerShape(percent = 25))
                                .background(Color.White)
                                .padding(start = 14.dp, top = 12.dp, bottom = 12.dp),
                            singleLine = true,
                            cursorBrush = SolidColor(Color.Gray),
                            textStyle = TextStyle(
                                fontSize = 16.sp,
                                color = Color.Gray
                            ),
                            decorationBox = { innerTextField ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        Modifier.weight(1f)
                                    ) {
                                        if (query.isEmpty()) {
                                            Text(
                                                text = "Search",
                                                fontSize = 16.sp,
                                                color = Color.Gray,
                                            )
                                        }
                                        innerTextField()
                                    }
                                    IconButton(
                                        onClick = { /*onQueryChange("")*/ },
                                        modifier = Modifier
                                            .heightIn(max = 16.dp)
                                    ) {
                                        Image(
                                            imageVector = ImageVector.vectorResource(R.drawable.search),
                                            contentDescription = "Clear",
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                }
                            }
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    val tabs = HomeTab.entries
                    val pagerState = rememberPagerState(initialPage = homeTab, pageCount = { tabs.size })
                    val scope = rememberCoroutineScope()

                    val selectedColor = colorResource(R.color.selected_tab)
                    val unselectedColor = colorResource(R.color.unselected_tab)

                    Column(modifier = Modifier.fillMaxWidth()) {
                        SecondaryTabRow(
                            selectedTabIndex = pagerState.currentPage,
//                            selectedTabIndex = homeTab,
                            containerColor = Color.Transparent,
                            contentColor = selectedColor,
                            indicator = {
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier
                                        .tabIndicatorOffset(
                                            selectedTabIndex = pagerState.currentPage,
                                            matchContentSize = false
                                        ),
                                    color = colorResource(R.color.selected_tab),
                                )
                            },
                            tabs = {
                                tabs.forEachIndexed { index, tab ->
                                    Tab(
                                        selected = pagerState.currentPage == index,
                                        onClick = {
                                            scope.launch {
                                                pagerState.scrollToPage(index)
                                            }
                                            homeTab = index
                                        },
                                        selectedContentColor = selectedColor,
                                        unselectedContentColor = unselectedColor,
                                        text = { Text(tab.label) }
                                    )
                                }
                            })
                    }

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                    ) { page ->
                        when (tabs[page]) {
                            HomeTab.Campaigns -> CampaignsView(
                                behavior = behavior,
                                parentItems = parentItems,
                                onChildClick = { parent, child -> onCampaignClick(child) },
                                expandedIds = expandedIds.toSet(),
                                onExpandedChange = onExpandedChange
                            )
                            HomeTab.Events -> EventsView(behavior = behavior)
                            HomeTab.Context -> ContextView(behavior = behavior)
                        }
                    }
                }
            }
        }
    }
}


private fun convertCampaignsToParentItems(campaigns: List<Campaign>): List<ParentItem> {

    val acceptedCampaigns = mutableListOf<Campaign>()
    val rejectedCampaigns = mutableListOf<Campaign>()

   QAAssistant2.currentVisitor?.let { currentVisitor ->

       val flagCampaignIds = currentVisitor.flags.values.mapNotNull { flag ->
           flag.metadata.campaignId
       }

       campaigns.forEach { campaign ->
           val isCampaignAccepted = campaign.campaignMetadata.campaignId in flagCampaignIds
//           val childItem = ChildItem(
//               id = campaign.campaignMetadata.campaignId,
//               label = campaign.campaignMetadata.campaignName,
//               details = campaign.campaignMetadata.campaignType,
//               status = if (isCampaignAccepted) Status.Accepted else Status.Rejected
//           )
//           campaign.status(if (isCampaignAccepted) CampaignStatus.Accepted else CampaignStatus.Rejected)
//           if (isCampaignAccepted) {
//               acceptedCampaigns.add(campaign)
//           } else {
//               rejectedCampaigns.add(campaign)
//           }
           campaign.status()?.let { status ->
               if (status == CampaignStatus.Accepted) {
                   acceptedCampaigns.add(campaign)
               } else {
                   rejectedCampaigns.add(campaign)
               }
           }
       }

   }

    val parentItems = mutableListOf<ParentItem>()

    if (acceptedCampaigns.isNotEmpty()) {
        parentItems.add(
            ParentItem(
                id = "accepted",
                title = "Accepted",
                campaigns = acceptedCampaigns,
                status = CampaignStatus.Accepted
            )
        )
    }

    if (rejectedCampaigns.isNotEmpty()) {
        parentItems.add(
            ParentItem(
                id = "rejected",
                title = "Rejected",
                campaigns = rejectedCampaigns,
                status = CampaignStatus.Rejected
            )
        )
    }

    return parentItems
}



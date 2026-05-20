package com.abtasty.qa_assistant_android.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abtasty.flagship.model.Campaign
import com.abtasty.flagship.model.CampaignStatus
import com.abtasty.qa_assistant_android.R
import com.abtasty.qa_assistant_android.ui.components.Header
import com.abtasty.qa_assistant_android.ui.components.PillBadge
import com.abtasty.qa_assistant_android.ui.components.QABadge
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch

private interface Tab {
    val label: String
}
private enum class CampaignTab(override val label: String): Tab {
    Variations("Variations"),
    Targeting("Targeting"),
    Allocation("Allocation")
}

private enum class ToggleTab(override val label: String): Tab {
    Variations("Variations"),
    Targeting("Targeting"),
}

internal var savedDetailTab : Int = 0

@Composable
fun CampaignDetailScreen(
    behavior: BottomSheetBehavior<*>,
    campaign: Campaign,
    onCampaignStatusChanged: (Campaign, CampaignStatus) -> Unit,
    onBack: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {

    var detailTab by rememberSaveable { mutableStateOf(savedDetailTab) }

    savedDetailTab = detailTab

    Column(

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
                    Header(onBack = onBack, onClose = onClose)
                    Spacer(Modifier.height(4.dp))
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .background(color = colorResource(R.color.white_bg))
//                                .padding(18.dp)
                        ) {
                            Text(
                                text = campaign.campaignMetadata.campaignName,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Normal,
                                color = colorResource(R.color.text_bold),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 18.dp, start = 18.dp, end = 18.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 18.dp, top = 18.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val color = when (campaign.status()) {
                                    CampaignStatus.Accepted -> colorResource(R.color.accepted)
                                    CampaignStatus.Forced -> colorResource(R.color.forced)
                                    CampaignStatus.Hidden -> colorResource(R.color.forced)
                                    else -> colorResource(R.color.rejected)
                                }

                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    println("#Det status ${campaign.status()?.title}")
                                    PillBadge(
                                        label = campaign.status()?.title
                                            ?: CampaignStatus.Rejected.title,
                                        modifier = Modifier
                                            .padding(start = 22.dp),
                                        backgroundColor = color
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    QABadge(
                                        campaignStatus = campaign.status()
                                            ?: CampaignStatus.Rejected,
                                        modifier = Modifier,
                                        onClick = { oldStatus: CampaignStatus, newStatus: CampaignStatus ->

                                        }
                                    )
                                }
                            }
                            if (campaign.status() == CampaignStatus.Accepted || campaign.status() == CampaignStatus.Forced) {
                                Column (
                                    modifier = Modifier
                                        .fillMaxWidth()
                                    ,
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "You are viewing: ${campaign.getSelectedVariation()?.variationMetadata?.variationName}",
                                        color = colorResource(R.color.green_text),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier
                                            .background(color = colorResource(R.color.green_bg))
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        textAlign = TextAlign.Center,
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                }

                            }

                            val tabs = if (campaign.campaignMetadata.campaignType == "toggle") ToggleTab.entries else CampaignTab.entries
                            val pagerState = rememberPagerState(initialPage = detailTab, pageCount = { tabs.size })
                            val scope = rememberCoroutineScope()

                            val selectedColor = colorResource(R.color.selected_tab)
                            val unselectedColor = colorResource(R.color.unselected_tab)

                            Column(modifier = Modifier.fillMaxWidth()) {
                                SecondaryTabRow(

                                    selectedTabIndex = pagerState.currentPage,
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
//                                                        pagerState.animateScrollToPage(index)
                                                        pagerState.scrollToPage(index)
                                                    }
                                                    detailTab = index
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
                                val tabLabel = tabs[page].label
                                when (tabLabel) {
                                    "Variations" -> VariationView(
                                        behavior = behavior,
                                        campaign = campaign,
                                        onVariationSwitched = { campaign, variation ->

                                        }
                                    )
                                    "Targeting" -> TargetingView(
                                        behavior = behavior,
                                        campaign = campaign,
                                        onBack = {

                                        }
                                    )
                                    "Allocation" ->  AllocationView(
                                        behavior = behavior,
                                        campaign = campaign,
                                        onBack = {

                                        },
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


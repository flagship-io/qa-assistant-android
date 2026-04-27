package com.abtasty.qa_assistant_android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abtasty.flagship.model.Campaign
import com.abtasty.flagship.model.CampaignStatus
import com.abtasty.flagship.model.Variation
import com.abtasty.qa_assistant_android.R
import com.abtasty.qa_assistant_android.ui.screens.VariationView

@Composable
fun VariationListHeader(
    campaign: Campaign,
    variation: Variation,
    expanded: Boolean,
    onClick: () -> Unit,
    isLast: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(top = 12.dp, bottom = 12.dp)
            ,

            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,

            ) {
            val iconResource =
                if (expanded) ImageVector.vectorResource(R.drawable.icon_caret_top) else ImageVector.vectorResource(
                    R.drawable.icon_caret_down
                )
            Icon(
                imageVector = iconResource,
                contentDescription = null,
                modifier = Modifier
                    .padding(
                        start = 12.dp,
                        end = 8.dp
                    )
            )
            val title = when(variation.variationMetadata.campaignType) {
                "toggle" -> variation.variationMetadata.variationGroupName
                "perso" -> variation.variationMetadata.variationGroupName + " - " + variation.variationMetadata.variationName
                else -> {
                    variation.variationMetadata.variationName

                }
            }
            Text(
                text = title,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f)
                ,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = colorResource(R.color.text_bold)
            )
            if (campaign.status() == CampaignStatus.Accepted || campaign.status() == CampaignStatus.Forced) {
                if (variation.isSelected) {
                    PillBadge(
                        label = "Your version",
                        modifier = Modifier,
                        backgroundColor = colorResource(R.color.accepted)
                    )
                } else {
                    VariationViewBadge(
                        variation = variation,
                        modifier = Modifier,
                        onClick = {

                        }
                    )
                }
                Spacer(modifier = Modifier.width(18.dp))
            }
        }
    }
}
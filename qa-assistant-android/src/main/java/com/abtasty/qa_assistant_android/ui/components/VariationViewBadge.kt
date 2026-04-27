package com.abtasty.qa_assistant_android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.abtasty.flagship.model.CampaignStatus
import com.abtasty.flagship.model.Variation
import com.abtasty.qa_assistant_android.R

@Composable
fun VariationViewBadge(
    variation: Variation,
    modifier: Modifier,
    onClick: (Variation) -> Unit,
    round: Int = 10
) {
    Row(
        modifier = modifier
            .background(
                colorResource(R.color.white_bg),
                shape = RoundedCornerShape(round)
            )
            .border(
                width = 1.dp,
                color = colorResource(R.color.cell_border),
                shape = RoundedCornerShape(round)
            )
            .padding(all = 8.dp)
            .clickable {
                onClick(variation)
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(
            text = "View",
            modifier = Modifier
//                .padding(end = 4.dp)
            ,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                color = colorResource(R.color.text_bold),
                textAlign = TextAlign.Center
            ),
            maxLines = 1,
        )
    }
}
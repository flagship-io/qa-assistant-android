package com.abtasty.qa_assistant_android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abtasty.flagship.model.Campaign
import com.abtasty.flagship.model.CampaignStatus
import com.abtasty.flagship.model.Variation
import com.abtasty.flagship.model.VariationGroup
import com.abtasty.flagship.model._Flag
import com.abtasty.qa_assistant_android.R
import org.json.JSONObject


@Composable
fun VariationListItem(
    campaign: Campaign,
    variationGroup: VariationGroup,
    variation: Variation,
    flag: _Flag
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isPressed) colorResource(R.color.item_pressed) else Color.Transparent
            )
            .padding(
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 12.dp, end = 12.dp)
                    .fillMaxWidth()
                    .background(
                        color = colorResource(R.color.event_content_background),
                        shape = RoundedCornerShape(8)
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                val isJson = try {
                    flag.value?.let { JSONObject(it.toString()) }
                } catch (e: Exception) {
                    null
                }
                isJson?.let {
                    Column(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth(),

                        ) {
                        Text(
                            modifier = Modifier
                                .padding(all = 8.dp)
                                .fillMaxWidth(),
                            text = flag.key,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                            color = colorResource(R.color.text_bold),
                            fontSize = 14.sp,
                        )
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 8.dp)
                                .background(
                                    color = colorResource(R.color.event_type_background),
                                    shape = RoundedCornerShape(8)
                                )
                                .padding(all = 8.dp),
                            text = it.toString(4),
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                            color = colorResource(R.color.text_bold),
                            fontSize = 14.sp,
                        )
                    }
                } ?: run {
                    Text(
                        modifier = Modifier
                            .padding(all = 8.dp)
                            .weight(1f),
                        text = flag.key,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                        color = colorResource(R.color.text_bold),
                        fontSize = 14.sp,
                    )

                    Text(
                        modifier = Modifier
                            .padding(all = 8.dp)
                            .background(
                                color = colorResource(R.color.event_type_background),
                                shape = RoundedCornerShape(50)
                            )
                            .padding(all = 8.dp),
                        text = flag.value.toString(),
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                        color = colorResource(R.color.text_bold),
                        fontSize = 14.sp,
                    )
                }
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
        }
    }
}
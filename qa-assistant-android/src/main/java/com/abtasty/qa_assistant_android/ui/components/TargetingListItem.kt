package com.abtasty.qa_assistant_android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abtasty.flagship.model.VariationGroup
import com.abtasty.qa_assistant_android.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.json.JSONArray

@Composable
fun TargetingListItem(
    behavior: BottomSheetBehavior<*>,
    variationGroup: VariationGroup,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {


    data class Targeting(
        val key: String,
        val operator: String,
        val values: ArrayList<Any?>
    ) {
        var isTargetingValid: Boolean = false
        var matchingValue: Any? = null

    }

    data class TargetingAND(val targetingList: ArrayList<Targeting>)
    data class TargetingOR(val targetingList: ArrayList<TargetingAND>)

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


    fun transform(): TargetingOR {
        val targetingOR = ArrayList<TargetingAND>()
        variationGroup.targetingGroups?.targetingGroups?.let { targetingGroups ->
            for (targeting in targetingGroups) {
                val targetingAND = ArrayList<Targeting>()
                targeting.targetingList?.let { targetingList ->
                    for (targeting in targetingList) {

                        targetingAND.add(
                            Targeting(
                                targeting.key,
                                targeting.operator,
                                extractTargetingValues(targeting.value),
                            )
                        )
                    }
                }
                targetingOR.add(TargetingAND(targetingList = targetingAND))
            }
        }
        return TargetingOR(targetingList = targetingOR)
    }

    val targeting: TargetingOR = transform()

    data class AllocationStyleResources(
        val backgroundColor: Int,
        val borderColor: Int,
        val iconResource: Int,
        val iconTint: Int,
        val warning: String? = null
    )


//    val style = when (campaign.status()) {
//        CampaignStatus.Accepted ->
//            AllocationStyleResources(
//                R.color.light_green,
//                R.color.accepted,
//                R.drawable.check,
//                R.color.event_now
//            )
//
//        CampaignStatus.Hidden ->
//            AllocationStyleResources(
//                R.color.light_green,
//                R.color.accepted,
//                R.drawable.check,
//                R.color.event_now
//            )
//
//        CampaignStatus.Forced ->
//            AllocationStyleResources(
//                R.color.light_yellow,
//                R.color.forced,
//                R.drawable.check,
//                R.color.gold,
//                "Allocation has been bypassed"
//            )
//
//        CampaignStatus.AllocationRejected ->
//            AllocationStyleResources(
//                R.color.light_red,
//                R.color.rejected,
//                R.drawable.close,
//                R.color.event_clear,
//                "You are part of the untracked traffic"
//            )
//
//        CampaignStatus.Rejected,
//        CampaignStatus.TargetingRejected,
//        null ->
//            AllocationStyleResources(
//                R.color.light_red,
//                R.color.rejected,
//                R.drawable.close,
//                R.color.event_clear
//            )
//    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        for (a in targeting.targetingList) {
            println("#Targ a : " + a.toString())
            for (b in a.targetingList) {
                println("#Targ b : " + b.toString())

            }
        }
//        variationGroup.targetingGroups?.targetingGroups?.let { targetingGroups ->
//            for (targeting in targetingGroups) {
        var orIndex = 0
        for (targetingOR in targeting.targetingList) {
            //or
            Column(

            ) {
//                    targeting.targetingList?.let { targetingList ->
//                        for (targeting in targetingList) {
                var andIndex = 0
                Box() {
                    Column(
                        modifier = Modifier
                            .matchParentSize()
                            .padding(start = 48.dp, end = 12.dp)
                            .align(Alignment.CenterEnd)
                            .background(
                                color = colorResource(R.color.event_type_background),
                                shape = RoundedCornerShape(5)
                            ),
                    ) {

                    }
                    Column() {
                        for (targetingAND in targetingOR.targetingList) {

                            //and

                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(end = 12.dp),
                                verticalAlignment = CenterVertically
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.check4), //todo
                                    contentDescription = null,
                                    tint = colorResource(R.color.gold), //todo
                                    modifier = Modifier
                                        .padding(all = 12.dp)
                                        .width(24.dp)
                                        .height(24.dp)
                                        .alpha(1f),
                                )
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .padding(all = 8.dp),
                                        text = targetingAND.key + " " + targetingAND.operator + " " + targetingAND.values,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colorResource(R.color.text_bold),
                                        fontSize = 14.sp,
                                    )
                                }
                            }
                            if (andIndex < (targetingOR.targetingList.size - 1)) {
                                TargetingBadge(
                                    modifier = Modifier
                                        .padding(start = 48.dp),
                                    text = "AND"
                                )
                            }
                            andIndex++
                        }
                    }
                }

            }

            if (orIndex < (targeting.targetingList.size - 1)) {
                Row(
                    Modifier
                        .fillMaxWidth(),
                    verticalAlignment = CenterVertically
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.check4), //todo
                        contentDescription = null,
                        tint = colorResource(R.color.gold), //todo
                        modifier = Modifier
                            .padding(all = 12.dp)
                            .width(24.dp)
                            .height(24.dp)
                            .alpha(0f)
                    )
                    TargetingBadge(text = "OR")
                }

            }
            orIndex++
        }
    }
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
//    }
//}
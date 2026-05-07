package com.abtasty.qa_assistant_android.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.abtasty.flagship.model.VariationGroup
import com.abtasty.qa_assistant_android.ui.screens.TargetingOR
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.json.JSONArray


//data class Targeting(
//    val key: String,
//    val operator: String,
//    val values: ArrayList<Any?>
//) {
//    var isTargetingValid: Boolean = false
//    var matchingValue: Any? = null
//}
//
//data class TargetingAND(val targetingList: ArrayList<Targeting>) {
//    var isTargetingValid: Boolean = false
//}
//
//data class TargetingOR(val targetingList: ArrayList<TargetingAND>)
//
//fun extractTargetingValues(value: Any): ArrayList<Any?> {
//    val result = ArrayList<Any?>()
//    if (value is JSONArray) {
//        for (i in 0 until value.length()) {
//            result.add(value.get(i))
//        }
//    } else {
//        result.add(value)
//    }
//    return result
//}


@Composable
fun TargetingListItem(
    behavior: BottomSheetBehavior<*>,
    targetingOR: TargetingOR,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {

//    fun transform(): TargetingOR {
//        val targetingOR = ArrayList<TargetingAND>()
//        variationGroup.targetingGroups?.targetingGroups?.let { targetingGroups ->
//
//            for (targeting in targetingGroups) {
//                var allTargetingValid = true
//                val targetingAND = ArrayList<Targeting>()
//                targeting.targetingList?.let { targetingList ->
//                    for (targeting in targetingList) {
//                        val targetingTrans = Targeting(
//                            targeting.key,
//                            targeting.operator,
//                            extractTargetingValues(targeting.value),
//                        )
//                        targetingTrans.isTargetingValid = targeting.isTargetingValid
//                        targetingTrans.isTargetingValid = targeting.isTargetingValid
//                        if (!targeting.isTargetingValid) {
//                            allTargetingValid = false
//                        }
//                        targetingAND.add(targetingTrans)
//                    }
//                }
//                val targetingAnd = TargetingAND(targetingList = targetingAND)
//                targetingAnd.isTargetingValid = allTargetingValid
//                targetingOR.add(targetingAnd)
//            }
//        }
//        return TargetingOR(targetingList = targetingOR)
//    }

//    val targetingOR: TargetingOR = transform()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
       TargetingOrItem(targeting = targetingOR)
    }
}
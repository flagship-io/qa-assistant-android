package com.abtasty.qa_assistant_android.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.abtasty.flagship.visitor.VisitorDelegateDTO
import com.abtasty.qa_assistant_android.QAAssistant2
import com.abtasty.qa_assistant_android.ui.components.JsonBox
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.json.JSONObject

data class Visitor(
    val content: JSONObject = JSONObject()
        .put("visitorId", "8976239")
        .put("visitorName", "toto")
) {
    init {
        for (i in 0..60) {
            content.put("key$i", "value$i")
        }
    }
}

@Composable
fun ContextView(behavior: BottomSheetBehavior<*>, visitor: VisitorDelegateDTO? = QAAssistant2.currentVisitor) {
    val scrollState = rememberScrollState()

    // Equivalent de canScrollBackward pour ScrollState
    val contentNotAtTop by remember {
        derivedStateOf { scrollState.value > 0 }
    }

    DisposableEffect(contentNotAtTop) {
        behavior.isDraggable = !contentNotAtTop
        onDispose { behavior.isDraggable = true }
    }

    JsonBox(
        visitor?.contextToJson()?.toString(4) ?: "{}",
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)

    )
}
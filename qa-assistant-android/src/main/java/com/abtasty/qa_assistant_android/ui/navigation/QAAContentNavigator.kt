package com.abtasty.qa_assistant_android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.abtasty.qa_assistant_android.ui.screens.HomeScreen
import com.google.android.material.bottomsheet.BottomSheetBehavior

private sealed interface Screen {
    data object Home : Screen
    data class Detail(val campaignId: String) : Screen
}

@Composable
fun QAAContentNavigator(behavior: BottomSheetBehavior<*>, onClose: () -> Unit) {
    var screen = remember { mutableStateOf<Screen>(Screen.Home) }.value

    androidx.activity.compose.BackHandler {
        when (screen) {
            Screen.Home -> onClose()
            is Screen.Detail -> screen = Screen.Home
        }
    }

    when (val s = screen) {
        Screen.Home -> HomeScreen(
            behavior = behavior,
            onCampaignClick = { id -> screen = Screen.Detail(id) },
            onClose = onClose
        )
//        is Screen.Detail -> CampaignDetailScreen(
//            campaignId = s.campaignId,
//            onBack = { screen = Screen.Home }
//        )
        else -> {
            screen = Screen.Home
        }
    }
}
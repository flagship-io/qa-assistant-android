package com.abtasty.qa_assistant_android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.abtasty.flagship.model.Campaign
import com.abtasty.qa_assistant_android.ui.screens.CampaignDetailScreen
import com.abtasty.qa_assistant_android.ui.screens.HomeScreen
import com.google.android.material.bottomsheet.BottomSheetBehavior

sealed interface Screen {
    data object Home : Screen
    data class Detail(val campaign: Campaign) : Screen
}

internal var savedScreen: Screen? = null

@Composable
fun QAAContentNavigator(
    behavior: BottomSheetBehavior<*>,
    onClose: () -> Unit
) {
    var screen by remember { mutableStateOf<Screen>(savedScreen ?: Screen.Home) }

    savedScreen = screen

    androidx.activity.compose.BackHandler {
        when (screen) {
            Screen.Home -> onClose()
            is Screen.Detail -> screen = Screen.Home
        }
    }

    when (val s = screen) {
        is Screen.Home -> HomeScreen(
            behavior = behavior,
            onCampaignClick = { campaign ->
                println("QA DETAILS CLICKED: $campaign.id")
                screen = Screen.Detail(campaign) },
            onClose = onClose
        )
        is Screen.Detail -> CampaignDetailScreen(
            campaign = s.campaign,
            onCampaignStatusChanged = {campaign, status -> },
            onBack = { screen = Screen.Home },
            onClose = onClose
        )
        else -> {
            screen = Screen.Home
        }
    }
}
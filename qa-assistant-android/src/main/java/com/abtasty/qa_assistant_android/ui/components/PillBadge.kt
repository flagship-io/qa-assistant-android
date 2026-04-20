package com.abtasty.qa_assistant_android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.abtasty.qa_assistant_android.R

@Composable
fun PillBadge(label: String, modifier: Modifier, backgroundColor: Color, round: Int = 50) {

    BasicText(
        text = label,
        modifier = modifier
            .background(
                backgroundColor,
                shape = RoundedCornerShape(round)
            )
            .padding(all = 8.dp)
        ,
        style = TextStyle(
            fontWeight = FontWeight.SemiBold,
            color = colorResource(R.color.text_bold),
            textAlign = TextAlign.Center
        ),
    )
}
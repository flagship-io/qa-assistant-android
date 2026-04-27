package com.abtasty.qa_assistant_android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.abtasty.qa_assistant_android.R

@Composable
fun TargetingBadge(
    modifier: Modifier = Modifier,
    round: Int = 10,
    text: String = ""
) {
    Row(
        modifier = modifier
            .background(
                colorResource(R.color.event_content_background),
                shape = RoundedCornerShape(round)
            )
            .border(
                width = 1.dp,
                color = colorResource(R.color.cell_border),
                shape = RoundedCornerShape(round)
            )
            .padding(all = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(
            text = text,
            modifier = Modifier,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                color = colorResource(R.color.text_bold),
                textAlign = TextAlign.Center
            ),
            maxLines = 1,
        )
    }
}
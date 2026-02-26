package com.abtasty.qa_assistant_android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abtasty.qa_assistant_android.R

@Composable
fun JsonBox(content: String, modifier: Modifier? = null) {
    SelectionContainer {
        BasicText(
            text = content,
            modifier = (modifier ?: Modifier)
                .fillMaxSize()
                .padding(all = 12.dp)
                .background(
                    colorResource(R.color.event_content_background),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(all = 4.dp),
            style = TextStyle(
                color = colorResource(R.color.text_light_grey),
                fontSize = 14.sp
            )

        )
    }
}
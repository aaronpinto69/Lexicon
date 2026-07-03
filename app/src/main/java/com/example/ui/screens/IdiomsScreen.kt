package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.example.viewmodel.VocabViewModel
import com.example.ui.theme.*

@Composable
fun IdiomsScreen(viewModel: VocabViewModel) {
    val idioms by viewModel.allIdioms.collectAsState()
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "IDIOMS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Zinc500,
            letterSpacing = 2.sp
        )
        Text(
            text = "Figures of Speech",
            fontSize = 32.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            letterSpacing = (-0.5).sp
        )
        Spacer(modifier = Modifier.height(32.dp))

        if (idioms.isEmpty()) {
            Text(text = "No idioms found.", color = Zinc500)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 64.dp)
            ) {
                items(idioms) { idiom ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.selectIdiom(idiom) 
                            }
                    ) {
                        Text(
                            text = idiom.idiom,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = idiom.meaning,
                            fontSize = 15.sp,
                            color = Zinc400,
                            lineHeight = 22.sp,
                            maxLines = 2,
                            fontWeight = FontWeight.Light
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Zinc900, thickness = 1.dp)
                    }
                }
            }
        }
    }
}

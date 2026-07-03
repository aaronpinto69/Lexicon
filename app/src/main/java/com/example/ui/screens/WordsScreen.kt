package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
fun WordsScreen(viewModel: VocabViewModel) {
    val allWords by viewModel.allWords.collectAsState()
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "DICTIONARY",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Zinc500,
            letterSpacing = 2.sp
        )
        Text(
            text = "All Words",
            fontSize = 32.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            letterSpacing = (-0.5).sp
        )
        Spacer(modifier = Modifier.height(32.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 64.dp)
        ) {
            items(allWords) { word ->
                WordListItem(
                    word = word.word,
                    partOfSpeech = word.partOfSpeech,
                    meaning = word.simpleMeaning,
                    onClick = { viewModel.selectWord(word) }
                )
            }
        }
    }
}

@Composable
fun WordListItem(word: String, partOfSpeech: String, meaning: String, onClick: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { 
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = word,
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White
            )
            Text(
                text = partOfSpeech,
                fontSize = 14.sp,
                color = Zinc500
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = meaning,
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

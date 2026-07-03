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
fun BookmarksScreen(viewModel: VocabViewModel) {
    val words by viewModel.bookmarkedWords.collectAsState()
    val idioms by viewModel.bookmarkedIdioms.collectAsState()
    val roots by viewModel.bookmarkedRoots.collectAsState()
    val prefixes by viewModel.bookmarkedPrefixes.collectAsState()
    val suffixes by viewModel.bookmarkedSuffixes.collectAsState()

    val hasBookmarks = words.isNotEmpty() || idioms.isNotEmpty() || roots.isNotEmpty() || prefixes.isNotEmpty() || suffixes.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "SAVED",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Zinc500,
            letterSpacing = 2.sp
        )
        Text(
            text = "Your Library",
            fontSize = 32.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            letterSpacing = (-0.5).sp
        )
        Spacer(modifier = Modifier.height(32.dp))

        if (!hasBookmarks) {
            Text(text = "No saved items yet.", color = Zinc500)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 64.dp)
            ) {
                if (words.isNotEmpty()) {
                    item { SectionHeader("Words") }
                    items(words) { word ->
                        SavedItem(
                            title = word.word, 
                            subtitle = word.simpleMeaning, 
                            onClick = { viewModel.selectWord(word) }
                        )
                    }
                }
                
                if (idioms.isNotEmpty()) {
                    item { SectionHeader("Idioms") }
                    items(idioms) { idiom ->
                        SavedItem(
                            title = idiom.idiom, 
                            subtitle = idiom.meaning, 
                            onClick = { viewModel.selectIdiom(idiom) }
                        )
                    }
                }

                if (roots.isNotEmpty()) {
                    item { SectionHeader("Roots") }
                    items(roots) { root ->
                        SavedItem(
                            title = root.root, 
                            subtitle = root.meaning, 
                            onClick = { viewModel.selectRoot(root) }
                        )
                    }
                }

                if (prefixes.isNotEmpty()) {
                    item { SectionHeader("Prefixes") }
                    items(prefixes) { prefix ->
                        SavedItem(
                            title = prefix.prefix, 
                            subtitle = prefix.meaning, 
                            onClick = { viewModel.selectPrefix(prefix) }
                        )
                    }
                }

                if (suffixes.isNotEmpty()) {
                    item { SectionHeader("Suffixes") }
                    items(suffixes) { suffix ->
                        SavedItem(
                            title = suffix.suffix, 
                            subtitle = suffix.meaning, 
                            onClick = { viewModel.selectSuffix(suffix) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SavedItem(title: String, subtitle: String, onClick: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { 
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = subtitle,
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

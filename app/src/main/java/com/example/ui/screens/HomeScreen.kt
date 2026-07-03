package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.example.viewmodel.VocabViewModel
import com.example.ui.theme.*

@Composable
fun HomeScreen(viewModel: VocabViewModel) {
    val wordOfTheDay by viewModel.wordOfTheDay.collectAsState()
    val idiomOfTheDay by viewModel.idiomOfTheDay.collectAsState()
    val rootOfTheDay by viewModel.rootOfTheDay.collectAsState()
    
    val query by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val searchIdiomResults by viewModel.searchIdiomResults.collectAsState()
    val searchRootResults by viewModel.searchRootResults.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()
    
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "LEXICON",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Zinc500,
            letterSpacing = 2.sp
        )
        Text(
            text = "Daily Discovery",
            fontSize = 32.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            letterSpacing = (-0.5).sp
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Search Input
        OutlinedTextField(
            value = query,
            onValueChange = { viewModel.updateSearchQuery(it) },
            placeholder = { Text("Search words, idioms, or roots...", color = Zinc600, fontSize = 15.sp, fontWeight = FontWeight.Light) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Zinc500,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                            tint = Zinc500,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Black,
                unfocusedContainerColor = Color.Black,
                focusedBorderColor = Zinc500,
                unfocusedBorderColor = Zinc800,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_text_input")
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = 64.dp)
        ) {
            if (query.isBlank()) {
                if (recentSearches.isNotEmpty()) {
                    item {
                        SectionHeader("Recent Searches")
                    }
                    items(recentSearches.take(3)) { search ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    viewModel.updateSearchQuery(search.query) 
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = Zinc600,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = search.query,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Light
                            )
                        }
                    }
                    item {
                        HorizontalDivider(color = Zinc900, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                    }
                }

                item {
                    SectionHeader("Word of the Day")
                    if (wordOfTheDay != null) {
                        DiscoveryItem(
                            title = wordOfTheDay!!.word,
                            subtitle = wordOfTheDay!!.simpleMeaning,
                            label = wordOfTheDay!!.partOfSpeech,
                            onClick = { viewModel.selectWord(wordOfTheDay) }
                        )
                    } else {
                        EmptyItem("No words available.")
                    }
                }
                item {
                    HorizontalDivider(color = Zinc900, thickness = 1.dp)
                }
                item {
                    SectionHeader("Idiom of the Day")
                    if (idiomOfTheDay != null) {
                        DiscoveryItem(
                            title = idiomOfTheDay!!.idiom,
                            subtitle = idiomOfTheDay!!.meaning,
                            label = "Idiom",
                            onClick = { viewModel.selectIdiom(idiomOfTheDay) }
                        )
                    } else {
                        EmptyItem("No idioms available.")
                    }
                }
                item {
                    HorizontalDivider(color = Zinc900, thickness = 1.dp)
                }
                item {
                    SectionHeader("Root of the Day")
                    if (rootOfTheDay != null) {
                        DiscoveryItem(
                            title = rootOfTheDay!!.root,
                            subtitle = rootOfTheDay!!.meaning,
                            label = "Root",
                            onClick = { viewModel.selectRoot(rootOfTheDay) }
                        )
                    } else {
                        EmptyItem("No roots available.")
                    }
                }
            } else {
                // Search Results View
                if (searchResults.isNotEmpty()) {
                    item { SectionHeader("Words") }
                    items(searchResults) { word ->
                        SearchWordListItem(
                            word = word.word,
                            partOfSpeech = word.partOfSpeech,
                            meaning = word.simpleMeaning,
                            onClick = { viewModel.selectWord(word) }
                        )
                    }
                }
                if (searchIdiomResults.isNotEmpty()) {
                    item { SectionHeader("Idioms") }
                    items(searchIdiomResults) { idiom ->
                        SearchWordListItem(
                            word = idiom.idiom,
                            partOfSpeech = "Idiom",
                            meaning = idiom.meaning,
                            onClick = { viewModel.selectIdiom(idiom) }
                        )
                    }
                }
                if (searchRootResults.isNotEmpty()) {
                    item { SectionHeader("Roots") }
                    items(searchRootResults) { root ->
                        SearchWordListItem(
                            word = root.root,
                            partOfSpeech = "Root",
                            meaning = root.meaning,
                            onClick = { viewModel.selectRoot(root) }
                        )
                    }
                }
                if (searchResults.isEmpty() && searchIdiomResults.isEmpty() && searchRootResults.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No results found for \"$query\".",
                                fontSize = 16.sp,
                                color = Zinc600,
                                fontWeight = FontWeight.Light
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = Zinc500,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
fun DiscoveryItem(title: String, subtitle: String, label: String, onClick: () -> Unit) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    fontSize = 14.sp,
                    color = Zinc500
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "View Details",
                tint = Zinc500,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = subtitle,
            fontSize = 16.sp,
            color = Zinc300,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Light,
            maxLines = 3
        )
    }
}

@Composable
fun SearchWordListItem(word: String, partOfSpeech: String, meaning: String, onClick: () -> Unit) {
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

@Composable
fun EmptyItem(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        color = Zinc600,
        fontWeight = FontWeight.Light
    )
}

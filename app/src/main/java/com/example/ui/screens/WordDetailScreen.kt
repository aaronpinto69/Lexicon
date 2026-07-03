package com.example.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.example.data.model.ExampleSentence
import com.example.data.model.Word
import com.example.data.model.WordFamilyForm
import com.example.ui.theme.*
import com.example.viewmodel.VocabViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WordDetailScreen(
    word: Word,
    viewModel: VocabViewModel,
    onBack: () -> Unit,
    onBookmarkToggle: () -> Unit
) {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var ttsInitialized by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    DisposableEffect(Unit) {
        val instance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsInitialized = true
            }
        }
        tts = instance
        onDispose {
            instance.stop()
            instance.shutdown()
        }
    }

    val moshi = remember { Moshi.Builder().add(KotlinJsonAdapterFactory()).build() }
    val synonymsList = remember(word.synonymsJson) { parseSimpleList(word.synonymsJson) }
    val antonymsList = remember(word.antonymsJson) { parseSimpleList(word.antonymsJson) }
    val collocationsList = remember(word.collocationsJson) { parseSimpleList(word.collocationsJson) }
    val relatedList = remember(word.relatedWordsJson) { parseSimpleList(word.relatedWordsJson) }
    val examplesList = remember(word.examplesJson) {
        try {
            val type = Types.newParameterizedType(List::class.java, ExampleSentence::class.java)
            moshi.adapter<List<ExampleSentence>>(type).fromJson(word.examplesJson) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    val familyList = remember(word.wordFamilyJson) {
        try {
            val type = Types.newParameterizedType(List::class.java, WordFamilyForm::class.java)
            moshi.adapter<List<WordFamilyForm>>(type).fromJson(word.wordFamilyJson) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }


    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 64.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // Top Nav
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.clickable(onClick = onBack),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Zinc400,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Back", color = Zinc400, fontSize = 16.sp)
                }
                
                Row(
                    modifier = Modifier.clickable { 
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onBookmarkToggle() 
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (word.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Save",
                        tint = if (word.isBookmarked) Color.White else Zinc400,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save", color = if (word.isBookmarked) Color.White else Zinc400, fontSize = 16.sp)
                }
            }
        }

        // Title and Pronunciation
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = word.word,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        letterSpacing = (-1).sp
                    )
                    if (ttsInitialized) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Pronounce",
                            tint = Zinc400,
                            modifier = Modifier
                                .size(28.dp)
                                .clickable {
                                    tts?.language = Locale.US
                                    tts?.speak(word.word, TextToSpeech.QUEUE_FLUSH, null, null)
                                }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = word.partOfSpeech.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                        fontSize = 16.sp,
                        color = Zinc400
                    )
                    Text(
                        text = "•",
                        fontSize = 16.sp,
                        color = Zinc600
                    )
                    Text(
                        text = word.ipa,
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Zinc400
                    )
                }
            }
        }

        // 1. Meaning
        item {
            Column {
                Text(
                    text = word.simpleMeaning,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    lineHeight = 32.sp
                )
                if (word.detailedMeaning.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = word.detailedMeaning,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Light,
                        color = Zinc300,
                        lineHeight = 24.sp
                    )
                }
            }
        }

        // 2. Examples
        if (examplesList.isNotEmpty()) {
            item {
                ElegantSection(title = "Examples") {
                    examplesList.forEachIndexed { idx, ex ->
                        val annotatedStr = buildAnnotatedString {
                            val lowerText = ex.text.lowercase()
                            val lowerWord = word.word.lowercase()
                            val startIndex = lowerText.indexOf(lowerWord)
                            if (startIndex >= 0) {
                                append(ex.text.substring(0, startIndex))
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) {
                                    append(ex.text.substring(startIndex, startIndex + word.word.length))
                                }
                                append(ex.text.substring(startIndex + word.word.length))
                            } else {
                                append(ex.text)
                            }
                        }
                        Text(
                            text = annotatedStr,
                            fontSize = 18.sp,
                            color = Zinc200,
                            lineHeight = 28.sp,
                            fontWeight = FontWeight.Light
                        )
                        if (idx < examplesList.size - 1) {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }

        // 3. Synonyms
        if (synonymsList.isNotEmpty()) {
            item {
                ElegantSection(title = "Synonyms") {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        synonymsList.forEach { synonym ->
                            ElegantChip(text = synonym) {
                                viewModel.selectWordByName(synonym)
                            }
                        }
                    }
                }
            }
        }

        // 4. Antonyms
        if (antonymsList.isNotEmpty()) {
            item {
                ElegantSection(title = "Antonyms") {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        antonymsList.forEach { antonym ->
                            ElegantChip(text = antonym) {
                                viewModel.selectWordByName(antonym)
                            }
                        }
                    }
                }
            }
        }

        // 5. Word Family
        if (familyList.isNotEmpty()) {
            item {
                ElegantSection(title = "Word Family") {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        familyList.forEach { form ->
                            ElegantChip(text = form.word) {
                                viewModel.selectWordByName(form.word)
                            }
                        }
                    }
                }
            }
        }        // 6. Root
        if (word.rootWord.isNotEmpty()) {
            item {
                ElegantSection(title = "Root") {
                    val rootObj = viewModel.allRoots.value.find { it.root.equals(word.rootWord, ignoreCase = true) }
                    
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = word.rootWord, fontSize = 20.sp, color = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        if (rootObj != null) {
                            Text(text = rootObj.meaning, fontSize = 16.sp, color = Zinc300)
                            if (rootObj.origin.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = rootObj.origin, fontSize = 14.sp, color = Zinc500)
                            }
                        } else {
                            Text(text = "Meaning", fontSize = 12.sp, color = Zinc500)
                            Text(text = "See dedicated root entry for details", fontSize = 16.sp, color = Zinc300)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.clickable { viewModel.selectRootByName(word.rootWord) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "View Root",
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "View Root",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // 7. Prefix & Suffix
        if (word.prefix.isNotEmpty() || word.suffix.isNotEmpty()) {
            item {
                ElegantSection(title = "Prefix & Suffix") {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        if (word.prefix.isNotEmpty()) {
                            Column {
                                Text(text = "Prefix", fontSize = 14.sp, color = Zinc500)
                                Spacer(modifier = Modifier.height(8.dp))
                                ElegantChip(text = "${word.prefix}-") {
                                    viewModel.selectPrefixByName(word.prefix)
                                }
                            }
                        }
                        if (word.suffix.isNotEmpty()) {
                            Column {
                                Text(text = "Suffix", fontSize = 14.sp, color = Zinc500)
                                Spacer(modifier = Modifier.height(8.dp))
                                ElegantChip(text = "-${word.suffix}") {
                                    viewModel.selectSuffixByName(word.suffix)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 8. Etymology
        if (word.etymology.isNotEmpty()) {
            item {
                ElegantSection(title = "Etymology") {
                    Text(
                        text = word.etymology.replace(" -> ", "\n↓\n").replace("; ", "\n↓\n"),
                        fontSize = 16.sp,
                        color = Zinc300,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Light
                    )
                }
            }
        }


        // 10. Related Words
        if (relatedList.isNotEmpty()) {
            item {
                ElegantSection(title = "Related Words") {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        relatedList.forEach { related ->
                            ElegantChip(text = related) {
                                viewModel.selectWordByName(related)
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun ElegantSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Zinc500
        )
        Spacer(modifier = Modifier.height(16.dp))
        content()
    }
}

@Composable
fun ElegantChip(text: String, onClick: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    Surface(
        color = Zinc900,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.clickable { 
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick() 
        }
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        )
    }
}


fun parseSimpleList(json: String?): List<String> {
    if (json.isNullOrBlank() || json == "[]") return emptyList()
    return try {
        json.replace("[", "")
            .replace("]", "")
            .replace("\"", "")
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    } catch (e: Exception) {
        emptyList()
    }
}

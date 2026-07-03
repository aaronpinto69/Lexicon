package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "words")
@JsonClass(generateAdapter = true)
data class Word(
    @PrimaryKey val word: String,
    val ipa: String = "",
    val simpleMeaning: String = "",
    val detailedMeaning: String = "",
    val partOfSpeech: String = "",
    val etymology: String = "",
    val rootWord: String = "",
    val prefix: String = "",
    val suffix: String = "",
    val synonymsJson: String = "[]",
    val antonymsJson: String = "[]",
    val examplesJson: String = "[]",
    val collocationsJson: String = "[]",
    val wordFamilyJson: String = "[]",
    val relatedWordsJson: String = "[]",
    val difficulty: String = "Medium",
    val frequency: String = "",
    val isBookmarked: Boolean = false
)

@JsonClass(generateAdapter = true)
data class ExampleSentence(
    val text: String,
    val context: String = ""
)

@JsonClass(generateAdapter = true)
data class WordFamilyForm(
    val word: String,
    val partOfSpeech: String
)

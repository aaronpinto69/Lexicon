package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "suffixes")
@JsonClass(generateAdapter = true)
data class Suffix(
    @PrimaryKey val suffix: String,
    val meaning: String = "",
    val origin: String = "",
    val exampleWordsJson: String = "[]",
    val relatedSuffixesJson: String = "[]",
    val isBookmarked: Boolean = false
)

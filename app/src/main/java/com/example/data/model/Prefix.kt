package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "prefixes")
@JsonClass(generateAdapter = true)
data class Prefix(
    @PrimaryKey val prefix: String,
    val meaning: String = "",
    val origin: String = "",
    val exampleWordsJson: String = "[]",
    val relatedPrefixesJson: String = "[]",
    val isBookmarked: Boolean = false
)

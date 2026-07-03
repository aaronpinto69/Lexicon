package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "idioms")
@JsonClass(generateAdapter = true)
data class Idiom(
    @PrimaryKey val idiom: String,
    val meaning: String = "",
    val literalMeaning: String = "",
    val origin: String = "",
    val examplesJson: String = "[]",
    val similarIdiomsJson: String = "[]",
    val relatedIdiomsJson: String = "[]",
    val category: String = "",
    val difficulty: String = "Medium",
    val isBookmarked: Boolean = false
)

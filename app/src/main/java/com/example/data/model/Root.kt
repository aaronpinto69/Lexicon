package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "roots")
@JsonClass(generateAdapter = true)
data class Root(
    @PrimaryKey val root: String,
    val meaning: String = "",
    val origin: String = "",
    val exampleWordsJson: String = "[]",
    val relatedRootsJson: String = "[]",
    val isBookmarked: Boolean = false
)

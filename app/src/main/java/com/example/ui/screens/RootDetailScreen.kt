package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Root
import com.example.ui.theme.*

@Composable
fun RootDetailScreen(
    root: Root,
    onBack: () -> Unit,
    onBookmarkToggle: () -> Unit
) {
    val exampleWordsList = remember(root.exampleWordsJson) { parseSimpleList(root.exampleWordsJson) }
    val relatedList = remember(root.relatedRootsJson) { parseSimpleList(root.relatedRootsJson) }

    LazyColumn(
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
                    modifier = Modifier.clickable(onClick = onBookmarkToggle),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (root.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Save",
                        tint = if (root.isBookmarked) Color.White else Zinc400,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save", color = if (root.isBookmarked) Color.White else Zinc400, fontSize = 16.sp)
                }
            }
        }

        // Title
        item {
            Column {
                Text(
                    text = root.root,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    letterSpacing = (-1).sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Root",
                    fontSize = 16.sp,
                    color = Zinc400
                )
            }
        }

        item {
            Column {
                Text(
                    text = root.meaning,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    lineHeight = 32.sp
                )
            }
        }

        if (root.origin.isNotEmpty()) {
            item {
                ElegantSection(title = "Origin") {
                    Text(
                        text = root.origin,
                        fontSize = 16.sp,
                        color = Zinc300,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Light
                    )
                }
            }
        }

        if (exampleWordsList.isNotEmpty()) {
            item {
                ElegantSection(title = "Example Words") {
                    Text(
                        text = exampleWordsList.joinToString(", "),
                        fontSize = 16.sp,
                        color = Zinc300,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Light
                    )
                }
            }
        }

        if (relatedList.isNotEmpty()) {
            item {
                ElegantSection(title = "Related Roots") {
                    Text(
                        text = relatedList.joinToString(", "),
                        fontSize = 16.sp,
                        color = Zinc300,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Light
                    )
                }
            }
        }
    }
}

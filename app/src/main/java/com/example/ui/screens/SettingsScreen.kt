package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.VocabViewModel
import com.example.ui.theme.*

@Composable
fun SettingsScreen(viewModel: VocabViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "SETTINGS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Zinc500,
            letterSpacing = 2.sp
        )
        Text(
            text = "Preferences",
            fontSize = 32.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            letterSpacing = (-0.5).sp
        )
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Lexicon is a premium, offline-first English vocabulary reference. All data is stored locally on your device.",
            fontSize = 16.sp,
            color = Zinc300,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Light
        )

        Spacer(modifier = Modifier.height(32.dp))
        HorizontalDivider(color = Zinc900, thickness = 1.dp)
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.clearSearchHistory() },
            colors = ButtonDefaults.buttonColors(containerColor = Zinc900, contentColor = Color.White),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            Text("Clear Search History", fontSize = 16.sp, fontWeight = FontWeight.Normal)
        }
    }
}

package com.example.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.example.viewmodel.VocabViewModel
import com.example.ui.theme.Zinc900
import com.example.ui.theme.Zinc600

@Composable
fun MainScreen(viewModel: VocabViewModel) {
    var currentTab by remember { mutableStateOf(Tab.HOME) }
    val haptic = LocalHapticFeedback.current
    
    val selectedWord by viewModel.selectedWord.collectAsState()
    val selectedIdiom by viewModel.selectedIdiom.collectAsState()
    val selectedRoot by viewModel.selectedRoot.collectAsState()
    val selectedPrefix by viewModel.selectedPrefix.collectAsState()
    val selectedSuffix by viewModel.selectedSuffix.collectAsState()

    val isDetailViewOpen = selectedWord != null || selectedIdiom != null || selectedRoot != null || selectedPrefix != null || selectedSuffix != null

    Scaffold(
        bottomBar = {
            if (!isDetailViewOpen) {
                OledBottomNavigation(
                    selectedTab = currentTab,
                    onTabSelected = { currentTab = it }
                )
            }
        },
        containerColor = Color.Black,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(innerPadding)
        ) {
            if (isDetailViewOpen) {
                when {
                    selectedWord != null -> WordDetailScreen(
                        word = selectedWord!!,
                        viewModel = viewModel,
                        onBack = { 
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.selectWord(null) 
                        },
                        onBookmarkToggle = { 
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.toggleBookmarkWord(selectedWord!!) 
                        }
                    )
                    selectedIdiom != null -> IdiomDetailScreen(
                        idiom = selectedIdiom!!,
                        onBack = { 
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.selectIdiom(null) 
                        },
                        onBookmarkToggle = { 
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.toggleBookmarkIdiom(selectedIdiom!!) 
                        }
                    )
                    selectedRoot != null -> RootDetailScreen(
                        root = selectedRoot!!,
                        onBack = { 
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.selectRoot(null) 
                        },
                        onBookmarkToggle = { 
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.toggleBookmarkRoot(selectedRoot!!) 
                        }
                    )
                    selectedPrefix != null -> PrefixDetailScreen(
                        prefix = selectedPrefix!!,
                        onBack = { 
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.selectPrefix(null) 
                        },
                        onBookmarkToggle = { 
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.toggleBookmarkPrefix(selectedPrefix!!) 
                        }
                    )
                    selectedSuffix != null -> SuffixDetailScreen(
                        suffix = selectedSuffix!!,
                        onBack = { 
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.selectSuffix(null) 
                        },
                        onBookmarkToggle = { 
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.toggleBookmarkSuffix(selectedSuffix!!) 
                        }
                    )
                }
            } else {
                Crossfade(targetState = currentTab, label = "tab_fade") { tab ->
                    when (tab) {
                        Tab.HOME -> HomeScreen(viewModel)
                        Tab.WORDS -> WordsScreen(viewModel)
                        Tab.IDIOMS -> IdiomsScreen(viewModel)
                        Tab.SAVED -> BookmarksScreen(viewModel)
                        Tab.SETTINGS -> SettingsScreen(viewModel)
                    }
                }
            }
        }
    }
}

enum class Tab(val title: String, val filledIcon: ImageVector, val outlinedIcon: ImageVector) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home),
    WORDS("Words", Icons.AutoMirrored.Filled.MenuBook, Icons.AutoMirrored.Outlined.MenuBook),
    IDIOMS("Idioms", Icons.Filled.ChatBubble, Icons.Outlined.ChatBubbleOutline),
    SAVED("Saved", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder),
    SETTINGS("Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
}

@Composable
fun OledBottomNavigation(
    selectedTab: Tab,
    onTabSelected: (Tab) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    Surface(
        color = Color.Black,
        tonalElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Column {
            HorizontalDivider(color = Zinc900, thickness = 1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(Color.Black),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Tab.values().forEach { tab ->
                    val isSelected = selectedTab == tab
                    val icon = if (isSelected) tab.filledIcon else tab.outlinedIcon
                    val contentColor = if (isSelected) Color.White else Zinc600

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(onClick = { 
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onTabSelected(tab) 
                            })
                            .testTag("nav_tab_${tab.name.lowercase()}"),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = tab.title,
                            tint = contentColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = tab.title.uppercase(),
                            color = contentColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.5).sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

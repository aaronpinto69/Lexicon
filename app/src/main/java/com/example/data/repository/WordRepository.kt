package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.local.AppDatabase
import com.example.data.local.WordDao
import com.example.data.model.SearchHistory
import com.example.data.model.Word
import com.example.data.model.Idiom
import com.example.data.model.Root
import com.example.data.model.Prefix
import com.example.data.model.Suffix
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class WordRepository(context: Context) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val wordDao: WordDao = AppDatabase.getDatabase(context, scope).wordDao()

    val allWords: Flow<List<Word>> = wordDao.getAllWords()
    val bookmarkedWords: Flow<List<Word>> = wordDao.getBookmarkedWords()
    val totalWordsCount: Flow<Int> = wordDao.getTotalWordsCount()
    val recentSearches: Flow<List<SearchHistory>> = wordDao.getRecentSearchHistory()

    val allIdioms: Flow<List<Idiom>> = wordDao.getAllIdioms()
    val bookmarkedIdioms: Flow<List<Idiom>> = wordDao.getBookmarkedIdioms()

    val allRoots: Flow<List<Root>> = wordDao.getAllRoots()
    val bookmarkedRoots: Flow<List<Root>> = wordDao.getBookmarkedRoots()

    val allPrefixes: Flow<List<Prefix>> = wordDao.getAllPrefixes()
    val bookmarkedPrefixes: Flow<List<Prefix>> = wordDao.getBookmarkedPrefixes()

    val allSuffixes: Flow<List<Suffix>> = wordDao.getAllSuffixes()
    val bookmarkedSuffixes: Flow<List<Suffix>> = wordDao.getBookmarkedSuffixes()

    suspend fun getWordOffline(wordName: String): Word? = withContext(Dispatchers.IO) {
        wordDao.getWord(wordName)
    }

    suspend fun toggleBookmarkWord(word: Word) = withContext(Dispatchers.IO) {
        val updated = word.copy(isBookmarked = !word.isBookmarked)
        wordDao.updateWord(updated)
    }

    suspend fun toggleBookmarkIdiom(idiom: Idiom) = withContext(Dispatchers.IO) {
        val updated = idiom.copy(isBookmarked = !idiom.isBookmarked)
        wordDao.updateIdiom(updated)
    }

    suspend fun toggleBookmarkRoot(root: Root) = withContext(Dispatchers.IO) {
        val updated = root.copy(isBookmarked = !root.isBookmarked)
        wordDao.updateRoot(updated)
    }

    suspend fun toggleBookmarkPrefix(prefix: Prefix) = withContext(Dispatchers.IO) {
        val updated = prefix.copy(isBookmarked = !prefix.isBookmarked)
        wordDao.updatePrefix(updated)
    }

    suspend fun toggleBookmarkSuffix(suffix: Suffix) = withContext(Dispatchers.IO) {
        val updated = suffix.copy(isBookmarked = !suffix.isBookmarked)
        wordDao.updateSuffix(updated)
    }

    fun searchWords(query: String): Flow<List<Word>> {
        return wordDao.searchWords(query)
    }

    suspend fun insertSearch(query: String) = withContext(Dispatchers.IO) {
        if (query.trim().isNotEmpty()) {
            wordDao.insertSearch(SearchHistory(query = query.trim(), timestamp = System.currentTimeMillis()))
        }
    }

    suspend fun deleteSearch(query: String) = withContext(Dispatchers.IO) {
        wordDao.deleteSearch(query)
    }

    suspend fun clearSearchHistory() = withContext(Dispatchers.IO) {
        wordDao.clearSearchHistory()
    }
}

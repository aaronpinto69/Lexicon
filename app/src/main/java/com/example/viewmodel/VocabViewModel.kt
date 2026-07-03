package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.SearchHistory
import com.example.data.model.Word
import com.example.data.model.Idiom
import com.example.data.model.Root
import com.example.data.model.Prefix
import com.example.data.model.Suffix
import com.example.data.repository.WordRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class VocabViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = WordRepository(application)

    // Expose flows from Repository
    val allWords: StateFlow<List<Word>> = repository.allWords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarkedWords: StateFlow<List<Word>> = repository.bookmarkedWords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentSearches: StateFlow<List<SearchHistory>> = repository.recentSearches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalWordsCount: StateFlow<Int> = repository.totalWordsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val allIdioms: StateFlow<List<Idiom>> = repository.allIdioms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarkedIdioms: StateFlow<List<Idiom>> = repository.bookmarkedIdioms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRoots: StateFlow<List<Root>> = repository.allRoots
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarkedRoots: StateFlow<List<Root>> = repository.bookmarkedRoots
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPrefixes: StateFlow<List<Prefix>> = repository.allPrefixes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarkedPrefixes: StateFlow<List<Prefix>> = repository.bookmarkedPrefixes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSuffixes: StateFlow<List<Suffix>> = repository.allSuffixes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarkedSuffixes: StateFlow<List<Suffix>> = repository.bookmarkedSuffixes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search state
    val searchQuery = MutableStateFlow("")
    
    // Search results (Words only for universal search integration for now)
    val searchResults: StateFlow<List<Word>> = combine(allWords, searchQuery) { words, query ->
        if (query.isBlank()) {
            emptyList()
        } else {
            val q = query.trim().lowercase()
            
            // 1. Direct or partial word matches
            val directMatches = words.filter { it.word.lowercase().contains(q) }
            
            // 2. Meaning/Definition matches
            val meaningMatches = words.filter { 
                it.simpleMeaning.lowercase().contains(q) || it.detailedMeaning.lowercase().contains(q) 
            }
            
            // 3. Synonym matches
            val synonymMatches = words.filter { 
                it.synonymsJson.lowercase().contains(q) 
            }
            
            // 4. Typo-correction (Levenshtein distance <= 2 for word start/end)
            val typoMatches = words.filter { word ->
                val w = word.word.lowercase()
                val dist = levenshteinDistance(w, q)
                dist <= 2 && w.length >= 4 && q.length >= 4
            }
            
            // Combine all matches with priority
            (directMatches + synonymMatches + meaningMatches + typoMatches).distinctBy { it.word }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val searchIdiomResults: StateFlow<List<Idiom>> = combine(allIdioms, searchQuery) { idioms, query ->
        if (query.isBlank()) emptyList() else {
            val q = query.trim().lowercase()
            idioms.filter { it.idiom.lowercase().contains(q) || it.meaning.lowercase().contains(q) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val searchRootResults: StateFlow<List<Root>> = combine(allRoots, searchQuery) { roots, query ->
        if (query.isBlank()) emptyList() else {
            val q = query.trim().lowercase()
            roots.filter { it.root.lowercase().contains(q) || it.meaning.lowercase().contains(q) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selection states
    val selectedWord = MutableStateFlow<Word?>(null)
    val selectedIdiom = MutableStateFlow<Idiom?>(null)
    val selectedRoot = MutableStateFlow<Root?>(null)
    val selectedPrefix = MutableStateFlow<Prefix?>(null)
    val selectedSuffix = MutableStateFlow<Suffix?>(null)

    // Daily Features
    val wordOfTheDay: StateFlow<Word?> = allWords.map { items ->
        if (items.isEmpty()) null else items[getDayOfYearIndex(items.size)]
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val idiomOfTheDay: StateFlow<Idiom?> = allIdioms.map { items ->
        if (items.isEmpty()) null else items[getDayOfYearIndex(items.size)]
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val rootOfTheDay: StateFlow<Root?> = allRoots.map { items ->
        if (items.isEmpty()) null else items[getDayOfYearIndex(items.size)]
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private fun getDayOfYearIndex(size: Int): Int {
        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        return dayOfYear % size
    }

    // Actions
    fun updateSearchQuery(query: String) {
        searchQuery.value = query
        if (query.isNotBlank()) {
            viewModelScope.launch {
                repository.insertSearch(query)
            }
        }
    }

    fun deleteSearchHistory(query: String) {
        viewModelScope.launch {
            repository.deleteSearch(query)
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            repository.clearSearchHistory()
        }
    }

    fun selectWord(word: Word?) { selectedWord.value = word }
    fun selectIdiom(idiom: Idiom?) { selectedIdiom.value = idiom }
    fun selectRoot(root: Root?) { selectedRoot.value = root }
    fun selectPrefix(prefix: Prefix?) { selectedPrefix.value = prefix }
    fun selectSuffix(suffix: Suffix?) { selectedSuffix.value = suffix }

    fun selectWordByName(wordName: String) {
        viewModelScope.launch {
            val word = repository.getWordOffline(wordName)
            if (word != null) {
                selectedWord.value = word
            }
        }
    }

    fun selectRootByName(rootName: String) {
        val root = allRoots.value.find { it.root.equals(rootName, ignoreCase = true) || "-${it.root}-".equals(rootName, ignoreCase = true) }
        if (root != null) {
            selectedRoot.value = root
        }
    }

    fun selectPrefixByName(prefixName: String) {
        val prefix = allPrefixes.value.find { it.prefix.equals(prefixName, ignoreCase = true) || "${it.prefix}-".equals(prefixName, ignoreCase = true) }
        if (prefix != null) {
            selectedPrefix.value = prefix
        }
    }

    fun selectSuffixByName(suffixName: String) {
        val suffix = allSuffixes.value.find { it.suffix.equals(suffixName, ignoreCase = true) || "-${it.suffix}".equals(suffixName, ignoreCase = true) }
        if (suffix != null) {
            selectedSuffix.value = suffix
        }
    }

    fun toggleBookmarkWord(word: Word) {
        viewModelScope.launch {
            repository.toggleBookmarkWord(word)
            if (selectedWord.value?.word == word.word) {
                selectedWord.value = selectedWord.value?.copy(isBookmarked = !word.isBookmarked)
            }
        }
    }

    fun toggleBookmarkIdiom(idiom: Idiom) {
        viewModelScope.launch {
            repository.toggleBookmarkIdiom(idiom)
            if (selectedIdiom.value?.idiom == idiom.idiom) {
                selectedIdiom.value = selectedIdiom.value?.copy(isBookmarked = !idiom.isBookmarked)
            }
        }
    }

    fun toggleBookmarkRoot(root: Root) {
        viewModelScope.launch {
            repository.toggleBookmarkRoot(root)
            if (selectedRoot.value?.root == root.root) {
                selectedRoot.value = selectedRoot.value?.copy(isBookmarked = !root.isBookmarked)
            }
        }
    }

    fun toggleBookmarkPrefix(prefix: Prefix) {
        viewModelScope.launch {
            repository.toggleBookmarkPrefix(prefix)
            if (selectedPrefix.value?.prefix == prefix.prefix) {
                selectedPrefix.value = selectedPrefix.value?.copy(isBookmarked = !prefix.isBookmarked)
            }
        }
    }

    fun toggleBookmarkSuffix(suffix: Suffix) {
        viewModelScope.launch {
            repository.toggleBookmarkSuffix(suffix)
            if (selectedSuffix.value?.suffix == suffix.suffix) {
                selectedSuffix.value = selectedSuffix.value?.copy(isBookmarked = !suffix.isBookmarked)
            }
        }
    }

    // Levenshtein typo-correction formula
    private fun levenshteinDistance(lhs: CharSequence, rhs: CharSequence): Int {
        val len0 = lhs.length + 1
        val len1 = rhs.length + 1
        var cost = IntArray(len0)
        var newcost = IntArray(len0)
        for (i in 0 until len0) cost[i] = i
        for (j in 1 until len1) {
            newcost[0] = j
            for (i in 1 until len0) {
                val match = if (lhs[i - 1] == rhs[j - 1]) 0 else 1
                val costReplace = cost[i - 1] + match
                val costInsert = cost[i] + 1
                val costDelete = newcost[i - 1] + 1
                newcost[i] = costInsert.coerceAtMost(costDelete).coerceAtMost(costReplace)
            }
            val swap = cost
            cost = newcost
            newcost = swap
        }
        return cost[len0 - 1]
    }
}

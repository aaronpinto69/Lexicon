package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.SearchHistory
import com.example.data.model.Word
import com.example.data.model.Idiom
import com.example.data.model.Root
import com.example.data.model.Prefix
import com.example.data.model.Suffix
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words ORDER BY word ASC")
    fun getAllWords(): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE word = :word LIMIT 1")
    suspend fun getWord(word: String): Word?

    @Query("SELECT * FROM words WHERE word LIKE '%' || :query || '%' OR simpleMeaning LIKE '%' || :query || '%' OR detailedMeaning LIKE '%' || :query || '%' ORDER BY word ASC")
    fun searchWords(query: String): Flow<List<Word>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: Word)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<Word>)

    @Update
    suspend fun updateWord(word: Word)

    @Query("SELECT * FROM words WHERE isBookmarked = 1 ORDER BY word ASC")
    fun getBookmarkedWords(): Flow<List<Word>>

    @Query("SELECT COUNT(*) FROM words")
    fun getTotalWordsCount(): Flow<Int>

    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 20")
    fun getRecentSearchHistory(): Flow<List<SearchHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(search: SearchHistory)

    @Query("DELETE FROM search_history")
    suspend fun clearSearchHistory()
    
    @Query("DELETE FROM search_history WHERE `query` = :query")
    suspend fun deleteSearch(query: String)

    // Idioms
    @Query("SELECT * FROM idioms ORDER BY idiom ASC")
    fun getAllIdioms(): Flow<List<Idiom>>

    @Query("SELECT * FROM idioms WHERE idiom = :idiom LIMIT 1")
    suspend fun getIdiom(idiom: String): Idiom?

    @Query("SELECT * FROM idioms WHERE isBookmarked = 1 ORDER BY idiom ASC")
    fun getBookmarkedIdioms(): Flow<List<Idiom>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIdioms(idioms: List<Idiom>)

    @Update
    suspend fun updateIdiom(idiom: Idiom)

    // Roots
    @Query("SELECT * FROM roots ORDER BY root ASC")
    fun getAllRoots(): Flow<List<Root>>

    @Query("SELECT * FROM roots WHERE root = :root LIMIT 1")
    suspend fun getRoot(root: String): Root?

    @Query("SELECT * FROM roots WHERE isBookmarked = 1 ORDER BY root ASC")
    fun getBookmarkedRoots(): Flow<List<Root>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoots(roots: List<Root>)

    @Update
    suspend fun updateRoot(root: Root)

    // Prefixes
    @Query("SELECT * FROM prefixes ORDER BY prefix ASC")
    fun getAllPrefixes(): Flow<List<Prefix>>

    @Query("SELECT * FROM prefixes WHERE prefix = :prefix LIMIT 1")
    suspend fun getPrefix(prefix: String): Prefix?

    @Query("SELECT * FROM prefixes WHERE isBookmarked = 1 ORDER BY prefix ASC")
    fun getBookmarkedPrefixes(): Flow<List<Prefix>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrefixes(prefixes: List<Prefix>)

    @Update
    suspend fun updatePrefix(prefix: Prefix)

    // Suffixes
    @Query("SELECT * FROM suffixes ORDER BY suffix ASC")
    fun getAllSuffixes(): Flow<List<Suffix>>

    @Query("SELECT * FROM suffixes WHERE suffix = :suffix LIMIT 1")
    suspend fun getSuffix(suffix: String): Suffix?

    @Query("SELECT * FROM suffixes WHERE isBookmarked = 1 ORDER BY suffix ASC")
    fun getBookmarkedSuffixes(): Flow<List<Suffix>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSuffixes(suffixes: List<Suffix>)

    @Update
    suspend fun updateSuffix(suffix: Suffix)
}

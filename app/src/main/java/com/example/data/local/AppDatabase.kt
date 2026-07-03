package com.example.data.local

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.SearchHistory
import com.example.data.model.Word
import com.example.data.model.Idiom
import com.example.data.model.Root
import com.example.data.model.Prefix
import com.example.data.model.Suffix
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.InputStreamReader

@Database(
    entities = [
        Word::class, 
        SearchHistory::class, 
        Idiom::class, 
        Root::class, 
        Prefix::class, 
        Suffix::class
    ], 
    version = 3, 
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao

    private class AppDatabaseCallback(
        private val context: Context,
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.wordDao(), context)
                }
            }
        }

        private suspend fun populateDatabase(dao: WordDao, context: Context) {
            try {
                // Populate Words
                val wordsJsonString = InputStreamReader(context.assets.open("words.json")).readText()
                val wordsArray = JSONArray(wordsJsonString)
                val words = mutableListOf<Word>()
                for (i in 0 until wordsArray.length()) {
                    val obj = wordsArray.getJSONObject(i)
                    words.add(Word(
                        word = obj.getString("word"),
                        ipa = obj.optString("ipa", ""),
                        simpleMeaning = obj.getString("simpleMeaning"),
                        detailedMeaning = obj.optString("detailedMeaning", ""),
                        partOfSpeech = obj.getString("partOfSpeech"),
                        etymology = obj.optString("etymology", ""),
                        synonymsJson = obj.optString("synonymsJson", "[]"),
                        antonymsJson = obj.optString("antonymsJson", "[]"),
                        examplesJson = obj.optString("examplesJson", "[]"),
                        wordFamilyJson = obj.optString("wordFamilyJson", "[]"),
                        difficulty = obj.optString("difficulty", ""),
                        frequency = obj.optString("frequency", ""),
                        rootWord = obj.optString("rootWord", ""),
                        prefix = obj.optString("prefix", ""),
                        suffix = obj.optString("suffix", "")
                    ))
                }
                for (word in words) {
                    dao.insertWord(word)
                }

                // Populate Idioms
                val idiomsJsonString = InputStreamReader(context.assets.open("idioms.json")).readText()
                val idiomsArray = JSONArray(idiomsJsonString)
                val idioms = mutableListOf<Idiom>()
                for (i in 0 until idiomsArray.length()) {
                    val obj = idiomsArray.getJSONObject(i)
                    idioms.add(Idiom(
                        idiom = obj.getString("idiom"),
                        meaning = obj.getString("meaning"),
                        literalMeaning = obj.optString("literalMeaning", ""),
                        origin = obj.optString("origin", ""),
                        examplesJson = obj.optString("examplesJson", "[]"),
                        similarIdiomsJson = obj.optString("similarIdiomsJson", "[]"),
                        category = obj.optString("category", ""),
                        difficulty = obj.optString("difficulty", "")
                    ))
                }
                dao.insertIdioms(idioms)

                // Populate Roots
                val rootsJsonString = InputStreamReader(context.assets.open("roots.json")).readText()
                val rootsArray = JSONArray(rootsJsonString)
                val roots = mutableListOf<Root>()
                for (i in 0 until rootsArray.length()) {
                    val obj = rootsArray.getJSONObject(i)
                    roots.add(Root(
                        root = obj.getString("root"),
                        meaning = obj.getString("meaning"),
                        origin = obj.optString("origin", ""),
                        exampleWordsJson = obj.optString("exampleWordsJson", "[]")
                    ))
                }
                dao.insertRoots(roots)

                // Populate Prefixes
                val prefixesJsonString = InputStreamReader(context.assets.open("prefixes.json")).readText()
                val prefixesArray = JSONArray(prefixesJsonString)
                val prefixes = mutableListOf<Prefix>()
                for (i in 0 until prefixesArray.length()) {
                    val obj = prefixesArray.getJSONObject(i)
                    prefixes.add(Prefix(
                        prefix = obj.getString("prefix"),
                        meaning = obj.getString("meaning"),
                        origin = obj.optString("origin", ""),
                        exampleWordsJson = obj.optString("exampleWordsJson", "[]")
                    ))
                }
                dao.insertPrefixes(prefixes)

                // Populate Suffixes
                val suffixesJsonString = InputStreamReader(context.assets.open("suffixes.json")).readText()
                val suffixesArray = JSONArray(suffixesJsonString)
                val suffixes = mutableListOf<Suffix>()
                for (i in 0 until suffixesArray.length()) {
                    val obj = suffixesArray.getJSONObject(i)
                    suffixes.add(Suffix(
                        suffix = obj.getString("suffix"),
                        meaning = obj.getString("meaning"),
                        origin = obj.optString("origin", ""),
                        exampleWordsJson = obj.optString("exampleWordsJson", "[]")
                    ))
                }
                dao.insertSuffixes(suffixes)
                
            } catch (e: Exception) {
                Log.e("AppDatabase", "Error populating database from JSON", e)
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lexicon_database"
                )
                .addCallback(AppDatabaseCallback(context.applicationContext, scope))
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

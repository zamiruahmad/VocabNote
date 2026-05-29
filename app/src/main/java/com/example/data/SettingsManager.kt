package com.example.data

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("lexicon_settings", Context.MODE_PRIVATE)

    var dailyRevisionGoal: Int
        get() = prefs.getInt("daily_revision_goal", 20)
        set(value) = prefs.edit().putInt("daily_revision_goal", value).apply()

    var totalRevisions: Int
        get() = prefs.getInt("total_revisions", 0)
        set(value) = prefs.edit().putInt("total_revisions", value).apply()

    fun incrementTotalRevisions() {
        totalRevisions += 1
    }

    var categoriesString: String
        get() = prefs.getString("custom_categories", "Vocabulary,Grammar,Proverb,Sentence,Expression,Idiom,Question,Other") ?: "Vocabulary,Grammar,Proverb,Sentence,Expression,Idiom,Question,Other"
        set(value) = prefs.edit().putString("custom_categories", value).apply()

    var languagesString: String
        get() = prefs.getString("custom_languages", "English,Bengali,Arabic,French,Spanish,German,Chinese,Japanese") ?: "English,Bengali,Arabic,French,Spanish,German,Chinese,Japanese"
        set(value) = prefs.edit().putString("custom_languages", value).apply()
}

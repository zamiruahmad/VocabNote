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
}

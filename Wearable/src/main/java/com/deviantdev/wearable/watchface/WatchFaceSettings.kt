package com.deviantdev.wearable.watchface

import android.content.Context
import android.graphics.Color

class WatchFaceSettings {

    var backgroundColor = Color.BLACK
    var watchHandHighlightColor = Color.RED
    var watchHandAndComplicationsColor = Color.BLACK
    var watchHandShadowColor = Color.WHITE

    /** User's preference for if they want visual shown to indicate unread notifications. */
    var unreadNotifications: Boolean = true

    fun reloadSavedPreferences(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFERENCE_FILE_KEY,
                Context.MODE_PRIVATE)

        backgroundColor = sharedPreferences.getInt(SAVED_BACKGROUND_COLOR, Color.BLACK)
        watchHandHighlightColor = sharedPreferences.getInt(SAVED_MARKERS_COLOR, Color.RED)
        if (backgroundColor == Color.WHITE) {
            watchHandAndComplicationsColor = Color.BLACK
            watchHandShadowColor = Color.WHITE
        } else {
            watchHandAndComplicationsColor = Color.WHITE
            watchHandShadowColor = Color.BLACK
        }
        unreadNotifications = sharedPreferences.getBoolean(SAVED_UNREAD_NOTIFICATIONS, true)
    }

    fun commitChangedPreferences(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFERENCE_FILE_KEY,
                Context.MODE_PRIVATE)
        val edit = sharedPreferences.edit()
        edit.putInt(SAVED_BACKGROUND_COLOR, backgroundColor)
        edit.putInt(SAVED_MARKERS_COLOR, watchHandHighlightColor)
        edit.putBoolean(SAVED_UNREAD_NOTIFICATIONS, unreadNotifications)
        edit.apply()
    }

    companion object {
        const val PREFERENCE_FILE_KEY = "com.deviantdev.wearable.watchface.PREFERENCE_FILE_KEY"

        const val SAVED_BACKGROUND_COLOR = "saved_background_color"
        const val SAVED_MARKERS_COLOR = "saved_markers_color"

        const val SAVED_UNREAD_NOTIFICATIONS = "saved_unread_notifications"

    }

}
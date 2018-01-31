package com.deviantdev.wearable.watchface.config.list

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.support.wearable.complications.ComplicationHelperActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import com.deviantdev.wearable.watchface.R
import com.deviantdev.wearable.watchface.WatchFaceComplicationConfiguration
import com.deviantdev.wearable.watchface.WatchFacePreferences
import com.deviantdev.wearable.watchface.WatchFaceService
import com.deviantdev.wearable.watchface.config.color.ColorSelectionActivity

/**
 * Displays icon to indicate there are more options below the fold.
 */
class MoreOptionsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val mMoreOptionsImageView: ImageView = view.findViewById(R.id.more_options_image_view)

    fun setIcon(resourceId: Int) {
        val context = mMoreOptionsImageView.context
        mMoreOptionsImageView.setImageDrawable(context.getDrawable(resourceId))
    }
}

/**
 * Displays color options for the an item on the watch face. These could include marker color,
 * background color, etc.
 */
class ColorPickerViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

    private val mAppearanceButton: Button = view.findViewById(R.id.color_picker_button)

    private var mLaunchActivityToSelectColor: Class<ColorSelectionActivity>? = null

    init {
        view.setOnClickListener(this)
    }

    fun setName(name: String) {
        mAppearanceButton.text = name
    }

    fun setIcon(resourceId: Int) {
        val context = mAppearanceButton.context
        mAppearanceButton.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(resourceId), null, null, null)
    }

    fun setLaunchActivityToSelectColor(activity: Class<ColorSelectionActivity>) {
        mLaunchActivityToSelectColor = activity
    }

    override fun onClick(view: View) {
        val position = adapterPosition
        Log.d(TAG, "Complication onClick() position: " + position)

        if (mLaunchActivityToSelectColor != null) {
            val launchIntent = Intent(view.context, mLaunchActivityToSelectColor)

            val activity = view.context as Activity
            activity.startActivityForResult(launchIntent,
                    AnalogComplicationConfigActivity.UPDATE_COLORS_CONFIG_REQUEST_CODE)
        }
    }

    companion object {
        val TAG = "" + ColorPickerViewHolder::class.java.simpleName
    }
}

/**
 * Displays switch to indicate whether or not icon appears for unread notifications. User can
 * toggle on/off.
 */
class UnreadNotificationViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

    private val mUnreadNotificationSwitch: Switch = view.findViewById(R.id.unread_notification_switch)

    private var mEnabledIconResourceId: Int = 0
    private var mDisabledIconResourceId: Int = 0

    init {
        view.setOnClickListener(this)
    }

    fun setName(name: String) {
        mUnreadNotificationSwitch.text = name
    }

    fun setIcons(enabledIconResourceId: Int, disabledIconResourceId: Int) {

        mEnabledIconResourceId = enabledIconResourceId
        mDisabledIconResourceId = disabledIconResourceId

        val context = mUnreadNotificationSwitch.context

        // Set default to enabled.
        mUnreadNotificationSwitch.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(mEnabledIconResourceId),
                null, null, null)
    }

    private fun updateIcon(context: Context, currentState: Boolean?) {
        val currentIconResourceId: Int = if (currentState!!) mEnabledIconResourceId else mDisabledIconResourceId

        mUnreadNotificationSwitch.isChecked = currentState
        mUnreadNotificationSwitch.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(currentIconResourceId),
                null, null, null)
    }

    override fun onClick(view: View) {
        val position = adapterPosition
        Log.d(TAG, "Complication onClick() position: " + position)

        val watchFacePreferences = WatchFacePreferences(view.context)

        // Since user clicked on a switch, new state should be opposite of current state.
        watchFacePreferences.unreadNotifications = !watchFacePreferences.unreadNotifications
        watchFacePreferences.commitChangedPreferences(view.context)

        updateIcon(view.context, watchFacePreferences.unreadNotifications)
    }

    companion object {
        val TAG = "" + ColorPickerViewHolder::class.java.simpleName
    }
}

/**
 * Displays button to trigger background image complication selector.
 */
class BackgroundComplicationViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

    private val mBackgroundComplicationButton: Button = view.findViewById(R.id.background_complication_button)

    init {

        view.setOnClickListener(this)
    }

    fun setName(name: String) {
        mBackgroundComplicationButton.text = name
    }

    fun setIcon(resourceId: Int) {
        val context = mBackgroundComplicationButton.context
        mBackgroundComplicationButton.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(resourceId), null,
                null, null)
    }

    override fun onClick(view: View) {
        val position = adapterPosition
        Log.d(TAG, "Background Complication onClick() position: " + position)

        val currentActivity = view.context as Activity

        val watchFace = ComponentName(currentActivity, WatchFaceService::class.java)

        currentActivity.startActivityForResult(
                ComplicationHelperActivity.createProviderChooserHelperIntent(currentActivity, watchFace,
                        WatchFaceComplicationConfiguration.Complication.BACKGROUND.id,
                        *WatchFaceComplicationConfiguration.Complication.BACKGROUND.supportedTypes),
                AnalogComplicationConfigActivity.COMPLICATION_CONFIG_REQUEST_CODE)

    }

    companion object {
        val TAG = "" + ColorPickerViewHolder::class.java.simpleName
    }
}
package com.deviantdev.wearable.watchface.config.list

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.support.wearable.complications.ComplicationHelperActivity
import android.support.wearable.complications.ComplicationProviderInfo
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Switch
import com.deviantdev.wearable.watchface.R
import com.deviantdev.wearable.watchface.WatchFaceComplicationConfiguration.Complication
import com.deviantdev.wearable.watchface.WatchFacePreferences
import com.deviantdev.wearable.watchface.WatchFaceService
import com.deviantdev.wearable.watchface.config.color.ColorSelectionActivity

/**
 * Displays watch face preview along with complication locations. Allows user to tap on the
 * complication they want to change and preview updates dynamically.
 */
class PreviewAndComplicationsViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

    private val watchFacePreferences = WatchFacePreferences(view.context)

    private val mWatchFaceArmsAndTicksView: View = view.findViewById(R.id.watch_face_arms_and_ticks)
    private val mWatchFaceHighlightPreviewView: View = view.findViewById(R.id.watch_face_highlight)
    private val mWatchFaceBackgroundPreviewImageView: ImageView = view.findViewById(
            R.id.watch_face_background)

    private val mLeftComplicationBackground: ImageView = view.findViewById(
            R.id.left_complication_background)
    private val mRightComplicationBackground: ImageView = view.findViewById(
            R.id.right_complication_background)

    private val mLeftComplication: ImageButton = view.findViewById(R.id.left_complication)
    private val mRightComplication: ImageButton = view.findViewById(R.id.right_complication)

    private var mDefaultComplicationDrawable: Drawable? = null
    private var mBackgroundComplicationEnabled: Boolean = false

    private var complication = Complication.BACKGROUND

    init {
        mLeftComplication.setOnClickListener(this)
        mRightComplication.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        if (view == mLeftComplication) {
            Log.d(TAG, "Left Complication click()")
            launchComplicationHelperActivity(view.context as Activity, Complication.LEFT)

        } else if (view == mRightComplication) {
            Log.d(TAG, "Right Complication click()")
            launchComplicationHelperActivity(view.context as Activity, Complication.RIGHT)
        }
    }

    private fun launchComplicationHelperActivity(currentActivity: Activity,
            complication: Complication) {
        this.complication = complication
        val watchFace = ComponentName(currentActivity, WatchFaceService::class.java)
        currentActivity.startActivityForResult(
                ComplicationHelperActivity.createProviderChooserHelperIntent(currentActivity,
                        watchFace, complication.id, *complication.supportedTypes),
                AnalogComplicationConfigActivity.COMPLICATION_CONFIG_REQUEST_CODE)

    }

    internal fun updateWatchFaceColors(context: Context) {
        watchFacePreferences.reloadSavedPreferences(context)
        mWatchFaceBackgroundPreviewImageView.background.colorFilter = PorterDuffColorFilter(
                watchFacePreferences.backgroundColor, PorterDuff.Mode.SRC_ATOP)

        mWatchFaceHighlightPreviewView.background.colorFilter = PorterDuffColorFilter(
                watchFacePreferences.watchHandHighlightColor, PorterDuff.Mode.SRC_ATOP)
    }

    internal fun setDefaultComplicationDrawable(resourceId: Int) {
        val context = mWatchFaceArmsAndTicksView.context
        mDefaultComplicationDrawable = context.getDrawable(resourceId)

        mLeftComplication.setImageDrawable(mDefaultComplicationDrawable)
        mLeftComplicationBackground.visibility = View.INVISIBLE

        mRightComplication.setImageDrawable(mDefaultComplicationDrawable)
        mRightComplicationBackground.visibility = View.INVISIBLE
    }

    internal fun updateComplicationViews(context: Context,
            complicationProviderInfo: ComplicationProviderInfo?) {

        when (complication) {
            Complication.BACKGROUND -> if (complicationProviderInfo != null) {
                mBackgroundComplicationEnabled = true

                // Since we can't get the background complication image outside of the
                // watch face, we set the icon for that provider instead with a gray background.
                val backgroundColorFilter = PorterDuffColorFilter(Color.GRAY,
                        PorterDuff.Mode.SRC_ATOP)

                mWatchFaceBackgroundPreviewImageView.background.colorFilter = backgroundColorFilter
                mWatchFaceBackgroundPreviewImageView.setImageIcon(
                        complicationProviderInfo.providerIcon)

            } else {
                mBackgroundComplicationEnabled = false

                // Clears icon for background if it was present before.
                mWatchFaceBackgroundPreviewImageView.setImageResource(android.R.color.transparent)

                val backgroundColorFilter = PorterDuffColorFilter(
                        watchFacePreferences.backgroundColor, PorterDuff.Mode.SRC_ATOP)

                mWatchFaceBackgroundPreviewImageView.background.colorFilter = backgroundColorFilter
            }
            Complication.LEFT -> updateComplicationView(context, complicationProviderInfo,
                    mLeftComplication, mLeftComplicationBackground)
            Complication.RIGHT -> updateComplicationView(context, complicationProviderInfo,
                    mRightComplication, mRightComplicationBackground)
        }
    }

    private fun updateComplicationView(context: Context,
            complicationProviderInfo: ComplicationProviderInfo?, button: ImageButton,
            background: ImageView) {
        if (complicationProviderInfo != null) {
            button.setImageIcon(complicationProviderInfo.providerIcon)
            button.contentDescription = context.getString(R.string.edit_complication,
                    complicationProviderInfo.appName + " " + complicationProviderInfo.providerName)
            background.visibility = View.VISIBLE
        } else {
            button.setImageDrawable(mDefaultComplicationDrawable)
            button.contentDescription = context.getString(R.string.add_complication)
            background.visibility = View.INVISIBLE
        }
    }

    internal fun initializesColorsAndComplications(context: Context) {
        mWatchFaceHighlightPreviewView.background.colorFilter = PorterDuffColorFilter(
                watchFacePreferences.watchHandHighlightColor, PorterDuff.Mode.SRC_ATOP)

        mWatchFaceBackgroundPreviewImageView.background.colorFilter = PorterDuffColorFilter(
                Color.GRAY, PorterDuff.Mode.SRC_ATOP)
    }

    companion object {
        val TAG = PreviewAndComplicationsViewHolder::class.java.simpleName!!

    }
}

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
        mAppearanceButton.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(resourceId),
                null, null, null)
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
        val TAG = ColorPickerViewHolder::class.java.simpleName!!
    }
}

/**
 * Displays switch to indicate whether or not icon appears for unread notifications. User can
 * toggle on/off.
 */
class UnreadNotificationViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

    private val mUnreadNotificationSwitch: Switch = view.findViewById(
            R.id.unread_notification_switch)

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
        mUnreadNotificationSwitch.setCompoundDrawablesWithIntrinsicBounds(
                context.getDrawable(mEnabledIconResourceId), null, null, null)
    }

    private fun updateIcon(context: Context, currentState: Boolean?) {
        val currentIconResourceId: Int = if (currentState!!) mEnabledIconResourceId else mDisabledIconResourceId

        mUnreadNotificationSwitch.isChecked = currentState
        mUnreadNotificationSwitch.setCompoundDrawablesWithIntrinsicBounds(
                context.getDrawable(currentIconResourceId), null, null, null)
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
        val TAG = ColorPickerViewHolder::class.java.simpleName!!
    }
}

/**
 * Displays button to trigger background image complication selector.
 */
class BackgroundComplicationViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

    private val mBackgroundComplicationButton: Button = view.findViewById(
            R.id.background_complication_button)

    init {
        view.setOnClickListener(this)
    }

    fun setName(name: String) {
        mBackgroundComplicationButton.text = name
    }

    fun setIcon(resourceId: Int) {
        mBackgroundComplicationButton.setCompoundDrawablesWithIntrinsicBounds(
                mBackgroundComplicationButton.context.getDrawable(resourceId), null, null, null)
    }

    override fun onClick(view: View) {
        val position = adapterPosition
        Log.d(TAG, "Background Complication onClick() position: " + position)

        val currentActivity = view.context as Activity

        val watchFace = ComponentName(currentActivity, WatchFaceService::class.java)

        currentActivity.startActivityForResult(
                ComplicationHelperActivity.createProviderChooserHelperIntent(currentActivity,
                        watchFace, Complication.BACKGROUND.id,
                        *Complication.BACKGROUND.supportedTypes),
                AnalogComplicationConfigActivity.COMPLICATION_CONFIG_REQUEST_CODE)

    }

    companion object {
        val TAG = ColorPickerViewHolder::class.java.simpleName!!
    }
}
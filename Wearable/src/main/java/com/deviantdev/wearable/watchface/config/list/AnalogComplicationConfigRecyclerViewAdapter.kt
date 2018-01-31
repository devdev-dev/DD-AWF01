/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.deviantdev.wearable.watchface.config.list

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.support.wearable.complications.ComplicationHelperActivity
import android.support.wearable.complications.ComplicationProviderInfo
import android.support.wearable.complications.ProviderInfoRetriever
import android.support.wearable.complications.ProviderInfoRetriever.OnProviderInfoReceivedCallback
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import com.deviantdev.wearable.watchface.R
import com.deviantdev.wearable.watchface.WatchFaceComplicationConfiguration.Complication
import com.deviantdev.wearable.watchface.WatchFacePreferences
import com.deviantdev.wearable.watchface.WatchFaceService
import com.deviantdev.wearable.watchface.model.AnalogComplicationConfigData.BackgroundComplicationConfigItem
import com.deviantdev.wearable.watchface.model.AnalogComplicationConfigData.ColorConfigItem
import com.deviantdev.wearable.watchface.model.AnalogComplicationConfigData.ConfigItemType
import com.deviantdev.wearable.watchface.model.AnalogComplicationConfigData.MoreOptionsConfigItem
import com.deviantdev.wearable.watchface.model.AnalogComplicationConfigData.PreviewAndComplicationsConfigItem
import com.deviantdev.wearable.watchface.model.AnalogComplicationConfigData.UnreadNotificationConfigItem
import java.util.*
import java.util.concurrent.Executors

/**
 * Displays different layouts for configuring watch face's complications and appearance settings
 * (highlight color [second arm], background color, unread notifications, etc.).
 *
 * All appearance settings are saved via [SharedPreferences].
 * Layouts provided by this adapter are split into 5 main view types.
 */
class AnalogComplicationConfigRecyclerViewAdapter(private val mContext: Context,
        watchFaceServiceClass: Class<*>, private val mSettingsDataSet: ArrayList<ConfigItemType>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val watchFacePreferences: WatchFacePreferences = WatchFacePreferences(mContext)

    // ComponentName associated with watch face service (service that renders watch face). Used to retrieve complication information.
    private val mWatchFaceComponentName: ComponentName = ComponentName(mContext,
            watchFaceServiceClass)

    // Selected complication id by user.
    private var mSelectedComplicationId: Int = 0

    // Required to retrieve complication data from watch face for preview.
    private val mProviderInfoRetriever: ProviderInfoRetriever

    // Maintains reference view holder to dynamically update watch face preview. Used instead of
    // notifyItemChanged(int position) to avoid flicker and re-inflating the view.
    private var mPreviewAndComplicationsViewHolder: PreviewAndComplicationsViewHolder? = null

    init {

        watchFacePreferences.reloadSavedPreferences(mContext)

        // Default value is invalid (only changed when user taps to change complication).
        mSelectedComplicationId = -1

        // Initialization of code to retrieve active complication data for the watch face.
        mProviderInfoRetriever = ProviderInfoRetriever(mContext, Executors.newCachedThreadPool())
        mProviderInfoRetriever.init()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        Log.d(TAG, "onCreateViewHolder(): viewType: " + viewType)

        var viewHolder: RecyclerView.ViewHolder? = null

        when (viewType) {
            TYPE_PREVIEW_AND_COMPLICATIONS_CONFIG -> {
                // Need direct reference to watch face preview view holder to update watch face
                // preview based on selections from the user.
                mPreviewAndComplicationsViewHolder = PreviewAndComplicationsViewHolder(
                        LayoutInflater.from(parent.context).inflate(
                                R.layout.config_list_preview_and_complications_item, parent, false))
                viewHolder = mPreviewAndComplicationsViewHolder
            }

            TYPE_MORE_OPTIONS -> viewHolder = MoreOptionsViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                            R.layout.config_list_more_options_item, parent, false))

            TYPE_COLOR_CONFIG -> viewHolder = ColorPickerViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.config_list_color_item,
                            parent, false))

            TYPE_UNREAD_NOTIFICATION_CONFIG -> viewHolder = UnreadNotificationViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                            R.layout.config_list_unread_notif_item, parent, false))

            TYPE_BACKGROUND_COMPLICATION_IMAGE_CONFIG -> viewHolder = BackgroundComplicationViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                            R.layout.config_list_background_complication_item, parent, false))
        }

        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        Log.d(TAG, "Element $position set.")

        // Pulls all data required for creating the UX for the specific setting option.
        val configItemType = mSettingsDataSet[position]

        when (viewHolder.itemViewType) {
            TYPE_PREVIEW_AND_COMPLICATIONS_CONFIG -> {
                val previewAndComplicationsViewHolder = viewHolder as PreviewAndComplicationsViewHolder

                val previewAndComplicationsConfigItem = configItemType as PreviewAndComplicationsConfigItem

                val defaultComplicationResourceId = previewAndComplicationsConfigItem.defaultComplicationResourceId
                previewAndComplicationsViewHolder.setDefaultComplicationDrawable(
                        defaultComplicationResourceId)

                previewAndComplicationsViewHolder.initializesColorsAndComplications()
            }

            TYPE_MORE_OPTIONS -> {
                val moreOptionsViewHolder = viewHolder as MoreOptionsViewHolder
                val moreOptionsConfigItem = configItemType as MoreOptionsConfigItem

                moreOptionsViewHolder.setIcon(moreOptionsConfigItem.iconResourceId)
            }

            TYPE_COLOR_CONFIG -> {
                val colorPickerViewHolder = viewHolder as ColorPickerViewHolder
                val colorConfigItem = configItemType as ColorConfigItem

                val iconResourceId = colorConfigItem.iconResourceId
                val name = colorConfigItem.name
                val activity = colorConfigItem.activityToChoosePreference

                colorPickerViewHolder.setIcon(iconResourceId)
                colorPickerViewHolder.setName(name)
                colorPickerViewHolder.setLaunchActivityToSelectColor(activity)
            }

            TYPE_UNREAD_NOTIFICATION_CONFIG -> {
                val unreadViewHolder = viewHolder as UnreadNotificationViewHolder

                val unreadConfigItem = configItemType as UnreadNotificationConfigItem

                val unreadEnabledIconResourceId = unreadConfigItem.iconEnabledResourceId
                val unreadDisabledIconResourceId = unreadConfigItem.iconDisabledResourceId

                val unreadName = unreadConfigItem.name

                unreadViewHolder.setIcons(unreadEnabledIconResourceId, unreadDisabledIconResourceId)
                unreadViewHolder.setName(unreadName)
            }

            TYPE_BACKGROUND_COMPLICATION_IMAGE_CONFIG -> {
                val backgroundComplicationViewHolder = viewHolder as BackgroundComplicationViewHolder

                val backgroundComplicationConfigItem = configItemType as BackgroundComplicationConfigItem

                val backgroundIconResourceId = backgroundComplicationConfigItem.iconResourceId
                val backgroundName = backgroundComplicationConfigItem.name

                backgroundComplicationViewHolder.setIcon(backgroundIconResourceId)
                backgroundComplicationViewHolder.setName(backgroundName)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val configItemType = mSettingsDataSet[position]
        return configItemType.configType
    }

    override fun getItemCount(): Int {
        return mSettingsDataSet.size
    }

    /**
     * Updates the selected complication id saved earlier with the new information.
     */
    internal fun updateSelectedComplication(complicationProviderInfo: ComplicationProviderInfo) {

        Log.d(TAG, "updateSelectedComplication: " + mPreviewAndComplicationsViewHolder!!)

        // Checks if view is inflated and complication id is valid.
        if (mPreviewAndComplicationsViewHolder != null && mSelectedComplicationId >= 0) {
            mPreviewAndComplicationsViewHolder!!.updateComplicationViews(mSelectedComplicationId,
                    complicationProviderInfo)
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
        super.onDetachedFromRecyclerView(recyclerView)
        // Required to release retriever for active complication data on detach.
        mProviderInfoRetriever.release()
    }

    fun updatePreviewColors() {
        Log.d(TAG, "updatePreviewColors(): " + mPreviewAndComplicationsViewHolder!!)

        if (mPreviewAndComplicationsViewHolder != null) {
            mPreviewAndComplicationsViewHolder!!.updateWatchFaceColors()
        }
    }

    /**
     * Displays watch face preview along with complication locations. Allows user to tap on the
     * complication they want to change and preview updates dynamically.
     */
    inner class PreviewAndComplicationsViewHolder(view: View) : RecyclerView.ViewHolder(view),
            OnClickListener {

        private val mWatchFaceArmsAndTicksView: View = view.findViewById(
                R.id.watch_face_arms_and_ticks)
        private val mWatchFaceHighlightPreviewView: View = view.findViewById(
                R.id.watch_face_highlight)
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

        init {
            mLeftComplication.setOnClickListener(this)
            mRightComplication.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            if (view == mLeftComplication) {
                Log.d(TAG, "Left Complication click()")

                val currentActivity = view.context as Activity
                launchComplicationHelperActivity(currentActivity, Complication.LEFT)

            } else if (view == mRightComplication) {
                Log.d(TAG, "Right Complication click()")

                val currentActivity = view.context as Activity
                launchComplicationHelperActivity(currentActivity, Complication.RIGHT)
            }
        }

        internal fun updateWatchFaceColors() {

            // Only update background colors for preview if background complications are disabled.
            if (!mBackgroundComplicationEnabled) {

                val backgroundColorFilter = PorterDuffColorFilter(
                        watchFacePreferences.backgroundColor, PorterDuff.Mode.SRC_ATOP)

                mWatchFaceBackgroundPreviewImageView.background.colorFilter = backgroundColorFilter

            } else {
                // Inform user that they need to disable background image for color to work.
                val text = "Selected image overrides background color."
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(mContext, text, duration)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }

            // Updates highlight color (just second arm).
            val highlightColorFilter = PorterDuffColorFilter(
                    watchFacePreferences.watchHandHighlightColor, PorterDuff.Mode.SRC_ATOP)

            mWatchFaceHighlightPreviewView.background.colorFilter = highlightColorFilter
        }

        // Verifies the watch face supports the complication location, then launches the helper
        // class, so user can choose their complication data provider.
        private fun launchComplicationHelperActivity(currentActivity: Activity,
                complication: Complication) {

            mSelectedComplicationId = complication.id

            mBackgroundComplicationEnabled = false

            val watchFace = ComponentName(currentActivity, WatchFaceService::class.java)

            currentActivity.startActivityForResult(
                    ComplicationHelperActivity.createProviderChooserHelperIntent(currentActivity,
                            watchFace, complication.id, *complication.supportedTypes),
                    AnalogComplicationConfigActivity.COMPLICATION_CONFIG_REQUEST_CODE)

        }

        internal fun setDefaultComplicationDrawable(resourceId: Int) {
            val context = mWatchFaceArmsAndTicksView.context
            mDefaultComplicationDrawable = context.getDrawable(resourceId)

            mLeftComplication.setImageDrawable(mDefaultComplicationDrawable)
            mLeftComplicationBackground.visibility = View.INVISIBLE

            mRightComplication.setImageDrawable(mDefaultComplicationDrawable)
            mRightComplicationBackground.visibility = View.INVISIBLE
        }

        internal fun updateComplicationViews(watchFaceComplicationId: Int,
                complicationProviderInfo: ComplicationProviderInfo?) {
            Log.d(TAG, "updateComplicationViews(): id: " + watchFaceComplicationId)
            Log.d(TAG, "\tinfo: " + complicationProviderInfo!!)

            if (watchFaceComplicationId == Complication.BACKGROUND.id) {
                if (complicationProviderInfo != null) {
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
                    mWatchFaceBackgroundPreviewImageView.setImageResource(
                            android.R.color.transparent)

                    val backgroundColorFilter = PorterDuffColorFilter(
                            watchFacePreferences.backgroundColor, PorterDuff.Mode.SRC_ATOP)

                    mWatchFaceBackgroundPreviewImageView.background.colorFilter = backgroundColorFilter
                }

            } else if (watchFaceComplicationId == Complication.LEFT.id) {
                updateComplicationView(complicationProviderInfo, mLeftComplication,
                        mLeftComplicationBackground)

            } else if (watchFaceComplicationId == Complication.RIGHT.id) {
                updateComplicationView(complicationProviderInfo, mRightComplication,
                        mRightComplicationBackground)
            }
        }

        private fun updateComplicationView(complicationProviderInfo: ComplicationProviderInfo?,
                button: ImageButton, background: ImageView) {
            if (complicationProviderInfo != null) {
                button.setImageIcon(complicationProviderInfo.providerIcon)
                button.contentDescription = mContext.getString(R.string.edit_complication,
                        complicationProviderInfo.appName + " " + complicationProviderInfo.providerName)
                background.visibility = View.VISIBLE
            } else {
                button.setImageDrawable(mDefaultComplicationDrawable)
                button.contentDescription = mContext.getString(R.string.add_complication)
                background.visibility = View.INVISIBLE
            }
        }

        internal fun initializesColorsAndComplications() {

            // Initializes highlight color (just second arm and part of complications).
            val highlightColorFilter = PorterDuffColorFilter(
                    watchFacePreferences.watchHandHighlightColor, PorterDuff.Mode.SRC_ATOP)

            mWatchFaceHighlightPreviewView.background.colorFilter = highlightColorFilter

            // Initializes background color to gray (updates to color or complication icon based
            // on whether the background complication is live or not.
            val backgroundColorFilter = PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP)

            mWatchFaceBackgroundPreviewImageView.background.colorFilter = backgroundColorFilter

            mProviderInfoRetriever.retrieveProviderInfo(object : OnProviderInfoReceivedCallback() {
                override fun onProviderInfoReceived(watchFaceComplicationId: Int,
                        complicationProviderInfo: ComplicationProviderInfo?) {

                    Log.d(TAG, "onProviderInfoReceived: " + complicationProviderInfo!!)

                    updateComplicationViews(watchFaceComplicationId, complicationProviderInfo)
                }
            }, mWatchFaceComponentName, *Complication.getAllIds())
        }
    }

    companion object {

        val TYPE_PREVIEW_AND_COMPLICATIONS_CONFIG = 0
        val TYPE_MORE_OPTIONS = 1
        val TYPE_COLOR_CONFIG = 2
        val TYPE_UNREAD_NOTIFICATION_CONFIG = 3
        val TYPE_BACKGROUND_COMPLICATION_IMAGE_CONFIG = 4

        private val TAG = "CompConfigAdapter"
    }

}

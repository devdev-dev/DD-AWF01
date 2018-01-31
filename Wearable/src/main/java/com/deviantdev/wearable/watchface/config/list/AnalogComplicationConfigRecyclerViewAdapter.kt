package com.deviantdev.wearable.watchface.config.list

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.widget.RecyclerView
import android.support.wearable.complications.ComplicationProviderInfo
import android.view.LayoutInflater
import android.view.ViewGroup
import com.deviantdev.wearable.watchface.R
import com.deviantdev.wearable.watchface.model.AnalogComplicationConfigData.BackgroundComplicationConfigItem
import com.deviantdev.wearable.watchface.model.AnalogComplicationConfigData.ColorConfigItem
import com.deviantdev.wearable.watchface.model.AnalogComplicationConfigData.ConfigItemType
import com.deviantdev.wearable.watchface.model.AnalogComplicationConfigData.MoreOptionsConfigItem
import com.deviantdev.wearable.watchface.model.AnalogComplicationConfigData.PreviewAndComplicationsConfigItem
import com.deviantdev.wearable.watchface.model.AnalogComplicationConfigData.UnreadNotificationConfigItem
import java.util.*

/**
 * Displays different layouts for configuring watch face's complications and appearance settings
 * (highlight color [second arm], background color, unread notifications, etc.).
 *
 * All appearance settings are saved via [SharedPreferences].
 * Layouts provided by this adapter are split into 5 main view types.
 */
class AnalogComplicationConfigRecyclerViewAdapter(private val mContext: Context,
        private val watchFaceServiceClass: Class<*>,
        private val mSettingsDataSet: ArrayList<ConfigItemType>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Selected complication id by user. Default value is invalid.
    private var mSelectedComplicationId: Int = -1

    private lateinit var mPreviewAndComplicationsViewHolder: PreviewAndComplicationsViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return when (viewType) {
            TYPE_PREVIEW_AND_COMPLICATIONS_CONFIG -> {
                mPreviewAndComplicationsViewHolder = PreviewAndComplicationsViewHolder(
                        LayoutInflater.from(parent.context).inflate(
                                R.layout.config_list_preview_and_complications_item, parent, false))
                mPreviewAndComplicationsViewHolder
            }

            TYPE_MORE_OPTIONS -> MoreOptionsViewHolder(LayoutInflater.from(parent.context).inflate(
                    R.layout.config_list_more_options_item, parent, false))

            TYPE_COLOR_CONFIG -> ColorPickerViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.config_list_color_item,
                            parent, false))

            TYPE_UNREAD_NOTIFICATION_CONFIG -> UnreadNotificationViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                            R.layout.config_list_unread_notif_item, parent, false))

            TYPE_BACKGROUND_COMPLICATION_IMAGE_CONFIG -> BackgroundComplicationViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                            R.layout.config_list_background_complication_item, parent, false))
            else -> null
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        // Pulls all data required for creating the UX for the specific setting option.
        val configItemType = mSettingsDataSet[position]

        when (viewHolder.itemViewType) {
            TYPE_PREVIEW_AND_COMPLICATIONS_CONFIG -> {
                val previewAndComplicationsViewHolder = viewHolder as PreviewAndComplicationsViewHolder

                val previewAndComplicationsConfigItem = configItemType as PreviewAndComplicationsConfigItem

                val defaultComplicationResourceId = previewAndComplicationsConfigItem.defaultComplicationResourceId
                previewAndComplicationsViewHolder.setDefaultComplicationDrawable(
                        defaultComplicationResourceId)

                previewAndComplicationsViewHolder.initializesColorsAndComplications(mContext)
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
    internal fun updateSelectedComplication(complicationProviderInfo: ComplicationProviderInfo?) {
        mPreviewAndComplicationsViewHolder.updateComplicationViews(mContext,
                complicationProviderInfo)
    }

    fun updatePreviewColors() {
        mPreviewAndComplicationsViewHolder.updateWatchFaceColors(mContext)
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

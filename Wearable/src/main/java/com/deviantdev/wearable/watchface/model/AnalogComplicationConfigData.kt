package com.deviantdev.wearable.watchface.model

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import com.deviantdev.wearable.watchface.R
import com.deviantdev.wearable.watchface.WatchFaceService
import com.deviantdev.wearable.watchface.config.color.ColorSelectionActivity
import com.deviantdev.wearable.watchface.config.list.AnalogComplicationConfigActivity
import com.deviantdev.wearable.watchface.config.list.AnalogComplicationConfigRecyclerViewAdapter
import java.util.*

/**
 * Data represents different views for configuring the [WatchFaceService] watch face's appearance and complications via [AnalogComplicationConfigActivity].
 */
object AnalogComplicationConfigData {

    /**
     * Returns Watch Face Service class associated with configuration Activity.
     */
    val watchFaceServiceClass: Class<*>
        get() = WatchFaceService::class.java

    /**
     * Returns Material Design color options.
     */
    // White
    // Yellow
    // Amber
    // Orange
    // Deep Orange
    // Red
    // Pink
    // Purple
    // Deep Purple
    // Indigo
    // Blue
    // Light Blue
    // Cyan
    // Teal
    // Green
    // Lime Green
    // Lime
    // Blue Grey
    // Grey
    // Brown
    // Black
    val colorOptionsDataSet: ArrayList<Int>
        get() {
            val colorOptionsDataSet = ArrayList<Int>()
            colorOptionsDataSet.add(Color.parseColor("#FFFFFF"))

            colorOptionsDataSet.add(Color.parseColor("#FFEB3B"))
            colorOptionsDataSet.add(Color.parseColor("#FFC107"))
            colorOptionsDataSet.add(Color.parseColor("#FF9800"))
            colorOptionsDataSet.add(Color.parseColor("#FF5722"))

            colorOptionsDataSet.add(Color.parseColor("#F44336"))
            colorOptionsDataSet.add(Color.parseColor("#E91E63"))

            colorOptionsDataSet.add(Color.parseColor("#9C27B0"))
            colorOptionsDataSet.add(Color.parseColor("#673AB7"))
            colorOptionsDataSet.add(Color.parseColor("#3F51B5"))
            colorOptionsDataSet.add(Color.parseColor("#2196F3"))
            colorOptionsDataSet.add(Color.parseColor("#03A9F4"))

            colorOptionsDataSet.add(Color.parseColor("#00BCD4"))
            colorOptionsDataSet.add(Color.parseColor("#009688"))
            colorOptionsDataSet.add(Color.parseColor("#4CAF50"))
            colorOptionsDataSet.add(Color.parseColor("#8BC34A"))
            colorOptionsDataSet.add(Color.parseColor("#CDDC39"))

            colorOptionsDataSet.add(Color.parseColor("#607D8B"))
            colorOptionsDataSet.add(Color.parseColor("#9E9E9E"))
            colorOptionsDataSet.add(Color.parseColor("#795548"))
            colorOptionsDataSet.add(Color.parseColor("#000000"))

            return colorOptionsDataSet
        }

    /**
     * Includes all data to populate each of the 5 different custom
     * [ViewHolder] types in [AnalogComplicationConfigRecyclerViewAdapter].
     */
    fun getDataToPopulateAdapter(context: Context): ArrayList<ConfigItemType> {

        val settingsConfigData = ArrayList<ConfigItemType>()

        // Data for watch face preview and complications UX in settings Activity.
        val complicationConfigItem = PreviewAndComplicationsConfigItem(R.drawable.add_complication)
        settingsConfigData.add(complicationConfigItem)

        // Data for "more options" UX in settings Activity.
        val moreOptionsConfigItem = MoreOptionsConfigItem(R.drawable.ic_expand_more_white_18dp)
        settingsConfigData.add(moreOptionsConfigItem)

        // Data for highlight/marker (second hand) color UX in settings Activity.
        val markerColorConfigItem = ColorConfigItem(context.getString(R.string.config_marker_color_label),
                R.drawable.icn_styles, ColorSelectionActivity::class.java)
        settingsConfigData.add(markerColorConfigItem)

        // Data for Background color UX in settings Activity.
        val backgroundColorConfigItem = ColorConfigItem(context.getString(R.string.config_background_color_label),
                R.drawable.icn_styles, ColorSelectionActivity::class.java)
        settingsConfigData.add(backgroundColorConfigItem)

        // Data for 'Unread Notifications' UX (toggle) in settings Activity.
        val unreadNotificationsConfigItem = UnreadNotificationConfigItem(
                context.getString(R.string.config_unread_notifications_label), R.drawable.ic_notifications_white_24dp,
                R.drawable.ic_notifications_off_white_24dp)
        settingsConfigData.add(unreadNotificationsConfigItem)

        // Data for background complications UX in settings Activity.
        val backgroundImageComplicationConfigItem =
                // TODO (jewalker): Revised in another CL to support background complication.
                BackgroundComplicationConfigItem(context.getString(R.string.config_background_image_complication_label),
                        R.drawable.ic_landscape_white)
        settingsConfigData.add(backgroundImageComplicationConfigItem)

        return settingsConfigData
    }

    /**
     * Interface all ConfigItems must implement so the [RecyclerView]'s Adapter associated
     * with the configuration activity knows what type of ViewHolder to inflate.
     */
    interface ConfigItemType {
        val configType: Int
    }

    /**
     * Data for Watch Face Preview with Complications Preview item in RecyclerView.
     */
    class PreviewAndComplicationsConfigItem internal constructor(val defaultComplicationResourceId: Int) :
            ConfigItemType {

        override val configType: Int
            get() = AnalogComplicationConfigRecyclerViewAdapter.TYPE_PREVIEW_AND_COMPLICATIONS_CONFIG
    }

    /**
     * Data for "more options" item in RecyclerView.
     */
    class MoreOptionsConfigItem internal constructor(val iconResourceId: Int) : ConfigItemType {

        override val configType: Int
            get() = AnalogComplicationConfigRecyclerViewAdapter.TYPE_MORE_OPTIONS
    }

    /**
     * Data for color picker item in RecyclerView.
     */
    class ColorConfigItem internal constructor(val name: String, val iconResourceId: Int,
            val activityToChoosePreference: Class<ColorSelectionActivity>) : ConfigItemType {

        override val configType: Int
            get() = AnalogComplicationConfigRecyclerViewAdapter.TYPE_COLOR_CONFIG
    }

    /**
     * Data for Unread Notification preference picker item in RecyclerView.
     */
    class UnreadNotificationConfigItem internal constructor(val name: String, val iconEnabledResourceId: Int,
            val iconDisabledResourceId: Int) : ConfigItemType {

        override val configType: Int
            get() = AnalogComplicationConfigRecyclerViewAdapter.TYPE_UNREAD_NOTIFICATION_CONFIG
    }

    /**
     * Data for background image complication picker item in RecyclerView.
     */
    class BackgroundComplicationConfigItem internal constructor(val name: String, val iconResourceId: Int) :
            ConfigItemType {

        override val configType: Int
            get() = AnalogComplicationConfigRecyclerViewAdapter.TYPE_BACKGROUND_COMPLICATION_IMAGE_CONFIG
    }
}
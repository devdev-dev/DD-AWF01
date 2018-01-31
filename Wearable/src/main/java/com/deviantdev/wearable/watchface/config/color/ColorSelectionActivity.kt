package com.deviantdev.wearable.watchface.config.color

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.wear.widget.WearableRecyclerView

import com.deviantdev.wearable.watchface.R
import com.deviantdev.wearable.watchface.model.AnalogComplicationConfigData

/**
 * Allows user to select color for something on the watch face (background, highlight,etc.) and
 * saves it to [android.content.SharedPreferences] in [android.support.v7.widget.RecyclerView.Adapter].
 */
class ColorSelectionActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_selection_config)

        val mColorSelectionRecyclerViewAdapter = ColorSelectionRecyclerViewAdapter(
                AnalogComplicationConfigData.colorOptionsDataSet)

        val mConfigAppearanceWearableRecyclerView = findViewById<WearableRecyclerView>(R.id.wearable_recycler_view)

        // Aligns the first and last items on the list vertically centered on the screen.
        mConfigAppearanceWearableRecyclerView.isEdgeItemsCenteringEnabled = true

        mConfigAppearanceWearableRecyclerView.layoutManager = LinearLayoutManager(this)

        // Improves performance because we know changes in content do not change the layout size of
        // the RecyclerView.
        mConfigAppearanceWearableRecyclerView.setHasFixedSize(true)

        mConfigAppearanceWearableRecyclerView.adapter = mColorSelectionRecyclerViewAdapter
    }
}
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
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.wear.widget.WearableRecyclerView
import android.support.wearable.complications.ComplicationProviderInfo
import android.support.wearable.complications.ProviderChooserIntent
import android.util.Log

import com.deviantdev.wearable.watchface.R
import com.deviantdev.wearable.watchface.WatchFaceService
import com.deviantdev.wearable.watchface.model.AnalogComplicationConfigData

/**
 * The watch-side config activity for [WatchFaceService], which
 * allows for setting the left and right complications of watch face along with the second's marker
 * color, background color, unread notifications toggle, and background complication image.
 */
class AnalogComplicationConfigActivity : Activity() {

    private var mAdapter: AnalogComplicationConfigRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_analog_complication_config)

        mAdapter = AnalogComplicationConfigRecyclerViewAdapter(applicationContext,
                AnalogComplicationConfigData.getWatchFaceServiceClass(),
                AnalogComplicationConfigData.getDataToPopulateAdapter(this))

        val mWearableRecyclerView = findViewById<WearableRecyclerView>(R.id.wearable_recycler_view)

        // Aligns the first and last items on the list vertically centered on the screen.
        mWearableRecyclerView.isEdgeItemsCenteringEnabled = true

        mWearableRecyclerView.layoutManager = LinearLayoutManager(this)

        // Improves performance because we know changes in content do not change the layout size of
        // the RecyclerView.
        mWearableRecyclerView.setHasFixedSize(true)

        mWearableRecyclerView.adapter = mAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        if (requestCode == COMPLICATION_CONFIG_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            // Retrieves information for selected Complication provider.
            val complicationProviderInfo = data.getParcelableExtra<ComplicationProviderInfo>(
                    ProviderChooserIntent.EXTRA_PROVIDER_INFO)
            Log.d(TAG, "Provider: " + complicationProviderInfo)

            // Updates preview with new complication information for selected complication id.
            // Note: complication id is saved and tracked in the adapter class.
            mAdapter!!.updateSelectedComplication(complicationProviderInfo)

        } else if (requestCode == UPDATE_COLORS_CONFIG_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            // Updates highlight and background colors based on the user preference.
            mAdapter!!.updatePreviewColors()
        }
    }

    companion object {
        val TAG: String = AnalogComplicationConfigActivity::class.java.simpleName

        val COMPLICATION_CONFIG_REQUEST_CODE = 1001
        val UPDATE_COLORS_CONFIG_REQUEST_CODE = 1002
    }
}
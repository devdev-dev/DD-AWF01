package com.deviantdev.wearable.watchface.config.list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.wear.widget.WearableRecyclerView
import android.support.wearable.complications.ProviderChooserIntent
import com.deviantdev.wearable.watchface.R
import com.deviantdev.wearable.watchface.WatchFaceService
import com.deviantdev.wearable.watchface.model.AnalogComplicationConfigData

/**
 * The watch-side config activity for [WatchFaceService], which
 * allows for setting the left and right complications of watch face along with the second's marker
 * color, background color, unread notifications toggle, and background complication image.
 */
class AnalogComplicationConfigActivity : Activity() {

    private lateinit var mAdapter: AnalogComplicationConfigRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_analog_complication_config)

        mAdapter = AnalogComplicationConfigRecyclerViewAdapter(applicationContext,
                AnalogComplicationConfigData.watchFaceServiceClass,
                AnalogComplicationConfigData.getDataToPopulateAdapter(this))

        val mWearableRecyclerView = findViewById<WearableRecyclerView>(R.id.wearable_recycler_view)

        mWearableRecyclerView.isEdgeItemsCenteringEnabled = true
        mWearableRecyclerView.layoutManager = LinearLayoutManager(this)
        mWearableRecyclerView.setHasFixedSize(true)
        mWearableRecyclerView.adapter = mAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                COMPLICATION_CONFIG_REQUEST_CODE -> mAdapter.updateSelectedComplication(
                        data?.getParcelableExtra(ProviderChooserIntent.EXTRA_PROVIDER_INFO))
                UPDATE_COLORS_CONFIG_REQUEST_CODE -> mAdapter.updatePreviewColors()
            }
        }
    }

    companion object {
        val TAG: String = AnalogComplicationConfigActivity::class.java.simpleName

        val COMPLICATION_CONFIG_REQUEST_CODE = 1001
        val UPDATE_COLORS_CONFIG_REQUEST_CODE = 1002
    }
}
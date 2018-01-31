package com.deviantdev.wearable.watchface

import android.support.wearable.complications.ComplicationData

class WatchFaceComplicationConfiguration {

    /**
     * @param id Unique id for each complication. The settings activity that supports allowing users to select their complication data provider requires numbers to be >= 0.
     */
    enum class Complication(val id: Int, val supportedTypes: IntArray) {

        BACKGROUND(0, intArrayOf( //
                ComplicationData.TYPE_LARGE_IMAGE)),

        LEFT(100, intArrayOf( //
                ComplicationData.TYPE_RANGED_VALUE, //
                ComplicationData.TYPE_ICON, //
                ComplicationData.TYPE_SHORT_TEXT, //
                ComplicationData.TYPE_SMALL_IMAGE)), //

        RIGHT(101, intArrayOf(ComplicationData.TYPE_RANGED_VALUE, //
                ComplicationData.TYPE_ICON, //
                ComplicationData.TYPE_SHORT_TEXT, //
                ComplicationData.TYPE_SMALL_IMAGE));

        companion object {
            fun getAllIds(): IntArray {
                return enumValues<Complication>().map { it.id }.toIntArray()
            }

            fun valuesReverse(): Array<Complication> {
                return values().reversedArray()
            }
        }
    }

}
package com.ssttkkl.fgoplanningtool.ui.servantfilter

import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R

enum class ItemFilterMode {
    And, Or;

    val localizedName: String
        get() = MyApp.context.resources.getStringArray(R.array.mode_item_servantfilter)[ordinal]
}
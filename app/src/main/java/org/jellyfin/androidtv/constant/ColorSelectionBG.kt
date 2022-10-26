package org.jellyfin.androidtv.constant

import android.graphics.Color
import androidx.annotation.ColorInt

enum class ColorSelectionBG(@ColorInt val colorValue: Int) {
    TRANSPARENT(Color.TRANSPARENT),
    TRANSPARENT_BLACK_DARK(Color.parseColor("#C0101010")),
    TRANSPARENT_BLACK_LIGHT(Color.parseColor("#A0101010")),
    TRANSPARENT_GREY_DARK(Color.parseColor("#C0202020")),
    TRANSPARENT_GREY_LIGHT(Color.parseColor("#A0303030")),
    SOLID_BLACK(Color.parseColor("#FF101010")),
    SOLID_GREY_DARK(Color.parseColor("#FF202020")),
    SOLID_GREY_LIGHT(Color.parseColor("#FF303030")),
}
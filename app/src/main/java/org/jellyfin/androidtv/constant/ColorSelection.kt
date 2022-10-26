package org.jellyfin.androidtv.constant

import android.graphics.Color
import androidx.annotation.ColorInt

enum class ColorSelection(@ColorInt val colorValue: Int) {
    THEME(0),
    JELLYFIN_BLUE(Color.parseColor("#00A4DC")),
    JELLYFIN_PURPLE(Color.parseColor("#AA5CC3")),
    SPANISH_BLUE(Color.parseColor("#1E76BA")),
    FUCHSIA(Color.parseColor("fuchsia")),
    LIME(Color.parseColor("lime")),
    MAROON(Color.parseColor("maroon")),
    ORANGE(Color.parseColor("#FF5400")),
    RED(Color.parseColor("red")),
    WHITE(Color.parseColor("#BFBFBF")),
    LIGHT_GREY(Color.parseColor("#808080")),
}
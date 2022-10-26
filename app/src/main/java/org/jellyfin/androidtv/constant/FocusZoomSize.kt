package org.jellyfin.androidtv.constant

import android.annotation.SuppressLint
import androidx.leanback.widget.FocusHighlight
import org.jellyfin.androidtv.JellyfinApplication
import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.ui.preference.dsl.EnumDisplayOptions

enum class FocusZoomSize(val value: Int) {

    @EnumDisplayOptions(R.string.lbl_none)
    NONE(FocusHighlight.ZOOM_FACTOR_NONE),

    @EnumDisplayOptions(R.string.image_size_smallest)
    SMALLEST(FocusHighlight.ZOOM_FACTOR_XSMALL),

    @EnumDisplayOptions(R.string.image_size_small)
    SMALL(FocusHighlight.ZOOM_FACTOR_SMALL),

    @EnumDisplayOptions(R.string.image_size_medium)
    MED(FocusHighlight.ZOOM_FACTOR_MEDIUM),

    @EnumDisplayOptions(R.string.image_size_large)
    LARGE(FocusHighlight.ZOOM_FACTOR_LARGE);

    @SuppressLint("PrivateResource")
    fun getPctValue(): Float = when(this) {
        NONE -> 1.0f
        SMALLEST -> JellyfinApplication.appContext.resources.getFraction(R.fraction.lb_focus_zoom_factor_xsmall, 1, 1)
        SMALL -> JellyfinApplication.appContext.resources.getFraction(R.fraction.lb_focus_zoom_factor_small, 1, 1)
        MED -> JellyfinApplication.appContext.resources.getFraction(R.fraction.lb_focus_zoom_factor_medium, 1, 1)
        LARGE -> JellyfinApplication.appContext.resources.getFraction(R.fraction.lb_focus_zoom_factor_large, 1, 1)
    }
}
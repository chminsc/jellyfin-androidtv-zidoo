package org.jellyfin.androidtv.constant

import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.ui.preference.dsl.EnumDisplayOptions

enum class FocusIconSize(val maxSizeDP: Int) {
    @EnumDisplayOptions(R.string.lbl_none)
    NONE(0),

    @EnumDisplayOptions(R.string.image_size_small)
    SMALL(45),

    @EnumDisplayOptions(R.string.image_size_medium)
    MED(75),

    @EnumDisplayOptions(R.string.image_size_large)
    LARGE(105);
}
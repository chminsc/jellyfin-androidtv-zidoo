package org.jellyfin.androidtv.constant

import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.ui.preference.dsl.EnumDisplayOptions

enum class HomeSize(val value: Int) {
    @EnumDisplayOptions(R.string.image_size_small)
    SMALL(4),

    @EnumDisplayOptions(R.string.image_size_medium)
    MEDIUM(3),

    @EnumDisplayOptions(R.string.image_size_large)
    LARGE(2);
}
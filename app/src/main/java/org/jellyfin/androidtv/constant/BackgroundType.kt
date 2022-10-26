package org.jellyfin.androidtv.constant

import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.ui.preference.dsl.EnumDisplayOptions

enum class BackgroundType {
    @EnumDisplayOptions(R.string.lbl_none)
    NONE,

    @EnumDisplayOptions(R.string.bg_type_normal)
    NORMAL,

    @EnumDisplayOptions(R.string.bg_type_hq)
    HIGH,

    @EnumDisplayOptions(R.string.bg_type_low)
    LOW
}
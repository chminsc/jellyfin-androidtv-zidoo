package org.jellyfin.androidtv.constant

import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.ui.preference.dsl.EnumDisplayOptions

enum class FocusBorderSize(val sizeDP: Int) {
    @EnumDisplayOptions(R.string.lbl_none)
    NONE(0),

    @EnumDisplayOptions(R.string.focus_border_minimal)
    MINIMAL(0),

    @EnumDisplayOptions(R.string.focus_border_thin)
    THIN(2),

    @EnumDisplayOptions(R.string.focus_border_normal)
    NORMAl(4),

    @EnumDisplayOptions(R.string.focus_border_thick)
    THICK(6);
}
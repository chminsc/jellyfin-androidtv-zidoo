package org.jellyfin.androidtv.constant

import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.ui.preference.dsl.EnumDisplayOptions

enum class CardInfoType {
    @EnumDisplayOptions(R.string.card_info_type_noinfo)
    NO_INFO,

    @EnumDisplayOptions(R.string.card_info_type_overlay)
    OVERLAY,

    @EnumDisplayOptions(R.string.card_info_type_overlay_icon)
    OVERLAY_ICON,

    @EnumDisplayOptions(R.string.card_info_type_under_mini)
    UNDER_MINI,

    @EnumDisplayOptions(R.string.card_info_type_under_full)
    UNDER_FULL
}

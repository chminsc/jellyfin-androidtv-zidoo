package org.jellyfin.androidtv.constant

import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.ui.preference.dsl.EnumDisplayOptions

enum class CardSpacing(val sizeDP: Int) {
    @EnumDisplayOptions(R.string.card_spaceing_zero)
    ZERO(0),

    @EnumDisplayOptions(R.string.card_spaceing_minimal)
    MINIMAL(2),

    @EnumDisplayOptions(R.string.card_spaceing_narrow)
    NARROW(4),

    @EnumDisplayOptions(R.string.card_spaceing_normal)
    NORMAL(8),

    @EnumDisplayOptions(R.string.card_spaceing_wide)
    WIDE(14),

    @EnumDisplayOptions(R.string.card_spaceing_widest)
    WIDEST(18);
}
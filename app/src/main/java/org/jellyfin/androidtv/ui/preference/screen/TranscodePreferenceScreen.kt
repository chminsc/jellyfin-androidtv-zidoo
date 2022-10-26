package org.jellyfin.androidtv.ui.preference.screen

import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.preference.UserPreferences
import org.jellyfin.androidtv.preference.constant.AudioCodecOut
import org.jellyfin.androidtv.ui.preference.dsl.OptionsFragment
import org.jellyfin.androidtv.ui.preference.dsl.checkbox
import org.jellyfin.androidtv.ui.preference.dsl.enum
import org.jellyfin.androidtv.ui.preference.dsl.optionsScreen
import org.koin.android.ext.android.inject

class TranscodePreferenceScreen : OptionsFragment() {
    private val userPreferences: UserPreferences by inject()

    override val screen by optionsScreen {
        setTitle(R.string.pref_transcode_link)

        category {

            checkbox {
                setTitle(R.string.lbl_transcode_force_bitstream_surround_codec)
                setContent(R.string.desc_transcode_force_bitstream_surround_codec)
                bind(userPreferences, UserPreferences.forceBitstreamCompliantSurroundCodecs)
            }

            enum<AudioCodecOut> {
                setTitle(R.string.lbl_forced_audio_codec)
                bind(userPreferences, UserPreferences.forcedAudioCodec)
            }

            checkbox {
                setTitle(R.string.lbl_force_stereo_audio)
                setContent(R.string.desc_force_stereo_audio)
                bind(userPreferences, UserPreferences.forceStereo)
                depends { !userPreferences[UserPreferences.forceBitstreamCompliantSurroundCodecs] }
            }

            checkbox {
                setTitle(R.string.lbl_show_transcode_info_popup)
                setContent(R.string.desc_show_transcode_info_popup)
                bind(userPreferences, UserPreferences.showTranscodePlaybackPopup)
            }

        }
    }
}
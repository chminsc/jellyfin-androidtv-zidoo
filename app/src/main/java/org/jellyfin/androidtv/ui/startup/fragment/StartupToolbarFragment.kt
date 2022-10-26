package org.jellyfin.androidtv.ui.startup.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.ui.ToolbarView
import org.jellyfin.androidtv.ui.preference.PreferencesActivity
import org.jellyfin.androidtv.ui.preference.screen.AuthPreferencesScreen

class StartupToolbarFragment : Fragment() {

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return ToolbarView(requireContext()).apply {

			addButton(R.drawable.ic_help, R.string.help) {
				parentFragmentManager.commit {
					addToBackStack(null)
					replace<ConnectHelpAlertFragment>(R.id.content_view)
				}
			}

			addButton(R.drawable.ic_settings, R.string.lbl_settings) {
				val intent = Intent(activity, PreferencesActivity::class.java)
				intent.putExtra(PreferencesActivity.EXTRA_SCREEN, AuthPreferencesScreen::class.qualifiedName)
				intent.putExtra(PreferencesActivity.EXTRA_SCREEN_ARGS, bundleOf(
					AuthPreferencesScreen.ARG_SHOW_ABOUT to true
				))
				activity?.startActivity(intent)
			}

		}.rootView
	}
}

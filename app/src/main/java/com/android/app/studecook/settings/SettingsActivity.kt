package com.android.app.studecook.settings

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.android.app.studecook.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            darkModeSetting()
        }

        private fun darkModeSetting() {
            val darkMode = findPreference<SwitchPreference>(getString(R.string.setting_dark))
            val sharedPreferences = this.context?.getSharedPreferences("dark", 0)

            if (sharedPreferences?.getBoolean("dark_mode", false)!!) {
                darkMode?.setDefaultValue(true)
            } else {
                darkMode?.setDefaultValue(false)
            }

            darkMode?.setOnPreferenceChangeListener { _, newValue ->
                if (newValue as Boolean) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    sharedPreferences.edit().putBoolean("dark_mode", true).apply()
                    true
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    sharedPreferences.edit().putBoolean("dark_mode", false).apply()
                    true
                }
            }
        }

        private fun languageSetting() {
            val language = findPreference<ListPreference>(getString(R.string.setting_language))
            val sharedPreference = this.context?.getSharedPreferences("language", 0)

            when (sharedPreference?.getString("language_value", "fr")) {
                "fr" -> language?.setDefaultValue("Français")
                else -> language?.setDefaultValue("English")
            }

            language?.setOnPreferenceChangeListener { _, newValue ->
                if (newValue as String == "Français") {
                    sharedPreference?.edit()?.putString("language_value", "fr")?.apply()
                    true
                } else {
                    sharedPreference?.edit()?.putString("language_value", "en")?.apply()
                    true
                }
            }
        }
    }
}
package com.android.example.studecook.settings

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.android.example.studecook.R

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
            findPreference<SwitchPreference>(getString(R.string.setting_dark))
                ?.setOnPreferenceChangeListener { _, newValue ->
                    if (newValue as Boolean) {
                        Toast.makeText(this.context, "TODO : change style to dark", Toast.LENGTH_LONG).show()
                        true
                    } else {
                        Toast.makeText(this.context, "TODO : change style to light", Toast.LENGTH_LONG).show()
                        true
                    }
                }
        }
    }
}
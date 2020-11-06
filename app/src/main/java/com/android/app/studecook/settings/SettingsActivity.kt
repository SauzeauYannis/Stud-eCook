package com.android.app.studecook.settings

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.android.app.studecook.MainActivity
import com.android.app.studecook.R
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

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
            accountSetting()
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

        private fun accountSetting() {
            val account = findPreference<Preference>(getString(R.string.setting_account))
            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser == null) {
                account?.summary = getString(R.string.account_off)
            } else {
                account?.summary = getString(R.string.account_on, if (currentUser.displayName == null) getString(R.string.anonymous) else currentUser.displayName)
            }

            account?.setOnPreferenceClickListener {
                if (currentUser != null) {
                    val builder = AlertDialog.Builder(this.context)
                    builder.setMessage(getString(R.string.alert_setting_account))
                            .setPositiveButton(android.R.string.ok) { _, _ ->
                                this.context?.let { it1 ->
                                    AuthUI.getInstance()
                                            .signOut(it1)
                                            .addOnCompleteListener {
                                                startActivity(Intent(this.context, MainActivity::class.java))
                                            }
                                }
                            }
                            .setNegativeButton(android.R.string.cancel) { _, _ -> }
                            .show()
                    true
                } else {
                    Toast.makeText(this.context, getString(R.string.account_off), Toast.LENGTH_SHORT).show()
                    false
                }
            }
        }
    }
}
package com.android.app.studecook

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.android.app.studecook.settings.SettingsActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences = getSharedPreferences("dark", 0)
        val isDark = sharedPreferences.getBoolean("dark_mode", true)
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        nav_view.setupWithNavController(navController)
        val user = FirebaseAuth.getInstance().currentUser
        if(user == null) {
            createSignInIntent()
        }

        main_menu.setOnClickListener { v -> showMenu(v) }
    }

    private fun createSignInIntent() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.AnonymousBuilder().build(),
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build(),
            AuthUI.IdpConfig.TwitterBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.ic_launcher_foreground)
                .setTheme(R.style.AppTheme)
                .build(),
            RC_SIGN_IN)
    }

    companion object {
        const val RC_SIGN_IN = 123
    }

    private fun showMenu(v: View) {
        val popupMenu = PopupMenu(this, v)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.param -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.logout -> {
                    AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener {
                        }
                    createSignInIntent()
                    true
                }
                else -> false
            }
        }

        popupMenu.inflate(R.menu.main_menu)

        try {
            val fieldMenuPopup = PopupMenu::class.java.getDeclaredField("mPopup")
            fieldMenuPopup.isAccessible = true
            val menuPopup = fieldMenuPopup.get(popupMenu)
            menuPopup.javaClass
                .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(menuPopup, true)
        } catch (e: Exception) {
            Log.e("Main", "Error showing icons in main menu", e)
        } finally {
            popupMenu.show()
        }
    }
}
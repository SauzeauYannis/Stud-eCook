package com.mobile.app.studecook

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.mobile.app.studecook.fragment.home.HomeFragmentDirections
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

open class MainActivity : AppCompatActivity() {

    private val db = Firebase.firestore

    private lateinit var navController: NavController

    companion object {
        const val RC_SIGN_IN = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()
        setLanguage()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        nav_view.setupWithNavController(navController)

        if (FirebaseAuth.getInstance().currentUser == null) {
            createSignInIntent()
        }
    }

    override fun onRestart() {
        setLanguage()
        super.onRestart()
    }

    @Suppress("DEPRECATION")
    private fun setLanguage() {
        var lang = "fr"
        val configuration = Configuration(resources.configuration)
        if (configuration.locale.displayLanguage != "français")
            lang = "en"
        val sharedPreferencesLang = getSharedPreferences("lang", 0)
        val langSetting = sharedPreferencesLang.getString("lang", lang)
        if (langSetting == "fr")
            configuration.locale = Locale.FRENCH
        else
            configuration.locale = Locale.ENGLISH
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    private fun setTheme() {
        val currentNightMode = (resources.configuration.uiMode
                and Configuration.UI_MODE_NIGHT_MASK)
        val nightMode = currentNightMode == Configuration.UI_MODE_NIGHT_YES
        val sharedPreferencesNight = getSharedPreferences("dark", 0)
        val isDark = sharedPreferencesNight.getBoolean("dark_mode", nightMode)
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)

        }
    }

    private fun createSignInIntent() {
        val providers = arrayListOf(
                AuthUI.IdpConfig.AnonymousBuilder().build(),
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.FacebookBuilder().build(),
                AuthUI.IdpConfig.TwitterBuilder().build()
        )

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.AppTheme)
                        .enableAnonymousUsersAutoUpgrade()
                        .setTosAndPrivacyPolicyUrls("https://strikza.github.io/studecookEULA.com/", "https://strikza.github.io/studecookEULA.com/")
                        .setLogo(R.drawable.ic_launcher) // TODO: 27-Feb-21 Changer par le logo de l'app
                        .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                if (!FirebaseAuth.getInstance().currentUser!!.isAnonymous) {
                    saveUser()
                }
                else
                    dialogAnonymous()
            } else {
                createSignInIntent()
            }
        }
    }

    private fun dialogAnonymous() {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_title_ano))
                .setMessage(getString(R.string.dialog_message_ano))
                .setPositiveButton(android.R.string.ok) { dialog, _ ->
                    dialog.cancel()
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                    AuthUI.getInstance()
                            .signOut(this)
                            .addOnCompleteListener {
                                onRestart()
                            }
                }
                .show()
    }

    private fun saveUser() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userRef = db.collection(getString(R.string.collection_users)).document(currentUser!!.uid)

        userRef.get()
                .addOnSuccessListener { doc ->
                    if (!doc.exists()) {
                        val user = hashMapOf(
                                "name" to currentUser.displayName,
                                "image" to "",
                                "description" to "",
                                "subs" to ArrayList<String>(),
                                "favorites" to ArrayList<String>()
                        )
                        userRef.set(user)
                                .addOnSuccessListener {
                                    Toast.makeText(this, getString(R.string.toast_welcome), Toast.LENGTH_LONG).show()
                                    navController.navigate(HomeFragmentDirections.actionNavigationHomeToNavigationAccount())
                                }
                    } else {
                        navController.navigate(HomeFragmentDirections.actionNavigationHomeToNavigationAccount())
                    }
                }
    }
}
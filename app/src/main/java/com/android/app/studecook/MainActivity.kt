package com.android.app.studecook

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

open class MainActivity : AppCompatActivity() {

    companion object {
        const val RC_SIGN_IN = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences = getSharedPreferences("dark", 0)
        val isDark = sharedPreferences.getBoolean("dark_mode", false)
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
        if(FirebaseAuth.getInstance().currentUser == null) {
            createSignInIntent()
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
                    .build(),
            RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                if (!FirebaseAuth.getInstance().currentUser!!.isAnonymous)
                    saveUser()
            }
        }
    }

    private fun saveUser() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val db = Firebase.firestore
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
                                }
                    }
                }
    }
}
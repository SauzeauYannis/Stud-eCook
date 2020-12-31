package com.android.app.studecook.ui.account

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.app.studecook.R
import com.google.firebase.auth.FirebaseAuth

class AccountViewModel(application: Application) : AndroidViewModel(application) {

    private val _text = MutableLiveData<String>().apply {
        val user = FirebaseAuth.getInstance().currentUser
        value = if (user == null) {
            application.getString(R.string.text_no_account)
        } else {
            if (user.displayName != null) {
                application.getString(R.string.text_account,
                    FirebaseAuth.getInstance().currentUser?.displayName
                )
            } else {
                application.getString(R.string.text_account,
                    application.getString(R.string.anonymous)
                )
            }
        }
    }
    val text: LiveData<String> = _text
}
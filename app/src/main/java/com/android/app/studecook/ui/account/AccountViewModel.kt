package com.android.app.studecook.ui.account

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.app.studecook.R
import com.google.firebase.auth.FirebaseAuth

class AccountViewModel(application: Application) : AndroidViewModel(application) {

    private val _text = MutableLiveData<String>().apply {
        value = application.getString(R.string.text_account,
            FirebaseAuth.getInstance().currentUser?.displayName
        )
    }
    val text: LiveData<String> = _text
}
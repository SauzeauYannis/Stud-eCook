package com.android.app.studecook.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.app.studecook.R
import kotlinx.android.synthetic.main.fragment_account_subs.*
import kotlinx.android.synthetic.main.fragment_account_subs.view.*

class AccountSubsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                button_acc_subs_back.callOnClick()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_account_subs, container, false)

        root.button_acc_subs_back.setOnClickListener {
            findNavController().navigate(R.id.action_accountSubsFragment_to_navigation_account)
        }

        return root
    }
}
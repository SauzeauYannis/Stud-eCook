package com.android.app.studecook.fragment.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.android.app.studecook.R
import com.android.app.studecook.adapter.SubsListAdapter
import kotlinx.android.synthetic.main.fragment_account_subs.*
import kotlinx.android.synthetic.main.fragment_account_subs.view.*

class AccountSubsFragment : Fragment() {

    private val args by navArgs<AccountSubsFragmentArgs>()

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
        val subs = args.subs
        val subsArrayList = ArrayList<String>(subs.size)
        subsArrayList.addAll(subs)

        root.button_acc_subs_back.setOnClickListener {
            findNavController().navigate(R.id.action_accountSubsFragment_to_navigation_account)
        }

        val adapter = SubsListAdapter(requireContext(), subsArrayList)

        root.list_acc_subs.adapter = adapter

        root.list_acc_subs.setOnItemClickListener { _, _, position, _ ->
            adapter.viewSub(position, this)
        }

        return root
    }
}
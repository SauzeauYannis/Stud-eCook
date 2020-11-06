package com.android.app.studecook.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.app.studecook.R
import kotlinx.android.synthetic.main.fragment_add_step3.view.*

class AddStep3Fragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_navigation_add_step3_to_navigation_add_step2)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_add_step3, container, false)

        root.text_add_title.text = getString(R.string.text_add_title, "3")

        root.button_add_next.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_add_step3_to_navigation_add_step4)
        }

        root.button_back.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_add_step3_to_navigation_add_step2)
        }

        return root
    }
}
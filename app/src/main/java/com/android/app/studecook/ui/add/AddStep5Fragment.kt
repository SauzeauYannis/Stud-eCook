package com.android.app.studecook.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.app.studecook.R
import kotlinx.android.synthetic.main.fragment_add_step5.view.*

class AddStep5Fragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_navigation_add_step5_to_navigation_add_step4)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_add_step5, container, false)

        root.text_add_title.text = getString(R.string.text_add_title, "5")

        root.button_add_next.setOnClickListener {
            Toast.makeText(root.context, "TODO: Sent to database", Toast.LENGTH_LONG).show()
        }

        root.button_back.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_add_step5_to_navigation_add_step4)
        }

        return root
    }
}
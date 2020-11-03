package com.android.app.studecook.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.app.studecook.R
import kotlinx.android.synthetic.main.fragment_add_step2.view.*

class AddStep2Fragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_add_step2, container, false)

        root.text_add_title.text = getString(R.string.text_add_title, "2")

        root.button_add_next.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_add_step2_to_navigation_add_step3)
        }

        return root
    }
}
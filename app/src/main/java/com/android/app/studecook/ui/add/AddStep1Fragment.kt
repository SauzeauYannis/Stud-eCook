package com.android.app.studecook.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.app.studecook.R
import kotlinx.android.synthetic.main.fragment_add_step1.view.*

class AddStep1Fragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_add_step1, container, false)

        root.text_add_title.text = getString(R.string.text_add_title, "1")

        ArrayAdapter.createFromResource(
                root.context,
                R.array.time_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            root.array_add_time.adapter = adapter
        }

        ArrayAdapter.createFromResource(
                root.context,
                R.array.price_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            root.array_add_price.adapter = adapter
        }

        root.button_add_next.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_add_step1_to_navigation_add_step2)
        }

        return root
    }
}
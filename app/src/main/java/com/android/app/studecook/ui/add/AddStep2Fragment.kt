package com.android.app.studecook.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.app.studecook.R
import kotlinx.android.synthetic.main.fragment_add_step1.view.*
import kotlinx.android.synthetic.main.fragment_add_step2.view.*
import kotlinx.android.synthetic.main.fragment_add_step2.view.button_add_next
import kotlinx.android.synthetic.main.fragment_add_step2.view.text_add_title

class AddStep2Fragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_navigation_add_step2_to_navigation_add_step1)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_add_step2, container, false)

        root.text_add_title.text = getString(R.string.text_add_title, "2")

        root.text_add_nbP.text = getString(R.string.text_add_nbP, "1");

        root.seek_add.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                root.text_add_nbP.text = getString(R.string.text_add_nbP, (root.seek_add.progress + 1).toString());
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        ArrayAdapter.createFromResource(
                root.context,
                R.array.type_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            root.array_add_type.adapter = adapter
        }

        ArrayAdapter.createFromResource(
                root.context,
                R.array.diet_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            root.array_add_diet.adapter = adapter
        }

        root.button_add_next.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_add_step2_to_navigation_add_step3)
        }

        root.button_back.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_add_step2_to_navigation_add_step1)
        }

        return root
    }
}
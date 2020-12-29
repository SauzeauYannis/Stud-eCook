package com.android.app.studecook.ui.add

import android.content.Context
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
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)

        root.text_add_title.text = getString(R.string.text_add_title, "2")

        root.text_add_nbP.text = getString(R.string.text_add_nbP, (sharedPref!!.getInt(getString(R.string.saved_add_number_key), 0) + 1).toString())
        root.seek_add.progress = sharedPref.getInt(getString(R.string.saved_add_number_key), 0)

        root.seek_add.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                root.text_add_nbP.text = getString(R.string.text_add_nbP, (root.seek_add.progress + 1).toString())
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
            root.array_add_type.setSelection(
                    sharedPref.getInt(getString(R.string.saved_add_type_key), 0)
            )
        }

        ArrayAdapter.createFromResource(
                root.context,
                R.array.diet_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            root.array_add_diet.adapter = adapter
            root.array_add_diet.setSelection(
                    sharedPref.getInt(getString(R.string.saved_add_diet_key), 0)
            )
        }

        root.button_add_next.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_add_step2_to_navigation_add_step3)
            with (sharedPref.edit()) {
                putInt(getString(R.string.saved_add_number_key), root.seek_add.progress)
                putInt(getString(R.string.saved_add_type_key), root.array_add_type.selectedItemPosition)
                putInt(getString(R.string.saved_add_diet_key), root.array_add_diet.selectedItemPosition)
                apply()
            }
        }

        root.button_back.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_add_step2_to_navigation_add_step1)
        }

        return root
    }
}
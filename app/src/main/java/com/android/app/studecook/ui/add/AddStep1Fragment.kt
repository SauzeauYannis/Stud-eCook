package com.android.app.studecook.ui.add

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.app.studecook.R
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_add_step1.view.*

class AddStep1Fragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_navigation_add_step1_to_navigation_home2)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    private lateinit var textInputName: TextInputLayout

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_add_step1, container, false)
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)

        root.text_add_title.text = getString(R.string.text_add_title, 1)
        root.text_input_add_name.setText(sharedPref?.getString(getString(R.string.saved_add_name_key), null))

        textInputName = root.text_input_layout_add_name
        root.text_input_layout_add_name.editText?.setOnClickListener {
            textInputName.error = null
        }

        ArrayAdapter.createFromResource(
                root.context,
                R.array.time_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            root.array_add_time.adapter = adapter
            root.array_add_time.setSelection(
                sharedPref!!.getInt(getString(R.string.saved_add_time_key), 0)
            )
        }

        ArrayAdapter.createFromResource(
                root.context,
                R.array.price_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            root.array_add_price.adapter = adapter
            root.array_add_price.setSelection(
                sharedPref!!.getInt(getString(R.string.saved_add_price_key), 0)
            )
        }

        root.button_add_next.setOnClickListener {
            if (validateRecipename()) {
                findNavController().navigate(R.id.action_navigation_add_step1_to_navigation_add_step2)
            } else {
                Toast.makeText(root.context, getString(R.string.add_name_false), Toast.LENGTH_SHORT).show()
            }
            saveData(sharedPref, root)
        }

        return root
    }

    private fun saveData(sharedPref: SharedPreferences?, root: View) {
        with(sharedPref!!.edit()) {
            putString(getString(R.string.saved_add_name_key), root.text_input_add_name.text.toString())
            putInt(getString(R.string.saved_add_time_key), root.array_add_time.selectedItemPosition)
            putInt(getString(R.string.saved_add_price_key), root.array_add_price.selectedItemPosition)
            apply()
        }
    }

    private fun validateRecipename() : Boolean {
        val name = textInputName.editText?.text.toString().trim()

        return when {
            name.length < 4 -> {
                textInputName.error = getString(R.string.add_name_false_short)
                false
            }
            name.length > 50 -> {
                textInputName.error = getString(R.string.add_name_false_long)
                false
            }
            else -> {
                textInputName.error = null
                true
            }
        }
    }
}
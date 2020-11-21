package com.android.app.studecook.ui.add

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.app.studecook.R
import kotlinx.android.synthetic.main.fragment_add_step3.view.*
import java.util.ArrayList
import java.util.Arrays.sort

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

        val listU = resources.getStringArray(R.array.utensil_array)
        sort(listU)
        val check = BooleanArray(listU.size)
        val mUserUtensil = ArrayList<Int>()

        root.button_add_utensil.setOnClickListener {
            val mBuilder = AlertDialog.Builder(root.context)
            mBuilder.setTitle(getString(R.string.dialog_titile_ustensil))
            mBuilder.setMultiChoiceItems(listU, check) { _, position, isChecked ->
                if (isChecked) {
                    if (!mUserUtensil.contains(position)) {
                        mUserUtensil.add(position)
                    }
                } else if (mUserUtensil.contains(position)) {
                    mUserUtensil.remove(position)
                }
            }
            mBuilder.setCancelable(false)
            mBuilder.setPositiveButton(android.R.string.ok) { _, _ ->
                var stringItem = getString(R.string.text_add_utensils_list)
                for (i in mUserUtensil) {
                    stringItem += " "
                    stringItem += listU[i]
                    stringItem += if (i != mUserUtensil[mUserUtensil.size - 1]) {
                        ","
                    } else {
                        "."
                    }
                }
                root.text_add_utensils.text = stringItem
            }
            mBuilder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            mBuilder.create().show()
        }

        root.button_add_next.setOnClickListener {
            if (root.text_add_utensils.text == getString(R.string.text_add_utensils_list)) {
                Toast.makeText(root.context, getString(R.string.text_add_utensils_false), Toast.LENGTH_LONG).show()
            } else {
                findNavController().navigate(R.id.action_navigation_add_step3_to_navigation_add_step4)
            }
        }

        root.button_back.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_add_step3_to_navigation_add_step2)
        }

        return root
    }
}
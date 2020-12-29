package com.android.app.studecook.ui.add

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.app.studecook.R
import kotlinx.android.synthetic.main.fragment_add_step3.view.*
import kotlinx.android.synthetic.main.fragment_add_step3.view.button_add_next
import kotlinx.android.synthetic.main.fragment_add_step3.view.button_back
import kotlinx.android.synthetic.main.fragment_add_step3.view.text_add_title
import kotlinx.android.synthetic.main.layout_add_ingredient.view.*
import java.util.ArrayList
import java.util.Arrays.sort

class AddStep3Fragment : Fragment() {

    private val maxIngredient = 15
    private var ingredientCount = 0

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
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)

        root.text_add_title.text = getString(R.string.text_add_title, "3")

        val listU = resources.getStringArray(R.array.utensil_array)
        sort(listU)
        val check = BooleanArray(listU.size)
        val mUserUtensil = ArrayList<Int>()

        root.button_add_utensil.setOnClickListener {
            val mBuilder = AlertDialog.Builder(root.context)
            mBuilder.setTitle(getString(R.string.dialog_title_utensil))
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
                mUserUtensil.sort()
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

        root.button_add_ingredient.setOnClickListener {
            if (ingredientCount < maxIngredient) {
                var isGood = true
                if (ingredientCount != 0) {
                    isGood = isIngredientListGood(root.layout_ingredient, root.context)
                }
                if (isGood) {
                    val ingredientView = layoutInflater.inflate(R.layout.layout_add_ingredient, container, false)

                    ArrayAdapter.createFromResource(
                            root.context,
                            R.array.ingredient_type_array,
                            android.R.layout.simple_spinner_item
                    ).also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        ingredientView.array_add_ingredient_type.adapter = adapter
                    }

                    ingredientView.image_ingredient_delete.setOnClickListener {
                        root.layout_ingredient.removeView(ingredientView)
                        ingredientCount--
                    }

                    root.layout_ingredient.addView(ingredientView)
                    ingredientCount++
                }
            } else {
                Toast.makeText(root.context, getString(R.string.text_add_ingredient_too_much), Toast.LENGTH_LONG).show()
            }
        }

        root.button_add_next.setOnClickListener {
            when {
                root.text_add_utensils.text == getString(R.string.text_add_utensils_list) -> {
                    Toast.makeText(root.context, getString(R.string.text_add_utensils_false), Toast.LENGTH_LONG).show()
                }
                ingredientCount == 0 -> {
                    Toast.makeText(root.context, getString(R.string.text_add_ingredient_false), Toast.LENGTH_LONG).show()
                }
                else -> {
                    if (isIngredientListGood(root.layout_ingredient, root.context)) {
                        findNavController().navigate(R.id.action_navigation_add_step3_to_navigation_add_step4)
                    }
                }
            }
        }

        root.button_back.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_add_step3_to_navigation_add_step2)
        }

        return root
    }

    private fun isIngredientListGood(layout: LinearLayout, context: Context): Boolean {
        for (l in layout) {
            if (l.text_input_add_num_ingredient.text.toString().isEmpty()) {
                Toast.makeText(context, getString(R.string.text_add_ingredient_num_empty), Toast.LENGTH_LONG).show()
                return false
            } else if (l.text_input_add_ingredient.text.toString().isEmpty()) {
                Toast.makeText(context, getString(R.string.text_add_ingredient_empty), Toast.LENGTH_LONG).show()
                return false
            }
        }
        return true
    }
}
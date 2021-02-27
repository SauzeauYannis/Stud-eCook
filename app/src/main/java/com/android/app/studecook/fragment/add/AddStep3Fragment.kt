package com.android.app.studecook.fragment.add

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
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
import kotlinx.android.synthetic.main.fragment_add_step3.*
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
                button_back.callOnClick()
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

        root.text_add_title.text = getString(R.string.text_add_title, 3)

        root.text_add_ingredients.append(getString(R.string.text_add_min_max, 1, maxIngredient))

        val listU = resources.getStringArray(R.array.utensil_array)
        val check = BooleanArray(listU.size)
        val mUserUtensil = ArrayList<Int>()
        val savedUtensil = sharedPref!!.getStringSet(getString(R.string.saved_add_utensils_key), HashSet<String>())

        for (i in listU.indices) {
            for (utensil in savedUtensil!!) {
                if (listU[i] == utensil) {
                    check[i] = true
                    mUserUtensil.add(i)
                }
            }
        }

        val savedIngredientNumber = sharedPref.getInt(getString(R.string.saved_add_ingredient_number_key), 0)

        if (savedIngredientNumber > 0) {
            val savedIngredientsQuantity = sharedPref.getString(getString(R.string.saved_add_ingredients_quantity_key), null)?.split(",")
            val savedIngredientsType = sharedPref.getString(getString(R.string.saved_add_ingredients_type_key), null)?.split(",")
            val savedIngredientsName = sharedPref.getString(getString(R.string.saved_add_ingredients_name_key), null)?.split(",")

            for (i in 0 until savedIngredientNumber) {
                val ingredientView = generateIngredientView(container, root)

                ingredientView!!.text_input_add_num_ingredient.setText(
                        savedIngredientsQuantity?.get(i)
                )
                ingredientView.array_add_ingredient_type.setSelection(
                        savedIngredientsType?.get(i)!!.toInt()
                )
                ingredientView.text_input_add_ingredient.setText(
                        savedIngredientsName?.get(i)
                )

                root.layout_ingredient.addView(ingredientView)
                ingredientCount++
            }
        }

        root.text_add_utensils.text = generateUtensilsText(mUserUtensil, listU)

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
                root.text_add_utensils.text = generateUtensilsText(mUserUtensil, listU)
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
                    root.layout_ingredient.addView(generateIngredientView(container, root))
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
                        saveData(mUserUtensil, listU, root, sharedPref)
                    }
                }
            }
        }

        root.button_back.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_add_step3_to_navigation_add_step2)
            saveData(mUserUtensil, listU, root, sharedPref)
        }

        return root
    }

    private fun saveData(mUserUtensil: ArrayList<Int>, listU: Array<String>, root: View, sharedPref: SharedPreferences) {
        val utensilsName = HashSet<String>()
        for (i in mUserUtensil) {
            utensilsName.add(listU[i])
        }

        val ingredientsQuantity = ArrayList<String>()
        val ingredientsType = ArrayList<String>()
        val ingredientsName = ArrayList<String>()
        for (view in root.layout_ingredient) {
            ingredientsQuantity.add(view.text_input_add_num_ingredient.text.toString())
            ingredientsType.add(view.array_add_ingredient_type.selectedItemPosition.toString())
            ingredientsName.add(view.text_input_add_ingredient.text.toString())
        }

        with(sharedPref.edit()) {
            putStringSet(getString(R.string.saved_add_utensils_key), utensilsName)
            putInt(getString(R.string.saved_add_ingredient_number_key), ingredientCount)
            putString(getString(R.string.saved_add_ingredients_quantity_key), TextUtils.join(",", ingredientsQuantity))
            putString(getString(R.string.saved_add_ingredients_type_key), TextUtils.join(",", ingredientsType))
            putString(getString(R.string.saved_add_ingredients_name_key), TextUtils.join(",", ingredientsName))
            apply()
        }
    }

    private fun generateIngredientView(container: ViewGroup?, root: View): View? {
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
        return ingredientView
    }

    private fun generateUtensilsText(mUserUtensil: ArrayList<Int>, listU: Array<String>): String {
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
        return stringItem
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
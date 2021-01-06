package com.android.app.studecook.ui.add

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.app.studecook.R
import kotlinx.android.synthetic.main.fragment_add_step4.*
import kotlinx.android.synthetic.main.fragment_add_step4.view.*
import kotlinx.android.synthetic.main.fragment_add_step4.view.button_add_next
import kotlinx.android.synthetic.main.fragment_add_step4.view.button_back
import kotlinx.android.synthetic.main.fragment_add_step4.view.text_add_title
import kotlinx.android.synthetic.main.layout_add_step.view.*
import java.util.ArrayList

class AddStep4Fragment : Fragment() {

    private val maxStep = 15
    private var stepCount = 0

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
        val root = inflater.inflate(R.layout.fragment_add_step4, container, false)
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)

        root.text_add_title.text = getString(R.string.text_add_title, "4")

        val savedStepsNumber = sharedPref!!.getInt(getString(R.string.saved_add_step_number_key), 0)

        if (savedStepsNumber > 0) {
            val savedSteps = sharedPref.getString(getString(R.string.saved_add_steps_key), null)?.split("\n\n\n")

            for (i in 0 until savedStepsNumber) {
                val stepView = generateStepView(container, root)

                stepView!!.text_input_step.setText(
                        savedSteps?.get(i)
                )

                root.layout_step.addView(stepView)
                stepCount++
            }
        }

        root.button_add_step.setOnClickListener {
            if (stepCount < maxStep) {
                var isGood = true
                if (stepCount != 0) {
                    isGood = isStepListGood(root.layout_step, root.context)
                }
                if (isGood) {
                    root.layout_step.addView(generateStepView(container, root))
                    stepCount++
                }
            } else {
                Toast.makeText(context, getString(R.string.text_add_step_too_much), Toast.LENGTH_LONG).show()
            }
        }

        root.button_add_next.setOnClickListener {
            if (stepCount == 0) {
                Toast.makeText(context, getString(R.string.text_add_step_empty), Toast.LENGTH_LONG).show()
            } else {
                if (isStepListGood(root.layout_step, root.context)) {
                    findNavController().navigate(R.id.action_navigation_add_step4_to_navigation_add_step5)
                    saveData(root, sharedPref)
                }
            }
        }

        root.button_back.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_add_step4_to_navigation_add_step3)
            saveData(root, sharedPref)
        }

        return root
    }

    private fun saveData(root: View, sharedPref: SharedPreferences) {
        val steps = ArrayList<String>()
        for (view in root.layout_step) {
            steps.add(view.text_input_step.text.toString())
        }

        with(sharedPref.edit()) {
            putInt(getString(R.string.saved_add_step_number_key), stepCount)
            putString(getString(R.string.saved_add_steps_key), TextUtils.join("\n\n\n", steps))
            apply()
        }
    }

    private fun generateStepView(container: ViewGroup?, root: View): View? {
        val stepView = layoutInflater.inflate(R.layout.layout_add_step, container, false)

        stepView.text_input_step.hint = getString(R.string.text_add_ingredient_hint, stepCount + 1)

        stepView.image_step_delete.setOnClickListener {
            root.layout_step.removeView(stepView)
            stepCount--
        }
        return stepView
    }

    private fun isStepListGood(layout: LinearLayout, context: Context): Boolean {
        for (l in layout) {
            if (l.text_input_step.text.toString().isEmpty()) {
                Toast.makeText(context, getString(R.string.text_add_step_isEmpty), Toast.LENGTH_LONG).show()
                return false
            }
        }
        return true
    }
}
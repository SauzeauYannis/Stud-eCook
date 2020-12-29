package com.android.app.studecook.ui.add

import android.content.Context
import android.os.Bundle
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
import kotlinx.android.synthetic.main.fragment_add_step4.view.*
import kotlinx.android.synthetic.main.fragment_add_step4.view.button_add_next
import kotlinx.android.synthetic.main.fragment_add_step4.view.button_back
import kotlinx.android.synthetic.main.fragment_add_step4.view.text_add_title
import kotlinx.android.synthetic.main.layout_add_step.view.*

class AddStep4Fragment : Fragment() {

    private val maxStep = 15
    private var stepCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_navigation_add_step4_to_navigation_add_step3)
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
        // val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)

        root.text_add_title.text = getString(R.string.text_add_title, "4")

        root.button_add_step.setOnClickListener {
            if (stepCount < maxStep) {
                var isGood = true
                if (stepCount != 0) {
                    isGood = isStepListGood(root.layout_step, root.context)
                }
                if (isGood) {
                    val stepView = layoutInflater.inflate(R.layout.layout_add_step, container, false)

                    stepView.text_input_step.hint = getString(R.string.text_add_ingredient_hint, stepCount+1)

                    stepView.image_step_delete.setOnClickListener {
                        root.layout_step.removeView(stepView)
                        stepCount--
                    }

                    root.layout_step.addView(stepView)
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
                }
            }
        }

        root.button_back.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_add_step4_to_navigation_add_step3)
        }

        return root
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
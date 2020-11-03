package com.android.app.studecook.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.app.studecook.R
import kotlinx.android.synthetic.main.fragment_add_step4.view.*

class AddStep4Fragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_add_step4, container, false)

        root.text_add_title.text = getString(R.string.text_add_title, "4")

        root.button_add_final.setOnClickListener {
            Toast.makeText(root.context, "TODO: Sent to database", Toast.LENGTH_LONG).show()
        }

        return root
    }
}
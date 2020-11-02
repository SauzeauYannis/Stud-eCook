package com.android.app.studecook.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.app.studecook.R
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_add.view.*

class AddFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_add, container, false)

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
            Toast.makeText(root.context, "TODO: Next step", Toast.LENGTH_LONG).show()
        }

        return root
    }
}
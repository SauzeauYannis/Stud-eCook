package com.android.example.studecook.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.example.studecook.R
import kotlinx.android.synthetic.main.fragment_dashboard.view.*

class AddFragment : Fragment() {

    private lateinit var dashboardViewModel: AddViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
                ViewModelProvider(this).get(AddViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val textView: TextView = root.text_dashboard
        dashboardViewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
        })
        return root
    }
}
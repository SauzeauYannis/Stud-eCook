package com.android.app.studecook.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.app.studecook.R
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finishAffinity()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        root.image_home_search.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_homeSearchFragment)
        }

        root.image_home_follow.setOnClickListener {
            Toast.makeText(context, getString(R.string.button_home_follow), Toast.LENGTH_SHORT).show()
        }

        root.image_home_disc.setOnClickListener {
            Toast.makeText(context, getString(R.string.button_home_disc), Toast.LENGTH_SHORT).show()
        }

        root.image_home_fav.setOnClickListener {
            Toast.makeText(context, getString(R.string.button_home_fav), Toast.LENGTH_SHORT).show()
        }

        root.fab_fliter.setOnClickListener {
            Toast.makeText(context, getString(R.string.button_filter), Toast.LENGTH_SHORT).show()
        }

        return root
    }
}
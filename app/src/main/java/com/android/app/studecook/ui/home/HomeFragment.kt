package com.android.app.studecook.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.app.studecook.R
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {

    private lateinit var current : ImageView

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

        current = root.image_home_disc
        current.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorAccentLight))

        root.image_home_search.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_homeSearchFragment)
        }

        root.image_home_follow.setOnClickListener {
            changeCurrent(it as ImageView)
        }

        root.image_home_disc.setOnClickListener {
            changeCurrent(it as ImageView)
        }

        root.image_home_fav.setOnClickListener {
            changeCurrent(it as ImageView)
        }

        root.fab_fliter.setOnClickListener {
            Toast.makeText(context, getString(R.string.button_filter), Toast.LENGTH_SHORT).show()
        }

        return root
    }

    private fun changeCurrent(newCurrent : ImageView) {
        current.colorFilter = newCurrent.colorFilter
        current = newCurrent
        current.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorAccentLight))
    }
}
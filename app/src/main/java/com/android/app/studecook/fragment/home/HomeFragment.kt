package com.android.app.studecook.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.android.app.studecook.R
import com.android.app.studecook.adapter.ViewPagerAdapter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {

    private lateinit var current : ImageView
    private lateinit var viewPagerAdapter: ViewPagerAdapter

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

        viewPagerAdapter = ViewPagerAdapter()

        root.view_pager.adapter = viewPagerAdapter

        root.view_pager.setCurrentItem(1, false)

        root.image_home_follow.setOnClickListener {
            changeCurrent(it as ImageView)
            root.view_pager.setCurrentItem(0, true)
            root.text_home_name.text = getString(R.string.text_home_sub)
        }

        root.image_home_disc.setOnClickListener {
            changeCurrent(it as ImageView)
            root.view_pager.setCurrentItem(1, true)
            root.text_home_name.text = getString(R.string.app_name)
        }

        root.image_home_fav.setOnClickListener {
            changeCurrent(it as ImageView)
            root.view_pager.setCurrentItem(2, true)
            root.text_home_name.text = getString(R.string.text_home_favorite)
        }

        root.view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        changeCurrent(root.image_home_follow)
                        root.text_home_name.text = getString(R.string.text_home_sub)
                    }
                    1 -> {
                        changeCurrent(root.image_home_disc)
                        root.text_home_name.text = getString(R.string.app_name)
                    }
                    else -> {
                        changeCurrent(root.image_home_fav)
                        root.text_home_name.text = getString(R.string.text_home_favorite)
                    }}
                super.onPageSelected(position)
            }
        })

        return root
    }

    private fun changeCurrent(newCurrent : ImageView) {
        current.colorFilter = newCurrent.colorFilter
        current = newCurrent
        current.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorAccentLight))
    }

    override fun onStart() {
        super.onStart()
        viewPagerAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        viewPagerAdapter.stopListening()
    }
}
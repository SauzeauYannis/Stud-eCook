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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.app.studecook.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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

        refresh(root.home_swipeRefreshLayout)

        val gridLayoutManager = GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false)
        root.home_recyclerView.layoutManager = gridLayoutManager

        val items = getRecipe()
        root.home_recyclerView.adapter = RecipeAdapater(requireContext(), items)

        return root
    }

    private fun changeCurrent(newCurrent : ImageView) {
        current.colorFilter = newCurrent.colorFilter
        current = newCurrent
        current.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorAccentLight))
    }

    private fun refresh(refreshLayout: SwipeRefreshLayout) {
        refreshLayout.setOnRefreshListener {
            Toast.makeText(refreshLayout.context, "refresh", Toast.LENGTH_SHORT).show()
            refreshLayout.isRefreshing = false
        }
    }

    private fun getRecipe() : ArrayList<RecipeGrid> {
        val items = ArrayList<RecipeGrid>()
        Firebase.firestore.collection("recipes")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val name = document.getString("name")
                        items.add(RecipeGrid(name!!))
                    }
                }
        return items
    }
}
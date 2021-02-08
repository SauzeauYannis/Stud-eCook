package com.android.app.studecook.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.app.studecook.R
import com.android.app.studecook.ui.recipe.RecipeModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {

    private lateinit var current : ImageView

    private val db = FirebaseFirestore.getInstance()
    private val collectionReference = db.collection("recipes")

    private var recipeAdapter: RecipeAdapter? = null

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
            val rotation: Animation by lazy {
                AnimationUtils.loadAnimation(context, R.anim.rotation)
            }
            it.startAnimation(rotation)
        }

        root.home_swipeRefreshLayout.setOnRefreshListener {
            val query  = collectionReference.
            orderBy("date", Query.Direction.DESCENDING)
            val firestoreRecyclerOptions = FirestoreRecyclerOptions.Builder<RecipeModel>()
                    .setQuery(query, RecipeModel::class.java)
                    .build()

            recipeAdapter!!.updateOptions(firestoreRecyclerOptions)

            Toast.makeText(context, getString(R.string.toast_up_to_date), Toast.LENGTH_SHORT).show()

            root.home_swipeRefreshLayout.isRefreshing = false
        }

        setUpRecyclerView(root)

        return root
    }

    private fun changeCurrent(newCurrent : ImageView) {
        current.colorFilter = newCurrent.colorFilter
        current = newCurrent
        current.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorAccentLight))
    }

    private fun setUpRecyclerView(root: View) {

        root.home_swipeRefreshLayout.isRefreshing = true

        val query  = collectionReference.
        orderBy("date", Query.Direction.DESCENDING)
        val firestoreRecyclerOptions = FirestoreRecyclerOptions.Builder<RecipeModel>()
                .setQuery(query, RecipeModel::class.java)
                .build()

        recipeAdapter = RecipeAdapter(firestoreRecyclerOptions)

        root.home_recyclerView.layoutManager = GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false)
        root.home_recyclerView.adapter = recipeAdapter

        root.home_swipeRefreshLayout.isRefreshing = false
    }

    override fun onStart() {
        super.onStart()
        recipeAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        recipeAdapter!!.stopListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (recipeAdapter != null) {
            recipeAdapter!!.stopListening()
        }
    }
}
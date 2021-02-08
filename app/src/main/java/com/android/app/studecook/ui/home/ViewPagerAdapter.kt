package com.android.app.studecook.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.app.studecook.R
import com.android.app.studecook.ui.recipe.RecipeModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ViewPagerAdapter() : RecyclerView.Adapter<ViewPagerAdapter.Pager2ViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val collectionReference = db.collection("recipes")

    private var recipeAdapter: RecipeAdapter? = null

    inner class Pager2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemSwipe: SwipeRefreshLayout = itemView.findViewById(R.id.home_swipeRefreshLayout)
        val itemRecycler: RecyclerView = itemView.findViewById(R.id.home_recyclerView)
        val itemFabFilter: FloatingActionButton = itemView.findViewById(R.id.fab_filter)
    }

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ViewPagerAdapter.Pager2ViewHolder {
        return Pager2ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_swipe_home, parent, false))
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun onBindViewHolder(holder: ViewPagerAdapter.Pager2ViewHolder, position: Int) {
        holder.itemFabFilter.setOnClickListener {
            val rotation: Animation by lazy {
                AnimationUtils.loadAnimation(holder.itemView.context, R.anim.rotation)
            }
            it.startAnimation(rotation)
        }

        holder.itemSwipe.setOnRefreshListener {
            val query  = collectionReference.
            orderBy("date", Query.Direction.DESCENDING)
            val firestoreRecyclerOptions = FirestoreRecyclerOptions.Builder<RecipeModel>()
                    .setQuery(query, RecipeModel::class.java)
                    .build()

            recipeAdapter!!.updateOptions(firestoreRecyclerOptions)

            Toast.makeText(holder.itemView.context, holder.itemView.context.getString(R.string.toast_up_to_date), Toast.LENGTH_SHORT).show()

            holder.itemSwipe.isRefreshing = false
        }

        setUpRecyclerView(holder.itemRecycler, holder.itemSwipe)

        recipeAdapter!!.startListening()
    }

    private fun setUpRecyclerView(home_recyclerView: RecyclerView, home_swipeRefreshLayout: SwipeRefreshLayout) {

        home_swipeRefreshLayout.isRefreshing = true

        val query  = collectionReference.
        orderBy("date", Query.Direction.DESCENDING)
        val firestoreRecyclerOptions = FirestoreRecyclerOptions.Builder<RecipeModel>()
                .setQuery(query, RecipeModel::class.java)
                .build()

        recipeAdapter = RecipeAdapter(firestoreRecyclerOptions)

        home_recyclerView.layoutManager = GridLayoutManager(home_recyclerView.context, 2, LinearLayoutManager.VERTICAL, false)
        home_recyclerView.adapter = recipeAdapter

        home_swipeRefreshLayout.isRefreshing = false
    }

    fun stopListening() {
        recipeAdapter!!.stopListening()
    }
}
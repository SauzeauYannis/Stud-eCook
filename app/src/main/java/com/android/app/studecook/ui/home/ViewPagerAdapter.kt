package com.android.app.studecook.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.app.studecook.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ViewPagerAdapter(var context: Context) : RecyclerView.Adapter<ViewPagerAdapter.Pager2ViewHolder>() {

    inner class Pager2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val refreshLayout: SwipeRefreshLayout = itemView.findViewById(R.id.home_swipeRefreshLayout)
        val recyclerView: RecyclerView = itemView.findViewById(R.id.home_recyclerView)
    }

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ViewPagerAdapter.Pager2ViewHolder {
        val root = Pager2ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.view_home, parent, false))

        refresh(root.refreshLayout)

        val gridLayoutManager = GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false)
        root.recyclerView.layoutManager = gridLayoutManager

        val items = ArrayList<RecipeGrid>()
        Firebase.firestore.collection("recipes")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val name = document.getString("name")
                           items.add(RecipeGrid(name!!))
                    }
                }

        root.recyclerView.adapter = RecipeAdapater(context, items)

        return root
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun onBindViewHolder(holder: ViewPagerAdapter.Pager2ViewHolder, position: Int) {
    }

    private fun refresh(refreshLayout: SwipeRefreshLayout) {
        refreshLayout.setOnRefreshListener {
            Toast.makeText(refreshLayout.context, "refresh", Toast.LENGTH_SHORT).show()
            refreshLayout.isRefreshing = false
        }
    }
}
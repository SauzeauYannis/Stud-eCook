package com.android.app.studecook.ui.home

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

class ViewPagerAdapter() : RecyclerView.Adapter<ViewPagerAdapter.Pager2ViewHolder>() {

    inner class Pager2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val refreshLayout: SwipeRefreshLayout = itemView.findViewById(R.id.home_swipeRefreshLayout)
        val recyclerView: RecyclerView = itemView.findViewById(R.id.home_recyclerView)
    }

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ViewPagerAdapter.Pager2ViewHolder {
        return Pager2ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.view_home, parent, false))
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun onBindViewHolder(holder: ViewPagerAdapter.Pager2ViewHolder, position: Int) {
        refresh(holder.refreshLayout)
        val gridLayoutManager = GridLayoutManager(holder.itemView.context, 2, LinearLayoutManager.VERTICAL, false)
        holder.recyclerView.layoutManager = gridLayoutManager
        holder.recyclerView.adapter = RecipeAdapater(holder.itemView.context, setDataInList())
    }

    private fun refresh(refreshLayout: SwipeRefreshLayout) {
        refreshLayout.setOnRefreshListener {
            Toast.makeText(refreshLayout.context, "refresh", Toast.LENGTH_SHORT).show()
            refreshLayout.isRefreshing = false
        }
    }

    private fun setDataInList() : ArrayList<RecipeGrid> {
        val items = ArrayList<RecipeGrid>()

        val db = Firebase.firestore

        db.collection("recipes")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    items.add(RecipeGrid(db, document.id))
                }
            }

        return items
    }
}
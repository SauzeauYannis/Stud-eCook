package com.android.app.studecook.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.app.studecook.R
import com.android.app.studecook.adapter.HomeRecipeAdapter
import com.android.app.studecook.adapter.RecipeAdapter
import com.android.app.studecook.ui.account.UserModel
import com.android.app.studecook.ui.recipe.RecipeModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject

class ViewPagerAdapter : RecyclerView.Adapter<ViewPagerAdapter.Pager2ViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val collectionReference = db.collection("recipes")

    private var recipeAdapter: RecipeAdapter? = null

    inner class Pager2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemSwipe: SwipeRefreshLayout = itemView.findViewById(R.id.home_swipeRefreshLayout)
        val itemRecycler: RecyclerView = itemView.findViewById(R.id.home_recyclerView)
        val itemFabFilter: FloatingActionButton = itemView.findViewById(R.id.fab_filter)
        val itemText: TextView = itemView.findViewById(R.id.text_slide)
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
            Toast.makeText(holder.itemView.context, holder.itemView.context.getString(R.string.toast_up_to_date), Toast.LENGTH_SHORT).show()
            holder.itemSwipe.isRefreshing = false
        }

        setUpRecyclerView(holder, position)
    }

    private fun setUpRecyclerView(holder: Pager2ViewHolder, position: Int) {
        holder.itemSwipe.isRefreshing = true

        when (position) {
            0 -> {
                loadSubsPage(holder)
            }
            1 -> {
                loadHomePage(holder)
            }
            else -> {
                loadFavPage(holder)
            }
        }

        holder.itemSwipe.isRefreshing = false
    }

    private fun loadSubsPage(holder: Pager2ViewHolder) {
        db.collection("users")
                .document(FirebaseAuth.getInstance().currentUser!!.uid)
                .get()
                .addOnSuccessListener { doc ->
                    val user = doc.toObject<UserModel>()!!
                    if (user.subs!!.isEmpty()) {
                        holder.itemText.visibility = TextView.VISIBLE
                        holder.itemFabFilter.visibility = FloatingActionButton.INVISIBLE
                    } else {
                        val query = collectionReference.orderBy("date", Query.Direction.DESCENDING)
                                .whereIn("uid", user.subs!!)
                        val firestoreRecyclerOptions = FirestoreRecyclerOptions.Builder<RecipeModel>()
                                .setQuery(query, RecipeModel::class.java)
                                .build()

                        recipeAdapter = HomeRecipeAdapter(firestoreRecyclerOptions)
                        holder.itemRecycler.layoutManager = GridLayoutManager(holder.itemRecycler.context, 2, LinearLayoutManager.VERTICAL, false)
                        holder.itemRecycler.adapter = recipeAdapter
                        recipeAdapter!!.startListening()
                    }
                }
    }

    private fun loadHomePage(holder: Pager2ViewHolder) {
        val query = collectionReference.orderBy("date", Query.Direction.DESCENDING)
        val firestoreRecyclerOptions = FirestoreRecyclerOptions.Builder<RecipeModel>()
                .setQuery(query, RecipeModel::class.java)
                .build()

        recipeAdapter = HomeRecipeAdapter(firestoreRecyclerOptions)
        holder.itemRecycler.layoutManager = GridLayoutManager(holder.itemRecycler.context, 2, LinearLayoutManager.VERTICAL, false)
        holder.itemRecycler.adapter = recipeAdapter
        recipeAdapter!!.startListening()
    }

    private fun loadFavPage(holder: Pager2ViewHolder) {
        db.collection("users")
                .document(FirebaseAuth.getInstance().currentUser!!.uid)
                .get()
                .addOnSuccessListener { doc ->
                    val user = doc.toObject<UserModel>()!!
                    if (user.favorites!!.isEmpty()) {
                        holder.itemText.visibility = TextView.VISIBLE
                        holder.itemFabFilter.visibility = FloatingActionButton.INVISIBLE
                    } else {
                        val query = db.collection("recipes").orderBy("date", Query.Direction.DESCENDING)
                                .whereIn("__name__", user.favorites!!)
                        val firestoreRecyclerOptions = FirestoreRecyclerOptions.Builder<RecipeModel>()
                                .setQuery(query, RecipeModel::class.java)
                                .build()

                        recipeAdapter = HomeRecipeAdapter(firestoreRecyclerOptions)
                        holder.itemRecycler.layoutManager = GridLayoutManager(holder.itemRecycler.context, 2, LinearLayoutManager.VERTICAL, false)
                        holder.itemRecycler.adapter = recipeAdapter
                        recipeAdapter!!.startListening()
                    }
                }
    }

    fun startListening() {
        recipeAdapter?.startListening()
    }

    fun stopListening() {
        recipeAdapter?.stopListening()
    }
}
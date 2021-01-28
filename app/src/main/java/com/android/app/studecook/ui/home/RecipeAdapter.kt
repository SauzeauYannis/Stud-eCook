package com.android.app.studecook.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.app.studecook.R
import com.android.app.studecook.RecipeModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.grid_layout_list_recipe.view.*

class RecipeAdapter(options: FirestoreRecyclerOptions<RecipeModel>) :
    FirestoreRecyclerAdapter<RecipeModel, RecipeAdapter.RecipeAdapaterVH>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeAdapaterVH {
        return RecipeAdapaterVH(LayoutInflater.from(parent.context)
            .inflate(R.layout.grid_layout_list_recipe, parent, false))
    }

    override fun onBindViewHolder(holder: RecipeAdapaterVH, position: Int, model: RecipeModel) {
        holder.name.text = model.name
    }

    class RecipeAdapaterVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image = itemView.home_recipe_image
        var name = itemView.home_recipe_name
    }

}
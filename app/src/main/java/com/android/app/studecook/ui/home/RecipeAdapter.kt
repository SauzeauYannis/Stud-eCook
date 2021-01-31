package com.android.app.studecook.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.android.app.studecook.R
import com.android.app.studecook.databinding.FragmentHomeBinding
import com.android.app.studecook.ui.recipe.RecipeModel
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.grid_layout_list_recipe.view.*

class RecipeAdapter(options: FirestoreRecyclerOptions<RecipeModel>) :
    FirestoreRecyclerAdapter<RecipeModel, RecipeAdapter.RecipeAdapaterVH>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeAdapaterVH {
        return RecipeAdapaterVH(LayoutInflater.from(parent.context)
            .inflate(R.layout.grid_layout_list_recipe, parent, false))
    }

    override fun onBindViewHolder(holder: RecipeAdapaterVH, position: Int, model: RecipeModel) {
        if (model.images!!.isNotEmpty()) {
            val storageReference = FirebaseStorage.getInstance().reference.child(model.images!![0])
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(holder.recipe.context)
                        .load(uri)
                        .into(holder.image)
            }
        }
        holder.name.text = model.name
        holder.recipe.setOnClickListener {
            val homeFragment = holder.itemView.findFragment<HomeFragment>()
            val action = HomeFragmentDirections.actionNavigationHomeToRecipeFragment(model)
            findNavController(homeFragment).navigate(action)
        }
    }

    class RecipeAdapaterVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image = itemView.home_recipe_image
        var name: TextView = itemView.home_recipe_name
        var recipe: CardView = itemView.cardView
    }

}

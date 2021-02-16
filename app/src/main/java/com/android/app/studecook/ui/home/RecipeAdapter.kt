package com.android.app.studecook.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.findFragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.android.app.studecook.R
import com.android.app.studecook.ui.recipe.RecipeModel
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.grid_layout_list_recipe.view.*

class RecipeAdapter(options: FirestoreRecyclerOptions<RecipeModel>) :
    FirestoreRecyclerAdapter<RecipeModel, RecipeAdapter.RecipeAdapaterVH>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeAdapaterVH {
        return RecipeAdapaterVH(LayoutInflater.from(parent.context)
            .inflate(R.layout.grid_layout_list_recipe, parent, false))
    }

    override fun onBindViewHolder(holder: RecipeAdapaterVH, position: Int, model: RecipeModel) {
        for (elem in holder.euros) {
            elem.alpha = 0.1F
        }
        for (elem in holder.times) {
            elem.alpha = 0.1F
        }
        if (model.image != null) {
            val storageReference = FirebaseStorage.getInstance().reference.child(model.image!!)
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(holder.recipe.context)
                    .load(uri)
                    .into(holder.image)
            }
        } else
            holder.image.setImageResource(R.drawable.ic_no_image)
        for (i in 0..model.price!!)
            holder.euros[i].alpha = 1.0F
        for (i in 0..model.time!!)
            holder.times[i].alpha = 1.0F
        if (model.name!!.length > 23)
            holder.name.text = model.name!!.substring(0, 20).plus("...")
        else
            holder.name.text = model.name
        holder.recipe.setOnClickListener {
            val homeFragment = holder.itemView.findFragment<HomeFragment>()
            val action: NavDirections = HomeFragmentDirections.actionNavigationHomeToRecipeFragment(model)
            findNavController(homeFragment).navigate(action)
        }
    }

    class RecipeAdapaterVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var euros = listOf<ImageView>(
                itemView.home_euro1,
                itemView.home_euro2,
                itemView.home_euro3)
        var times = listOf<ImageView>(
                itemView.home_time1,
                itemView.home_time2,
                itemView.home_time3)
        var image: ImageView = itemView.home_recipe_image
        var name: TextView = itemView.home_recipe_name
        var recipe: CardView = itemView.cardView
    }
}

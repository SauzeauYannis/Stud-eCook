package com.android.app.studecook.ui.home

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.app.studecook.R
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class RecipeAdapater(var context: Context, var arrayList: ArrayList<RecipeGrid>) : RecyclerView.Adapter<RecipeAdapater.ItemHolder>() {

    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var image = itemView.findViewById<ImageView>(R.id.home_recipe_image)
        var name = itemView.findViewById<TextView>(R.id.home_recipe_name)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.grid_layout_list_recipe, parent, false))
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val recipe = arrayList[position]

/*        val storageRef = Firebase.storage.reference.child(recipe.image!!)
        val ONE_MEGABYTE: Long = 1024 * 1024
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
            holder.image.setImageBitmap(
                    BitmapFactory.decodeByteArray(it, 0, it.size)
            )
        }*/
        holder.image.setImageResource(R.drawable.ic_add)
        holder.name.text = recipe.text
    }
}
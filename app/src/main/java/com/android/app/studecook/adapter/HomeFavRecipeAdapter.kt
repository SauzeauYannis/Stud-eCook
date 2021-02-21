package com.android.app.studecook.adapter

import android.app.AlertDialog
import androidx.fragment.app.findFragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import com.android.app.studecook.R
import com.android.app.studecook.fragment.account.AccountFragment
import com.android.app.studecook.fragment.account.AccountFragmentDirections
import com.android.app.studecook.fragment.home.HomeFragment
import com.android.app.studecook.fragment.home.HomeFragmentDirections
import com.android.app.studecook.model.RecipeModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class HomeFavRecipeAdapter(options: FirestoreRecyclerOptions<RecipeModel>) : RecipeAdapter(options) {

    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onBindViewHolder(holder: RecipeAdapaterVH, position: Int, model: RecipeModel) {
        super.onBindViewHolder(holder, position, model)
        holder.recipe.setOnClickListener {
            val homeFragment = holder.itemView.findFragment<HomeFragment>()
            val action: NavDirections = HomeFragmentDirections.actionNavigationHomeToRecipeFragment(model)
            NavHostFragment.findNavController(homeFragment).navigate(action)
        }
        holder.recipe.setOnLongClickListener {
            val context = holder.recipe.context
            AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.dialog_unfav_title))
                    .setMessage(context.getString(R.string.dialog_unfav_message))
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        db.collection(context.getString(R.string.collection_recipes))
                                .whereEqualTo("date", model.date)
                                .get()
                                .addOnSuccessListener { docs ->
                                    for (doc in docs) {
                                        val fav = doc.getLong("fav")
                                        db.collection(context.getString(R.string.collection_users))
                                                .document(currentUser!!.uid)
                                                .update("favorites", FieldValue.arrayRemove(doc.id))
                                        db.collection(context.getString(R.string.collection_recipes))
                                                .document(doc.id)
                                                .update("fav", fav!!.minus(1))
                                        val homeFragment = holder.itemView.findFragment<HomeFragment>()
                                        NavHostFragment.findNavController(homeFragment).navigate(
                                                HomeFragmentDirections.actionNavigationHomeSelf()
                                        )
                                        break
                                    }
                                }
                    }
                    .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                        dialog.cancel()
                    }
                    .show()
            false
        }
    }
}
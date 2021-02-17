package com.android.app.studecook.adapter

import android.widget.Toast
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.NavHostFragment
import com.android.app.studecook.ui.account.AccountFragment
import com.android.app.studecook.ui.account.AccountFragmentDirections
import com.android.app.studecook.ui.recipe.RecipeModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class MyRecipeAdapter(options: FirestoreRecyclerOptions<RecipeModel>) : RecipeAdapter(options) {

    override fun onBindViewHolder(holder: RecipeAdapaterVH, position: Int, model: RecipeModel) {
        super.onBindViewHolder(holder, position, model)
        holder.recipe.setOnClickListener {
            val accountFragment = holder.itemView.findFragment<AccountFragment>()
            val action = AccountFragmentDirections.actionNavigationAccountToRecipeFragment(model)
            NavHostFragment.findNavController(accountFragment).navigate(action)
        }
        holder.recipe.setOnLongClickListener {
            Toast.makeText(holder.recipe.context, "TODO: delete recipe", Toast.LENGTH_LONG).show() // TODO: 15-Feb-21 Delete recipe
            true
        }
    }
}

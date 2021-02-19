package com.android.app.studecook.adapter

import androidx.fragment.app.findFragment
import androidx.navigation.fragment.NavHostFragment
import com.android.app.studecook.fragment.account.AccountViewFragment
import com.android.app.studecook.fragment.account.AccountViewFragmentDirections
import com.android.app.studecook.model.RecipeModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class HisRecipeAdapter(options: FirestoreRecyclerOptions<RecipeModel>) : RecipeAdapter(options) {

    override fun onBindViewHolder(holder: RecipeAdapaterVH, position: Int, model: RecipeModel) {
        super.onBindViewHolder(holder, position, model)
        holder.recipe.setOnClickListener {
            val accountViewFragment = holder.itemView.findFragment<AccountViewFragment>()
            val action = AccountViewFragmentDirections.actionAccountViewFragmentToRecipeFragment(model)
            NavHostFragment.findNavController(accountViewFragment).navigate(action)
        }
    }
}

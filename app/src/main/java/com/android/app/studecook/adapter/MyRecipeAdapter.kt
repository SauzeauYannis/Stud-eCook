package com.android.app.studecook.adapter

import android.app.AlertDialog
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.NavHostFragment
import com.android.app.studecook.R
import com.android.app.studecook.fragment.account.AccountFragment
import com.android.app.studecook.fragment.account.AccountFragmentDirections
import com.android.app.studecook.model.RecipeModel
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
            val context = holder.recipe.context
            AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.dialog_delete_title))
                    .setMessage(context.getString(R.string.dialog_delete_message))
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        snapshots.getSnapshot(position).reference.delete()
                        val accountFragment = holder.itemView.findFragment<AccountFragment>()
                        NavHostFragment.findNavController(accountFragment).navigate(
                                AccountFragmentDirections.actionNavigationAccountSelf()
                        )
                    }
                    .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                        dialog.cancel()
                    }
                    .show()
            false
        }
    }
}

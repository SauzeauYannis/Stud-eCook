package com.android.app.studecook.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.android.app.studecook.R
import com.android.app.studecook.ui.recipe.RecipeModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.fragment_search.view.*

class HomeSearchFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_homeSearchFragment_to_navigation_home)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_search, container, false)

        root.image_search_prev.setOnClickListener {
            findNavController().navigate(R.id.action_homeSearchFragment_to_navigation_home)
        }

        val recipeNames = ArrayList<String>()

        db.collection(getString(R.string.collection_recipes))
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (recipe in task.result!!) {
                            recipe.getString("name")?.let { recipeNames.add(it) }
                        }
                        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, recipeNames)
                        root.listview_search.adapter = adapter
                    }
                }

        root.searchview.setOnClickListener {
            root.searchview.isIconified = false
        }

        root.searchview.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                root.searchview.clearFocus()
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                adapter.filter.filter(p0)
                return false
            }
        })

        root.listview_search.setOnItemClickListener { _, _, i, _ ->
            val selectedRecipe = root.listview_search.getItemAtPosition(i)
            db.collection(getString(R.string.collection_recipes))
                    .whereEqualTo("name", selectedRecipe)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (recipe in task.result!!) {
                                val model = recipe.toObject<RecipeModel>()
                                val action = HomeSearchFragmentDirections.actionHomeSearchFragmentToRecipeFragment(model)
                                NavHostFragment.findNavController(this).navigate(action)
                                break
                            }
                        }
                    }
        }

        return root
    }
}
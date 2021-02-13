package com.android.app.studecook.ui.recipe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.android.app.studecook.R
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_recipe.view.*

class RecipeFragment : Fragment() {

    private val args by navArgs<RecipeFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_recipeFragment_to_navigation_home)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_recipe, container, false)

        root.image_recipe_prev.setOnClickListener {
            findNavController().navigate(R.id.action_recipeFragment_to_navigation_home)
        }

        root.text_recipe_name.text = args.currentRecipe.name

        if (args.currentRecipe.image != null) {
            val storageReference = FirebaseStorage.getInstance().reference.child(args.currentRecipe.image!!)
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(requireContext())
                    .load(uri)
                    .into(root.image_recipe)
            }
        } else
            root.image_recipe.visibility = ImageView.INVISIBLE

        return root
    }
}
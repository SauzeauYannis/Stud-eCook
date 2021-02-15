package com.android.app.studecook.ui.recipe

import android.os.Bundle
import android.os.UserManager
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.android.app.studecook.R
import com.android.app.studecook.ui.account.UserModel
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_recipe.view.*

class RecipeFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()

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
        val recipe = args.currentRecipe

        root.image_recipe_prev.setOnClickListener {
            findNavController().navigate(R.id.action_recipeFragment_to_navigation_home)
        }

        root.text_recipe_name.text = recipe.name

        loadImage(recipe.image, root.image_recipe)

        root.text_recipe_type.append(" " + resources.getStringArray(R.array.type_array)[recipe.type!!])

        root.text_recipe_diet.append(" " + resources.getStringArray(R.array.diet_array)[recipe.diet!!])

        loadIcons(root, recipe)

        db.collection("users")
                .document(recipe.uid!!)
                .get().addOnSuccessListener { doc ->
                    val user = doc.toObject<UserModel>()!!
                    val name = user.name!!
                    clickableOwner(root.text_recipe_owner, name)
                }


        return root
    }

    private fun loadImage(imagePath: String?, imageView: ImageView) {
        if (imagePath != null) {
            val storageReference = FirebaseStorage.getInstance().reference.child(imagePath)
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(requireContext())
                        .load(uri)
                        .into(imageView)
            }
        } else
            imageView.visibility = ImageView.GONE
    }

    private fun loadIcons(root: View, recipe: RecipeModel) {
        val pricesImages = listOf<ImageView>(
                root.image_recipe_price,
                root.image_recipe_price2,
                root.image_recipe_price3)
        val timesImages = listOf<ImageView>(
                root.image_recipe_time,
                root.image_recipe_time2,
                root.image_recipe_time3)

        for (i in 0..recipe.price!!)
            pricesImages[i].alpha = 1.0F
        for (i in 0..recipe.time!!)
            timesImages[i].alpha = 1.0F

        root.text_recipe_fav.text = recipe.fav.toString()
    }

    private fun clickableOwner(text: TextView, name: String) {
        val clickableName = SpannableString(name)
        clickableName.setSpan(object: ClickableSpan(){
            override fun onClick(p0: View) {
                Toast.makeText(context, "TODO: envoyer sur la page perso du créateur $name", Toast.LENGTH_LONG).show()
            }
        }, 0, name.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        text.append(" ")
        text.append(clickableName)
        text.movementMethod = LinkMovementMethod.getInstance()
    }
}
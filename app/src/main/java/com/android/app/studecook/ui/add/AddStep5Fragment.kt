package com.android.app.studecook.ui.add

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.app.studecook.MainActivity
import com.android.app.studecook.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import kotlinx.android.synthetic.main.fragment_add_step5.*
import kotlinx.android.synthetic.main.fragment_add_step5.view.*
import java.util.*
import kotlin.collections.HashSet


class AddStep5Fragment : Fragment() {

    private var imageUri: Uri? = null
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_navigation_add_step5_to_navigation_add_step4)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)

        storage = Firebase.storage
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_add_step5, container, false)
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)

        root.text_add_title.text = getString(R.string.text_add_title, 5)

        root.button_add_picture.setOnClickListener {
            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(4, 3)
                .setFixAspectRatio(true)
                .start(requireContext(), this)
        }

        root.button_delete_pic.setOnClickListener {
            imageUri = null
            root.image_add_recipe.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_no_image))
        }

        root.button_back.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_add_step5_to_navigation_add_step4)
        }

        root.button_add_next.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user!!.isAnonymous) {
                Toast.makeText(context, getString(R.string.text_add_recipe_anonymous), Toast.LENGTH_LONG).show()
            } else {
                sendToDataBase(sharedPref, user)
            }
        }

        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            imageUri = result!!.uri
            image_add_recipe.setImageURI(result.uri)
        }
    }

    private fun sendImages(recipeId: String): String? {
        return if (imageUri != null) {
            val storageRef = storage.reference
            val imagePath = "images/$recipeId"
            val imageRef = storageRef.child(imagePath)

            imageRef.putFile(imageUri!!).addOnFailureListener {
                Toast.makeText(context, getString(R.string.text_add_recipe_failure_image), Toast.LENGTH_LONG).show()
            }

            imagePath
        } else
            null
    }

    private fun sendToDataBase(sharedPref: SharedPreferences?, user: FirebaseUser?) {
        val db = Firebase.firestore

        val recipeId = db.collection(getString(R.string.collection_recipes)).document().id

        val imagePath = sendImages(recipeId)

        val name = sharedPref?.getString(getString(R.string.saved_add_name_key), "")
        val time = sharedPref?.getInt(getString(R.string.saved_add_time_key), 0)
        val price = sharedPref?.getInt(getString(R.string.saved_add_price_key), 0)
        val number = sharedPref?.getInt(getString(R.string.saved_add_number_key), 0)
        val type = sharedPref?.getInt(getString(R.string.saved_add_type_key), 0)
        val diet = sharedPref?.getInt(getString(R.string.saved_add_diet_key), 0)
        val utensils = sharedPref?.getStringSet(getString(R.string.saved_add_utensils_key), HashSet<String>())?.toList()
        val ingredientsQuantity = sharedPref?.getString(getString(R.string.saved_add_ingredients_quantity_key), null)?.split(",")
        val ingredientsType = sharedPref?.getString(getString(R.string.saved_add_ingredients_type_key), null)?.split(",")
        val ingredientsName = sharedPref?.getString(getString(R.string.saved_add_ingredients_name_key), null)?.split(",")
        val steps = sharedPref?.getString(getString(R.string.saved_add_steps_key), null)?.split("\n\n\n")

        val recipe = hashMapOf(
                "name" to name,
                "time" to time,
                "price" to price,
                "number" to number,
                "type" to type,
                "diet" to diet,
                "utensils" to utensils,
                "ingredientsQuantity" to ingredientsQuantity,
                "ingredientsType" to ingredientsType,
                "ingredientsName" to ingredientsName,
                "steps" to steps,
                "image" to imagePath,
                "uid" to user!!.uid,
                "date" to Calendar.getInstance().time,
                "fav" to 0
        )

        db.collection(getString(R.string.collection_recipes)).document(recipeId)
            .set(recipe)
            .addOnSuccessListener {
                sharedPref!!.edit().clear().apply()
                AlertDialog.Builder(context)
                        .setTitle(getString(R.string.dialog_add_title))
                        .setMessage(getString(R.string.dialog_add_text))
                        .setPositiveButton(getString(R.string.dialog_add_ok)) { _, _ ->
                            val intent = Intent(context, MainActivity::class.java)
                            startActivity(intent)
                        }
                        .show()
            }
            .addOnFailureListener {
                Toast.makeText(context, getString(R.string.text_add_recipe_failure), Toast.LENGTH_LONG).show()
            }
    }
}
package com.android.app.studecook.ui.add

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.app.studecook.MainActivity
import com.android.app.studecook.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import kotlinx.android.synthetic.main.fragment_add_step5.*
import kotlinx.android.synthetic.main.fragment_add_step5.view.*

class AddStep5Fragment : Fragment() {

    private val maxImage = 3
    private var images: ArrayList<Uri?>? = null
    private var downloadUri: ArrayList<Uri?>? = null
    private var position = 0
    private var recipeNumber = 0
    private lateinit var storage: FirebaseStorage

    companion object {
        private const val PICK_IMAGE_CODE = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_navigation_add_step5_to_navigation_add_step4)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)

        images = ArrayList()
        downloadUri = ArrayList()

        storage = Firebase.storage
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_add_step5, container, false)
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)

        root.text_add_title.text = getString(R.string.text_add_title, "5")

        root.imageswitcher_add_recipe.setFactory {
            ImageView(context)
        }

        root.button_add_picture.setOnClickListener {
            pickImagesIntent()
        }

        root.button_precedent_picture.setOnClickListener {
            if (position > 0) {
                position--
                imageswitcher_add_recipe.setImageURI(images!![position])
                visibilityChanges()
            }
        }

        root.button_next_picture.setOnClickListener {
            if (position < images!!.size-1) {
                position++
                imageswitcher_add_recipe.setImageURI(images!![position])
                visibilityChanges()
            }
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

    private fun visibilityChanges() {
        if (images!!.size > 1) {
            when (position) {
                0 -> {
                    button_next_picture.visibility = Button.VISIBLE
                    button_precedent_picture.visibility = Button.INVISIBLE
                }
                images!!.size-1 -> {
                    button_next_picture.visibility = Button.INVISIBLE
                    button_precedent_picture.visibility = Button.VISIBLE
                }
                else -> {
                    button_next_picture.visibility = Button.VISIBLE
                    button_precedent_picture.visibility = Button.VISIBLE
                }
            }
            text_number_pictures.visibility = TextView.VISIBLE
            text_number_pictures.text = getString(R.string.text_number_pictures, position+1, images!!.size)
        }
    }

    private fun pickImagesIntent() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(gallery, PICK_IMAGE_CODE)
    }

    private fun sendImages(recipeId: String) {
        val storageRef = storage.reference

        for (i in 0 until images!!.size) {
            val imageRef = storageRef.child("images/$recipeId/${images!![i]?.lastPathSegment}")

            val uploadTask = images!![i]?.let { imageRef.putFile(it) }

            progress_download_images.progress = 0
            progress_download_images.visibility = ProgressBar.VISIBLE

            uploadTask?.addOnProgressListener { (bytesTransferred, totalByteCount) ->
                progress_download_images.progress = ((100.0 * bytesTransferred) / totalByteCount).toInt()
            }

            progress_download_images.visibility = ProgressBar.INVISIBLE

            uploadTask?.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                imageRef.downloadUrl
            }?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    downloadUri?.set(i, task.result)
                }
            }
        }
    }

    private fun sendToDataBase(sharedPref: SharedPreferences?, user: FirebaseUser?) {
        val db = Firebase.firestore

        recipeNumber = sharedPref?.getInt(getString(R.string.saved_add_recipe_number_key), 0)!!

        val recipeId = "recipe$recipeNumber"

        sendImages(recipeId)

        val name = sharedPref.getString(getString(R.string.saved_add_name_key), "")
        val time = sharedPref.getInt(getString(R.string.saved_add_time_key), 0)
        val price = sharedPref.getInt(getString(R.string.saved_add_price_key), 0)
        val number = sharedPref.getInt(getString(R.string.saved_add_number_key), 0)
        val type = sharedPref.getInt(getString(R.string.saved_add_type_key), 0)
        val diet = sharedPref.getInt(getString(R.string.saved_add_diet_key), 0)
        val utensils = sharedPref.getStringSet(getString(R.string.saved_add_utensils_key), HashSet<String>())?.toList()
        val ingredientsQuantity = sharedPref.getString(getString(R.string.saved_add_ingredients_quantity_key), null)?.split(",")
        val ingredientsType = sharedPref.getString(getString(R.string.saved_add_ingredients_type_key), null)?.split(",")
        val ingredientsName = sharedPref.getString(getString(R.string.saved_add_ingredients_name_key), null)?.split(",")
        val steps = sharedPref.getString(getString(R.string.saved_add_steps_key), null)?.split("\n\n\n")

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
            "images" to downloadUri,
            "uid" to user!!.uid
        )

        db.collection("recipes").document(recipeId)
            .set(recipe)
            .addOnSuccessListener {
                sharedPref.edit().clear().apply()
                sharedPref.edit {
                    putInt(getString(R.string.saved_add_recipe_number_key), (recipeNumber + 1))
                    commit()
                }
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(context, getString(R.string.text_add_recipe_failure), Toast.LENGTH_LONG).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if(data!!.clipData != null) {
                    val count = data.clipData!!.itemCount
                    if ((images!!.size + count) > maxImage) {
                        Toast.makeText(context, getString(R.string.text_add_pictures_too_much), Toast.LENGTH_LONG).show()
                    } else {
                        for (i in position until count+position) {
                            val imageUri = data.clipData!!.getItemAt(i).uri
                            images!!.add(imageUri)
                        }
                        imageswitcher_add_recipe.setImageURI(images!![position])
                    }
                } else {
                    val imageUri = data.data
                    imageswitcher_add_recipe.setImageURI(imageUri)
                }
                visibilityChanges()
            }
        }
    }
}
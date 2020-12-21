package com.android.app.studecook.ui.add

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.app.studecook.R
import kotlinx.android.synthetic.main.fragment_add_step5.*
import kotlinx.android.synthetic.main.fragment_add_step5.view.*

class AddStep5Fragment : Fragment() {

    //private val maxImage = 3
    private var images: ArrayList<Uri?>? = null
    private var position = 0

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
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_add_step5, container, false)

        root.text_add_title.text = getString(R.string.text_add_title, "5")

        root.button_add_next.setOnClickListener {
            Toast.makeText(root.context, "TODO: Sent to database", Toast.LENGTH_LONG).show()
        }

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
                buttonVisibilityChanges()
            }
        }

        root.button_next_picture.setOnClickListener {
            if (position < images!!.size-1) {
                position++
                imageswitcher_add_recipe.setImageURI(images!![position])
                buttonVisibilityChanges()
            }
        }

        root.button_back.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_add_step5_to_navigation_add_step4)
        }

        return root
    }

    private fun buttonVisibilityChanges() {
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
        }
    }

    private fun pickImagesIntent() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(gallery, PICK_IMAGE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if(data!!.clipData != null) {
                    val count = data.clipData!!.itemCount
                    for (i in position until count+position) {
                        val imageUri = data.clipData!!.getItemAt(i).uri
                        images!!.add(imageUri)
                    }
                    imageswitcher_add_recipe.setImageURI(images!![position])
                } else {
                    val imageUri = data.data
                    imageswitcher_add_recipe.setImageURI(imageUri)
                }
                buttonVisibilityChanges()
            }
        }
    }
}
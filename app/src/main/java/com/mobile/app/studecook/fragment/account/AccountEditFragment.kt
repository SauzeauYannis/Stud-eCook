package com.mobile.app.studecook.fragment.account

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mobile.app.studecook.R
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_account_edit.*
import kotlinx.android.synthetic.main.fragment_account_edit.view.*

class AccountEditFragment : Fragment() {

    private val args by navArgs<AccountEditFragmentArgs>()
    private val db = Firebase.firestore

    private var imageUri: Uri? = null
    private var changeImage = false

    private lateinit var textInputName: TextInputLayout
    private lateinit var textInputDesc: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                button_account_edit_back.callOnClick()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_account_edit, container, false)
        val user = args.currentUser

        textInputName = root.text_edit_account_name
        textInputDesc = root.text_edit_account_desc

        textInputName.setOnClickListener {
            textInputName.error = null
        }

        textInputDesc.setOnClickListener {
            textInputDesc.error = null
        }

        root.button_account_edit_back.setOnClickListener {
            confirmBack()
        }

        root.button_account_edit_add.setOnClickListener {
            changeImage = true
            addPicture()
        }

        root.button_account_edit_delete.setOnClickListener {
            root.image_account_edit.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_account_circle))
            imageUri = null
        }

        root.text_input_account_name.setText(user.name)

        root.text_input_account_desc.setText(user.description)

        root.button_account_edit_validate.setOnClickListener {
            if (validateName() && validateDesc())
                sendData()
        }

        loadImage(user.image, root.image_account_edit)

        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            Glide.with(requireContext())
                    .load(result?.uri)
                    .circleCrop()
                    .into(image_account_edit)
            imageUri = result?.uri
        }
    }

    private fun loadImage(imagePath: String?, imageView: ImageView) {
        if (imagePath != null) {
            if (imagePath != "") {
                FirebaseStorage.getInstance().reference.child(imagePath)
                        .downloadUrl.addOnSuccessListener { uri ->
                            Glide.with(requireContext())
                                    .load(uri)
                                    .circleCrop()
                                    .into(imageView)
                            imageUri = uri
                        }
            }
        }
    }

    private fun confirmBack() {
        AlertDialog.Builder(context)
                .setTitle(getString(R.string.alert_account_edit_title))
                .setMessage(getString(R.string.alert_account_edit_message))
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    findNavController().navigate(R.id.action_accountEditFragment_to_navigation_account)
                }
                .setNeutralButton(android.R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
                .show()
    }

    private fun addPicture() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .setFixAspectRatio(true)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(requireContext(), this)
    }

    private fun validateName(): Boolean {
        val name = textInputName.editText?.text.toString().trim()

        return when {
            name.length < 4 -> {
                textInputName.error = getString(R.string.account_name_false_short)
                false
            }
            name.length > 25 -> {
                textInputName.error = getString(R.string.account_name_false_long)
                false
            }
            else -> {
                textInputName.error = null
                true
            }
        }
    }

    private fun validateDesc(): Boolean {
        val desc = textInputDesc.editText?.text.toString().trim()

        return when {
            desc.length > 255 -> {
                textInputDesc.error = getString(R.string.desc_name_false_long)
                false
            }
            else -> {
                textInputDesc.error = null
                true
            }
        }
    }

    private fun sendImage(uid: String): String? {
        return if (imageUri != null) {
            val storageRef = Firebase.storage.reference
            val imagePath = "usersImage/$uid"

            if (changeImage) {
                val imageRef = storageRef.child(imagePath)

                imageRef.putFile(imageUri!!).addOnFailureListener {
                    Toast.makeText(context, getString(R.string.text_add_recipe_failure_image), Toast.LENGTH_LONG).show()
                }
            }

            imagePath
        } else
            null
    }

    private fun sendData() {
        db.collection(getString(R.string.collection_users)).document(args.id)
                .update("name", textInputName.editText?.text.toString())
                .addOnCompleteListener {
                    db.collection(getString(R.string.collection_users)).document(args.id)
                            .update("description", textInputDesc.editText?.text.toString())
                            .addOnCompleteListener {
                                db.collection(getString(R.string.collection_users)).document(args.id)
                                        .update("image", sendImage(args.id))
                                        .addOnCompleteListener {
                                            dialogSuccess()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, getString(R.string.text_add_recipe_failure), Toast.LENGTH_LONG).show()
                                        }
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, getString(R.string.text_add_recipe_failure), Toast.LENGTH_LONG).show()
                            }
                }
                .addOnFailureListener {
                    Toast.makeText(context, getString(R.string.text_add_recipe_failure), Toast.LENGTH_LONG).show()
                }
    }

    private fun dialogSuccess() {
        AlertDialog.Builder(context)
                .setMessage(getString(R.string.alert_account_edit_validate))
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    findNavController().navigate(R.id.action_accountEditFragment_to_navigation_account)
                }
                .show()
    }
}
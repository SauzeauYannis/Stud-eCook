package com.android.app.studecook.fragment.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.app.studecook.R
import com.android.app.studecook.adapter.HisRecipeAdapter
import com.android.app.studecook.model.UserModel
import com.android.app.studecook.model.RecipeModel
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_account_view.*
import kotlinx.android.synthetic.main.fragment_account_view.view.*

class AccountViewFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val args by navArgs<AccountViewFragmentArgs>()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    private var recipeAdapter: HisRecipeAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                button_view_acc_back.callOnClick()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_account_view, container, false)
        val user = args.currentUser
        val uid = args.id

        root.button_view_acc_back.setOnClickListener {
            findNavController().navigate(R.id.action_accountViewFragment_to_navigation_home)
        }

        root.text_view_acc_name.text = user.name

        root.text_view_acc_desc.text = user.description

        initButtons(root, uid)

        loadImage(user.image, root.image_view_acc)

        setUpRecyclerView(root, uid)

        recipeAdapter!!.startListening()

        return root
    }

    private fun initButtons(root: View, uid: String) {
        if (currentUser == null || currentUser.isAnonymous) {
            root.button_view_acc_sub.setOnClickListener {
                Toast.makeText(context, getString(R.string.text_forbid_ano), Toast.LENGTH_SHORT).show()
            }
        } else {
            db.collection(getString(R.string.collection_users))
                    .document(currentUser.uid)
                    .get()
                    .addOnSuccessListener { doc ->
                        val user = doc.toObject<UserModel>()!!
                        if (user.subs!!.contains(uid)) {
                            root.button_view_acc_sub.alpha = 0.1F
                            root.button_view_acc_unsub.alpha = 1F
                        }
                    }

            root.button_view_acc_sub.setOnClickListener {
                sub(root.button_view_acc_sub, root.button_view_acc_unsub, uid)
            }

            root.button_view_acc_unsub.setOnClickListener {
                unsub(root.button_view_acc_unsub, root.button_view_acc_sub, uid)
            }
        }
    }

    private fun sub(subButton: Button, unsubButton: Button, uid: String) {
        if (subButton.alpha == 1F) {
            db.collection(getString(R.string.collection_users))
                    .document(FirebaseAuth.getInstance().currentUser!!.uid)
                    .update("subs", FieldValue.arrayUnion(uid))
            subButton.alpha = 0.1F
            unsubButton.alpha = 1F
        }
    }

    private fun unsub(unsubButton: Button, subButton: Button, uid: String) {
        if (unsubButton.alpha == 1F) {
            db.collection(getString(R.string.collection_users))
                    .document(FirebaseAuth.getInstance().currentUser!!.uid)
                    .update("subs", FieldValue.arrayRemove(uid))
            unsubButton.alpha = 0.1F
            subButton.alpha = 1F
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
                        }
            }
        }
    }

    private fun setUpRecyclerView(root: View, uid: String) {
        val query  = db.collection(getString(R.string.collection_recipes))
                .whereEqualTo("uid", uid)
                .orderBy("date", Query.Direction.DESCENDING)
        val firestoreRecyclerOptions = FirestoreRecyclerOptions.Builder<RecipeModel>()
                .setQuery(query, RecipeModel::class.java)
                .build()

        recipeAdapter = HisRecipeAdapter(firestoreRecyclerOptions)

        root.recycler_view_acc.layoutManager = GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false)
        root.recycler_view_acc.adapter = recipeAdapter
    }

    override fun onStart() {
        super.onStart()
        recipeAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        recipeAdapter?.stopListening()
    }
}
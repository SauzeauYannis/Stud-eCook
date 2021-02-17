package com.android.app.studecook.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.app.studecook.R
import com.android.app.studecook.adapter.HisRecipeAdapter
import com.android.app.studecook.ui.recipe.RecipeModel
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_view_account.*
import kotlinx.android.synthetic.main.fragment_view_account.view.*

class AccountViewFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val args by navArgs<AccountViewFragmentArgs>()

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
        val root = inflater.inflate(R.layout.fragment_view_account, container, false)
        val user = args.currentUser
        val uid = args.id

        root.button_view_acc_back.setOnClickListener {
            findNavController().navigate(R.id.action_accountViewFragment_to_navigation_home)
        }

        root.text_view_acc_name.text = user.name

        root.text_view_acc_desc.text = user.description

        // TODO: 17-Feb-21 Faire marcher les boutons abonnements et désabonnements 

        loadImage(user.image, root.image_view_acc)

        setUpRecyclerView(root, uid)

        recipeAdapter!!.startListening()

        return root
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
package com.android.app.studecook.fragment.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.app.studecook.MainActivity
import com.android.app.studecook.R
import com.android.app.studecook.adapter.MyRecipeAdapter
import com.android.app.studecook.model.UserModel
import com.android.app.studecook.SettingsActivity
import com.android.app.studecook.model.RecipeModel
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_account.view.*
import kotlinx.android.synthetic.main.fragment_account.view.settings_button
import kotlinx.android.synthetic.main.fragment_account_anonymous.view.*

class AccountFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    private var recipeAdapter: MyRecipeAdapter? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = if (currentUser == null || currentUser.isAnonymous)
            inflater.inflate(R.layout.fragment_account_anonymous, container, false)
                    else
            inflater.inflate(R.layout.fragment_account, container, false)

        root.settings_button.setOnClickListener {
            val intent = Intent(this.context, SettingsActivity::class.java)
            startActivity(intent)
        }

        if (currentUser == null || currentUser.isAnonymous)
            loadAnonymous(root)
        else
            loadUserData(root)

        return root
    }

    private fun loadAnonymous(root: View) {
        root.text_ano.text = getString(R.string.text_account_anonymous)

        root.button_ano_connection.setOnClickListener {
            AuthUI.getInstance()
                    .signOut(requireContext())
                    .addOnCompleteListener {
                        startActivity(Intent(this.context, MainActivity::class.java))
                    }
        }
    }

    private fun loadUserData(root: View) {
        db.collection(getString(R.string.collection_users))
                .document(currentUser!!.uid)
                .get()
                .addOnSuccessListener { doc ->
                    val user = doc.toObject<UserModel>()!!
                    setUserView(root, user, currentUser.uid)
                }
    }

    private fun setUserView(root: View, user: UserModel, uid: String) {
        root.text_account_name.text = user.name

        root.text_account_description.text = user.description

        root.button_account_subs.setOnClickListener {
            if (user.subs!!.isEmpty())
                Toast.makeText(context, getString(R.string.toast_no_subs), Toast.LENGTH_LONG).show()
            else
                findNavController().navigate(AccountFragmentDirections.actionNavigationAccountToAccountSubsFragment(user.subs!!.toTypedArray()))
        }

        root.button_acount_edit.setOnClickListener {
            val action = AccountFragmentDirections.actionNavigationAccountToAccountEditFragment(user, uid)
            findNavController().navigate(action)
        }

        loadImage(user.image, root.image_my_account)

        setUpRecyclerView(root, uid)

        recipeAdapter?.startListening()
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

        recipeAdapter = MyRecipeAdapter(firestoreRecyclerOptions)

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
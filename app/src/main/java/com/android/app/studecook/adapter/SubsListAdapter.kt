package com.android.app.studecook.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.android.app.studecook.R
import com.android.app.studecook.fragment.account.AccountSubsFragmentDirections
import com.android.app.studecook.model.UserModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage

class SubsListAdapter(
        private val context: Context,
        private val subs: ArrayList<String>)
    : BaseAdapter() {

    private val layoutInflater = LayoutInflater.from(context)
    private val db = FirebaseFirestore.getInstance()

    private class ViewHolder(view: View?) {
        val image = view?.findViewById<ImageView>(R.id.image_acc)
        val name = view?.findViewById<TextView>(R.id.text_name)
        val unsubButton = view?.findViewById<Button>(R.id.button_unsub)
    }

    override fun getCount(): Int {
        return subs.size
    }

    override fun getItem(position: Int): Any {
        return subs[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View? {
        val viewHolder: ViewHolder
        val rowView: View?

        if (view == null) {
            rowView = layoutInflater.inflate(R.layout.layout_list_subs, viewGroup, false)
            viewHolder = ViewHolder(rowView)
            rowView.tag = viewHolder
        } else {
            rowView = view
            viewHolder = rowView.tag as ViewHolder
        }

        val subId = subs[position]

        db.collection("users").document(subId)
                .get()
                .addOnSuccessListener { doc ->
                    loadUser(doc, viewHolder, subId)
                }

        return rowView
    }

    fun viewSub(position: Int, fragment: Fragment) {
        val subId = subs[position]
        db.collection("users").document(subId)
                .get()
                .addOnSuccessListener { doc ->
                    val user = doc.toObject<UserModel>()
                    val action = AccountSubsFragmentDirections.actionAccountSubsFragmentToAccountViewFragment(user!!, subId)
                    NavHostFragment.findNavController(fragment).navigate(action)
                }
    }

    private fun loadUser(doc: DocumentSnapshot, viewHolder: ViewHolder, subId: String) {
        val user = doc.toObject<UserModel>()

        loadImage(user?.image, viewHolder.image!!)

        viewHolder.name!!.text = user?.name

        viewHolder.unsubButton!!.setOnClickListener {
            db.collection("users")
                    .document(FirebaseAuth.getInstance().currentUser!!.uid)
                    .update("subs", FieldValue.arrayRemove(subId))
            subs.remove(subId)
            notifyDataSetChanged()
        }
    }

    private fun loadImage(imagePath: String?, imageView: ImageView) {
        if (imagePath != null) {
            if (imagePath != "") {
                FirebaseStorage.getInstance().reference.child(imagePath)
                        .downloadUrl.addOnSuccessListener { uri ->
                            Glide.with(context)
                                    .load(uri)
                                    .circleCrop()
                                    .into(imageView)
                        }
            }
        }
    }
}
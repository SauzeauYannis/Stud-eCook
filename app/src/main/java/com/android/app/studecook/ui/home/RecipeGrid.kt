package com.android.app.studecook.ui.home

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore


class RecipeGrid(db: FirebaseFirestore, id: String) {

    var image: String? = null
    var text: String? = null

    init {
        db.collection("recipes")
            .document(id)
            .get()
            .addOnSuccessListener {
/*                val im = it.get("images") as ArrayList<*>
                this.image = im[0] as String?*/
                this.image = "images/uDb8plKDuU7WoH4E9beL/1326807047"
                this.text = it.getString("name")
            }
            .addOnFailureListener { exception ->
                Log.w("Error", "Error getting documents.", exception)
            }
    }
}
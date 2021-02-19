package com.android.app.studecook.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class RecipeModel : Parcelable {
    var date: Date? = null
    var diet: Int? = null
    var fav: Int? = null
    var image: String? = null
    var ingredientsName: List<String>? = null
    var ingredientsQuantity: List<String>? = null
    var ingredientsType: List<String>? = null
    var name: String? = null
    var number: Int? = null
    var price: Int? = null
    var steps: List<String>? = null
    var time: Int? = null
    var type: Int? = null
    var uid: String? = null
    var utensils: List<String>? = null
}
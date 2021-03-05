package com.mobile.app.studecook.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class UserModel : Parcelable {
    var description: String? = null
    var favorites: List<String>? = null
    var subs: List<String>? = null
    var image: String? = null
    var name: String? = null
}
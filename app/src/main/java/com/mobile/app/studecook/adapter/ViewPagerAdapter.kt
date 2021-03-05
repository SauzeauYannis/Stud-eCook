package com.mobile.app.studecook.adapter

import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mobile.app.studecook.R
import com.mobile.app.studecook.model.RecipeModel
import com.mobile.app.studecook.model.UserModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject

class ViewPagerAdapter : RecyclerView.Adapter<ViewPagerAdapter.Pager2ViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val collectionReference = db.collection("recipes")
    private val currentUser = FirebaseAuth.getInstance().currentUser

    private var recipeAdapter: RecipeAdapter? = null

    inner class Pager2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemSwipe: SwipeRefreshLayout = itemView.findViewById(R.id.home_swipeRefreshLayout)
        val itemRecycler: RecyclerView = itemView.findViewById(R.id.home_recyclerView)
        val itemFabFilter: FloatingActionButton = itemView.findViewById(R.id.fab_filter)
        val itemText: TextView = itemView.findViewById(R.id.text_slide)
    }

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): Pager2ViewHolder {
        return Pager2ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_swipe_home, parent, false))
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun onBindViewHolder(holder: Pager2ViewHolder, position: Int) {
        setUpRecyclerView(holder, position)
    }

    private fun setUpRecyclerView(holder: Pager2ViewHolder, position: Int) {
        holder.itemSwipe.isRefreshing = true

        when (position) {
            0 -> {
                loadSubsPage(holder)
            }
            1 -> {
                loadHomePage(holder)
            }
            else -> {
                loadFavPage(holder)
            }
        }

        holder.itemSwipe.isRefreshing = false
    }

    private fun loadSubsPage(holder: Pager2ViewHolder) {
        if (currentUser == null || currentUser.isAnonymous) {
            noRecipe(holder)
            holder.itemText.text = holder.itemText.context.getString(R.string.text_forbid_ano)
        } else {
            db.collection("users")
                    .document(currentUser.uid)
                    .get()
                    .addOnSuccessListener { doc ->
                        val user = doc.toObject<UserModel>()!!
                        if (user.subs!!.isEmpty()) {
                            noRecipe(holder)
                        } else {
                            val query = collectionReference.orderBy("date", Query.Direction.DESCENDING)
                                    .whereIn("uid", user.subs!!)
                            val firestoreRecyclerOptions = FirestoreRecyclerOptions.Builder<RecipeModel>()
                                    .setQuery(query, RecipeModel::class.java)
                                    .build()

                            setSwipe(holder, firestoreRecyclerOptions)

                            holder.itemFabFilter.setOnClickListener {
                                generateFilterDialog(holder, collectionReference.whereIn("uid", user.subs!!))
                            }

                            recipeAdapter = HomeRecipeAdapter(firestoreRecyclerOptions)
                            holder.itemRecycler.layoutManager = GridLayoutManager(holder.itemRecycler.context, 2, LinearLayoutManager.VERTICAL, false)
                            holder.itemRecycler.adapter = recipeAdapter
                            recipeAdapter!!.startListening()
                        }
                    }
        }
    }

    private fun loadHomePage(holder: Pager2ViewHolder) {
        val query = collectionReference.orderBy("date", Query.Direction.DESCENDING)
        val firestoreRecyclerOptions = FirestoreRecyclerOptions.Builder<RecipeModel>()
                .setQuery(query, RecipeModel::class.java)
                .build()

        setSwipe(holder, firestoreRecyclerOptions)

        holder.itemFabFilter.setOnClickListener {
            generateFilterDialog(holder, collectionReference)
        }

        recipeAdapter = HomeRecipeAdapter(firestoreRecyclerOptions)
        holder.itemRecycler.layoutManager = GridLayoutManager(holder.itemRecycler.context, 2, LinearLayoutManager.VERTICAL, false)
        holder.itemRecycler.adapter = recipeAdapter
        recipeAdapter!!.startListening()
    }

    private fun loadFavPage(holder: Pager2ViewHolder) {
        if (currentUser == null || currentUser.isAnonymous) {
            noRecipe(holder)
            holder.itemText.text = holder.itemText.context.getString(R.string.text_forbid_ano)
        } else {
            db.collection("users")
                    .document(currentUser.uid)
                    .get()
                    .addOnSuccessListener { doc ->
                        val user = doc.toObject<UserModel>()!!
                        if (user.favorites!!.isEmpty()) {
                            noRecipe(holder)
                        } else {
                            val query = collectionReference.orderBy("date", Query.Direction.DESCENDING)
                                    .whereIn("__name__", user.favorites!!)
                            val firestoreRecyclerOptions = FirestoreRecyclerOptions.Builder<RecipeModel>()
                                    .setQuery(query, RecipeModel::class.java)
                                    .build()

                            holder.itemFabFilter.setOnClickListener {
                                generateFilterDialog(holder, collectionReference.whereIn("__name__", user.favorites!!))
                            }

                            setSwipe(holder, firestoreRecyclerOptions)

                            recipeAdapter = HomeFavRecipeAdapter(firestoreRecyclerOptions)
                            holder.itemRecycler.layoutManager = GridLayoutManager(holder.itemRecycler.context, 2, LinearLayoutManager.VERTICAL, false)
                            holder.itemRecycler.adapter = recipeAdapter
                            recipeAdapter!!.startListening()
                        }
                    }
        }
    }

    private fun generateFilterDialog(holder: Pager2ViewHolder, query: Query) {
        val dialog = Dialog(holder.itemFabFilter.context)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_filter)
        generateCheckButtons(dialog)
        dialog.findViewById<ImageView>(R.id.image_hide_filter).setOnClickListener {
            dialog.hide()
        }
        dialog.findViewById<Button>(R.id.filter_button).setOnClickListener {
            val generatedQuery = generateQuery(dialog, query)
            val firestoreRecyclerOptions = FirestoreRecyclerOptions.Builder<RecipeModel>()
                    .setQuery(generatedQuery, RecipeModel::class.java)
                    .build()
            recipeAdapter!!.updateOptions(firestoreRecyclerOptions)
            dialog.hide()
        }
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()
    }

    private fun generateCheckButtons(dialog: Dialog) {
        val checkPrim: SwitchMaterial = dialog.findViewById(R.id.switchPrimary)
        val radioGroupPrim: RadioGroup = dialog.findViewById(R.id.filter_radioGroup_primary)

        val checkCat: SwitchMaterial = dialog.findViewById(R.id.switchCategory)
        val radioGroupCat: RadioGroup = dialog.findViewById(R.id.filter_radioGroup_category)

        val checkDiet: SwitchMaterial = dialog.findViewById(R.id.switchDiet)
        val radioGroupDiet: RadioGroup = dialog.findViewById(R.id.filter_radioGroup_diet)

        setEnableRadioGroup(checkPrim, radioGroupPrim)
        setEnableRadioGroup(checkCat, radioGroupCat)
        setEnableRadioGroup(checkDiet, radioGroupDiet)
    }

    private fun generateQuery(dialog: Dialog, query: Query): Query {
        var newQuery = query

        val checkPrim: SwitchMaterial = dialog.findViewById(R.id.switchPrimary)
        val checkCat: SwitchMaterial = dialog.findViewById(R.id.switchCategory)
        val checkDiet: SwitchMaterial = dialog.findViewById(R.id.switchDiet)

        val filterPrimaries = dialog.findViewById<RadioGroup>(R.id.filter_radioGroup_primary)

        if(checkPrim.isChecked){
            for (i in 1 until filterPrimaries.childCount) {
                val checkBox: CheckBox = filterPrimaries.getChildAt(i) as CheckBox
                if(checkBox.isChecked){
                    newQuery = when (i) {
                        1 -> newQuery.orderBy("fav", Query.Direction.DESCENDING)
                        2 -> newQuery.orderBy("price", Query.Direction.ASCENDING)
                        else -> newQuery.orderBy("time", Query.Direction.ASCENDING)
                    }
                }
            }
        }

        val filterCategory = dialog.findViewById<RadioGroup>(R.id.filter_radioGroup_category)
        val radioCheckedFilterCategory = filterCategory.checkedRadioButtonId

        if(checkCat.isChecked) {
            newQuery = when (filterCategory.indexOfChild(filterCategory.findViewById(radioCheckedFilterCategory))) {
                1 -> newQuery.whereEqualTo("type", 0)
                2 -> newQuery.whereEqualTo("type", 1)
                3 -> newQuery.whereEqualTo("type", 2)
                else -> newQuery.whereEqualTo("type", 3)
            }
        }

        val filterRegime = dialog.findViewById<RadioGroup>(R.id.filter_radioGroup_diet)
        val radioCheckedFilterRegime = filterRegime.checkedRadioButtonId

        if(checkDiet.isChecked) {
            newQuery = when (filterRegime.indexOfChild(filterRegime.findViewById(radioCheckedFilterRegime))) {
                1 -> newQuery.whereEqualTo("diet", 0)
                2 -> newQuery.whereEqualTo("diet", 1)
                else -> newQuery.whereEqualTo("diet", 2)
            }
        }

        return newQuery
    }

    private fun setEnableRadioGroup(checkPrim:SwitchMaterial ,group: RadioGroup){
        checkPrim.setOnClickListener{
            for (i in 0 until group.childCount) {
                group.getChildAt(i).isEnabled = checkPrim.isChecked
            }
        }
    }

    private fun setSwipe(
            holder: Pager2ViewHolder,
            firestoreRecyclerOptions: FirestoreRecyclerOptions<RecipeModel>
    ) {
        holder.itemSwipe.setOnRefreshListener {
            recipeAdapter!!.updateOptions(firestoreRecyclerOptions)
            Toast.makeText(holder.itemView.context, holder.itemView.context.getString(R.string.toast_up_to_date), Toast.LENGTH_SHORT).show()
            holder.itemSwipe.isRefreshing = false
        }
    }

    private fun noRecipe(holder: Pager2ViewHolder) {
        holder.itemText.visibility = TextView.VISIBLE
        holder.itemFabFilter.visibility = FloatingActionButton.INVISIBLE
    }

    fun startListening() {
        recipeAdapter?.startListening()
    }

    fun stopListening() {
        recipeAdapter?.stopListening()
    }
}
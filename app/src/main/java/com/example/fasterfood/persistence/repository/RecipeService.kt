package com.example.fasterfood.persistence.repository

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.fasterfood.persistence.dao.IRecipeDAO
import com.google.firebase.database.*

// this recipe service implemented the IRecipeDAO so instead of writing edit recipe code in fragment class
// its better to have separate service for them
class RecipeService : IRecipeDAO {
    override fun editRecipe(
        databaseRef: DatabaseReference,
        name: String?,
        description: String?,
        ingredients: String?,
        steps: String?,
        factivity: FragmentActivity,
        view: View
    ) {
        var prefs: SharedPreferences? = factivity.getSharedPreferences(
            "Recipe", Context.MODE_PRIVATE
        )
        val recipeId = prefs!!.getString("recipeId","")

        databaseRef.child(recipeId!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.ref.child("name").setValue(name)
                    dataSnapshot.ref.child("description").setValue(description)
                    dataSnapshot.ref.child("ingredients").setValue(ingredients)

                    val stepsArr: List<String> = steps!!.split(",")
                    var index = 0
                    for (s in stepsArr) {
                        dataSnapshot.ref.child("steps").child(index.toString()).setValue(s.toString().trim())
                        index++
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(view?.context,"Something went wrong!", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
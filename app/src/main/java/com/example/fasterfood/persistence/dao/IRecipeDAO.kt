package com.example.fasterfood.persistence.dao

import android.view.View
import androidx.fragment.app.FragmentActivity
import com.google.firebase.database.DatabaseReference

// this is interface which hold recipe operations and service class will implement these methods
interface IRecipeDAO {
    fun editRecipe( databaseRef: DatabaseReference, name: String ?= null, description: String ?= null, ingredients: String ?= null, steps: String ?= null, factivity: FragmentActivity, view: View)
}
package com.example.fasterfood.fragment.recipe

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.fasterfood.HomeActivity
import com.example.fasterfood.R
import com.example.fasterfood.model.CreateRecipes
import com.example.fasterfood.model.Rating
import com.example.fasterfood.persistence.repository.RecipeService
import com.example.fasterfood.persistence.repository.UserService
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_updateprofile.*
import java.io.File


class EditRecipeFragment : Fragment() {

    // declare all variables
    private lateinit var steps: EditText
    private lateinit var name: EditText
    private lateinit var ingredients: EditText
    private lateinit var description: EditText
    private lateinit var btnUpdate: Button

    private lateinit var databaseRef : DatabaseReference
    private lateinit var recipeRef: DatabaseReference

    private lateinit var imageId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_edit_recipe, container, false)

        //initialize all the variables
        name = view.findViewById<EditText>(R.id.et_update_recipe_name)
        steps = view.findViewById<EditText>(R.id.et_update_recipe_steps)
        ingredients = view.findViewById<EditText>(R.id.et_update_recipe_ingredients)
        description = view.findViewById<EditText>(R.id.et_update_recipe_desc)
        btnUpdate = view.findViewById<Button>(R.id.btn_edit_recipe)

        // this function will get data from firebase and show it on screen
        getRecipeData(view)

        // click event on update button
        btnUpdate.setOnClickListener {
            // this fun is checking is user loggedin or not
            if(isUserLoggedIn()) {
                // if user is logged in allow them to edit the recipe by showing the dialog box
                dialog("Recipe Update", "Are you sure you want to update the recipe?", view.context)
            } else {
                Toast.makeText(view.context, "You must have to login to update the recipe", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    // this fun update the data in firebase calls recipe Service class which is
    // responsible for all the operation related to recipe (single responsible principle)
    private fun updateRecipe(){
        databaseRef = FirebaseDatabase.getInstance().getReference("Recipes/")
        var recipeService: RecipeService = RecipeService()
        view?.let {
            recipeService.editRecipe(databaseRef, name.text.toString(), description.text.toString(),
                ingredients.text.toString(), steps.text.toString(), this.requireActivity(), it
            )
        }
    }

    // this fun will return the specific recipe data which user want to edit based on the id
    private fun getRecipeData(view: View) {
        var prefs: SharedPreferences? = this.activity?.getSharedPreferences(
            "Recipe", Context.MODE_PRIVATE
        )
        val recipeId = prefs!!.getString("recipeId","")

        databaseRef = FirebaseDatabase.getInstance().getReference("Recipes")

        recipeRef = databaseRef.child(recipeId!!)

        recipeRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val stepsText = snapshot.child("steps").value.toString()
                val recipeText = snapshot.child("name").value.toString()
                val ingredientsText = snapshot.child("ingredients").value.toString()
                val descriptionText = snapshot.child("description").value.toString()
                imageId = snapshot.child("imageId").value.toString()

                name.setText(recipeText)
                ingredients.setText(ingredientsText)
                description.setText(descriptionText)
                steps.setText(stepsText.replace("[","").replace("]",""))

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("getRecipeDetails","Firebase real time database error")
            }
        })

    }


    // this is dialog function, it will prompt and will ask user are you sure to update the profile
    fun dialog(title: String, body: String, view: Context){
        val editRecipeBuilder = AlertDialog.Builder(view)
        editRecipeBuilder.setTitle(title)
        editRecipeBuilder.setMessage(body)

        editRecipeBuilder.setPositiveButton("Yes") { dialog, which ->
            updateRecipe()
            Toast.makeText(view,
                "Recipe Updated", Toast.LENGTH_SHORT).show()

            // once update is completed, redirect the user to view my recipe fragment
            val fragment: Fragment = ViewAllMyRecipesFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.nav_host_fragment_content_home, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()

        }

        editRecipeBuilder.setNegativeButton("No") { dialog, which ->
            Toast.makeText(view,
                "Not updated", Toast.LENGTH_SHORT).show()
        }

        editRecipeBuilder.show()
    }

    // this fun is checking is userloggedin or not using shared preferences
    fun isUserLoggedIn(): Boolean {
        val prefs: SharedPreferences? = this.activity?.getSharedPreferences(
            "Auth", Context.MODE_PRIVATE
        )
        val userId = prefs!!.getString("UserId","")

        if(userId.toString() != "") return true

        return false
    }
}
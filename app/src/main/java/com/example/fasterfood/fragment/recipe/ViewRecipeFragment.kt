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
import android.widget.*
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.fasterfood.HomeActivity
import com.example.fasterfood.R
import com.example.fasterfood.persistence.repository.UserService
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_updateprofile.*
import java.io.File

/**
 * A simple [Fragment] subclass.
 * Use the [ViewRecipeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewRecipeFragment : Fragment() {
    private lateinit var databaseRef : DatabaseReference
    private lateinit var stepMap: HashMap<String, String>
    private lateinit var userMap: HashMap<String, String>
    private var ratingNumber: String = ""
    private var numUsersRated: String = ""

    private lateinit var recipeImage: ImageView
    private lateinit var recipeName: TextView
    private lateinit var recipeIngredients: TextView
    private lateinit var recipeDescription: TextView
    private lateinit var recipeInstructions: TextView
    private lateinit var recipeRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_viewrecipe, container, false)
        val btnDelete = view.findViewById<Button>(R.id.recipe_delete)
        val btnEdit = view.findViewById<Button>(R.id.btn_edit_recipe)
        getRecipeDetails(view)

        // Handle the submit button click
        btnDelete.setOnClickListener{
            // Check if user is logged in
            if(isUserLoggedIn()) {
                dialog("Recipe Delete","Are you sure to delete a recipe?",view.context, view)
            } else {
                Toast.makeText(view.context, "You must login to delete the recipe", Toast.LENGTH_SHORT).show()
            }
        }

        btnEdit.setOnClickListener {
            if(isUserLoggedIn()) {
                val fragment: Fragment = EditRecipeFragment()
                val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_host_fragment_content_home, fragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            } else {
                Toast.makeText(view.context, "You must login to delete the recipe", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun deleteRecipe(view: View) {
        // Get Recipe Id from shared preferences
        val prefs = this.activity?.getSharedPreferences(
            "Recipe", Context.MODE_PRIVATE
        )
        val recipeId = prefs!!.getString("recipeId","")

        databaseRef.child(recipeId!!).child("deleted").setValue(true)
    }

    private fun getRecipeDetails(view: View) {
        recipeImage = view.findViewById(R.id.view_recipe)
        recipeName = view.findViewById(R.id.view_recipe_name)
        recipeIngredients = view.findViewById(R.id.ingredients_body)
        recipeDescription = view.findViewById(R.id.description_body)
        recipeInstructions = view.findViewById(R.id.instructions_body)

        stepMap = hashMapOf()
        userMap = hashMapOf()
        databaseRef = FirebaseDatabase.getInstance().getReference("Recipes")

        var prefs: SharedPreferences? = this.activity?.getSharedPreferences(
            "Recipe", Context.MODE_PRIVATE
        )
        val recipeId = prefs!!.getString("recipeId","")
        prefs = this.activity?.getSharedPreferences(
            "Auth", Context.MODE_PRIVATE
        )
        val userId = prefs?.getString("UserId", "")


        recipeRef = databaseRef.child(recipeId!!)

        recipeRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val imageId = snapshot.child("imageId").value.toString()
                val recipeText = snapshot.child("name").value.toString()
                val ingredientsText = snapshot.child("ingredients").value.toString()
                val descriptionText = snapshot.child("description").value.toString()
                ratingNumber = snapshot.child("ratings").child("ratingNumber").value.toString()
                numUsersRated = snapshot.child("ratings").child("totalNumberOfUsersRated").value.toString()
                for(stepSnapshot in snapshot.child("steps").children) {
                    stepMap.put(stepSnapshot.key.toString(), stepSnapshot.value.toString())
                }
                for(userSnapshot in snapshot.child("ratings").child("userId").children) {
                    userMap.put(userSnapshot.key.toString(), userSnapshot.value.toString())
                }

                var instructionText = ""
                var i = 0
                while(i<stepMap.size) {
                    val step = stepMap.get(i.toString())
                    i++
                    instructionText = "$instructionText$i. $step\n"
                }

                val storageRef = FirebaseStorage.getInstance().reference.child("images/ $imageId")

                val localFile = File.createTempFile("tempImage", "jpeg")
                storageRef.getFile(localFile).addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                    recipeImage.setImageBitmap(bitmap)
                }.addOnFailureListener{
                    Log.e("getRecipeDetails", "Could not connect to Firebase")
                }

                recipeName.text = recipeText
                recipeIngredients.text = ingredientsText
                recipeDescription.text = descriptionText
                recipeInstructions.text = instructionText.dropLast(1)

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("getRecipeDetails","Firebase real time database error")
            }
        })


    }

    private fun isUserLoggedIn(): Boolean {
        val prefs: SharedPreferences? = this.activity?.getSharedPreferences(
            "Auth", Context.MODE_PRIVATE
        )
        val userId = prefs!!.getString("UserId","")

        if(userId.toString() != "") return true

        return false
    }

    // this is dialog function, it will prompt and will ask user are you sure to update the profile
    fun dialog(title: String, body: String, view: Context, myview: View){
        val myRecipeBuilder = AlertDialog.Builder(view)
        myRecipeBuilder.setTitle(title)
        myRecipeBuilder.setMessage(body)

        myRecipeBuilder.setPositiveButton("Yes") { dialog, which ->


            deleteRecipe(myview);
            Toast.makeText(view,
                "Recipe Deleted", Toast.LENGTH_SHORT).show()
            var intent = Intent(view.applicationContext, HomeActivity::class.java)
            startActivity(intent)
        }

        myRecipeBuilder.setNegativeButton("No") { dialog, which ->
            Toast.makeText(view,
                "No", Toast.LENGTH_SHORT).show()
        }

        myRecipeBuilder.show()
    }
}
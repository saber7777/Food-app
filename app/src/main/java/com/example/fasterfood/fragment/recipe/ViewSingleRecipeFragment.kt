package com.example.fasterfood.fragment.recipe

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.fasterfood.R
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.File

/**
 * A simple [Fragment] subclass.
 * Use the [ViewSingleRecipeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewSingleRecipeFragment : Fragment() {
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
    private lateinit var recipeRating: RatingBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_viewsinglerecipe, container, false)
        val btnSubmit = view.findViewById<Button>(R.id.rating_submit)
        getRecipeDetails(view)

        // Handle the submit button click
        btnSubmit.setOnClickListener{
            // Check if user is logged in
            if(isUserLoggedIn()) {
                submitRating(view, recipeRating.rating.toDouble())
            } else {
                Toast.makeText(view.context, "You must have to login to rate the recipe", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    /*
     Function to update rating in firebase
     */
    private fun submitRating(view: View, rating: Double) {
        // Get Recipe Id from shared preferences
        var prefs = this.activity?.getSharedPreferences(
            "Recipe", Context.MODE_PRIVATE
        )
        val recipeId = prefs!!.getString("recipeId","")

        // Get User Id from shared preferences
        prefs = this.activity?.getSharedPreferences(
            "Auth", Context.MODE_PRIVATE
        )
        val userId = prefs?.getString("UserId", "")

        // Calculate running average
        val avgRating = ( ratingNumber.toDouble() * numUsersRated.toInt() + rating ) / (numUsersRated.toInt() + 1)

        databaseRef.child(recipeId!!).child("ratings").child("ratingNumber").setValue(avgRating)
        databaseRef.child(recipeId).child("ratings").child("totalNumberOfUsersRated").setValue(numUsersRated.toInt() + 1)
        databaseRef.child(recipeId).child("ratings").child("userId").child(userMap.count().toString()).setValue(userId)

        val submitButton: Button = view.findViewById(R.id.rating_submit)
        submitButton.isEnabled = false
        Toast.makeText(view.context, "Rating submitted!", Toast.LENGTH_SHORT).show()
    }

    private fun getRecipeDetails(view: View) {
        recipeImage = view.findViewById(R.id.view_recipe)
        recipeName = view.findViewById(R.id.view_recipe_name)
        recipeIngredients = view.findViewById(R.id.ingredients_body)
        recipeDescription = view.findViewById(R.id.description_body)
        recipeInstructions = view.findViewById(R.id.instructions_body)
        recipeRating = view.findViewById(R.id.recipe_rating)

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


        val recipeRef: DatabaseReference = databaseRef.child(recipeId!!)

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
                recipeRating.rating = ratingNumber.toFloat()

                if (userMap.containsValue(userId)) {
                    val submitButton: Button = view.findViewById(R.id.rating_submit)
                    submitButton.isEnabled = false
                    return
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("getRecipeDetails","Firebase real time database error")
            }
        })
    }

    fun isUserLoggedIn(): Boolean {
        val prefs: SharedPreferences? = this.activity?.getSharedPreferences(
            "Auth", Context.MODE_PRIVATE
        )
        val userId = prefs!!.getString("UserId","")

        if(userId.toString() != "") return true

        return false
    }
}
package com.example.fasterfood.fragment.recipe

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fasterfood.HomeActivity
import com.example.fasterfood.R
import com.example.fasterfood.adapater.MyRecipeAdapter
import com.example.fasterfood.adapater.RecipeAdapter
import com.example.fasterfood.model.MyRecipe
import com.example.fasterfood.model.Recipes
import com.google.firebase.database.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ViewAllMyRecipesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewAllMyRecipesFragment : Fragment() {

    private lateinit var dbref : DatabaseReference

    private lateinit var recipeRecyclerView : RecyclerView
    private lateinit var recipeArrayList : ArrayList<MyRecipe>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(com.example.fasterfood.R.layout.fragment_view_all_recipes, container, false)
        // this function is checking if user is loggedIn or not
        // if user is not logged in it will redirect to home screen
        // otherwise it will allow the user to update the profile
        if(isUserLoggedIn() == false) {
            Toast.makeText(view.context, "You are not logged In", Toast.LENGTH_SHORT).show()
            var intent = Intent(view.context, HomeActivity::class.java)
            startActivity(intent)
        }

        recipeRecyclerView = view.findViewById(R.id.allRecipes)
        recipeRecyclerView.layoutManager = LinearLayoutManager(view.context)
        recipeRecyclerView.setHasFixedSize(true)
        setHasOptionsMenu(true)

        recipeArrayList = arrayListOf<MyRecipe>()
        getRecipeData(view)

        // Inflate the layout for this fragment
        return view
    }

    private fun getRecipeData(view: View) {
        dbref = FirebaseDatabase.getInstance().getReference("Recipes")

        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    for(recipeSnapshot in snapshot.children) {

                        val recipe = recipeSnapshot.getValue(MyRecipe::class.java)
                        if(recipe?.userId.toString().equals(getUserIdFromSession()) && recipe?.deleted == false) {
                            recipe?.recipeId = recipeSnapshot.key.toString()
                            recipeArrayList.add(recipe!!)
                        }

                    }

                    recipeRecyclerView.adapter = MyRecipeAdapter(recipeArrayList, view.context)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    //this function will check is user loggedin or not in the application
    fun isUserLoggedIn(): Boolean {
        val prefs: SharedPreferences? = this.activity?.getSharedPreferences(
            "Auth", Context.MODE_PRIVATE
        )
        val userId = prefs!!.getString("UserId","")

        if(userId.toString() != "") return true

        return false
    }


    fun getUserIdFromSession(): String {
        val prefs: SharedPreferences? = this.activity?.getSharedPreferences(
            "Auth", Context.MODE_PRIVATE
        )
        val userId = prefs!!.getString("UserId","")

        if(userId.toString() != "") return userId.toString()

        return ""
    }

}
package com.example.fasterfood.fragment.recipe

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fasterfood.R
import com.example.fasterfood.adapater.RecipeAdapter
import com.example.fasterfood.model.Recipes
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 * Use the [ViewAllRecipesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewAllRecipesFragment : Fragment() {

    //firebase database reference
    private lateinit var dbref : DatabaseReference

    private lateinit var recipeRecyclerView : RecyclerView
    //a list to store all recipe info(recipe's name, description and image etc)
    private lateinit var recipeArrayList : ArrayList<Recipes>
    //a temp list to store recipe info(recipe's name, description and image etc)
    private  lateinit var  tempArrayList : ArrayList<Recipes>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(com.example.fasterfood.R.layout.fragment_view_all_recipes, container, false)

        recipeRecyclerView = view.findViewById(R.id.allRecipes)
        recipeRecyclerView.layoutManager = LinearLayoutManager(view.context)
        recipeRecyclerView.setHasFixedSize(true)
        setHasOptionsMenu(true)

        recipeArrayList = arrayListOf<Recipes>()
        tempArrayList = arrayListOf<Recipes>()
        getRecipeData(view)

        // Inflate the layout for this fragment
        return view
    }

    //onCreateOptionsMenu - search function in the top bar and display search result
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater!!.inflate(R.menu.recipe_search, menu)
        val item = menu!!.findItem(R.id.search_action)
        val searchView = item!!.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }
            //filter the list based on user's input(lowercase/uppercase)
            //add search result to the tempArrayList
            override fun onQueryTextChange(newText: String?): Boolean {
                tempArrayList.clear()
                val searchText = newText!!.toLowerCase(Locale.getDefault())
                if (searchText.isNotEmpty()) {
                    recipeArrayList.forEach {
                        if (it.Name!!.toLowerCase(Locale.getDefault()).contains(searchText)){
                            tempArrayList.add(it)
                        }
                    }
                    recipeRecyclerView.adapter!!.notifyDataSetChanged()
                }
                else {
                    tempArrayList.clear()
                    tempArrayList.addAll(recipeArrayList)
                    recipeRecyclerView.adapter!!.notifyDataSetChanged()
                }
                return false
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    //getRecipeData - get recipe's info from firebase
    //display all recipes by adding them to RecyclerView
    private fun getRecipeData(view: View) {
        dbref = FirebaseDatabase.getInstance().getReference("Recipes")

        dbref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    for(recipeSnapshot in snapshot.children) {
                        val recipe = recipeSnapshot.getValue(Recipes::class.java)
                        if(recipe?.deleted != true) {

                            recipe?.recipeId = recipeSnapshot.key.toString()
                            recipeArrayList.add(recipe!!)
                        }
                    }
                    tempArrayList.addAll(recipeArrayList)

                    recipeRecyclerView.adapter = RecipeAdapter(tempArrayList, view.context)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}
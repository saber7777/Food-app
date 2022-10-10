package com.example.fasterfood.adapater

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.fasterfood.R
import com.example.fasterfood.model.MyRecipe
import com.example.fasterfood.model.Recipes
import com.google.firebase.storage.FirebaseStorage
import java.io.File

// this adapter is used for viewmyrecipe based on user id
// it will only show those recipes which have been created by the user
// user can edit or delete them
class MyRecipeAdapter(private val recipeList : ArrayList<MyRecipe>, val context: Context) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeAdapter.RecipeViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_all_recipes, parent, false)
        return RecipeAdapter.RecipeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecipeAdapter.RecipeViewHolder, position: Int) {
        val currentItem = recipeList[position]
        //holder.recipeImage.setImageResource(currentItem.recipeImage)
        holder.recipeName.text = currentItem.Name
        holder.recipeDescription.text = currentItem.Description
        val storageRef = FirebaseStorage.getInstance().reference.child("images/ "+currentItem.ImageId)

        val localFile = File.createTempFile("tempImage", "jpeg")
        storageRef.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            holder.recipeImage.setImageBitmap(bitmap)
        }.addOnFailureListener{

        }

        holder.itemView.setOnClickListener { v ->
            val prefs: SharedPreferences = context.getSharedPreferences(
                "Recipe", Context.MODE_PRIVATE
            )
            prefs.edit().putString("recipeId",currentItem.recipeId.toString()).apply()

            Navigation.findNavController(v).navigate(R.id.nav_view_my_recipe)
        }
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }

    class MyRecipeViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        //val recipeImage : ShapeableImageView = itemView.findViewById(R.id.recipe_image)
        val recipeName : TextView = itemView.findViewById(R.id.recipe_name)
        val recipeDescription: TextView = itemView.findViewById(R.id.recipe_description)

        val recipeImage: ImageView = itemView.findViewById(R.id.recipe_image)
    }
}
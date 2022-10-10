package com.example.fasterfood.fragment.recipe

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.fasterfood.HomeActivity
import com.example.fasterfood.model.CreateRecipes
import com.example.fasterfood.model.Rating
import com.example.fasterfood.model.Steps
import com.example.fasterfood.persistence.repository.UserService
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import java.util.regex.Pattern
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.annotations.NotNull
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_updateprofile.*
import java.util.*
import kotlin.collections.ArrayList

/*
 *  Name of file: CreateRecipeFragment.kt
 *  Author:  Siyuan Zhu
 *  Purpose: This class is like mapper help us in interacting with DB models
 *  Description: This class will behaves like a mapper so it will be easy when
 *               we are working by using getter and setter methods
 */



// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreateRecipeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateRecipeFragment : Fragment() {

    // declare all variables which we will help us to get data from firebase
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private lateinit var storage: FirebaseStorage
//    private lateinit var storageRef: StorageReference
    private lateinit var steps: EditText
    private lateinit var name: EditText
    private lateinit var ingredients: EditText
    private lateinit var description: EditText
    private lateinit var btn_image: Button
    private var imageId: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(com.example.fasterfood.R.layout.fragment_create_recipe, container, false)

        // getting firebase storage instance
        storage = FirebaseStorage.getInstance()

        //check if user is loggedin or not if user is not loggedin
        // redirect them to home screen
        if(isUserLoggedIn() == false) {
            Toast.makeText(view.context, "You are not logged In", Toast.LENGTH_SHORT).show()
            var intent = Intent(view.context, HomeActivity::class.java)
            startActivity(intent)
        }

        // setting up firebase database
        database = FirebaseDatabase.getInstance()
        myRef = database.getReference("Recipes").child(Date().getTime().toString())

        // initialize the variables
        name = view.findViewById<EditText>(com.example.fasterfood.R.id.et_recipt_name)
        ingredients = view.findViewById<EditText>(com.example.fasterfood.R.id.et_recipe_ingredients)
        steps = view.findViewById<EditText>(com.example.fasterfood.R.id.et_recipe_steps)
        description = view.findViewById<EditText>(com.example.fasterfood.R.id.et_recipe_desc)
        btn_image = view.findViewById(com.example.fasterfood.R.id.btn_upload_image)

        val btn_create: Button = view.findViewById<Button>(com.example.fasterfood.R.id.btn_create_recipe)

        val NAME_VAL = Pattern.compile(
            "[a-zA-Z][a-zA-Z ]+"
        )

        // name validation
        name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!NAME_VAL.matcher(name.text.toString()).matches()) {
                    name.setError("Name can only contains alphabets")
                    btn_create.isEnabled = false
                } else {
                    btn_create.isEnabled = true
                }
            }
        })

        // ingredients validation
        ingredients.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!NAME_VAL.matcher(name.text.toString()).matches()) {
                    ingredients.setError("Ingredients can only contains alphabets")
                    btn_create.isEnabled = false
                } else {
                    btn_create.isEnabled = true
                }
            }
        })

        // create button click event handler
        btn_create.setOnClickListener(View.OnClickListener{
            if(ingredients.text.toString() == "" || name.text.toString() == ""
                || steps.text.toString() == "" || description.text.toString() == ""
                || imageId == ""
            ) {
                Toast.makeText(view.context, "Please fill out all the fields", Toast.LENGTH_SHORT).show()
            } else {
                // this is dialog function which will prompt dialog
                dialog("Create Recipe","Are you sure you want to create new recipe?", view.context)

                btn_create.isEnabled = true
            }
        })

        // this handler is use to open separate activity so user can select images
        // from there device and upload to firebase storage
        var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data

                val uri = data?.data
                imageId = UUID.randomUUID().toString()
                val storageRef: StorageReference = storage.getReference().child("images/ "+imageId)

                if (uri != null) {
                    storageRef.putFile(uri).addOnCompleteListener(OnCompleteListener {
                        if(it.isSuccessful) {
                            Toast.makeText(view.context, "Image Upload", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(view.context, "Image Not Upload", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
        }

        btn_image.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
//            startActivity(intent)
//
//            val intent = Intent(this, SomeActivity::class.java)
            resultLauncher.launch(intent)
        })

        // Inflate the layout for this fragment
        return view
    }

    // function to check if user is loggedin to the system or not
    fun isUserLoggedIn(): Boolean {
        val prefs: SharedPreferences? = this.activity?.getSharedPreferences(
            "Auth", Context.MODE_PRIVATE
        )
        val userId = prefs!!.getString("UserId","")

        if(userId.toString() != "") return true

        return false
    }
    // function to get userId from shared preferences
    fun getUserIdFromSession(): String {

        val prefs: SharedPreferences? = this.activity?.getSharedPreferences(
            "Auth", Context.MODE_PRIVATE
        )
        val userId = prefs!!.getString("UserId","")

        if(userId.toString() != "") return userId.toString()

        return ""
    }

    // this is dialog function, it will prompt and will ask user are you sure to update the profile
    fun dialog(title: String, body: String, view: Context){
        val updateProfileBuilder = AlertDialog.Builder(view)
        updateProfileBuilder.setTitle(title)
        updateProfileBuilder.setMessage(body)

        updateProfileBuilder.setPositiveButton("Yes") { dialog, which ->

            val stepsList: MutableList<String> = ArrayList()
            val lstValues: List<String> = steps.text.split(",").map { it -> it.trim() }
            lstValues.forEach { it ->
                stepsList.add(it)
                //Do Something
            }

            val userIdRating: MutableList<String> = ArrayList()
            userIdRating.add(getUserIdFromSession())
            var rating: Rating = Rating(0, 0,userIdRating)

            var createRecipe: CreateRecipes = CreateRecipes(
                getUserIdFromSession(),
                name.text.toString(),
                description.text.toString(),
                ingredients.text.toString(),
                imageId,
                false,
                stepsList,
                rating
            )
            myRef.setValue(createRecipe)

            Toast.makeText(view,
                "Recipe Added!", Toast.LENGTH_SHORT).show()

            name.setText("")
            description.setText("")
            ingredients.setText("")
            steps.setText("")

            var intent = Intent(view, HomeActivity::class.java)
            startActivity(intent)
        }

        updateProfileBuilder.setNegativeButton("No") { dialog, which ->
            name.setText("")
            description.setText("")
            ingredients.setText("")
            steps.setText("")
        }

        updateProfileBuilder.show()
    }
}
package com.example.fasterfood.fragment.updateprofile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*

import android.content.Intent
import android.content.SharedPreferences
import com.example.fasterfood.HomeActivity
import com.example.fasterfood.model.User
import com.example.fasterfood.persistence.repository.UserService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.regex.Pattern
import com.google.firebase.database.DatabaseError

import com.google.firebase.database.DataSnapshot

import com.google.firebase.database.ValueEventListener

import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_updateprofile.*


/*
* This fragment class is responsible when user click on update profile it will show user details
* and allow user to update the profile
* */
class UpdateprofileFragment : Fragment() {

    // these variables are to use firebase db and to update profile
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var uid: String
    private lateinit var user: User
    private lateinit var database: FirebaseDatabase

    // these variables are use to fetch text user enter into the edittext from the screen
    private lateinit var first_name: EditText
    private lateinit var last_name: EditText
    private lateinit var email: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(com.example.fasterfood.R.layout.fragment_updateprofile, container, false)

        // this function is checking if user is loggedIn or not
        // if user is not logged in it will redirect to home screen
        // otherwise it will allow the user to update the profile
        if(isUserLoggedIn() == false) {
            Toast.makeText(view.context, "You are not logged In", Toast.LENGTH_SHORT).show()
            var intent = Intent(view.context, HomeActivity::class.java)
            startActivity(intent)
        }

        //setting up all the variables
        val btn_edit: Button = view.findViewById<Button>(com.example.fasterfood.R.id.btn_Edit)
        first_name = view.findViewById<EditText>(com.example.fasterfood.R.id.et_firstname)
        last_name = view.findViewById<EditText>(com.example.fasterfood.R.id.et_lastname)
        email = view.findViewById<EditText>(com.example.fasterfood.R.id.et_email)
        val spinner: Spinner = view.findViewById(com.example.fasterfood.R.id.spinner_skills)

        //getting the userid from shared preferences so we can update the user in firebase
        val prefs: SharedPreferences? = this.activity?.getSharedPreferences(
            "Auth", Context.MODE_PRIVATE
        )
        var id = prefs?.getString("UserId", "")
        uid = id.toString()

        //getting firebaseauth instance and getting firebase database instance
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().getReference("Users/")
        var userRef: DatabaseReference = databaseRef.child(uid)

        userRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                //I want first_name and all other details of users
                var firstname = snapshot.child("first_name").getValue(String::class.java).toString()
                var lastname = snapshot.child("last_name").getValue(String::class.java).toString()
                var emaill = snapshot.child("email").getValue(String::class.java).toString()
                var isProfessionalChef = snapshot.child("professional").getValue(Boolean::class.java)

                first_name.setText(firstname)
                last_name.setText(lastname)
                email.setText(emaill)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            view.context,
            com.example.fasterfood.R.array.skills_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }



        val NAME_VAL = Pattern.compile(
            "[a-zA-Z][a-zA-Z ]+"
        )

        //validation for firstname it will check when user start entering text in this field
        first_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!NAME_VAL.matcher(first_name.text.toString()).matches()) {
                    first_name.setError("Name can only contains alphabets")
                    btn_edit.isEnabled = false
                } else {
                    btn_edit.isEnabled = true
                }
            }
        })

        //validation for last_name it will check when user start entering text in this field
        last_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!NAME_VAL.matcher(last_name.text.toString()).matches()) {
                    last_name.setError("Last Name can only contains alphabets")
                    btn_edit.isEnabled = false
                } else {
                    btn_edit.isEnabled = true
                }
            }
        })

        //validation for email it will check when user start entering text in this field
        email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
                    email.setError("Invalid Email")
                    btn_edit.isEnabled = false
                } else {
                    btn_edit.isEnabled = true
                }
            }
        })
        btn_edit.setOnClickListener(View.OnClickListener {
            dialog("Update Profile", "Are you sure you want to update ypur profile?", view.context)
        })

        return view
    }

    // this is dialog function, it will prompt and will ask user are you sure to update the profile
    fun dialog(title: String, body: String, view: Context){
        val updateProfileBuilder = AlertDialog.Builder(view)
        updateProfileBuilder.setTitle(title)
        updateProfileBuilder.setMessage(body)

        updateProfileBuilder.setPositiveButton("Yes") { dialog, which ->
            var isProfessional: Boolean
            if (spinner_skills.selectedItem.toString() == "Professional") {
                isProfessional = true
            } else {
                isProfessional = false
            }

            // this will call userservice which is responsible for all the operations
            // related to the user
            val userService: UserService = UserService()
            this.activity?.let {
                userService.updateUserProfile(first_name.text.toString(), last_name.text.toString(), email.text.toString(), isProfessional,
                    it
                )
            }

            Toast.makeText(view,
                "Profile Update", Toast.LENGTH_SHORT).show()
        }

        updateProfileBuilder.setNegativeButton("No") { dialog, which ->
            Toast.makeText(view,
                "No", Toast.LENGTH_SHORT).show()
        }

        updateProfileBuilder.show()
    }

    //this function will check is user loggedin or not in the application
    fun isUserLoggedIn(): Boolean {
        val prefs: SharedPreferences? = this.activity?.getSharedPreferences(
            "Auth", Context.MODE_PRIVATE
        )
        val userId = prefs!!.getString("UserId","")

        Log.e("From Update Profile", userId.toString())
        if(userId.toString() != "") return true

        return false
    }


}
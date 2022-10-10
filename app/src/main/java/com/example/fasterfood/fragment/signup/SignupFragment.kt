package com.example.fasterfood.fragment.signup

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.example.fasterfood.HomeActivity
import com.example.fasterfood.R
import com.example.fasterfood.model.User
import com.example.fasterfood.persistence.repository.AuthService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SignupFragment : Fragment() {
    // declare variables
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // intialize all the variables
        val view: View = inflater.inflate(com.example.fasterfood.R.layout.fragment_signup, container, false)

        val first_name = view.findViewById<EditText>(R.id.FirstName)
        val last_name = view.findViewById<EditText>(R.id.lastName)
        val email = view.findViewById<EditText>(R.id.Email)
        val password = view.findViewById<EditText>(R.id.Password)
        val isProfessional = view.findViewById<CheckBox>(R.id.ProfessionalChef)

        val btn_signup = view.findViewById<Button>(R.id.LoginButton)

        mAuth = FirebaseAuth.getInstance()

        // this is button click when signup is pressed we will validate all the details provided by the user
        btn_signup.setOnClickListener {
            //Validation start
            var isValid: Boolean = true;
            if(first_name.text.trim().length == 0) {
                first_name.setError("First Name is required")
                isValid = false
            }

            if(!first_name.text.matches("^[a-zA-Z]*$".toRegex())){
                first_name.setError("First Name can only contain alphabetical characters")
                isValid = false
            }

            if(last_name.text.trim().length == 0) {
                last_name.setError("Last Name is required")
                isValid = false
            }

            if(!last_name.text.matches("^[a-zA-Z]*$".toRegex())){
                first_name.setError("Last Name can only contain alphabetical characters")
                isValid = false
            }

            if(email.text.trim().length == 0) {
                email.setError("Email is required")
                isValid = false
            }

            if(!email.text.matches("^(.+)@(.+)[.](.+)\$".toRegex())) {
                email.setError("Email must be of format ___@___.__")
                isValid = false
            }


            if(password.text.trim().length == 0) {
                password.setError("Password is required")
                isValid = false

            } else if (password.text.trim().length < 6) {
                password.setError("Password should be more than 6 characters")
                isValid = false
            }
            //Validation end

            if(isValid) {

                // this auth service is responsible for login and registration
                val authService: AuthService = AuthService()
                activity?.let { it1 ->
                    // this is the service class which is responsible for the actual registration and firebase operations
                    authService.register(email.text.toString(), password.text.toString(), first_name.text.toString(),
                        last_name.text.toString(), isProfessional.isChecked, mAuth, it1, view, this.requireActivity(),
                        mAuth.currentUser?.uid.toString()
                    )
                }
            }

        }
        // Inflate the layout for this fragment
        return view
    }

    fun saveUserIdSession(id: String) {
        val sharedPreferences: SharedPreferences? =
            this.activity?.getSharedPreferences("Auth", Context.MODE_PRIVATE)
        var editor: SharedPreferences.Editor = sharedPreferences!!.edit()
        editor.apply{
            putString("UserId", id)
        }.apply()
    }
}



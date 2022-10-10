package com.example.fasterfood.fragment.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.fasterfood.HomeActivity
import com.example.fasterfood.R
import com.example.fasterfood.persistence.repository.AuthService
import com.google.firebase.auth.FirebaseAuth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_login, container, false)

        val email = view.findViewById<EditText>(R.id.Email)
        val password = view.findViewById<EditText>(R.id.Password)
        val btn_login = view.findViewById<Button>(R.id.LoginButton)

        mAuth = FirebaseAuth.getInstance()

        // this is button click event when login is pressed we will validate email and password
        btn_login.setOnClickListener {
            //Validation start
            var isValid: Boolean = true;
            if (email.text.trim().length == 0) {
                email.setError("Email is required")
                isValid = false
            }

            if (password.text.trim().length == 0) {
                password.setError("Password is required")
                isValid = false
            }
            //Validation end

            if(isValid) {

                // this is auth service which is responsible for login and register
                val authService: AuthService = AuthService()
                this.activity?.let { it1 ->
                    // this is the service class which is responsible for the actual login and firebase operations
                    authService.login(email.text.toString(), password.text.toString(), mAuth,
                        it1,view, this.requireActivity(),mAuth.currentUser?.uid.toString())
                }

            }
        }

        // Inflate the layout for this fragment
        return view
    }
}
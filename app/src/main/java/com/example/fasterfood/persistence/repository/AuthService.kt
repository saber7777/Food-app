package com.example.fasterfood.persistence.repository

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import com.example.fasterfood.HomeActivity
import com.example.fasterfood.model.User
import com.example.fasterfood.persistence.dao.IAuthDAO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
// this auth service implemented the IAuthDAO so instead of writing login and register code in fragment class
// its better to have separate service for them
class AuthService : IAuthDAO {
    override fun login(email: String, password: String,
                       mAuth: FirebaseAuth, activity: Activity, view: View,
                       factivity: FragmentActivity, id: String
    ) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(view.context, "Signed In.", Toast.LENGTH_SHORT).show()


                    val prefs: SharedPreferences = factivity.getSharedPreferences(
                        "Auth", Context.MODE_PRIVATE
                    )
                    prefs.edit().putString("UserId", mAuth.currentUser?.uid.toString()).apply();
                    prefs.edit().putString("UserName", mAuth.currentUser?.email.toString()).apply();
                    Log.e("FROM LOGIN", prefs.getString("UserId", "").toString())

                    val intent = Intent(view.context, HomeActivity::class.java)
                    activity.startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(view.context, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun register(
        email: String,
        password: String,
        first_name: String,
        last_name: String,
        isProfessional: Boolean,
        mAuth: FirebaseAuth,
        activity: Activity,
        view: View,
        factivity: FragmentActivity,
        id: String
    ) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    var user1 = User(email,
                        password,
                        first_name,
                        last_name,
                        isProfessional)

                    val database = Firebase.database
                    val user = mAuth.currentUser
                    val myRef = database.getReference("Users/").child(user?.uid.toString())

                    myRef.setValue(user1)
                    val prefs: SharedPreferences = factivity.getSharedPreferences(
                        "Auth", Context.MODE_PRIVATE
                    )
                    prefs.edit().putString("UserId", mAuth.currentUser?.uid.toString()).apply();
                    prefs.edit().putString("UserName", mAuth.currentUser?.email.toString()).apply();
                    Toast.makeText(view.context, "User registered", Toast.LENGTH_SHORT).show()

                    // Move to home screen after signup
                    val intent = Intent(view.context, HomeActivity::class.java)
                    activity.startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(view.context, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
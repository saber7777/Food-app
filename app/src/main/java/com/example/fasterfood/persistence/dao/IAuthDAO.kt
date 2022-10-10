package com.example.fasterfood.persistence.dao

import android.app.Activity
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth

// this is interface which hold auth operations and service class will implement these methods
interface IAuthDAO {
    fun login(email: String, password: String, mAuth: FirebaseAuth,
              activity: Activity, view: View, factivity: FragmentActivity,
              id: String)

    fun register(email: String, password: String, first_name: String, last_name: String,
                 isProfessional: Boolean,mAuth: FirebaseAuth, activity: Activity, view: View,
                 factivity: FragmentActivity, id: String)
}
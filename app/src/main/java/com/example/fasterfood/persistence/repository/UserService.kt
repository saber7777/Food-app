package com.example.fasterfood.persistence.repository

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.example.fasterfood.fragment.updateprofile.UpdateprofileFragment
import com.example.fasterfood.persistence.dao.IUserDAO
import com.google.firebase.database.*

// this user service implemented the IUserDAO so instead of writing updatinguser code in fragment class
// its better to have separate service for them
class UserService : IUserDAO {

    private lateinit var databaseRef: DatabaseReference


    override fun updateUserProfile(
        fname: String,
        lname: String,
        email: String,
        isProfessional: Boolean,
        activity: FragmentActivity
    ) {
        databaseRef = FirebaseDatabase.getInstance().getReference("Users/")

        val pref: SharedPreferences = activity?.getSharedPreferences(
            "Auth", Context.MODE_PRIVATE
        )

//        var pref = activity?.getPreferences(Context.MODE_PRIVATE)
        var id = pref?.getString("UserId", "")
        var uid = id.toString()
        Log.e("FROM UPDATE", uid.toString())

        databaseRef.child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.ref.child("first_name").setValue(fname)
                    dataSnapshot.ref.child("last_name").setValue(lname)
                    dataSnapshot.ref.child("email").setValue(email)
                    dataSnapshot.ref.child("professional").setValue(isProfessional)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("User", databaseError.message)
                }
            })
    }
}
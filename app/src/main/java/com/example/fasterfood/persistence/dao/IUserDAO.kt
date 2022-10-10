package com.example.fasterfood.persistence.dao

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
// this is interface which hold users operations and service class will implement these methods
interface IUserDAO {
    fun updateUserProfile(fname: String, lname: String, email: String, isProfessional: Boolean, activity: FragmentActivity)
}
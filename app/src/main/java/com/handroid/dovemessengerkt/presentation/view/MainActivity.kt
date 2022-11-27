package com.handroid.dovemessengerkt.presentation.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.handroid.dovemessengerkt.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val db = Firebase.firestore
        instance()
    }

    private fun instance(){
        val user:MutableMap<String,Any> = HashMap()
        user["first"] = "Switlana"
        user["last"] = "Korzun"
        user["born"] = 1999
        FirebaseFirestore.getInstance().collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(LOG_TAG,"DocumentSnapshot add with ID: " + documentReference.id)
            }
            .addOnFailureListener { e->
                Log.w(LOG_TAG,"Error adding document", e)
            }
    }

    companion object {
        const val LOG_TAG = "LOG_TAG"
    }
}
package com.handroid.dovemessengerkt.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PackageManagerCompat.LOG_TAG
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.handroid.dovemessengerkt.databinding.ActivityAuthenticationBinding

class AuthenticationActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null
    private var emailEditText: EditText? = null
    private var passwordEditText: EditText? = null
    private var passwordRepeatEditText: EditText? = null
    private var nameEditText: EditText? = null
    private var toggleLoginSingUpTextView: TextView? = null
    private var loginSignUpButton: Button? = null
    private var loginModeActive = false
    private var database: FirebaseDatabase? = null
    private var usersDatabaseReference: DatabaseReference? = null

    private val binding by lazy {
        ActivityAuthenticationBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        usersDatabaseReference = database?.reference?.child("user")
        loginSignUpButton?.setOnClickListener {
            loginSignUpUser(emailEditText?.text.toString().trim { it <= ' ' },
                passwordEditText?.text.toString().trim { it <= ' ' })
        }
        if (auth?.currentUser != null) {
            startActivity(Intent(this@AuthenticationActivity, UserListActivity::class.java))
        }
    }

    private fun loginSignUpUser(email: String, password: String) {
        if (loginModeActive) {
            if (passwordEditText!!.text.toString().trim { it <= ' ' }.length < 7) {
                Toast.makeText(
                    this, "Passwords must be at least 7 characters",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (emailEditText!!.text.toString().trim { it <= ' ' } == "") {
                Toast.makeText(
                    this, "Please input your email",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                auth?.signInWithEmailAndPassword(email, password)
                    ?.addOnCompleteListener(this, object : OnCompleteListener<AuthResult?>() {
                        fun onComplete(task: Task<AuthResult?>) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(LOG_TAG, "signInWithEmail:success")
                                val user: FirebaseUser? = auth?.currentUser
                                val intent = Intent(
                                    this@AuthenticationActivity,
                                    UserListActivity::class.java
                                )
                                intent.putExtra(
                                    "userName",
                                    nameEditText!!.text.toString().trim { it <= ' ' })
                                startActivity(intent)
                                //  updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(LOG_TAG, "signInWithEmail:failure", task.getException())
                                Toast.makeText(
                                    this@AuthenticationActivity, "Authentication failed.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                //  updateUI(null);
                                // ...
                            }

                            // ...
                        }
                    })
            }
        }
    } else
    {
        if (passwordEditText!!.text.toString()
                .trim { it <= ' ' } != passwordRepeatEditText!!.text.toString()
                .trim { it <= ' ' }
        ) {
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
        } else if (passwordEditText!!.text.toString().trim { it <= ' ' }.length < 7) {
            Toast.makeText(
                this, "Passwords must be at least 7 characters",
                Toast.LENGTH_SHORT
            ).show()
        } else if (emailEditText!!.text.toString().trim { it <= ' ' } == "") {
            Toast.makeText(
                this, "Please input your email",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, object : OnCompleteListener<AuthResult?>() {
                    fun onComplete(task: Task<AuthResult?>) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success")
                            val user: FirebaseUser = auth.getCurrentUser()
                            createUser(user)
                            val intent = Intent(
                                this@AuthenticationActivity,
                                UserListActivity::class.java
                            )
                            intent.putExtra(
                                "userName",
                                nameEditText!!.text.toString().trim { it <= ' ' })
                            startActivity(intent)
                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException())
                            Toast.makeText(
                                this@AuthenticationActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                            // updateUI(null);
                        }

                        // ...
                    }
                })
        }
    }
}

private fun createUser(firebaseUser: FirebaseUser) {
    val user = User()
    user.setId(firebaseUser.getUid())
    user.setEmail(firebaseUser.getEmail())
    user.setName(nameEditText!!.text.toString().trim { it <= ' ' })
    usersDatabaseReference.push().setValue(user)
}

fun toggleLoginMode(view: View?) {
    if (loginModeActive) {
        loginModeActive = false
        loginSignUpButton!!.text = "Sign Up"
        toggleLoginSingUpTextView!!.text = "Or, log in"
        // emailEditText.setVisibility(View.VISIBLE);
        passwordRepeatEditText!!.visibility = View.VISIBLE
    } else {
        loginModeActive = true
        loginSignUpButton!!.text = "Log In"
        //  emailEditText.setVisibility(View.GONE);
        toggleLoginSingUpTextView!!.text = "Or, sign up"
        passwordRepeatEditText!!.visibility = View.GONE
    }
}

companion object {
    private const val TAG = "AuthenticationActivity"
}
}
package com.handroid.dovemessengerkt.presentation

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.handroid.dovemessengerkt.R
import com.handroid.dovemessengerkt.domain.User
import com.handroid.dovemessengerkt.UserAdapter
import java.util.ArrayList

class UserListActivity : AppCompatActivity() {
    private var userName: String? = null
    private var auth: FirebaseAuth? = null
    private var usersDatabaseReference: DatabaseReference? = null
    private var usersChildEventListener: ChildEventListener? = null
    private var userArrayList: ArrayList<User>? = null
    private var usersRecyclerView: RecyclerView? = null
    private var userAdapter: UserAdapter? = null
    private var userLayoutManager: RecyclerView.LayoutManager? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)
        val intent: Intent = getIntent()
        if (intent != null) {
            userName = intent.getStringExtra(userName)
        }
        auth = FirebaseAuth.getInstance()
        userArrayList = ArrayList<User>()
        attachUserDatabaseReferenceListener()
        buildRecycleView()
    }

    private fun attachUserDatabaseReferenceListener() {
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("user")
        if (usersChildEventListener == null) {
            usersChildEventListener = object : ChildEventListener() {
                fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val user: User = snapshot.getValue(User::class.java)
                    if (!user.getId().equals(auth?.getCurrentUser()?.getUid())) {
                        user.setAvatarMockUpResource(R.drawable.ic_baseline_person_24)
                        userArrayList!!.add(user)
                        userAdapter?.notifyDataSetChanged()
                    }
                }

                fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                fun onChildRemoved(snapshot: DataSnapshot) {}
                fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                fun onCancelled(error: DatabaseError) {}
            }
            usersDatabaseReference.addChildEventListener(usersChildEventListener)
        }
    }

    private fun buildRecycleView() {
        usersRecyclerView = findViewById<RecyclerView>(R.id.userListRecyclerView)
        usersRecyclerView.setHasFixedSize(true)
        usersRecyclerView.addItemDecoration(
            DividerItemDecoration(
                usersRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        userLayoutManager = LinearLayoutManager(this)
        userAdapter = userArrayList?.let { UserAdapter(it) }
        usersRecyclerView.setLayoutManager(userLayoutManager)
        usersRecyclerView.setAdapter(userAdapter)
        userAdapter.setOnUserClickListener(object : UserAdapter.OnUserClickListener {
            fun onUserClick(position: Int) {
                goToChat(position)
            }
        })
    }

    private fun goToChat(position: Int) {
        val intent = Intent(this@UserListActivity, ChatActivity::class.java)
        intent.putExtra("recipientUserId", userArrayList!![position].id)
        intent.putExtra("recipientUserName", userArrayList!![position].name)
        intent.putExtra("userName", userName)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = getMenuInflater()
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_out -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(
                    Intent(
                        this@UserListActivity,
                        AuthenticationActivity::class.java
                    )
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
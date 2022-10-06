package com.handroid.dovemessengerkt.presentation

import com.handroid.dovemessengerkt.presentation.AuthenticationActivity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.handroid.dovemessengerkt.AwesomeMessage
import com.handroid.dovemessengerkt.AwesomeMessageAdapter
import com.handroid.dovemessengerkt.R
import com.handroid.dovemessengerkt.User


class ChatActivity : AppCompatActivity() {
    private var messageListView: ListView? = null
    private var adapter: AwesomeMessageAdapter? = null
    private var progressBar: ProgressBar? = null
    private var sendImageButton: ImageButton? = null
    private var sendMessageButton: Button? = null
    private var messageEditText: EditText? = null
    private var userName: String? = null
    private var recipientUserId: String? = null
    private var recipientUserName: String? = null
    private var auth: FirebaseAuth? = null
    private var database: FirebaseDatabase? = null
    private var messagesDatabaseReference: DatabaseReference? = null
    private var messagesChildEventListener: ChildEventListener? = null
    private var usersDatabaseReference: DatabaseReference? = null
    private var usersChildEventListener: ChildEventListener? = null
    private var imagesStorage: FirebaseStorage? = null
    private var imagesStorageReference: StorageReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        auth = FirebaseAuth.getInstance()
        val intent = intent
        if (intent != null) {
            userName = intent.getStringExtra("userName")
            recipientUserId = intent.getStringExtra("recipientUserId")
            recipientUserName = intent.getStringExtra("recipientUserName")
        }
        title = "Chat with $recipientUserName"
        database = FirebaseDatabase.getInstance()
        imagesStorage = FirebaseStorage.getInstance()
        messagesDatabaseReference = database.getReference().child("message")
        usersDatabaseReference = database.getReference().child("user")
        imagesStorageReference = imagesStorage.getReference().child("chat_images")
        progressBar = findViewById(R.id.progressBar)
        sendImageButton = findViewById(R.id.sendPhotoButton)
        sendMessageButton = findViewById(R.id.sendMessageButton)
        messageEditText = findViewById(R.id.messageEditText)
        messageListView = findViewById(R.id.messageListView)
        val awesomeMessageList: List<AwesomeMessage> = ArrayList()
        adapter = AwesomeMessageAdapter(
            this,
            R.layout.message_item, awesomeMessageList
        )
        messageListView.setAdapter(adapter)
        progressBar.setVisibility(ProgressBar.INVISIBLE)
        messageEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                sendMessageButton.setEnabled(s.toString().trim { it <= ' ' }.length > 0)
            }

            override fun afterTextChanged(s: Editable) {}
        })
        messageEditText.setFilters(
            arrayOf<InputFilter>(
                LengthFilter(500)
            )
        )
        sendMessageButton.setOnClickListener(View.OnClickListener {
            val message = AwesomeMessage()
            message.setText(messageEditText.getText().toString())
            message.setName(userName)
            message.setSender(auth.getCurrentUser().getUid())
            message.setRecipient(recipientUserId)
            message.setImageUrl(null)
            messagesDatabaseReference.push().setValue(message)
            messageEditText.setText("")
        })
        sendImageButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/jpeg"
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(
                Intent.createChooser(intent, "Choose an image"),
                RC_IMAGE_PICKER
            )
        })
        usersChildEventListener = object : ChildEventListener() {
            fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val user: User = snapshot.getValue(User::class.java)
                if (user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    userName = user.getName()
                }
            }

            fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            fun onChildRemoved(snapshot: DataSnapshot) {}
            fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            fun onCancelled(error: DatabaseError) {}
        }
        usersDatabaseReference.addChildEventListener(usersChildEventListener)
        messagesChildEventListener = object : ChildEventListener() {
            fun onChildAdded(
                snapshot: DataSnapshot,
                previousChildName: String?
            ) {
                val message: AwesomeMessage = snapshot.getValue(AwesomeMessage::class.java)
                if (message.getSender().equals(auth.getCurrentUser().getUid())
                    && message.getRecipient().equals(recipientUserId)
                ) {
                    message.setMine(true)
                    adapter.add(message)
                } else if (message.getRecipient().equals(auth.getCurrentUser().getUid())
                    && message.getSender().equals(recipientUserId)
                ) {
                    message.setMine(false)
                    adapter.add(message)
                }
            }

            fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            fun onChildRemoved(snapshot: DataSnapshot) {}
            fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            fun onCancelled(error: DatabaseError) {}
        }
        messagesDatabaseReference.addChildEventListener(messagesChildEventListener)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_out -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(
                    Intent(
                        this@ChatActivity,
                        AuthenticationActivity::class.java
                    )
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_IMAGE_PICKER && resultCode == RESULT_OK) {
            val selectedImageUri = data!!.data
            val imageReference: StorageReference = imagesStorageReference
                .child(selectedImageUri!!.lastPathSegment)
            //content://images/some_folder/3
            var uploadTask: UploadTask = imageReference.putFile(selectedImageUri)
            uploadTask = imageReference.putFile(selectedImageUri)
            val urlTask: Task<Uri> = uploadTask.continueWithTask(object :
                Continuation<UploadTask.TaskSnapshot?, Task<Uri?>?>() {
                @Throws(Exception::class)
                fun then(task: Task<UploadTask.TaskSnapshot?>): Task<Uri> {
                    if (!task.isSuccessful()) {
                        throw task.getException()
                    }

                    // Continue with the task to get the download URL
                    return imageReference.getDownloadUrl()
                }
            }).addOnCompleteListener(object : OnCompleteListener<Uri?>() {
                fun onComplete(task: Task<Uri?>) {
                    if (task.isSuccessful()) {
                        val downloadUri: Uri = task.getResult()
                        val message = AwesomeMessage()
                        message.setImageUrl(downloadUri.toString())
                        message.setSender(auth.getCurrentUser().getUid())
                        message.setRecipient(recipientUserId)
                        message.setName(userName)
                        messagesDatabaseReference.push().setValue(message)
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            })
        }
    }

    companion object {
        private const val RC_IMAGE_PICKER = 124
    }
}
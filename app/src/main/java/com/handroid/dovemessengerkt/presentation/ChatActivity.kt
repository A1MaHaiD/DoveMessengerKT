package com.handroid.dovemessengerkt.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.handroid.dovemessengerkt.AwesomeMessageAdapter
import com.handroid.dovemessengerkt.R
import com.handroid.dovemessengerkt.databinding.ActivityChatBinding
import com.handroid.dovemessengerkt.domain.AwesomeMessage
import com.handroid.dovemessengerkt.domain.User


class ChatActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityChatBinding.inflate(layoutInflater)
    }
    private var adapter: AwesomeMessageAdapter? = null
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
        setContentView(binding.root)
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
        messagesDatabaseReference = database?.reference?.child("message")
        usersDatabaseReference = database?.reference?.child("user")
        imagesStorageReference = imagesStorage?.reference?.child("chat_images")
        val awesomeMessageList: List<AwesomeMessage> = ArrayList()
        adapter = AwesomeMessageAdapter(
            this,
            R.layout.message_item, awesomeMessageList
        )
        binding.messageListView.setAdapter(adapter)
        binding.progressBar.setVisibility(ProgressBar.INVISIBLE)
        binding.messageEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.sendMessageButton.isEnabled = s.toString().trim { it <= ' ' }.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding.messageEditText.setFilters(
            arrayOf<InputFilter>(
                LengthFilter(500)
            )
        )
        binding.sendMessageButton.setOnClickListener {
            val message = AwesomeMessage(
                text = binding.messageEditText.text.toString(),
                name = userName.toString(),
                sender = auth?.currentUser?.uid.toString(),
                recipient = recipientUserId.toString(),
                imageUrl = "",
                isMine = false
            )
            messagesDatabaseReference?.push()?.setValue(message)
            binding.messageEditText.setText("")
        }
        binding.sendPhotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/jpeg"
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(
                Intent.createChooser(intent, "Choose an image"),
                RC_IMAGE_PICKER
            )
        }
        usersChildEventListener = object : ChildEventListener() {
            fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val user: User = snapshot.getValue(User::class.java)
                if (user.id == FirebaseAuth.getInstance().currentUser?.uid) {
                    userName = user.name
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
                if (message.sender == auth?.currentUser?.uid
                    && message.recipient == recipientUserId
                ) {
                    message.isMine = true
                    adapter.add(message)
                } else if (message.recipient == auth?.currentUser?.uid
                    && message.sender == recipientUserId
                ) {
                    message.isMine = false
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
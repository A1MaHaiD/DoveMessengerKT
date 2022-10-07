package com.handroid.dovemessengerkt

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.handroid.dovemessengerkt.UserAdapter.UserViewHolder
import com.handroid.dovemessengerkt.domain.User
import java.util.ArrayList

class UserAdapter(private val user: ArrayList<User>) : RecyclerView.Adapter<UserViewHolder?>() {
    private var listener: OnUserClickListener? = null
    private val avatarsStorage: FirebaseStorage? = null
    private val avatarsStorageReference: StorageReference? = null

    interface OnUserClickListener {
        fun onUserClick(position: Int)
    }

    fun setOnUserClickListener(listener: OnUserClickListener?) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.user_item, parent, false)
        return UserViewHolder(view, listener)
        //   avatarsStorageReference = avatarsStorage.getReference().child("avatar_images");
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val (name, _, _, avatarMockUpResource, avatarResource) = user[position]
        holder.avatarImageView.setImageResource(avatarMockUpResource)
        holder.avatarImageView.setImageResource(avatarResource)
        holder.userNameTextView.setText(name)
    }

    val itemCount: Int
        get() = user.size

    class UserViewHolder(itemView: View, listener: OnUserClickListener?) :
        RecyclerView.ViewHolder(itemView) {
        var avatarImageView: ImageView
        var userNameTextView: TextView

        init {
            avatarImageView = itemView.findViewById(R.id.avatarImageView)


            // Glide.with(avatarImageView.getContext())
            //        .load(RC_AVATAR_PICKER)
            //      .circleCrop()
            //    .into(avatarImageView);
            userNameTextView = itemView.findViewById<TextView>(R.id.userNameTextView)
            itemView.setOnClickListener {
                if (listener != null) {
                    val position: Int = getAdapterPosition()
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onUserClick(position)
                        avatarImageView.setOnClickListener(object : View.OnClickListener {
                            override fun onClick(viewImage: View) {
                                val intent = Intent(Intent.ACTION_GET_CONTENT)
                                intent.setType("image/*")
                                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
                                startActivityForResult(
                                    Intent.createChooser(intent, "Choose an avatar"),
                                    RC_AVATAR_PICKER
                                )
                            }

                            private fun startActivityForResult(
                                choose_an_avatar: Intent,
                                rcAvatarPicker: Int
                            ) {
                            }
                        })
                    }
                }
            }
        }
    }

    companion object {
        private const val RC_AVATAR_PICKER = 125
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}
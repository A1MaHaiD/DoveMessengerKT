package com.handroid.dovemessengerkt

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class AwesomeMessageAdapter(
    context: Activity,
    resource: Int,
    private val messages: List<AwesomeMessage>
) : ArrayAdapter<AwesomeMessage?>(context, resource, messages) {
    private val activity: Activity

    init {
        activity = context
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val viewHolder: ViewHolder
        val layoutInflater: LayoutInflater =
            activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val (text, name, _, _, imageUrl) = getItem(position)
        var layoutResource = 0
        val viewType = getItemViewType(position)
        if (viewType == 0) {
            layoutResource = R.layout.user_message_item
        } else {
            layoutResource = R.layout.recipient_message_item
        }
        if (convertView != null) {
            viewHolder = convertView.tag as ViewHolder
        } else {
            convertView = layoutInflater.inflate(layoutResource, parent, false)
            viewHolder = ViewHolder(convertView)
            convertView.tag = viewHolder
        }
        val isText = imageUrl == null
        if (isText) {
            viewHolder.messageTextView.setVisibility(View.VISIBLE)
            viewHolder.photoImageView.visibility = View.GONE
            viewHolder.userNameTextView.setVisibility(View.VISIBLE)
            viewHolder.userNameTextView.setText(name)
            viewHolder.messageTextView.setText(text)
        } else {
            viewHolder.messageTextView.setVisibility(View.GONE)
            viewHolder.photoImageView.visibility = View.VISIBLE
            viewHolder.userNameTextView.setVisibility(View.VISIBLE)
            viewHolder.userNameTextView.setText(name)
            Glide.with(viewHolder.photoImageView.context)
                .load(imageUrl)
                .into(viewHolder.photoImageView)
        }
        return convertView
    }

    override fun getItemViewType(position: Int): Int {
        val flag: Int
        val (_, _, _, _, _, isMine) = messages[position]
        flag = if (isMine) {
            0
        } else 1
        return flag
    }

    val viewTypeCount: Int
        get() = 2

    private inner class ViewHolder(view: View) {
        private val photoImageView: ImageView
        private val messageTextView: TextView
        private val userNameTextView: TextView

        init {
            photoImageView = view.findViewById(R.id.photoImageView)
            messageTextView = view.findViewById<TextView>(R.id.messageTextView)
            userNameTextView = view.findViewById<TextView>(R.id.userNameTextView)
        }
    }
}
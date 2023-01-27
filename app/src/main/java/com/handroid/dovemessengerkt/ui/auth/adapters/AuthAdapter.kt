package com.handroid.dovemessengerkt.ui.auth.adapters

import  android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.handroid.dovemessengerkt.data.model.Note
import com.handroid.dovemessengerkt.databinding.ItemNoteLayoutBinding
import com.handroid.dovemessengerkt.util.addChip
import com.handroid.dovemessengerkt.util.hide
import java.text.SimpleDateFormat

class AuthAdapter(
    val onItemClicked: (Int, Note) -> Unit
) : RecyclerView.Adapter<AuthAdapter.MyVH>() {

    val sdf = SimpleDateFormat("dd MM yyyy")
    private var list: MutableList<Note> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVH {
        val itemView =
            ItemNoteLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyVH(itemView)
    }

    override fun onBindViewHolder(holder: MyVH, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(list: MutableList<Note>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        list.removeAt(position)
        notifyItemChanged(position)
    }

    inner class MyVH(val binding: ItemNoteLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Note) {
            with(binding) {
                etTitle.setText(item.title)
                tvDate.setText(sdf.format(item.date))
                cgTags.apply {
                    if (item.tags.isNullOrEmpty()) {
                        hide()
                    } else {
                        removeAllViews()
                        if (item.tags.size > 2) {
                            item.tags.subList(0, 2).forEach { tag -> addChip(tag) }
                            addChip("+${item.tags.size - 2}")
                        } else {
                            item.tags.forEach { tag -> addChip(tag) }
                        }
                    }
                }
                tvDesc.apply {
                    if (item.description.length > 120) {
                        text = "${item.description.substring(0, 120)}..."
                    } else {
                        text = item.description
                    }
                }
                binding.itemLayout.setOnClickListener {
                    onItemClicked.invoke(adapterPosition, item)
                }
            }
        }
    }
}

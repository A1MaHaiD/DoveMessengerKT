package com.handroid.dovemessengerkt.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.handroid.dovemessengerkt.data.model.Note
import com.handroid.dovemessengerkt.databinding.ItemNoteLayoutBinding

class NoteListingAdapter(
    val onItemClicked: (Int, Note) -> Unit,
    val onEditClicked: (Int, Note) -> Unit,
    val onDeleteClicked: (Int, Note) -> Unit
) : RecyclerView.Adapter<NoteListingAdapter.MyVH>() {

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
                noteIdValue.setText(item.id)
                msg.setText(item.text)
                edit.setOnClickListener {
                    onEditClicked.invoke(adapterPosition, item)
                }
                delete.setOnClickListener {
                    onDeleteClicked.invoke(adapterPosition, item)
                }
                itemLayout.setOnClickListener {
                    onItemClicked.invoke(adapterPosition, item)
                }
            }
        }
    }
}

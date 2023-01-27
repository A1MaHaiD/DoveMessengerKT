package com.handroid.dovemessengerkt.ui.note.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.handroid.dovemessengerkt.R
import com.handroid.dovemessengerkt.data.model.Note
import com.handroid.dovemessengerkt.databinding.FragmentNoteDetailBinding
import com.handroid.dovemessengerkt.ui.note.viewmodel.NoteViewModel
import com.handroid.dovemessengerkt.util.*
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class NoteDetailFragment : Fragment() {

    private var _binding: FragmentNoteDetailBinding? = null
    private val binding: FragmentNoteDetailBinding
        get() = _binding ?: throw RuntimeException("NoteDetailFragment == null")

    private val viewModel: NoteViewModel by viewModels()
    var objNote: Note? = null
    var tagsList: MutableList<String> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
        observer()
    }

    private fun observer() {
        viewModel.addNote.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.pbNote.show()
                }
                is UiState.Failure -> {
                    binding.pbNote.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.pbNote.hide()
                    toast(state.data.second)
                    objNote = state.data.first
                    isMakeEnableUI(false)
                    binding.ivDone.hide()
                    binding.ivDelete.show()
                    binding.ivEdit.show()
                }
            }
        }
        viewModel.updateNote.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.pbNote.show()
                }
                is UiState.Failure -> {
                    binding.pbNote.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.pbNote.hide()
                    toast(state.data)
                    binding.ivEdit.show()
                    isMakeEnableUI(false)
                }
            }
        }
        viewModel.deleteNote.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.pbNote.show()
                }
                is UiState.Failure -> {
                    binding.pbNote.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.pbNote.hide()
                    toast(state.data)
                    findNavController().navigateUp()
                }
            }
        }
    }


    private fun updateUI() {
        val sdf = SimpleDateFormat("dd MM yyyy . hh:mm a")
        with(binding) {
            objNote =
                arguments?.getParcelable("note")//If user click on the note from listing screen then in that case we pass the object
            binding.cgTags.layoutParams.height = 40.dpToPx
            objNote?.let { note ->
                etTitle.setText(note.title)
                tvDate.setText(sdf.format(note.date))
                tagsList = note.tags
                addTags(tagsList)
                etDescription.setText(note.description)
                ivDone.hide()
                ivEdit.show()
                ivDelete.show()
                isMakeEnableUI(false)
            } ?: run { //If no note exists then It's mean user want to create a new note
                etTitle.setText("")
                tvDate.setText(sdf.format(Date()))
                etDescription.setText("")
                ivDone.hide()
                ivEdit.hide()
                ivDelete.hide()
                isMakeEnableUI(true)
            }
            ivBack.setOnClickListener {
                findNavController().navigateUp()
            }
            etTitle.setOnClickListener {
                isMakeEnableUI(true)
            }
            etDescription.setOnClickListener {
                isMakeEnableUI(true)
            }
            ivDelete.setOnClickListener {
                objNote?.let { viewModel.deleteNote(it) }
            }
            ivAddTag.setOnClickListener {
                showAddTagDialog()
            }
            ivEdit.setOnClickListener {
                isMakeEnableUI(true)
                ivDone.show()
                ivEdit.hide()
                etTitle.requestFocus()
            }
            ivDone.setOnClickListener {
                if (validation()) {
                    if (objNote == null) {
                        viewModel.addNote(getNote())
                    } else {
                        viewModel.updateNote(getNote())
                    }
                }
            }
            etTitle.doAfterTextChanged {
                ivDone.show()
                ivEdit.hide()
            }
            etDescription.doAfterTextChanged {
                ivDone.show()
                ivEdit.hide()
            }
        }
    }

    private fun getNote(): Note {
        return Note(
            id = objNote?.id ?: "",
            title = binding.etTitle.text.toString(),
            description = binding.etDescription.text.toString(),
            tags = tagsList,
            date = Date()
        )
    }


    private fun showAddTagDialog() {
        val dialog = requireContext().createDialog(R.layout.add_tag_dialog, true)
        val button = dialog.findViewById<MaterialButton>(R.id.tag_dialog_add)
        val editText = dialog.findViewById<EditText>(R.id.tag_dialog_et)
        button.setOnClickListener {
            if (editText.text.toString().isNullOrEmpty()) {
                toast("Enter text")
            } else {
                val text = editText.text.toString()
                tagsList.add(text)
                binding.cgTags.apply {
                    addChip(text, true) {
                        tagsList.forEachIndexed { index, tag ->
                            if (text.equals(tag)) {
                                tagsList.removeAt(index)
                                binding.cgTags.removeViewAt(index)
                            }
                        }
                        if (tagsList.size == 0) {
                            layoutParams.height = 40.dpToPx
                        }
                    }
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                }
                binding.ivDone.show()
                binding.ivEdit.hide()
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    //Make Ui enable when user want to update or create note otherwise disable Ui if user want to view note
    private fun isMakeEnableUI(isDisable: Boolean = false) {
        with(binding) {
            etTitle.isEnabled = isDisable
            tvDate.isEnabled = isDisable
            cgTags.isEnabled = isDisable
            ivAddTag.isEnabled = isDisable
            etDescription.isEnabled = isDisable
        }
    }

    private fun addTags(note: MutableList<String>) {
        if (note.size > 0) {
            binding.cgTags.apply {
                removeAllViews()
                note.forEachIndexed { index, tag ->
                    addChip(tag, true) {
                        if (isEnabled) {
                            note.removeAt(index)
                            this.removeViewAt(index)
                        }
                    }
                }
            }
        }
    }

    private fun validation(): Boolean {
        var isValid = true
        if (binding.etTitle.text.toString().isNullOrEmpty()) {
            isValid = false
            toast("Title missing")
        }
        if (binding.etDescription.text.toString().isNullOrEmpty()) {
            isValid = false
            toast("Description missing")
        }
        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val LOG_TAG = "NoteDetailFragment"
    }
}
package com.handroid.dovemessengerkt.presentation.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.handroid.dovemessengerkt.data.model.Note
import com.handroid.dovemessengerkt.databinding.FragmentNoteDetailBinding
import com.handroid.dovemessengerkt.presentation.viewmodel.NoteViewModel
import com.handroid.dovemessengerkt.util.UiState
import com.handroid.dovemessengerkt.util.hide
import com.handroid.dovemessengerkt.util.show
import com.handroid.dovemessengerkt.util.toast
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class NoteDetailFragment : Fragment() {

    private var _binding: FragmentNoteDetailBinding? = null
    private val binding: FragmentNoteDetailBinding
        get() = _binding ?: throw RuntimeException("NoteDetailFragment == null")

    private val viewModel: NoteViewModel by viewModels()
    var isEdit = false
    var objNote: Note? = null

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
        binding.btnMsg.setOnClickListener {
            if (isEdit) {
                updateNote()
            } else {
                createNote()
            }
        }
    }

    private fun createNote() {
        if (validation()) {
            viewModel.addNote(
                Note(
                    id = "",
                    text = binding.edNote.text.toString(),
                    date = Date()
                )
            )
        }
        Log.d(LOG_TAG, "createNote")
        viewModel.addNote.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.btnProgressBar.show()
                    binding.btnMsg.text = ""
                }
                is UiState.Failure -> {
                    binding.btnProgressBar.hide()
                    binding.btnMsg.text = "Create"
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.btnProgressBar.hide()
                    binding.btnMsg.text = "Create"
                    toast(state.data)
                }
            }
        }
    }

    private fun updateNote() {
        if (validation()) {
            viewModel.updateNote(
                Note(
                    id = objNote?.id ?: "",
                    text = binding.edNote.text.toString(),
                    date = Date()
                )
            )
        }
        Log.d(LOG_TAG, "updateNote")
        viewModel.updateNote.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.btnProgressBar.show()
                    binding.btnMsg.text = ""
                }
                is UiState.Failure -> {
                    binding.btnProgressBar.hide()
                    binding.btnMsg.text = "Update"
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.btnProgressBar.hide()
                    binding.btnMsg.text = "Update"
                    toast(state.data)
                }
            }
        }
    }

    private fun updateUI() {
        val type = arguments?.getString("type", null)
        type?.let {
            when (it) {
                "view" -> {
                    isEdit = false
                    binding.edNote.isEnabled = false
                    objNote = arguments?.getParcelable("note")
                    binding.edNote.setText(objNote?.text)
                    binding.btnMsg.hide()
                }
                "create" -> {
                    isEdit = false
                    binding.btnMsg.setText("Create")
                }
                "edit" -> {
                    isEdit = true
                    objNote = arguments?.getParcelable("note")
                    binding.edNote.setText(objNote?.text)
                    binding.btnMsg.setText("Update")
                }
            }
        }
    }

    private fun validation(): Boolean {
        var isValid = true
        if (binding.edNote.text.toString().isEmpty()) {
            isValid = false
            toast("Enter message")
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
package com.handroid.dovemessengerkt.presentation.view.fragment

import android.os.Bundle
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

    val viewModel: NoteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnUpdate.setOnClickListener {
            if (validation()) {
                viewModel.getAddNote(
                    Note(
                        id = "",
                        text = binding.edNote.text.toString(),
                        date = Date()
                    )
                )
            }
        }
        viewModel.addNote.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.btnProgressBar.show()
                }
                is UiState.Failure -> {
                    binding.btnProgressBar.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.btnProgressBar.hide()
                    toast("Note has been created successfully")
                }
            }
        }

    }

    private fun validation(): Boolean {
        var isValid = true
        if (binding.edNote.text.toString().isNullOrEmpty()) {
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
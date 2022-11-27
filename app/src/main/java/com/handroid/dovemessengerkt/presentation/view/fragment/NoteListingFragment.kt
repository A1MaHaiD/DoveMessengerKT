package com.handroid.dovemessengerkt.presentation.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.handroid.dovemessengerkt.databinding.FragmentNoteListingBinding
import com.handroid.dovemessengerkt.presentation.viewmodel.NoteViewModel

class NoteListingFragment : Fragment() {

    private val binding by lazy {
        FragmentNoteListingBinding.inflate(layoutInflater)
    }
    private val viewModel: NoteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewModel) {
            getNotes()
            note.observe(viewLifecycleOwner) {

            }
        }

    }

    companion object {
        const val LOG_TAG = "NoteListingFragment"
    }
}
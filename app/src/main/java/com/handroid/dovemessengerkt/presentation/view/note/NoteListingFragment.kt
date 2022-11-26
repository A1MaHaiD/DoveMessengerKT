package com.handroid.dovemessengerkt.presentation.view.note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.handroid.dovemessengerkt.databinding.FragmentNoteListingBinding

class NoteListingFragment : Fragment() {

    private val binding by lazy {
        FragmentNoteListingBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        const val LOG_TAG = "NoteListingFragment"
    }
}
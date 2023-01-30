package com.handroid.dovemessengerkt.ui.note.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.handroid.dovemessengerkt.R
import com.handroid.dovemessengerkt.databinding.FragmentNoteListingBinding
import com.handroid.dovemessengerkt.ui.auth.viewmodel.AuthViewModel
import com.handroid.dovemessengerkt.ui.note.adapter.NoteListingAdapter
import com.handroid.dovemessengerkt.ui.note.viewmodel.NoteViewModel
import com.handroid.dovemessengerkt.util.UiState
import com.handroid.dovemessengerkt.util.hide
import com.handroid.dovemessengerkt.util.show
import com.handroid.dovemessengerkt.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteListingFragment : Fragment() {

    private var _binding: FragmentNoteListingBinding? = null
    private val binding: FragmentNoteListingBinding
        get() = _binding ?: throw RuntimeException("NoteListingFragment == null")

    private val noteViewModel: NoteViewModel by viewModels()

    private val authViewModel: AuthViewModel by viewModels()

    val adapter by lazy {
        NoteListingAdapter(
            onItemClicked = { pos, item ->
                findNavController().navigate(R.id.action_noteListingFragment_to_noteDetailFragment,
                    Bundle().apply
                    {
                        putParcelable("note", item)
                    })
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteListingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        Log.d(LOG_TAG, "onViewCreated")
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        with(binding) {
            recyclerView.layoutManager = staggeredGridLayoutManager
            recyclerView.adapter = adapter
            btn.setOnClickListener {
                findNavController().navigate(R.id.action_noteListingFragment_to_noteDetailFragment)
            }
            ivLogout.setOnClickListener {
                authViewModel.logout {
                    findNavController().navigate(R.id.action_noteListingFragment_to_loginFragment)
                }
            }
        }
        noteViewModel.getNotes()
    }

    private fun observer() {
        with(binding){
            noteViewModel.note.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> {
                        progressBar.show()
                    }
                    is UiState.Failure -> {
                        progressBar.hide()
                        toast(state.error)
                    }
                    is UiState.Success -> {
                        progressBar.hide()
                        adapter.updateList(state.data.toMutableList())
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val LOG_TAG = "NoteListingFragment"
    }
}
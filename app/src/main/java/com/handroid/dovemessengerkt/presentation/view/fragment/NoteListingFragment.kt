package com.handroid.dovemessengerkt.presentation.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.handroid.dovemessengerkt.R
import com.handroid.dovemessengerkt.databinding.FragmentNoteListingBinding
import com.handroid.dovemessengerkt.presentation.adapters.NoteListingAdapter
import com.handroid.dovemessengerkt.presentation.viewmodel.NoteViewModel
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

    private val viewModel: NoteViewModel by viewModels()
    var deletePosition: Int = -1

    val adapter by lazy {
        NoteListingAdapter(
            onItemClicked = { pos, item ->
                findNavController().navigate(R.id.action_noteListingFragment_to_noteDetailFragment,
                    Bundle().apply
                    {
                        putString("type", "view")
                        putParcelable("note", item)
                    })
            },
            onEditClicked = { pos, item ->
                findNavController().navigate(R.id.action_noteListingFragment_to_noteDetailFragment,
                    Bundle().apply
                    {
                        putString("type", "edit")
                        putParcelable("note", item)
                    })
            },
            onDeleteClicked = { pos, item ->
                deletePosition = pos
                viewModel.deleteNote(item)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteListingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(LOG_TAG, "onViewCreated")
        with(binding){
            recyclerView.adapter = adapter
            recyclerView.itemAnimator = null
            btn.setOnClickListener {
                findNavController().navigate(R.id.action_noteListingFragment_to_noteDetailFragment,
                    Bundle().apply
                    {
                        putString("type", "create")
                    })
            }
            with(viewModel) {
                getNotes()
                note.observe(viewLifecycleOwner) { state ->
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
                deleteNote.observe(viewLifecycleOwner) { state ->
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
                            toast(state.data)
                            adapter.removeItem(deletePosition)
                        }
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
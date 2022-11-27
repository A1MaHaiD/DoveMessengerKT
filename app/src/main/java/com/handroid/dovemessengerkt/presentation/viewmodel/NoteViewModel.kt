package com.handroid.dovemessengerkt.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.handroid.dovemessengerkt.data.model.Note
import com.handroid.dovemessengerkt.data.repository.NoteRepository

class NoteViewModel(
    val repository: NoteRepository
) : ViewModel() {

    private val _notes = MutableLiveData<List<Note>>()
    val note: LiveData<List<Note>>
        get() = _notes

    fun getNotes() {
        _notes.value = repository.getNotes()
    }
}
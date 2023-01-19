package com.handroid.dovemessengerkt.data.repository

import com.handroid.dovemessengerkt.data.model.Note
import com.handroid.dovemessengerkt.util.UiState

interface NoteRepository {
    fun getNotes(result: (UiState<List<Note>>) -> Unit)
    fun addNote(note: Note, result: (UiState<Pair<Note, String>>) -> Unit)
    fun updateNote(note: Note, result: (UiState<String>) -> Unit)
    fun deleteNote(note: Note, result: (UiState<String>) -> Unit)
}
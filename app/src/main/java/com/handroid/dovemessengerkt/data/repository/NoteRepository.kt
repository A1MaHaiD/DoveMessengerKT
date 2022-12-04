package com.handroid.dovemessengerkt.data.repository

import com.handroid.dovemessengerkt.data.model.Note
import com.handroid.dovemessengerkt.util.UiState

interface NoteRepository {

    fun getNotes(): UiState<List<Note>>
}
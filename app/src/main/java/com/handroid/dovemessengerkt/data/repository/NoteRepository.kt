package com.handroid.dovemessengerkt.data.repository

import com.handroid.dovemessengerkt.data.model.Note

interface NoteRepository {

    fun getNotes(): List<Note>
}
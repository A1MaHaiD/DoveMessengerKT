package com.handroid.dovemessengerkt.data.repository

import com.handroid.dovemessengerkt.data.model.Note

class NoteRepositoryImpl : NoteRepository {

    override fun getNotes(): List<Note> {
        //We will get data from firebase
        return arrayListOf()
    }
}
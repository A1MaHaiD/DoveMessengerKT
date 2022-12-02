package com.handroid.dovemessengerkt.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.handroid.dovemessengerkt.data.model.Note
import java.util.*

class NoteRepositoryImpl(
    val database: FirebaseFirestore
) : NoteRepository {

    override fun getNotes(): List<Note> {
        //We will get data from firebase
        return arrayListOf(
            Note(
                id = "abcde",
                text = "Note 1",
                date = Date()
            ),
            Note(
                id = "edcba",
                text = "Note 2",
                date = Date()
            ),
            Note(
                id = "fghij",
                text = "Note 3",
                date = Date()
            )
        )
    }
}
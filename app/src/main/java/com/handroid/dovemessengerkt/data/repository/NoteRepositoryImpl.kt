package com.handroid.dovemessengerkt.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.handroid.dovemessengerkt.data.model.Note
import com.handroid.dovemessengerkt.util.FireStoreTables
import com.handroid.dovemessengerkt.util.UiState

class NoteRepositoryImpl(
    val database: FirebaseFirestore
) : NoteRepository {

    override fun getNotes(result: (UiState<List<Note>>) -> Unit) {
        database.collection(FireStoreTables.NOTE)
            .get()
            .addOnSuccessListener {
                val notes = arrayListOf<Note>()
                for (document in it) {
                    val note = document.toObject(Note::class.java)
                    notes.add(note)
                }
                result.invoke(
                    UiState.Success(notes)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
//        //We will get data from firebase
//        val data = listOf<Note>()
//
//        if (data.isNullOrEmpty()) {
//            return UiState.Failure("Data is Empty")
//        } else {
//            return UiState.Success(data)
//        }
    }

    override fun addNote(note: Note, result: (UiState<String>) -> Unit) {
        database.collection(FireStoreTables.NOTE)
            .add(note)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success(it.id)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }
}
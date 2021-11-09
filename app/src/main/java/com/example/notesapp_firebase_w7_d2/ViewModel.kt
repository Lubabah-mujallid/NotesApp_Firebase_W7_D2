package com.example.notesapp_firebase_w7_d2

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewModel(application: Application) : AndroidViewModel(application) {
    private var list = MutableLiveData<List<Note>>()
    private val database = Firebase.firestore

    init {
        getData()
    }

    private fun getData() {
        val tempNotes = ArrayList<Note>()
        database.collection("notes").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("TAG VIEW MODEL", "${document.id} => ${document.data}")
                    document.data.map { (key, value) -> tempNotes.add(Note(document.id, value.toString())) }
                }
                list.postValue(tempNotes)
            }
            .addOnFailureListener { exception -> Log.w("TAG VIEW MODEL", "Error getting documents.", exception) }
    }

    fun getNotes(): LiveData<List<Note>> {
        return list
    }

    fun addNote(text: String) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("TAG VIEW MODEL", "note is: $text")
            database.collection("notes").add(hashMapOf("text" to text))
            getData()
        }
        Log.d("TAG VIEW MODEL", "new data added")
    }

    fun updateNote(note: Note, nNote: String) {
        Log.d("TAG VIEW MODEL", "INSIDE UPDATE")
        CoroutineScope(Dispatchers.IO).launch {
            database.collection("notes").get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        Log.d("TAG VIEW MODEL", "DB: ${document.id} =? LOCAL: ${note.id}")
                        if (document.id == note.id)
                            database.collection("notes").document(note.id).update("text", nNote)
                    }
                    getData()
                }
                .addOnFailureListener { exception -> Log.w("TAG VIEW MODEL", "Error getting documents.", exception) }
        }
    }

    fun deleteNote(note: Note){
        Log.d("TAG VIEW MODEL", "INSIDE delete")
        CoroutineScope(Dispatchers.IO).launch {
            database.collection("notes").get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        Log.d("TAG VIEW MODEL", "DB: ${document.id} =? LOCAL: ${note.id}")
                        if (document.id == note.id)
                            database.collection("notes").document(note.id).delete()
                    }
                    getData()
                }
                .addOnFailureListener { exception -> Log.w("TAG VIEW MODEL", "Error getting documents.", exception) }

        }
    }

}
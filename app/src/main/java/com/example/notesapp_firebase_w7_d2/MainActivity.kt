package com.example.notesapp_firebase_w7_d2

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesapp_firebase_w7_d2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeViewModel()
        initializeBinding()
        initializeRecycler()
    }

    private lateinit var viewModel: ViewModel
    private fun initializeViewModel() {
        viewModel = ViewModelProvider(this).get(ViewModel::class.java)
        viewModel.getNotes().observe(this, {notes -> adapter.update(notes as ArrayList<Note>)})
        Log.d("TAG MAIN", "VIEW MODEL INITIALIZED")
    }

    private lateinit var binding: ActivityMainBinding
    private fun initializeBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("TAG MAIN", "Binding INITIALIZED")
    }

    private lateinit var adapter: Recycler
    private fun initializeRecycler() {
        adapter = Recycler(this)
        binding.rvMain.adapter = adapter
        binding.rvMain.layoutManager = LinearLayoutManager(this)
        Log.d("TAG MAIN", "RV INITIALIZED")
    }

    fun submitButton(view: View) {
        Log.d("TAG MAIN", "Button Pressed")
        val text = binding.etNote.text.toString()
        viewModel.addNote(text)
        binding.etNote.text.clear()
        binding.etNote.clearFocus()
        Toast.makeText(this, "data saved!!", Toast.LENGTH_LONG).show()
    }

    fun alertDialog(isUpdate: Boolean, note: Note) {
        val dialogBuilder = AlertDialog.Builder(this)
        val newLayout = LinearLayout(this)
        newLayout.orientation = LinearLayout.VERTICAL
        if (isUpdate) {
            val newTask = EditText(this)
            newTask.hint = "update note: "
            newLayout.addView(newTask)

            Log.d("TAG ALERT", "INSIDE UPDATE")

            dialogBuilder
                .setPositiveButton("Update") { _, _ ->
                    Log.d("TAG ALERT", "INSIDE POS BUTTON")
                    Log.d("TAG ALERT", "NOTE IS: $note")
                    viewModel.updateNote(note, newTask.text.toString())
                    //adapter.notifyDataSetChanged()
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        } // update note
        else {
            val text = TextView(this)
            text.text = "Are you sure you wish to delete note? "
            newLayout.addView(text)

            //pos > delete
            //neg > cancel
            dialogBuilder
                .setPositiveButton("Delete") { _, _ ->
                    Log.d("TAG ALERT", "INSIDE POS BUTTON")
                    Log.d("TAG ALERT", "NOTE IS: $note")
                    viewModel.deleteNote(note)
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        } //delete note
        val alert = dialogBuilder.create()
        alert.setTitle("Note")
        alert.setView(newLayout)
        alert.show()
    }

}
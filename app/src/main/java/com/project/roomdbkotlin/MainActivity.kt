package com.project.roomdbkotlin

import android.app.Activity
import android.content.Intent
import android.icu.lang.UCharacter.IndicPositionalCategory.LEFT
import android.icu.lang.UCharacter.IndicPositionalCategory.RIGHT
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.project.roomdbkotlin.db.Note
import com.project.roomdbkotlin.db.NoteViewModel


const val ADD_NOTE_REQUEST = 1
const val EDIT_NOTE_REQUEST = 2

class MainActivity : AppCompatActivity() {
    private lateinit var vm: NoteViewModel
    private lateinit var adapter: NoteAdapter
    private lateinit var button_add_note: FloatingActionButton
    private lateinit var recycler_view: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button_add_note = findViewById(R.id.button_add_note) as FloatingActionButton
        recycler_view = findViewById(R.id.recycler_view) as RecyclerView
        setUpRecyclerView()

        setUpListeners()

        vm = ViewModelProviders.of(this)[NoteViewModel::class.java]

        vm.getAllNotes().observe(this, Observer {
            Log.i("Notes observed", "$it")

            adapter.submitList(it)
        })

    }

    private fun setUpListeners() {
        button_add_note.setOnClickListener {
            val intent = Intent(this, AddEditNote::class.java)
            startActivityForResult(intent, ADD_NOTE_REQUEST)
        }

        // swipe listener
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, LEFT or RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val note = adapter.getNoteAt(viewHolder.adapterPosition)
                vm.delete(note)
            }

        }).attachToRecyclerView(recycler_view)
    }

    private fun setUpRecyclerView() {
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)

        adapter = NoteAdapter { clickedNote ->
            val intent = Intent(this, AddEditNote::class.java)
            intent.putExtra(EXTRA_ID, clickedNote.id)
            intent.putExtra(EXTRA_TITLE, clickedNote.title)
            intent.putExtra(EXTRA_DESCRIPTION, clickedNote.description)
            intent.putExtra(EXTRA_PRIORITY, clickedNote.priority)
            startActivityForResult(intent, EDIT_NOTE_REQUEST)
        }
        recycler_view.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data != null && requestCode == ADD_NOTE_REQUEST && resultCode == Activity.RESULT_OK) {
            val title: String = data.getStringExtra(EXTRA_TITLE).toString()
            val description: String = data.getStringExtra(EXTRA_DESCRIPTION).toString()
            val priority: Int = data.getIntExtra(EXTRA_PRIORITY, -1)
            vm.insert(Note(title, description, priority))
            Toast.makeText(this, "Event added Successfully!", Toast.LENGTH_SHORT).show()

        } else if(data != null && requestCode == EDIT_NOTE_REQUEST && resultCode == Activity.RESULT_OK) {
            val id = data.getIntExtra(EXTRA_ID, -1)
            if(id == -1) {
                Toast.makeText(this, "Event couldn't be updated!", Toast.LENGTH_SHORT).show()
                return
            }
            val title: String = data.getStringExtra(EXTRA_TITLE).toString()
            val description: String =
                data.getStringExtra(EXTRA_DESCRIPTION).toString()
            val priority: Int = data.getIntExtra(EXTRA_PRIORITY, -1)
            vm.update(Note(title, description, priority, id))
            Toast.makeText(this, "Event updated!", Toast.LENGTH_SHORT).show()

        } else {
            Toast.makeText(this, "Event not saved!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_all_notes -> {
                vm.deleteAllNotes()
                Toast.makeText(this, "All notes deleted!", Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    }
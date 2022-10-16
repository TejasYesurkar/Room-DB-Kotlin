package com.project.roomdbkotlin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

const val EXTRA_ID = " com.huawei.todolist.EXTRA_ID"
const val EXTRA_TITLE = " com.huawei.todolist.EXTRA_TITLE"
const val EXTRA_DESCRIPTION = " com.huawei.todolist.EXTRA_DESCRIPTION"
const val EXTRA_PRIORITY = " com.huawei.todolist.EXTRA_PRIORITY"

class AddEditNote : AppCompatActivity() {
    private lateinit var mode : Mode
    private var noteId: Int = -1
    private lateinit var number_picker_prior: NumberPicker
    private lateinit var et_title: EditText
    private lateinit var et_desc: EditText

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_note)
        number_picker_prior = findViewById(R.id.number_picker_priority) as NumberPicker
        et_title = findViewById(R.id.et_title) as EditText
        et_desc = findViewById(R.id.et_desc) as EditText
        number_picker_prior.minValue = 1
        number_picker_prior.maxValue = 10

            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)

            noteId = intent.getIntExtra(EXTRA_ID, -1)
            mode = if(noteId == -1) Mode.AddNote
            else Mode.EditNote

            when(mode) {
                Mode.AddNote -> title = "Add Note"
                Mode.EditNote -> {
                    title = "Edit Note"
                    et_title.setText(intent.getStringExtra(EXTRA_TITLE))
                    et_desc.setText(intent.getStringExtra(EXTRA_DESCRIPTION))
                    number_picker_prior.value = intent.getIntExtra(EXTRA_PRIORITY, -1)
                }
            }
        }

        override fun onCreateOptionsMenu(menu: Menu?): Boolean {
            val menuInflater = menuInflater
            menuInflater.inflate(R.menu.add_note_menu, menu)
            return true
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.save_note -> {
                    saveNote()
                    return true
                }
            }
            return super.onOptionsItemSelected(item)
        }

        private fun saveNote() {
            val title = et_title.text.toString()
            val desc = et_desc.text.toString()
            val priority = number_picker_prior.value

            if(title.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this, "please insert title and description", Toast.LENGTH_SHORT).show()
                return
            }

            val data = Intent()
            // only if note ID was provided i.e. we are editing
            if(noteId != -1)
                data.putExtra(EXTRA_ID, noteId)
            data.putExtra(EXTRA_TITLE, title)
            data.putExtra(EXTRA_DESCRIPTION, desc)
            data.putExtra(EXTRA_PRIORITY, priority)

            setResult(Activity.RESULT_OK, data)
            finish()
        }

        private sealed class Mode {
            object AddNote : Mode()
            object EditNote : Mode()

    }
}
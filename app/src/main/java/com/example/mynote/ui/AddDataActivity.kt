package com.example.mynote.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mynote.database.Note
import com.example.mynote.database.NoteDao
import com.example.mynote.database.NoteRoomDatabase
import com.example.papb_12.databinding.AddDataBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AddDataActivity : AppCompatActivity() {
    private lateinit var binding: AddDataBinding
    private lateinit var mNotesDao: NoteDao  // Add
    private var updateId: Int = 0
    private lateinit var existingNote: Note
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mNotesDao = NoteRoomDatabase.getDatabase(this)?.noteDao()!!

        // Mendapatkan updateId jika ada
        updateId = intent.getIntExtra("updateId", 0)

        if (updateId != 0) {
            // Jika updateId ada, berarti ini adalah mode update
            executorService.execute {
                existingNote = mNotesDao.getNoteById(updateId) ?: Note() // Menggunakan default constructor jika null

                runOnUiThread {
                    binding.txtTitle.setText(existingNote.title)
                    binding.txtDesc.setText(existingNote.description)
                    binding.txtDate.setText(existingNote.date)

                    binding.btnAdd.text = "Update Data"
                }
            }
        }

        binding.btnAdd.setOnClickListener {
            val title = binding.txtTitle.text.toString()
            val desc = binding.txtDesc.text.toString()
            val date = binding.txtDate.text.toString()

            if (updateId != 0) {
                // Jika updateId tidak sama dengan 0, berarti ini mode update
                // Update data ke database
                val updatedNote = Note(id = updateId, title = title, description = desc, date = date)
                updateData(updatedNote)
            } else {
                // Jika updateId sama dengan 0, berarti ini mode tambah data
                // Tambahkan data baru ke database
                addData(title, desc, date)
            }

            // Set result dan akhiri activity
            setResult(RESULT_OK)
            finish()
        }
    }

    private fun addData(title: String, desc: String, date: String) {
        val resultIntent = Intent()
        resultIntent.putExtra("title", title)
        resultIntent.putExtra("desc", desc)
        resultIntent.putExtra("date", date)
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun updateData(updatedNote: Note) {
        val resultIntent = Intent()
        resultIntent.putExtra("updateId", updatedNote.id)
        resultIntent.putExtra("title", updatedNote.title)
        resultIntent.putExtra("desc", updatedNote.description)
        resultIntent.putExtra("date", updatedNote.date)
        setResult(RESULT_OK, resultIntent)
        finish()
    }
}
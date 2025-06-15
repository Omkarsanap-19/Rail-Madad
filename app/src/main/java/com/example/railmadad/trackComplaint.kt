package com.example.railmadad

import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.text.InputFilter
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.railmadad.Data.Complaint
import com.example.railmadad.Data.Update
import com.example.railmadad.adapter.updateAdapter
import com.example.railmadad.databinding.ActivityTrackComplaintBinding
import com.example.railmadad.room.ComplaintDatabase
import com.example.railmadad.viewmodels.updateViewModel
import kotlin.getValue

class trackComplaint : AppCompatActivity() {
    val acces_token = "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"
    private lateinit var binding: ActivityTrackComplaintBinding
    private lateinit var upAdapter: updateAdapter
    private var list: MutableList<Update> = mutableListOf()
    private var arrayList: MutableList<String> = mutableListOf()


    private val viewModel: updateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTrackComplaintBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable the back arrow
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val userDao = ComplaintDatabase.getDatabase(application).trackDao()

        val readAllData =userDao.readAllData()


        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        // Access the internal EditText of SearchView
        val searchEditText = binding.searchView.findViewById<EditText>(
            androidx.appcompat.R.id.search_src_text
        )

        // Set numeric keyboard
        searchEditText.inputType = InputType.TYPE_CLASS_NUMBER

        // Set filters: max length 10 and only digits
        val digitOnlyFilter = InputFilter { source, _, _, _, _, _ ->
            if (source.matches(Regex("\\d*"))) source else ""
        }

        searchEditText.filters = arrayOf(
            InputFilter.LengthFilter(8),
            digitOnlyFilter
        )

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.bar.visibility = android.view.View.VISIBLE
                if (query != null && query.isNotEmpty()) {
                    viewModel.fetchComplaintById(query) {
                        if (it != null) {
                            val data = it.toObject(Complaint::class.java)
                            val update = it.toObject(Update::class.java)
                            viewModel.addComplaint(update!!,application)
                            Log.d("error Complaint", "Data fetched: ${data?.ID}")
                            binding.bar.visibility = android.view.View.GONE
                            Toast.makeText(this@trackComplaint, "Complaint Found", Toast.LENGTH_LONG).show()
                            viewModel.passTOdetails(this@trackComplaint, data!!)
                            Log.d("error Complaint", "Found: $data")

                        } else {
                            binding.bar.visibility = android.view.View.GONE
                            Toast.makeText(this@trackComplaint, "No complaint found for this ID", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        upAdapter = updateAdapter(this, list,viewModel)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = upAdapter

        readAllData.observe(this) { updates ->
            list.clear()
          upAdapter.updateList(updates)
            upAdapter.notifyDataSetChanged()
        }











    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed() // Correct way in modern APIs
        return true
    }

}
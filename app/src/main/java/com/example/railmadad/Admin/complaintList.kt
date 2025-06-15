package com.example.railmadad.Admin

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.railmadad.Admin.viewModel.adminViewmodel
import com.example.railmadad.Data.Update
import com.example.railmadad.R
import com.example.railmadad.adapter.openAdapter
import com.example.railmadad.adapter.updateAdapter
import com.example.railmadad.viewmodels.updateViewModel
import kotlin.getValue

class complaintList : AppCompatActivity() {

    val viewModel: adminViewmodel by viewModels()
    lateinit var adapter : openAdapter
    lateinit var emptyTextView : TextView
    lateinit var recyclerView : RecyclerView
    lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_complaint_list)
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


        val position = intent.getIntExtra("position",0)


         adapter  =  openAdapter(this,mutableListOf(),viewModel)
        recyclerView= findViewById<RecyclerView>(R.id.recyclerView)
        emptyTextView = findViewById<TextView>(R.id.emptyTV)
        progressBar = findViewById<ProgressBar>(R.id.bar)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        observeData(position)
        viewModel.sortCategory()

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                toggleEmptyView()
            }
        })

        viewModel.loadingItem.observe(this) {
            progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }


    }

    fun observeData(value : Int) {
        val observer: (Any?) -> Unit = {
            val complaintList = getComplaints(value)
            adapter.updateList(complaintList)
        }

        viewModel.arrayPD.observe(this, observer)
        viewModel.arrayFood.observe(this, observer)
        viewModel.arrayClean.observe(this, observer)
        viewModel.arrayStaff.observe(this, observer)
        viewModel.arrayTech.observe(this, observer)
        viewModel.arrayTicket.observe(this, observer)
        viewModel.arraySafe.observe(this, observer)
    }


    fun getComplaints(value : Int): MutableList<Update> {
        val complaintList = when(value){
            0->viewModel.arrayPD.value
            1->viewModel.arrayFood.value
            2->viewModel.arrayClean.value
            3->viewModel.arrayStaff.value
            4-> viewModel.arrayTech.value
            5->viewModel.arrayTicket.value
            6->viewModel.arraySafe.value

            else -> mutableListOf<Update?>()
        } as MutableList<Update>

        return complaintList

    }

    private fun toggleEmptyView() {
        val isEmpty = adapter.itemCount == 0
        emptyTextView.visibility = if (isEmpty) View.VISIBLE else View.GONE
        recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed() // Correct way in modern APIs
        return true
    }







}
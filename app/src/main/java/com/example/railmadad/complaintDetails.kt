package com.example.railmadad

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.railmadad.Data.Complaint
import com.example.railmadad.Data.upload_select
import com.example.railmadad.adapter.uploadAdapter
import com.example.railmadad.databinding.ActivityComplaintDetailsBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class complaintDetails : AppCompatActivity() {

    private lateinit var binding: ActivityComplaintDetailsBinding
    private lateinit var upload_Adapter: uploadAdapter
    private var arrayList: MutableList<upload_select> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityComplaintDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val complaint = intent.getParcelableExtra<Complaint>("data")

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable the back arrow
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.tvComplaintId.text = complaint?.ID ?: "N/A"
        binding.tvCategory.text = complaint?.Category ?: "N/A"
        binding.tvDescription.text = complaint?.Description ?: "N/A"
        binding.tvComplaintStatus.text = complaint?.Status ?: "N/A"
        binding.tvComplaintDate.text = complaint?.Date ?: "N/A"
        binding.tvComplaintTitle.text = complaint?.Tttle ?: "N/A"
        binding.tvPnrNumber.text = complaint?.pnr ?: "N/A"
        val chipGroup = findViewById<ChipGroup>(R.id.chipGroup)


        // Dynamically create and add chips
        for (word in complaint?.Image!!) {
            val chip = Chip(this).apply {
                text = word
            }
            chipGroup.addView(chip)
        }




        for ( name in complaint?.Files!!){
            arrayList.add(upload_select(R.drawable.generic_file ,name))
        }

        upload_Adapter = uploadAdapter(this,arrayList)

        binding.rvAttachedMedia.layoutManager = LinearLayoutManager(this)
        binding.rvAttachedMedia.adapter = upload_Adapter

        binding.btnAddFeedback.setOnClickListener {
            val intent = android.content.Intent(this, feedbackActivity::class.java)
            intent.putExtra("complaintId", complaint.ID)
            startActivity(intent)
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed() // Correct way in modern APIs
        return true
    }

}
package com.example.railmadad.Admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.railmadad.Admin.viewModel.adminViewmodel
import com.example.railmadad.Data.Complaint
import com.example.railmadad.Data.FeedGet
import com.example.railmadad.Data.upload_select
import com.example.railmadad.R
import com.example.railmadad.adapter.uploadAdapter
import com.example.railmadad.databinding.ActivityComplaintDetailsBinding
import com.example.railmadad.databinding.ActivityComplaintEditBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class complaintEdit : AppCompatActivity() {

    private lateinit var binding: ActivityComplaintEditBinding
    private lateinit var upload_Adapter: uploadAdapter
    lateinit var status_Dialog: BottomSheetDialog
    private var arrayList: MutableList<upload_select> = mutableListOf()

    val viewModel : adminViewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityComplaintEditBinding.inflate(layoutInflater)
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
        val userId = complaint?.userId ?: "N/A"


        for ( name in complaint?.Files!!){
            arrayList.add(upload_select(R.drawable.generic_file ,name))
        }

        upload_Adapter = uploadAdapter(this,arrayList)

        binding.rvAttachedMedia.layoutManager = LinearLayoutManager(this)
        binding.rvAttachedMedia.adapter = upload_Adapter

        viewModel.getFeedbackComplaint(this,complaint.ID!!,{
            val feedback = it?.getValue(FeedGet::class.java)

            feedback?.let {
                val score0 = it.sentiment?.getOrNull(0)?.score ?: 0.0
                val score1 = it.sentiment?.getOrNull(1)?.score ?: 0.0
                Log.d("error Feedback", "Score0: $score0, Score1: $score1")
                it.sign = if (score0 > score1) "Positive" else "Negative"

            }

            binding.messageFeedback.text = feedback?.message ?: "Not yet received"
            binding.sign.text = feedback?.sign ?: "N/A"

        })

        binding.cardAiAnalysis.setOnClickListener {
            val intent = Intent(this, aiAnalysis::class.java)
            intent.putExtra("data",complaint)
            startActivity(intent)
        }

        binding.btnChangeStatus.setOnClickListener {

            showDialogStatus(complaint.ID ?: "N/A",userId)

        }

        viewModel.loadingNotify.observe(this) {
            if (it) {
                binding.bar.visibility = android.view.View.VISIBLE
            } else {
                binding.bar.visibility = android.view.View.GONE
            }
        }




    }

    fun showDialogStatus(unique: String,userId: String) {
        status_Dialog = BottomSheetDialog(this)
        status_Dialog.setContentView(R.layout.status_dialog)
        status_Dialog.setCancelable(true)
        status_Dialog.setCanceledOnTouchOutside(true)

        status_Dialog.show()
        val pending = status_Dialog.findViewById<TextView>(R.id.pending)
        val progress = status_Dialog.findViewById<TextView>(R.id.mid)
        val done = status_Dialog.findViewById<TextView>(R.id.success)

        pending?.setOnClickListener {
            viewModel._loadingNotify.value = true
            binding.tvComplaintStatus.text = pending.text
            viewModel.statusChange(this, unique, pending.text.toString(),userId,this)
            status_Dialog.dismiss()
        }

        progress?.setOnClickListener {
            viewModel._loadingNotify.value = true
            binding.tvComplaintStatus.text = progress.text
            viewModel.statusChange(this, unique, progress.text.toString(),userId,this)
            status_Dialog.dismiss()
        }

        done?.setOnClickListener {
            viewModel._loadingNotify.value = true
            binding.tvComplaintStatus.text = done.text
            viewModel.statusChange(this, unique, done.text.toString(),userId,this)
            status_Dialog.dismiss()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed() // Correct way in modern APIs
        return true
    }

}
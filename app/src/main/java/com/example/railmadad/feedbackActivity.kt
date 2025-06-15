package com.example.railmadad

import android.os.Bundle
import android.util.Log.e
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import com.example.railmadad.Data.SentimentResponse
import com.example.railmadad.viewmodels.complaintViewModel
import com.example.railmadad.viewmodels.updateViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class feedbackActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_feedback)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val unique  = intent.getStringExtra("complaintId")
        val viewModel = complaintViewModel(application)
        val feedbackText = findViewById<TextInputEditText>(R.id.etFeedbackDescription)
        val submitButton = findViewById<MaterialButton>(R.id.btnSubmitFeedback)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable the back arrow
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        CoroutineScope(Dispatchers.Main).launch {
            if (viewModel.feedbackPresent(unique!!,this@feedbackActivity)) {
                submitButton.isClickable = false
                submitButton.text = "Feedback Already Submitted"
                Toast.makeText(this@feedbackActivity, "Feedback Already Submitted", Toast.LENGTH_SHORT).show()
            }
        }

        submitButton.setOnClickListener {
            val feedback = feedbackText.text.toString().trim()
            if (feedback.isEmpty()) {
                Toast.makeText(this, "Please enter your feedback", Toast.LENGTH_SHORT).show()
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.submitFeedback(feedback,this@feedbackActivity,unique!!)
                }
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed() // Correct way in modern APIs
        return true
    }


}
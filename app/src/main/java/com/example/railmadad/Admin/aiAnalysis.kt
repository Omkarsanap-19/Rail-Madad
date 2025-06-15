package com.example.railmadad.Admin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import com.example.railmadad.Data.Complaint
import com.example.railmadad.Data.Image
import com.example.railmadad.R
import com.example.railmadad.adapter.analysisAdapter
import com.example.railmadad.adapter.videoAdapter

class aiAnalysis : AppCompatActivity() {
    val imgData = mutableListOf<Image>()
    val videoData = mutableListOf<Image>()
    private lateinit var exoPlayer: ExoPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ai_analysis)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        exoPlayer = ExoPlayer.Builder(this).build()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable the back arrow
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val complaint = intent.getParcelableExtra<Complaint>("data")
        for (data in complaint?.attachData!!) {
            if (data.metadata?.get(2) == "Type: IMAGE") {
                imgData.add(data)
            } else if (data.metadata?.get(2) == "Type: VIDEO") {
                videoData.add(data)
            }
        }

        val imgAdapter = analysisAdapter(imgData,this)
        val  imgRecycle = findViewById<RecyclerView>(R.id.rvImageAnalysis)
        imgRecycle.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        imgRecycle.adapter = imgAdapter

        val videoAdapter = videoAdapter(videoData,exoPlayer)
        val videoRecycle = findViewById<RecyclerView>(R.id.rvVideoAnalysis)
        videoRecycle.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        videoRecycle.adapter = videoAdapter



    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed() // Correct way in modern APIs
        return true
    }

}
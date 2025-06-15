package com.example.railmadad.Admin.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.railmadad.Data.FeedGet
import com.example.railmadad.Data.Feedback
import com.example.railmadad.Data.Update
import com.example.railmadad.R
import com.example.railmadad.adapter.feedAdapter
import com.example.railmadad.viewmodels.authViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.getValue


class feedbackFragment : Fragment() {

    lateinit var  adapter: feedAdapter
    private  var list: MutableList<FeedGet> = mutableListOf()
    private val viewModel:authViewModel by viewModels()
    private lateinit var bar : ProgressBar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=  inflater.inflate(R.layout.fragment_feedback, container, false)

        bar = view.findViewById<ProgressBar>(R.id.bar)



        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        adapter = feedAdapter(requireContext(),list)
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        Log.d("error complaint","one")
        CoroutineScope(Dispatchers.Main).launch {
            Log.d("error complaint","two")
            getFeedback(requireContext())
        }

        return view
    }

    suspend fun getFeedback(context: Context) {
        val database = FirebaseDatabase.getInstance()
        val feedbackRef = database.getReference("feedback")

        feedbackRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()

                for (userSnap in snapshot.children) {
                    for (complaintSnap in userSnap.children) {
                        val feedback = complaintSnap.getValue(FeedGet::class.java)

                        feedback?.let {
                            if (it.sentiment?.getOrNull(0)?.label == "POSITIVE" && it.sentiment?.getOrNull(1)?.label == "NEGATIVE") {
                                Log.d("error Feedback", "Feedback: ${it.message}")
                                val score0 = it.sentiment?.getOrNull(0)?.score ?: 0.0
                                val score1 = it.sentiment?.getOrNull(1)?.score ?: 0.0
                                Log.d("error Feedback", "Score0: $score0, Score1: $score1")
                                it.sign = if (score0 > score1) "Positive" else "Negative"

                                list.add(it)
                            } else {
                                Log.d("error Feedback", "Invalid sentiment labels")
                                val score0 = it.sentiment?.getOrNull(0)?.score ?: 0.0
                                val score1 = it.sentiment?.getOrNull(1)?.score ?: 0.0
                                Log.d("error Feedback", "Score0: $score0, Score1: $score1")
                                it.sign = if (score0 > score1) "Negative" else "Positive"

                                list.add(it)
                            }

                        }
                    }
                }
                bar.visibility = View.GONE
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                bar.visibility = View.GONE
            }
        })
    }




}
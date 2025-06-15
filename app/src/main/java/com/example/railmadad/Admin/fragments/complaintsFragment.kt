package com.example.railmadad.Admin.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.railmadad.R
import com.example.railmadad.adapter.complaintAdapter


class complaintsFragment : Fragment() {
    val categoryList = listOf(
        "Punctuality & Delays",
        "Food & Catering",
        "Cleanliness & Hygiene",
        "Train Operation & Scheduling",
        "Technical Issues",
        "Ticketing & Seat Allocation",
        "Safety & Security"
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_complaints, container, false)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerView)

        val adapter = complaintAdapter(requireContext(),categoryList)

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter




        return view
    }


}
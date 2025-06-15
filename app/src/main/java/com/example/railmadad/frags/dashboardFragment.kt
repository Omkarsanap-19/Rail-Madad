package com.example.railmadad.frags

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.railmadad.Data.Update
import com.example.railmadad.R
import com.example.railmadad.adapter.updateAdapter
import com.example.railmadad.newComplaint
import com.example.railmadad.trackComplaint
import com.example.railmadad.viewmodels.complaintViewModel
import com.example.railmadad.viewmodels.updateViewModel
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.getValue


class dashboardFragment : Fragment() {
    private val viewModel:updateViewModel by viewModels()
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView : RecyclerView
    private lateinit var emptyTV: TextView
    val list =mutableListOf<Update>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=  inflater.inflate(R.layout.fragment_dashbord, container, false)
        val UniqueID = complaintViewModel(requireActivity().application).generateNumericId()
        val btn = view.findViewById<FloatingActionButton>(R.id.fabNewComplaint)
        val track = view.findViewById<MaterialCardView>(R.id.cardTrackComplaint)
        val newComplain = view.findViewById<MaterialCardView>(R.id.cardNewComplaint)
        val helpCard = view.findViewById<MaterialCardView>(R.id.cardChatbot)
        recyclerView = view.findViewById<RecyclerView>(R.id.rvRecentComplaints)
        progressBar = view.findViewById<ProgressBar>(R.id.bar)
        emptyTV = view.findViewById<TextView>(R.id.emptyTV)
        val viewAll = view.findViewById<TextView>(R.id.tvViewAllComplaints)
        val totalComplaints = view.findViewById<TextView>(R.id.tvTotalComplaints)
        val resolvedComplaints = view.findViewById<TextView>(R.id.tvResolvedComplaints)
        val pendingComplaints = view.findViewById<TextView>(R.id.tvPendingComplaints)
        val loadStat = view.findViewById<TextView>(R.id.loadingStats)
        val userName = view.findViewById<TextView>(R.id.tvUserName)


        viewModel.getComplaintStats(requireContext())

        lifecycleScope.launch(Dispatchers.Main) {
            userName.text = viewModel.getUserName()
        }

        btn.setOnClickListener {
            val intent = Intent(requireContext(), newComplaint::class.java)
            intent.putExtra("unique",UniqueID)
            startActivity(intent)

        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        getcomplaintUpdateToUser(requireContext())



        viewModel.loadingItem.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        helpCard.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_helpFragment,null,
                NavOptions.Builder()
                    .setPopUpTo(R.id.dashboardFragment, true)
                    .build())

        }

        viewAll.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_trackComplaint,null,
                NavOptions.Builder()
                    .setPopUpTo(R.id.dashboardFragment, true)
                    .build())
        }



        newComplain.setOnClickListener {
            val intent = Intent(requireContext(), newComplaint::class.java)
            intent.putExtra("unique",UniqueID)
            startActivity(intent)

        }

        track.setOnClickListener {
            val intent = Intent(requireContext(), trackComplaint::class.java)
            startActivity(intent)
        }

        viewModel.totalStat.observe(viewLifecycleOwner) {
            totalComplaints.text = it
        }

        viewModel.resolveStat.observe(viewLifecycleOwner) {
            resolvedComplaints.text = it
        }

        viewModel.pendingStat.observe(viewLifecycleOwner) {
            pendingComplaints.text =it
        }


        return view

    }

    fun getcomplaintUpdateToUser(context: Context) {
        val fireStore = FirebaseDatabase.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid!!
        fireStore.getReference("users").child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var count = 0
                    list.clear()
                    // going through each complaintID
                    Log.d("error complaint","chal on data change toh hua")
                    for (snap in snapshot.children ) {
                        count++
                        Log.d("error complaint","in for loop")
                        val data = snap.getValue(Update::class.java)
                        data?.let { list.add(it) }

                        Log.d("error complaint", list.toString())
                        if (count == 2) {
                            break // Limit to the latest 5 complaints
                        }
                    }

                    if (isAdded && view != null) {
                        val adapter = updateAdapter(requireContext(), list, viewModel)
                        recyclerView.adapter = adapter
                        adapter.notifyDataSetChanged()

                        val isEmpty = count == 0
                        emptyTV.visibility = if (isEmpty) View.VISIBLE else View.GONE
                        recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                }
            })

    }



}
package com.example.railmadad.frags

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.railmadad.Data.Update
import com.example.railmadad.R
import com.example.railmadad.adapter.updateAdapter
import com.example.railmadad.databinding.FragmentUpdateBinding
import com.example.railmadad.viewmodels.updateViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class updateFragment : Fragment() {

    private lateinit var upAdapter: updateAdapter
    private  var list: MutableList<Update> = mutableListOf()
    private var _updateBinding: FragmentUpdateBinding? = null
    private val updateBinding get() = _updateBinding

    private val viewModel: updateViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _updateBinding = FragmentUpdateBinding.inflate(inflater,container,false)
        val view = updateBinding?.root

        updateBinding?.bar?.visibility = View.VISIBLE

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        upAdapter = updateAdapter(requireContext(),list,viewModel)
        updateBinding?.recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        updateBinding?.recyclerView?.adapter = upAdapter

        // it wait to attach ui and then set visibility
        updateBinding?.bar?.post {
            updateBinding?.bar?.visibility = View.VISIBLE
        }
        getcomplaintUpdateToUser(requireContext())

        viewModel.loadingItem.observe(viewLifecycleOwner) { isLoading ->
            updateBinding?.bar?.visibility = if (isLoading) View.VISIBLE else View.GONE
        }


    }

     fun getcomplaintUpdateToUser(context: Context) {
        val fireStore = FirebaseDatabase.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid!!
        fireStore.getReference("users").child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    // going through each complaintID
                    Log.d("error complaint","chal on data change toh hua")
                    for (snap in snapshot.children) {
                        Log.d("error complaint","in for loop")
                        val data = snap.getValue(Update::class.java)
                        data?.let { list.add(it) }

                        Log.d("error complaint", list.toString())
                    }

                    upAdapter.notifyDataSetChanged()
                    updateBinding?.bar?.visibility = View.GONE

                    toggleEmptyView()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                    updateBinding?.bar?.visibility = View.GONE
                }
            })

    }

    private fun toggleEmptyView() {
        val isEmpty = upAdapter.itemCount == 0
        updateBinding?.emptyTV?.visibility = if (isEmpty) View.VISIBLE else View.GONE
        updateBinding?.recyclerView?.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }






}
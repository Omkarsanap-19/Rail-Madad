package com.example.railmadad.Admin.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.railmadad.Admin.viewModel.adminViewmodel
import com.example.railmadad.Data.Update
import com.example.railmadad.R
import com.example.railmadad.adapter.openAdapter
import com.example.railmadad.adapter.updateAdapter
import kotlin.getValue


class departmentPage : Fragment() {

    val viewModel: adminViewmodel by viewModels()
    private lateinit var upAdapter: openAdapter

    companion object {
        private const val ARG_CATEGORY = "category"

        fun newInstance(category: String): departmentPage {
            val fragment = departmentPage()
            val args = Bundle()
            args.putString(ARG_CATEGORY, category)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_department_page, container, false)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerView)
        recycler.layoutManager = LinearLayoutManager(requireContext())

         upAdapter = openAdapter(requireContext(),mutableListOf<Update>(), viewModel)
        recycler.adapter = upAdapter

        val category = arguments?.getString(ARG_CATEGORY) ?: ""
        observeData(category)
        viewModel.sortCategory()


        return view
    }


    private fun getItemsForCategory(category: String): MutableList<Update> {
        val complaints = when (category) {

            "Operating Department" -> {
                val list = viewModel.arrayPD.value
                Log.d("DepartmentPage", "Operating Department selected, items: ${list?.size}")
                list
            }

            "Mechanical (C&W) Department" -> {
                val list = viewModel.arrayClean.value
                Log.d("DepartmentPage", "Mechanical (C&W) Department selected, items: ${list?.size}")
                list
            }

            "IT & Electrical Department" -> {
                val list = viewModel.arrayTech.value
                Log.d("DepartmentPage", "IT & Electrical Department selected, items: ${list?.size}")
                list
            }

            "Catering Department" -> {
                val list = viewModel.arrayFood.value
                Log.d("DepartmentPage", "Catering Department selected, items: ${list?.size}")
                list
            }

            "Traffic Control Department" -> {
                val list = viewModel.arrayStaff.value
                Log.d("DepartmentPage", "Traffic Control Department selected, items: ${list?.size}")
                list
            }

            "Commercial Department" -> {
                val list = viewModel.arrayTicket.value
                Log.d("DepartmentPage", "Commercial Department selected, items: ${list?.size}")
                list
            }

            "Railway Protection Force (RPF)" -> {
                val list = viewModel.arraySafe.value
                Log.d("DepartmentPage", "RPF Department selected, items: ${list?.size}")
                list
            }

            else -> {
                Log.w("DepartmentPage", "Unknown department: $category")
                mutableListOf()
            }
        }

        return complaints ?: mutableListOf()
    }


    fun observeData(value : String) {
        val observer: (Any?) -> Unit = {

            val complaintList = getItemsForCategory(value)
            Log.d("DepartmentPage", "Observing data for category: $value, items: ${complaintList.size}")
            upAdapter.updateList(complaintList)
        }

        viewModel.arrayPD.observe(viewLifecycleOwner, observer)
        viewModel.arrayFood.observe(viewLifecycleOwner, observer)
        viewModel.arrayClean.observe(viewLifecycleOwner, observer)
        viewModel.arrayStaff.observe(viewLifecycleOwner, observer)
        viewModel.arrayTech.observe(viewLifecycleOwner, observer)
        viewModel.arrayTicket.observe(viewLifecycleOwner, observer)
        viewModel.arraySafe.observe(viewLifecycleOwner, observer)
    }







}


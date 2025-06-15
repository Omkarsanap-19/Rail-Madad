package com.example.railmadad.adapter

import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.railmadad.Data.Complaint
import com.example.railmadad.Data.Update
import com.example.railmadad.Data.upload_select
import com.example.railmadad.R
import com.example.railmadad.complaintDetails
import com.example.railmadad.viewmodels.updateViewModel
import com.google.android.material.button.MaterialButton

class updateAdapter(val context: Context, var list: MutableList<Update>, val viewModel: updateViewModel) : RecyclerView.Adapter<updateAdapter.myViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): updateAdapter.myViewHolder {
        val view  = LayoutInflater.from(context).inflate(R.layout.item_update,parent,false)
        return myViewHolder(view,this)
    }

    override fun onBindViewHolder(holder: updateAdapter.myViewHolder, position: Int) {
        val current = list[position]
        holder.tittle.text = current.Tttle
        holder.category.text = current.Category
        holder.date.text = current.Date
        holder.id.text = current.ID
        holder.status.text = current.Status
    }

    override fun getItemCount(): Int {
        return list.size
    }


    fun updateList(newList: MutableList<Update>) {
        list = newList
        notifyDataSetChanged()
    }

    fun openDetails(position: Int) {
        viewModel._loadingItem.value = true
        val unique = list[position].ID
        viewModel.fetchComplaintById(unique) {
            val data = it?.toObject(Complaint::class.java)
            data?.let {
                viewModel._loadingItem.value = false
                viewModel.passTOdetails(context,data)
            }

        }

    }

    class myViewHolder(val itemview : View,val adapter: updateAdapter) : RecyclerView.ViewHolder(itemview){
        val id = itemview.findViewById<TextView>(R.id.tvComplaintId)
        val status = itemview.findViewById<TextView>(R.id.tvComplaintStatus)
        val date = itemview.findViewById<TextView>(R.id.tvComplaintDate)
        val tittle = itemview.findViewById<TextView>(R.id.tvComplaintTitle)
        val category = itemview.findViewById<TextView>(R.id.tvComplaintCategory)
        val details = itemview.findViewById<MaterialButton>(R.id.btnViewDetails)


        init {
            details.setOnClickListener {
                adapter.openDetails(adapterPosition)
            }
        }

    }


}
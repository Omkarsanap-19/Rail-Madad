package com.example.railmadad.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.railmadad.Admin.viewModel.adminViewmodel
import com.example.railmadad.Data.Complaint
import com.example.railmadad.Data.Update
import com.example.railmadad.R
import com.example.railmadad.viewmodels.updateViewModel
import com.google.android.material.button.MaterialButton

class openAdapter (val context: Context, var list: MutableList<Update>, val viewModel: adminViewmodel) : RecyclerView.Adapter<openAdapter.myViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): openAdapter.myViewHolder {
        val view  = LayoutInflater.from(context).inflate(R.layout.item_update,parent,false)
        return myViewHolder(view,this)
    }

    override fun onBindViewHolder(holder: openAdapter.myViewHolder, position: Int) {
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

    class myViewHolder(val itemview : View,val adapter: openAdapter) : RecyclerView.ViewHolder(itemview){
        val id = itemview.findViewById<TextView>(R.id.tvComplaintId)
        val status = itemview.findViewById<TextView>(R.id.tvComplaintStatus)
        val date = itemview.findViewById<TextView>(R.id.tvComplaintDate)
        val tittle = itemview.findViewById<TextView>(R.id.tvComplaintTitle)
        val category = itemview.findViewById<TextView>(R.id.tvComplaintCategory)
        val viewDetails = itemview.findViewById<MaterialButton>(R.id.btnViewDetails)


        init {
            viewDetails.setOnClickListener {
                adapter.openDetails(adapterPosition)
            }
        }

    }




}
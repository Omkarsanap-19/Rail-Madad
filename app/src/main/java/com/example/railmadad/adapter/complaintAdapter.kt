package com.example.railmadad.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.railmadad.Admin.viewModel.adminViewmodel
import com.example.railmadad.R
import com.example.railmadad.newComplaint

class complaintAdapter(val context: Context,val arrayCategory : List<String>) : RecyclerView.Adapter<complaintAdapter.ViewHolder>() {

    // Define ViewHolder and other necessary methods here
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_complaints, parent, false)
        return ViewHolder(view,context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current= arrayCategory[position]
        holder.tittle.text = current
    }

    override fun getItemCount(): Int {
        return arrayCategory.size
    }

    class ViewHolder(itemView: View,val context: Context) : RecyclerView.ViewHolder(itemView) {

        val tittle = itemView.findViewById<TextView>(R.id.category)

        init {
            itemView.setOnClickListener {
                adminViewmodel().categoryToComplaint(context,adapterPosition)
            }

        }
    }
}
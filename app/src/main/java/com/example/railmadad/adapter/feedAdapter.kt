package com.example.railmadad.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.railmadad.Data.FeedGet
import com.example.railmadad.Data.Feedback
import com.example.railmadad.R
import androidx.core.graphics.toColorInt

class feedAdapter(val context: Context,val arraylist: MutableList<FeedGet>) : RecyclerView.Adapter<feedAdapter.myViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): feedAdapter.myViewHolder {
        val view  = LayoutInflater.from(parent.context).inflate(R.layout.item_feedback,parent,false)
        return myViewHolder(view)
    }

    override fun onBindViewHolder(holder: feedAdapter.myViewHolder, position: Int) {
        val currentItem = arraylist[position]
        holder.feedbackText.text = currentItem.message
        if (currentItem.sign == "Positive") {
            holder.predict.text = "Positive"
            holder.predict.setTextColor("#4CAF50".toColorInt())
        } else {
            holder.predict.text = "Negative"
            holder.predict.setTextColor("#F44336".toColorInt())
        }

        if (currentItem.sentiment?.getOrNull(0)?.label == "POSITIVE" && currentItem.sentiment?.getOrNull(1)?.label == "NEGATIVE"){
            val positive = currentItem.sentiment?.get(0)?.score
            val negative = currentItem.sentiment?.get(1)?.score
            if (positive != null && negative != null) {
                if (positive >= negative){
                    val result = positive
                    val percent = ( result/ (positive + negative)) * 100
                    holder.percent.text = String.format("%.2f", percent) + "%"
                    holder.percent.setTextColor("#4CAF50".toColorInt())
                }else{
                    val result = negative
                    val percent = ( result/ (positive + negative)) * 100
                    holder.percent.text = String.format("%.2f", percent) + "%"
                    holder.percent.setTextColor("#F44336".toColorInt())
                }
            } else {
                holder.percent.text = "N/A"
            }
        }else{
            val negative = currentItem.sentiment?.get(0)?.score
            val positive = currentItem.sentiment?.get(1)?.score
            if (positive != null && negative != null) {
                if (positive >= negative){
                    val result = positive
                    val percent = ( result/ (positive + negative)) * 100
                    holder.percent.text = String.format("%.2f", percent) + "%"
                    holder.percent.setTextColor("#4CAF50".toColorInt())
                }else{
                    val result = negative
                    val percent = ( result/ (positive + negative)) * 100
                    holder.percent.text = String.format("%.2f", percent) + "%"
                    holder.percent.setTextColor("#F44336".toColorInt())
                }
            } else {
                holder.percent.text = "N/A"
            }
        }







    }

    override fun getItemCount(): Int {
        return arraylist.size
    }


    class myViewHolder(val itemView:View): RecyclerView.ViewHolder(itemView){
        val feedbackText = itemView.findViewById<TextView>(R.id.messageFeedback)
        val predict = itemView.findViewById<TextView>(R.id.sign)
        val percent = itemView.findViewById<TextView>(R.id.tvConfidence)

    }

}
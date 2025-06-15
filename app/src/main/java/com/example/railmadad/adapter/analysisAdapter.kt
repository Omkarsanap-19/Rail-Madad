package com.example.railmadad.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.railmadad.Data.Image
import com.example.railmadad.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class analysisAdapter(val arrayList: List<Image>,val context: Context):RecyclerView.Adapter<analysisAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view  = LayoutInflater.from(parent.context).inflate(R.layout.item_media_analysis, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = arrayList[position]
        holder.extractedText.text = currentItem.ocrText?: "No text extracted"
        holder.location.text = currentItem.metadata?.get(1) ?: "No location data"
        holder.date.text = currentItem.metadata?.get(0) ?: "No date data"
//        holder.type.text = currentItem.metadata?.get(2) ?: "Unknown type"
        holder.name.text = currentItem.fileName ?: "No name"
        for (word in currentItem.keywords!!) {
            val chip = Chip(context).apply {
                text = word
            }
            holder.chips.addView(chip)
        }
        Glide.with(context).load(currentItem.url).placeholder(R.drawable.generic_file).error(R.drawable.generic_file).into(holder.img)

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img = itemView.findViewById<android.widget.ImageView>(R.id.ivMedia)
        val extractedText = itemView.findViewById<android.widget.TextView>(R.id.tvExtractedText)
        val location = itemView.findViewById<android.widget.TextView>(R.id.tvLocation)
        val date = itemView.findViewById<android.widget.TextView>(R.id.tvTimestamp)
        val chips = itemView.findViewById<ChipGroup>(R.id.chipGroupDetectedElements)
        val name = itemView.findViewById<android.widget.TextView>(R.id.tvFilename)
        val type = itemView.findViewById<android.widget.TextView>(R.id.tvMediaType)


    }




}
package com.example.railmadad.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView
import com.example.railmadad.Data.upload_select
import com.example.railmadad.R

class uploadAdapter(val context: Context, var arraList: MutableList<upload_select>): RecyclerView.Adapter<uploadAdapter.myViewHolder>() {

    private var Filelist: MutableList<String> = mutableListOf<String>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): uploadAdapter.myViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_selectedupload,parent,false)
        val delete_btn = view.findViewById<ImageButton>(R.id.deleteButton)
        if (arraList[0].img == R.drawable.generic_file) {
            delete_btn.visibility = View.GONE
        } else{
            delete_btn.visibility = View.GONE
        }

        return myViewHolder(view,arraList)
    }

    override fun onBindViewHolder(holder: uploadAdapter.myViewHolder, position: Int) {
        val current_item = arraList[position]
        holder.file.text = current_item.name
        holder.icon.setImageResource(current_item.img)


        holder.cancel_btn.setOnClickListener {
            arraList.removeAt(position)
            notifyDataSetChanged()
            Toast.makeText(context, "Removed Successfully!!", Toast.LENGTH_SHORT).show()
        }

    }

    override fun getItemCount(): Int {
        return arraList.size
    }

    fun updateList(newList: MutableList<upload_select>) {
        arraList = newList
        notifyDataSetChanged()
    }

    fun getFileList(): MutableList<String> {
        for (names in arraList) {
            Filelist.add(names.name)
        }
        return Filelist
    }

    class myViewHolder(itemView: View ,arraList: MutableList<upload_select> ): RecyclerView.ViewHolder(itemView){

        val icon = itemView.findViewById<ImageView>(R.id.fileTypeIcon)
        val file = itemView.findViewById<TextView>(R.id.fileNameText)
        val cancel_btn = itemView.findViewById<ImageButton>(R.id.deleteButton)


    }


}
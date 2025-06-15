package com.example.railmadad.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.RecyclerView
import com.example.railmadad.Data.Image
import com.example.railmadad.R
import com.google.android.material.chip.ChipGroup

class videoAdapter(val videoList: List<Image>, val player: ExoPlayer):RecyclerView.Adapter<videoAdapter.VideoViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video_analysis,parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val currentItem = videoList[position]
        holder.location.text = currentItem.metadata?.get(1) ?: "No location data"
        holder.date.text = currentItem.metadata?.get(0) ?: "No date data"
//        holder.type.text = currentItem.metadata?.get(2) ?: "Unknown type"
        holder.name.text = currentItem.fileName ?: "No name"

        holder.video.player = player
        val mediaItem = MediaItem.fromUri(currentItem.url!!)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true



    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val location = itemView.findViewById<android.widget.TextView>(R.id.tvLocation)
        val date = itemView.findViewById<android.widget.TextView>(R.id.tvTimestamp)
        val name = itemView.findViewById<android.widget.TextView>(R.id.tvFilename)
        val video = itemView.findViewById<PlayerView>(R.id.playerView)
    }

}
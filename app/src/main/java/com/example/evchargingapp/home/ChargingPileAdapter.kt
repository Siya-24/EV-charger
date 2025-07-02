package com.example.evchargingapp.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.evchargingapp.R
import com.example.evchargingapp.data.ChargingPile

class ChargingPileAdapter :
    ListAdapter<ChargingPile, ChargingPileAdapter.PileViewHolder>(ChargingPileDiffCallback()) {

    inner class PileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pileId: TextView = view.findViewById(R.id.pileIdTextView)
        val pileName: TextView = view.findViewById(R.id.pileNameTextView)
        val pileStatus: TextView = view.findViewById(R.id.pileStatusTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_charging_pile, parent, false)
        return PileViewHolder(view)
    }

    override fun onBindViewHolder(holder: PileViewHolder, position: Int) {
        val pile = getItem(position)
        holder.pileId.text = "ID: ${pile.id}"
        holder.pileName.text = pile.name
        holder.pileStatus.text = if (pile.isOnline) "Online" else "Offline"
    }

}

class ChargingPileDiffCallback : DiffUtil.ItemCallback<ChargingPile>() {
    override fun areItemsTheSame(oldItem: ChargingPile, newItem: ChargingPile): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ChargingPile, newItem: ChargingPile): Boolean {
        return oldItem == newItem
    }
}
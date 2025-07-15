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

/**
 * Adapter for displaying a list of ChargingPile items in a RecyclerView.
 * Uses ListAdapter with DiffUtil for efficient updates.
 *
 * @param onItemClick A lambda that will be called when a pile item is clicked.
 *
 * this is the charging item for the home page which connects to the item_charging_pile
 */
class ChargingPileAdapter(
    private val onItemClick: (ChargingPile) -> Unit
) : ListAdapter<ChargingPile, ChargingPileAdapter.PileViewHolder>(ChargingPileDiffCallback()) {

    /**
     * ViewHolder represents each item (charging pile) in the RecyclerView.
     */
    inner class PileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pileId: TextView = view.findViewById(R.id.pileIdTextView)
        val pileName: TextView = view.findViewById(R.id.pileNameTextView)
        val pileStatus: TextView = view.findViewById(R.id.pileStatusTextView)

        /**
         * Binds data and sets up the click listener for the item.
         */
        fun bind(pile: ChargingPile) {
            pileId.text = "ID: ${pile.id}"
            pileName.text = "Name: ${pile.name}"
            pileStatus.text = "Status: ${if (pile.isOnline) "Online" else "Offline"}"

            // Handle item click
            itemView.setOnClickListener {
                onItemClick(pile)
            }
        }
    }

    /**
     * Called when RecyclerView needs a new ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_charging_pile, parent, false)
        return PileViewHolder(view)
    }

    /**
     * Called to bind data to a ViewHolder at a given position.
     */
    override fun onBindViewHolder(holder: PileViewHolder, position: Int) {
        val pile = getItem(position)
        holder.bind(pile)
    }
}

/**
 * DiffUtil implementation to efficiently determine item differences.
 */
class ChargingPileDiffCallback : DiffUtil.ItemCallback<ChargingPile>() {
    override fun areItemsTheSame(oldItem: ChargingPile, newItem: ChargingPile): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ChargingPile, newItem: ChargingPile): Boolean {
        return oldItem == newItem
    }
}

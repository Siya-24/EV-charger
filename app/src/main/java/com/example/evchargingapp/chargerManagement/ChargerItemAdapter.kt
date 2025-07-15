package com.example.evchargingapp.chargerManagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.evchargingapp.R
import com.example.evchargingapp.data.ChargingPile

/**
 * Adapter for displaying charging piles in the Charger Management screen.
 * This adapter supports showing charger name, ID, status (online/offline),
 * and provides Rename/Delete action buttons for each item.
 */
class ChargerItemAdapter(
    private var chargerList: List<ChargingPile>,                 // List of ChargingPile objects to display
    private val onRename: (ChargingPile) -> Unit,                // Callback when Rename is clicked
    private val onDelete: (ChargingPile) -> Unit                 // Callback when Delete is clicked
) : RecyclerView.Adapter<ChargerItemAdapter.ChargerViewHolder>() {

    /**
     * ViewHolder that holds all the views in an individual item layout.
     */
    inner class ChargerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chargerName: TextView = itemView.findViewById(R.id.pileNameTextView)         // Charger name
        val pileIdTextView: TextView = itemView.findViewById(R.id.pileIdTextView)        // Charger ID
        val pileStatusTextView: TextView = itemView.findViewById(R.id.pileStatusTextView) // Online/Offline status label
        val statusDot: View = itemView.findViewById(R.id.statusDot)                      // Small color dot indicator
        val btnRename: ImageButton = itemView.findViewById(R.id.btnRename)               // Rename button
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)               // Delete button
    }

    /**
     * Inflate the layout for each charging pile card and create the ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChargerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_charger, parent, false)
        return ChargerViewHolder(view)
    }

    /**
     * Bind the data from a ChargingPile object to the views in the ViewHolder.
     */
    override fun onBindViewHolder(holder: ChargerViewHolder, position: Int) {
        val charger = chargerList[position]

        // Set the charger name
        holder.chargerName.text = charger.name

        // Set the charger ID
        holder.pileIdTextView.text = "ID: ${charger.id}"

        // Set the charger status text and color (Online/Offline)
        val isOnline = charger.isOnline
        holder.pileStatusTextView.text = if (isOnline) "Online" else "Offline"
        val color = if (isOnline) android.graphics.Color.parseColor("#00E676") else android.graphics.Color.RED
        holder.pileStatusTextView.setTextColor(color)
        holder.statusDot.backgroundTintList = android.content.res.ColorStateList.valueOf(color)

        // Hook up the Rename and Delete actions
        holder.btnRename.setOnClickListener { onRename(charger) }
        holder.btnDelete.setOnClickListener { onDelete(charger) }
    }

    /**
     * Return the number of charging piles in the list.
     */
    override fun getItemCount(): Int = chargerList.size

    /**
     * Update the list of charging piles and refresh the RecyclerView.
     */
    fun updateData(newList: List<ChargingPile>) {
        chargerList = newList
        notifyDataSetChanged()
    }
}

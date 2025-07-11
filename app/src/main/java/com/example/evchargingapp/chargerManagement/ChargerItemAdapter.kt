package com.example.evchargingapp.chargerManagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.evchargingapp.R
import com.example.evchargingapp.data.ChargingPile

//made this file use the charging pile data class

class ChargerItemAdapter(
    private var chargerList: List<ChargingPile>,
    private val onRename: (ChargingPile) -> Unit,
    private val onDelete: (ChargingPile) -> Unit
) : RecyclerView.Adapter<ChargerItemAdapter.ChargerViewHolder>() {

    inner class ChargerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chargerName: TextView = itemView.findViewById(R.id.tvChargerName)
        val btnRename: ImageButton = itemView.findViewById(R.id.btnRename)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChargerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_charger, parent, false)
        return ChargerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChargerViewHolder, position: Int) {
        val charger = chargerList[position]
        holder.chargerName.text = charger.name

        holder.btnRename.setOnClickListener { onRename(charger) }
        holder.btnDelete.setOnClickListener { onDelete(charger) }
    }

    override fun getItemCount(): Int = chargerList.size

    fun updateData(newList: List<ChargingPile>) {
        chargerList = newList
        notifyDataSetChanged()
    }
}

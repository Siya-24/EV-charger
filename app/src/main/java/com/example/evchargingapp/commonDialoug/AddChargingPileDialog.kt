package com.example.evchargingapp.common.dialogs

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import com.example.evchargingapp.R
import com.example.evchargingapp.data.ChargingPile

// Accepts any ViewModel with a matching function signature
class AddChargingPileDialog {
    companion object {
        /**
         * Shows the Add Charger dialog and triggers viewModel.addChargingPile(pile) when confirmed.
         *
         * @param context Activity or App context.
         * @param onAddPile A lambda that handles the ChargingPile creation (e.g., viewModel::addChargingPile)
         */
        fun show(
            context: Context,
            onAddPile: (ChargingPile) -> Unit
        ) {
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_charging_pile, null)

            val etPileId = view.findViewById<EditText>(R.id.etPileId)
            val etPileName = view.findViewById<EditText>(R.id.etPileName)
            val spinnerStatus = view.findViewById<Spinner>(R.id.spinnerStatus)
            val buttonAdd = view.findViewById<Button>(R.id.buttonAdd)
            val buttonCancel = view.findViewById<Button>(R.id.buttonCancel)

            // Set spinner options for Online/Offline
            val statuses = listOf("Online", "Offline")
            spinnerStatus.adapter = ArrayAdapter(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                statuses
            )

            val dialog = AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(false)
                .create()

            buttonCancel.setOnClickListener {
                dialog.dismiss()
            }

            buttonAdd.setOnClickListener {
                val id = etPileId.text.toString().trim()
                val name = etPileName.text.toString().trim()
                val isOnline = spinnerStatus.selectedItem.toString() == "Online"

                if (id.isEmpty() || name.isEmpty()) {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val newPile = ChargingPile(id = id, name = name, isOnline = isOnline)

                // ✅ ADD THIS LINE
                Log.d("AddDialog", "Creating pile: $newPile")

                onAddPile(newPile)  // This should trigger the ViewModel insert
                dialog.dismiss()
            }



            dialog.show()
        }
    }
}

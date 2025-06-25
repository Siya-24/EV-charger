package com.example.evchargingapp

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var databaseRef: DatabaseReference
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            userId = currentUser.uid
            databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

            checkChargingPileStatus()
        } else {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            finish()
        }

        findViewById<Button>(R.id.addButton).setOnClickListener {
            showAddPilePopup()
        }

        findViewById<Button>(R.id.homeBtn).setOnClickListener {
            Toast.makeText(this, "Home pressed", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.userBtn).setOnClickListener {
            Toast.makeText(this, "User pressed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkChargingPileStatus() {
        databaseRef.child("chargingPileId").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    showAddPilePopup()
                }
                // Else: already bound, do nothing
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DashboardActivity, "DB error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showAddPilePopup() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_pile, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)

        val dialog = dialogBuilder.create()

        dialogView.findViewById<Button>(R.id.cancelBtn).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.addPileBtn).setOnClickListener {
            bindChargingPile(dialog)
        }

        dialog.show()
    }

    private fun bindChargingPile(dialog: AlertDialog) {
        // Simulate adding a charging pile ID
        val dummyPileId = "pile_${System.currentTimeMillis()}"

        databaseRef.child("chargingPileId").setValue(dummyPileId)
            .addOnSuccessListener {
                Toast.makeText(this, "Charging pile added!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add charging pile: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

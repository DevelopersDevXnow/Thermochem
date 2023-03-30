package com.devxnow.thermochem

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.devxnow.thermochem.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

val database = FirebaseDatabase.getInstance()
val myRef = database.reference.child("1emb9P3AvPkBWtFSYt_PDl1VyaqDGDyjhO2haXufFy8U")


private lateinit var binding: ActivityMainBinding

// Create a list of temperature units
val units = listOf( "K","°C","°F" )

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initListeners()

        val gasList = ArrayList<String>()

        myRef.child("properties").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.child("name").value.toString()
                    gasList.add(user)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Firebase", "Failed to read value.", error.toException())
            }
        })

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, gasList)
        binding.gasElementDropdown.threshold = 1;
        binding.gasElementDropdown.setAdapter(adapter)


    }


    private fun initListeners() {

        // Add a listener to the spinner
        binding.unitSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Update the EditText's hint based on the selected unit
                val unit = units[position]
                binding.temperatureInput.hint = "Temperature ($unit)"

                // Change the minimum and maximum temperature based on the selected unit
                when (unit) {
                    "°C" -> {
                        binding.temperatureSlider.max = 3226
                        binding.temperatureSlider.min = -73
//                        binding.temperatureSlider.progress = 0
                        binding.minTemperature.text = "-73 °C"
                        binding.maxTemperature.text = "3226 °C"
                    }
                    "°F" -> {
                        binding.temperatureSlider.max = 5840
                        binding.temperatureSlider.min = -99
//                        binding.temperatureSlider.progress = 0
                        binding.minTemperature.text = "-99 °F"
                        binding.maxTemperature.text = "5840 °F"
                    }
                    "K" -> {
                        binding.temperatureSlider.max = 3500
                        binding.temperatureSlider.min = 200
//                        binding.temperatureSlider.progress = 200
                        binding.minTemperature.text = "200K"
                        binding.maxTemperature.text = "3500K"
                    }
                    // Add more cases for other temperature units as needed
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No action needed
            }
        }

        // Add a listener to the SeekBar
        binding.temperatureSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Update the EditText's value when the SeekBar changes
                if (fromUser) {
                    // Update the EditText's value when the SeekBar changes
                    val tempValue = progress
                    binding.temperatureInput.setText(tempValue.toString())
                    binding.temperatureInput.setSelection(binding.temperatureInput.text.length)
                }
              //  binding.temperatureInput.setText(progress.toString())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // No action needed
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // No action needed
            }
        })

// Add a listener to the EditText
        binding.temperatureInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No action needed
            }

            override fun afterTextChanged(s: Editable?) {
                // Update the SeekBar's value when the EditText changes
                val input = s.toString()
                if (!input.isEmpty()) {
                    val progress = if (input.endsWith(".")) {
                        input.substring(0, input.length - 1).toIntOrNull() ?: 0
                    } else {
                        input.toIntOrNull() ?: 0
                    }
                    if (progress in 200..3500) {
                        binding.temperatureSlider.progress = progress
                    }
                }
            }

        })

    }

}
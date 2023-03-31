package com.devxnow.thermochem

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.devxnow.thermochem.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.math.ln
import kotlin.math.pow


// Declare the variables outside the function to make them global
private var T1α1: String = ""
private var T1α2: String = ""
private var T1α3: String = ""
private var T1α4: String = ""
private var T1α5: String = ""
private var T1α6: String = ""
private var T1α7: String = ""
private var T2α1: String = ""
private var T2α2: String = ""
private var T2α3: String = ""
private var T2α4: String = ""
private var T2α5: String = ""
private var T2α6: String = ""
private var T2α7: String = ""

private var selectedTemperature = 200.0

val database = FirebaseDatabase.getInstance()
val myRef = database.reference.child("1emb9P3AvPkBWtFSYt_PDl1VyaqDGDyjhO2haXufFy8U")

private lateinit var binding: ActivityMainBinding

// Create a list of temperature units
val units = listOf("K", "°C", "°F")

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initListeners()


    }

    private fun calculateCp(
        T: Double,
        R: Double,
        alpha1: Double,
        alpha2: Double,
        alpha3: Double,
        alpha4: Double,
        alpha5: Double
    ): Double {
        val Cp = (alpha1 + alpha2 * T + alpha3 * T.pow(2.0) + alpha4 * T.pow(3.0) + alpha5 * T.pow(4.0)) * R
        return Cp
    }

    private fun calculateHt(
        T: Double,
        R: Double,
        alpha1: Double,
        alpha2: Double,
        alpha3: Double,
        alpha4: Double,
        alpha5: Double,
        alpha6: Double
    ): Double {
        val Ht =
            (alpha1 + alpha2 * T / 2 + alpha3 * T.pow(2) / 3 + alpha4 * T.pow(3) / 4 + alpha5 * T.pow(4) / 5 + alpha6 / T) * R * T
        return Ht
    }

    private fun calculateSt(
        T: Double,
        R: Double,
        alpha1: Double,
        alpha2: Double,
        alpha3: Double,
        alpha4: Double,
        alpha5: Double,
        alpha7: Double
    ): Double {
        val St = R * (alpha1 * ln(T) + alpha2 * T + alpha3 * T.pow(2) / 2 + alpha4 * T.pow(3) / 3 + alpha5 * T.pow(4) / 4 + alpha7)
        return St
    }


    private fun calculateGt(
        T: Double,
        Ht: Double,
        St: Double,

    ): Double {
        val Gt = Ht - selectedTemperature * St
        return Gt
    }



    private fun showResult() {


        if (!binding.gasElementDropdown.text.isNullOrEmpty()) {


            if (selectedTemperature > 1000) {

                val Cp = String.format("%.4f",calculateCp(
                    selectedTemperature,
                    8.314,
                    T1α1.toDouble(),
                    T1α2.toDouble(),
                    T1α3.toDouble(),
                    T1α4.toDouble(),
                    T1α5.toDouble()
                ))
                val Ht = String.format("%.4f",calculateHt(
                    selectedTemperature,
                    8.314,
                    T1α1.toDouble(),
                    T1α2.toDouble(),
                    T1α3.toDouble(),
                    T1α4.toDouble(),
                    T1α5.toDouble(),
                    T1α6.toDouble()
                ))

                val St = String.format("%.4f",calculateSt(
                    selectedTemperature,
                    8.314,
                    T1α1.toDouble(),
                    T1α2.toDouble(),
                    T1α3.toDouble(),
                    T1α4.toDouble(),
                    T1α5.toDouble(),
                    T1α7.toDouble()
                ))

                val Gt = String.format("%.4f",calculateGt(
                    selectedTemperature,
                    Ht.toDouble(),
                    St.toDouble()
                ))



                binding.specificHeatResult.text = "Cp:  $Cp J/mol K"
                binding.enthalpyResult.text = "Ht:  $Ht J/mol"
                binding.entropyResult.text = "St:  $St J/mol K"
                binding.gibbsFreeEnergyResult.text = "Gt:  $Gt J/mol "

            } else {

                val Cp = String.format("%.4f",calculateCp(
                    selectedTemperature,
                    8.314,
                    T2α1.toDouble(),
                    T2α2.toDouble(),
                    T2α3.toDouble(),
                    T2α4.toDouble(),
                    T2α5.toDouble()
                ))
                val Ht = String.format("%.4f",calculateHt(
                    selectedTemperature,
                    8.314,
                    T2α1.toDouble(),
                    T2α2.toDouble(),
                    T2α3.toDouble(),
                    T2α4.toDouble(),
                    T2α5.toDouble(),
                    T2α6.toDouble()
                ))

                val St = String.format("%.4f",calculateSt(
                    selectedTemperature,
                    8.314,
                    T2α1.toDouble(),
                    T2α2.toDouble(),
                    T2α3.toDouble(),
                    T2α4.toDouble(),
                    T2α5.toDouble(),
                    T2α7.toDouble()
                ))

                val Gt = String.format("%.4f",calculateGt(
                    selectedTemperature,
                    Ht.toDouble(),
                    St.toDouble()
                ))

                binding.specificHeatResult.text = "Cp:  $Cp J/mol K"
                binding.enthalpyResult.text = "Ht:  $Ht J/mol"
                binding.entropyResult.text = "St:  $St J/mol K"
                binding.gibbsFreeEnergyResult.text = "Gt:  $Gt J/mol "

            }

        } else {

            binding.specificHeatResult.text = "Cp: "
            binding.enthalpyResult.text = "St: "
            binding.entropyResult.text = "Ht:  "
            binding.gibbsFreeEnergyResult.text = "Gt: "
        }

    }


    private fun initListeners() {

        // Add a listener to the spinner
        binding.unitSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
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

//Select Gas Elements
        val gasList = ArrayList<String>()
        myRef.keepSynced(true)
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

        // set the on item click listener for the dropdown
        binding.gasElementDropdown.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                // get the selected gas element
                val selectedGasElement = adapter.getItem(position)

                // fetch data based on the selected gas element
                fetchData(selectedGasElement)
            }

        //gas element dropdown watch dog

        binding.gasElementDropdown.addTextChangedListener(object : TextWatcher {

            private var searchFor = ""
            private var searchRunnable = Runnable { fetchData(searchFor) }
            private val handler = Handler(Looper.getMainLooper())


            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No implementation needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No implementation needed
            }

            override fun afterTextChanged(s: Editable?) {
                // If gasElementDropdown is empty, clear the coefficient1TextView
                if (binding.gasElementDropdown.text.isNullOrEmpty()) {
                    T1α1 = ""
                    T1α2 = ""
                    T1α3 = ""
                    T1α4 = ""
                    T1α5 = ""
                    T1α6 = ""
                    T1α7 = ""
                    T2α1 = ""
                    T2α2 = ""
                    T2α3 = ""
                    T2α4 = ""
                    T2α5 = ""
                    T2α6 = ""
                    T2α7 = ""

                    binding.specificHeatResult.text = "Cp: "
                    binding.enthalpyResult.text = "St: "
                    binding.entropyResult.text = "Ht:  "
                    binding.gibbsFreeEnergyResult.text = "Gt: "

                    return
                }

                // Store the selected gas element
                val selectedGasElement = binding.gasElementDropdown.text.toString()

                // Cancel any previously queued searchRunnable
                handler.removeCallbacks(searchRunnable)

                // Set the searchFor string to the selectedGasElement
                searchFor = selectedGasElement

                // Queue a new searchRunnable with a delay of 500ms
                handler.postDelayed(searchRunnable, 500)

            }
        })


        // Add a listener to the SeekBar
        binding.temperatureSlider.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Update the EditText's value when the SeekBar changes
                if (fromUser) {
                    // Update the EditText's value when the SeekBar changes
                    val tempValue = progress
                    binding.temperatureInput.setText(tempValue.toString())
                    binding.temperatureInput.setSelection(binding.temperatureInput.text.length)
                    selectedTemperature = progress.toDouble()
                    showResult()
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

        // Set a click listener on the button
        binding.dialogButton.setOnClickListener {
// Create a new dialog
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.coefficients_dialog_layout)

            // Find the TextViews in the dialog
            val alpha1 = dialog.findViewById<TextView>(R.id.alpha_1)
            val alpha2 = dialog.findViewById<TextView>(R.id.alpha_2)
            val alpha3 = dialog.findViewById<TextView>(R.id.alpha_3)
            val alpha4 = dialog.findViewById<TextView>(R.id.alpha_4)
            val alpha5 = dialog.findViewById<TextView>(R.id.alpha_5)
            val alpha6 = dialog.findViewById<TextView>(R.id.alpha_6)
            val alpha7 = dialog.findViewById<TextView>(R.id.alpha_7)

// Set the text of the TextViews
            if (selectedTemperature > 1000) {

                alpha1.text = "$T1α1"
                alpha2.text = "$T1α2"
                alpha3.text = "$T1α3"
                alpha4.text = "$T1α4"
                alpha5.text = "$T1α5"
                alpha6.text = "$T1α6"
                alpha7.text = "$T1α7"
            } else {

                alpha1.text = "$T2α1"
                alpha2.text = "$T2α2"
                alpha3.text = "$T2α3"
                alpha4.text = "$T2α4"
                alpha5.text = "$T2α5"
                alpha6.text = "$T2α6"
                alpha7.text = "$T2α7"
            }


// Show the dialog
            dialog.show()


        }

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
                        selectedTemperature = progress.toDouble()
                        binding.temperatureSlider.progress = progress
                        showResult()
                    }
                }
            }

        })

    }

    private fun fetchData(selectedGasElement: String?) {
        if (selectedGasElement != null) {
//            val database = FirebaseDatabase.getInstance()
//            val myRef = database.getReference(selectedGasElement)
            myRef.keepSynced(true)
            myRef.child("properties").child(selectedGasElement)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        if (dataSnapshot.exists()) {

                            // handle the fetched data
                            // for example, update UI with the data
                            T1α1 = dataSnapshot.child("T1α1").value.toString()
                            T1α2 = dataSnapshot.child("T1α2").value.toString()
                            T1α3 = dataSnapshot.child("T1α3").value.toString()
                            T1α4 = dataSnapshot.child("T1α4").value.toString()
                            T1α5 = dataSnapshot.child("T1α5").value.toString()
                            T1α6 = dataSnapshot.child("T1α6").value.toString()
                            T1α7 = dataSnapshot.child("T1α7").value.toString()
                            T2α1 = dataSnapshot.child("T2α1").value.toString()
                            T2α2 = dataSnapshot.child("T2α2").value.toString()
                            T2α3 = dataSnapshot.child("T2α3").value.toString()
                            T2α4 = dataSnapshot.child("T2α4").value.toString()
                            T2α5 = dataSnapshot.child("T2α5").value.toString()
                            T2α6 = dataSnapshot.child("T2α6").value.toString()
                            T2α7 = dataSnapshot.child("T2α7").value.toString()

                            showResult()
                        } else {

                            T1α1 = ""
                            T1α2 = ""
                            T1α3 = ""
                            T1α4 = ""
                            T1α5 = ""
                            T1α6 = ""
                            T1α7 = ""
                            T2α1 = ""
                            T2α2 = ""
                            T2α3 = ""
                            T2α4 = ""
                            T2α5 = ""
                            T2α6 = ""
                            T2α7 = ""

                        }


                      //  Toast.makeText(this@MainActivity, T1α1, Toast.LENGTH_SHORT).show()
                        //  updateUI(specificHeat, enthalpy, entropy, gibbsFreeEnergy)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // handle the error
                        Log.w("firebase", "Failed to read value.", error.toException())
                    }
                })
        }
    }


}
package com.example.pogodaa

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuFragment : Fragment() {
    private val viewModel: WeatherViewModel by viewModels({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        val btnRefresh: Button = view.findViewById(R.id.btnRefresh)
        val btnChangeLocation: Button = view.findViewById(R.id.btnChangeLocation)
        val btnChangeUnits: Button = view.findViewById(R.id.btnChangeUnits)
        val btnAddLocation: Button = view.findViewById(R.id.btnAddLocation)
        val btnRemoveLocation: Button = view.findViewById(R.id.btnRemoveLocation)

        btnRefresh.setOnClickListener { viewModel.refreshWeatherData(requireContext()) }
        btnChangeLocation.setOnClickListener { showLocationDialog() }
        btnChangeUnits.setOnClickListener { showUnitDialog() }
        btnAddLocation.setOnClickListener { showAddLocationDialog() }
        btnRemoveLocation.setOnClickListener { showRemoveLocationDialog() }

        return view
    }

    private fun showLocationDialog() {
        val locations = AppSettings.favoriteLocations.toMutableList()
        locations.add(0, "Enter a custom location...")

        AlertDialog.Builder(requireContext()).apply {
            setTitle("Choose Location")
            setItems(locations.toTypedArray()) { dialog, which ->
                if (which == 0) {
                    showCustomLocationDialog()
                } else {
                    AppSettings.currentLocation = locations[which]
                    viewModel.refreshWeatherData(requireContext())
                }
                dialog.dismiss()
            }
            show()
        }
    }

    private fun showCustomLocationDialog() {
        val editText = EditText(context).apply {
            inputType = InputType.TYPE_CLASS_TEXT
            hint = "Type location name"
        }

        AlertDialog.Builder(requireContext()).apply {
            setTitle("Enter Location")
            setView(editText)
            setPositiveButton("Search") { dialog, _ ->
                val customLocation = editText.text.toString()
                if (customLocation.isNotEmpty()) {
                    fetchLocationSuggestions(customLocation) { suggestions ->
                        showLocationSuggestionsDialog(suggestions) { selectedLocation ->
                            val locationString = if (selectedLocation.state != null) {
                                "${selectedLocation.name}, ${selectedLocation.state}, ${selectedLocation.country}"
                            } else {
                                "${selectedLocation.name}, ${selectedLocation.country}"
                            }
                            AppSettings.currentLocation = locationString
                            viewModel.refreshWeatherData(requireContext())
                        }
                    }
                }
                dialog.dismiss()
            }
            setNegativeButton("Cancel", null)
            show()
        }
    }

    private fun showAddLocationDialog() {
        val editText = EditText(context)
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Add Favorite Location")
            setView(editText)
            setPositiveButton("Search") { dialog, _ ->
                val newLocation = editText.text.toString()
                if (newLocation.isNotEmpty()) {
                    fetchLocationSuggestions(newLocation) { suggestions ->
                        showLocationSuggestionsDialog(suggestions) { selectedLocation ->
                            val locationString = if (selectedLocation.state != null) {
                                "${selectedLocation.name}, ${selectedLocation.state}, ${selectedLocation.country}"
                            } else {
                                "${selectedLocation.name}, ${selectedLocation.country}"
                            }
                            AppSettings.favoriteLocations.add(locationString)
                            AppSettings.saveFavoriteLocations(requireContext())
                            Toast.makeText(requireContext(), "Location added", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                dialog.dismiss()
            }
            setNegativeButton("Cancel", null)
            show()
        }
    }


    private fun fetchLocationSuggestions(query: String, callback: (List<GeocodingResponse>) -> Unit) {
        RetrofitClient.instance.getLocations(query, 5, AppSettings.apiKey).enqueue(object : Callback<List<GeocodingResponse>> {
            override fun onResponse(call: Call<List<GeocodingResponse>>, response: Response<List<GeocodingResponse>>) {
                if (response.isSuccessful) {
                    val suggestions = response.body().orEmpty()
                    if (suggestions.isEmpty()) {
                        Toast.makeText(requireContext(), "No locations found", Toast.LENGTH_SHORT).show()
                    }
                    callback(suggestions)
                } else {
                    Log.e("MenuFragment", "Error fetching location suggestions: ${response.code()}")
                    callback(emptyList())
                }
            }

            override fun onFailure(call: Call<List<GeocodingResponse>>, t: Throwable) {
                Log.e("MenuFragment", "Failure: ${t.message}")
                callback(emptyList())
            }
        })
    }


    private fun showLocationSuggestionsDialog(suggestions: List<GeocodingResponse>, onSelect: (GeocodingResponse) -> Unit) {
        val suggestionNames = suggestions.map {
            if (it.state != null) {
                "${it.name}, ${it.state}, ${it.country}"
            } else {
                "${it.name}, ${it.country}"
            }
        }.toTypedArray()

        AlertDialog.Builder(requireContext()).apply {
            setTitle("Select Location")
            setItems(suggestionNames) { dialog, which ->
                onSelect(suggestions[which])
                dialog.dismiss()
            }
            show()
        }
    }



    private fun showRemoveLocationDialog() {
        val locations = AppSettings.favoriteLocations.toTypedArray()
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Remove Favorite Location")
            setItems(locations) { dialog, which ->
                AppSettings.favoriteLocations.remove(locations[which])
                AppSettings.saveFavoriteLocations(requireContext())
                Toast.makeText(requireContext(), "Location removed", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            show()
        }
    }

    private fun showUnitDialog() {
        if (!Utils.isOnline(requireContext())) {
            Toast.makeText(requireContext(), "Cannot change units while offline", Toast.LENGTH_SHORT).show()
            return
        }

        val units = arrayOf("Metric", "Imperial")
        val currentUnitIndex = if (AppSettings.units == "metric") 0 else 1

        AlertDialog.Builder(requireContext()).apply {
            setTitle("Select Units")
            setSingleChoiceItems(units, currentUnitIndex) { dialog, which ->
                AppSettings.units = if (which == 0) "metric" else "imperial"
                AppSettings.saveUnits(requireContext())
                viewModel.refreshWeatherData(requireContext())
                dialog.dismiss()
            }
            show()
        }
    }
}

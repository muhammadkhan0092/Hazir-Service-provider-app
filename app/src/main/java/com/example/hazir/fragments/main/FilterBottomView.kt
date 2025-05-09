package com.example.hazir.fragments.main


import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.hazir.data.GigData
import com.example.hazir.databinding.FilterBottomViewBinding
import com.example.hazir.utils.constants.allCategories
import com.example.hazir.viewModel.vm.CommentViewModel
import com.example.hazir.viewModel.vmf.CommentVmf
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class FilterBottomView(val gigs : List<GigData>,val c: Context) : BottomSheetDialogFragment() {
    private lateinit var binding : FilterBottomViewBinding
    private lateinit var city : String
    private lateinit var uniqueCities : List<String>
    private  var updatedGigs : MutableList<GigData> = mutableListOf()
    val viewModel by viewModels<CommentViewModel>{
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        CommentVmf(auth,firestore)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FilterBottomViewBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDistances()
    }

    private fun setDistances() {
        updatedGigs = sortByDistance(gigs).toMutableList()
        getUniqueCategories(updatedGigs,c)
        onClickListeners()
    }


    fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    fun sortByDistance(
        gigs: List<GigData>
    ): List<GigData> {
        val sharedPreferences = requireActivity().getSharedPreferences("locationData", MODE_PRIVATE)
        val lon = sharedPreferences.getFloat("lon",0f)
        val lat = sharedPreferences.getFloat("lat",0f)
        val newList : MutableList<GigData> = mutableListOf()
        gigs.forEach { gigs ->
            val dist = haversineDistance(lat.toDouble(), lon.toDouble(), gigs.locationData.latitiude, gigs.locationData.longitude)
            val newGig = gigs.copy(distance =  String.format("%.1f", dist).toDouble())
            newList.add(newGig)
        }
        newList.sortBy { it.distance }
        return newList
    }

    private fun onClickListeners() {
        onButtonClicked()
        onSpinnerClicked()
    }

    private fun onSpinnerClicked() {
        binding.spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, view: View?, position: Int, id: Long) {
                city = uniqueCities[position]
                Toast.makeText(requireContext(), "Selected: $city", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
            }
        }
    }

    private fun onButtonClicked() {
        binding.button12.setOnClickListener {
            val id = binding.radioGroup.checkedRadioButtonId
            if(id!=-1){
                val rb = binding.radioGroup.findViewById<RadioButton>(id)
                val text = rb.text.toString()
                val filtered = processForFilteration(updatedGigs,text,city)
                Log.d("khan","sending data $filtered")
                val result = Bundle().apply {
                    putParcelableArrayList("gigs", ArrayList(filtered))
                }
                parentFragmentManager.setFragmentResult("data", result)
                dismiss()
            }
        }
    }

    private fun processForFilteration(gigs: List<GigData>, filter: String, city: String) : List<GigData>{
        if(city=="All Cities"){
            Log.d("khan","gone to all cities")
            if(filter=="LOCATION"){
              return gigs.sortedBy { it.distance }
            }
            else
            {
                return gigs.sortedBy { it.startingPrice.toDouble() }
            }
        }
        else
        {
            Log.d("khan","gone to $city")
            val filterCitiesGig = getFilteredCities(gigs,city)
            if(filter=="LOCATION"){
                val sorted = filterCitiesGig.sortedBy { it.distance }
                return sorted
            }
            else
            {
                val sorted = filterCitiesGig.sortedBy { it.startingPrice }
                return sorted
            }
        }
    }



    private fun getFilteredCities(gigs: List<GigData>, selectedCity: String): List<GigData> {
        val filteredListByCity : MutableList<GigData> = mutableListOf()
        gigs.forEach {
            val cityReceived = getCityNameFromLocation(c,it.locationData.latitiude,it.locationData.longitude)
            if(cityReceived==selectedCity){
                filteredListByCity.add(it)
            }
        }
        return filteredListByCity
    }

    private fun getUniqueCategories(gigs: List<GigData>,c: Context) {
        val cities : MutableList<String> = mutableListOf("All Cities")
        gigs.forEach {
            val city = getCityNameFromLocation(c,it.locationData.latitiude,it.locationData.longitude)
            if(city!=null){
                cities.add(city)
            }
        }
        uniqueCities = cities.distinct()
        setupCategorySpinner(uniqueCities)
    }

    fun getCityNameFromLocation(context: Context, latitude: Double, longitude: Double): String? {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            if (!addresses.isNullOrEmpty()) {
                addresses[0].locality
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun setupCategorySpinner(uniqueCities: List<String>) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            uniqueCities
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner2.adapter = adapter
    }



}
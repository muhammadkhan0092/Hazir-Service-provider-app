package com.example.hazir.fragments.intro

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.hazir.R
import com.example.hazir.data.LocationData
import com.example.hazir.databinding.FragmentLocationBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class FragmentLocation : Fragment(){
    private lateinit var binding: FragmentLocationBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClickListener()

    }

    private fun onClickListener() {
        binding.button7.setOnClickListener {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }


    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            getUserLocation()
        } else {
            Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private fun getUserLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                Log.d("khan","log : ${longitude}")
                Log.d("khan","lat : ${latitude}")
                val loc = LocationData(longitude,latitude)
                val bundle = Bundle().also {
                    it.putParcelable("location",loc)
                }
                findNavController().navigate(R.id.action_fragmentLocation_to_fragmentGoToSignIn,bundle)
              //  openGoogleMaps(latitude, longitude)
                Toast.makeText(requireContext(), "Lat: $latitude, Lng: $longitude", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "Failed to get location", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Error getting location: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGoogleMaps(latitude: Double, longitude: Double) {
        val uri = "geo:$latitude,$longitude?q=$latitude,$longitude(Current Location)"
        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        //  Remove this: mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }


    private fun startNavigation(destinationLat: Double, destinationLng: Double) {
        val uri = "google.navigation:q=$destinationLat,$destinationLng&mode=d" // 'd' for driving mode
        val navIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        navIntent.setPackage("com.google.android.apps.maps")
        if (navIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(navIntent)
        } else {
            Toast.makeText(requireContext(), "Google Maps app is not installed", Toast.LENGTH_SHORT).show()
        }
    }






}
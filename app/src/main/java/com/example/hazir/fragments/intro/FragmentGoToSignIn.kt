package com.example.hazir.fragments.intro


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.hazir.R
import com.example.hazir.data.LocationData
import com.example.hazir.databinding.FragmentGoToSignInBinding


class FragmentGoToSignIn : Fragment(){
    private lateinit var binding: FragmentGoToSignInBinding
    private val navArgs by navArgs<FragmentGoToSignInArgs>()
    private var locationData : LocationData? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGoToSignInBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
        onClickListeners()

    }

    private fun onClickListeners() {
        binding.button2.setOnClickListener {
            val bundle = Bundle().also {
                it.putParcelable("location",locationData)
            }
            findNavController().navigate(R.id.action_fragmentGoToSignIn_to_fragmentSignIn,bundle)
        }
        binding.imageView4.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun getData() {
        locationData = navArgs.location
        Log.d("khan","location received in goto sign in ${locationData}")
    }


}
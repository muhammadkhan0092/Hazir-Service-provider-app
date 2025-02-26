package com.example.hazir.fragments.main


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.hazir.R

import com.example.hazir.databinding.FragmentIntroABinding
import com.example.hazir.databinding.FragmentProfileBinding
import com.example.hazir.databinding.FragmentSplashScreenBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FragmentProfile : Fragment(){
    private lateinit var binding: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClickListeners()

    }

    private fun onClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tvEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentProfile_to_fragmentEditProfile)
        }

    }


}
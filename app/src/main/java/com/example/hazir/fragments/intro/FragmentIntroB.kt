package com.example.hazir.fragments.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.hazir.R
import com.example.hazir.databinding.FragmentIntroBBinding


class FragmentIntroB : Fragment(){
    private lateinit var binding: FragmentIntroBBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntroBBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnGetStarted.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentIntroMain_to_fragmentLocation)
        }
    }



}
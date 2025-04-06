package com.example.hazir.fragments.intro


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.hazir.R
import com.example.hazir.activity.MainActivity

import com.example.hazir.databinding.FragmentSplashScreenBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FragmentSplashScreen : Fragment(){
    private lateinit var binding: FragmentSplashScreenBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashScreenBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isLoggedIn()
    }

    private fun isLoggedIn() {
        if(FirebaseAuth.getInstance().currentUser != null){
            Log.d("khan","user is ${FirebaseAuth.getInstance().uid}")
            val intent = Intent(requireContext(),MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        else
        {
                CoroutineScope(Dispatchers.IO).launch {
                    delay(2000)
                    withContext(Dispatchers.Main){
                        findNavController().navigate(R.id.action_fragmentSplashScreen_to_fragmentIntroMain)
                    }
                }
        }
    }
}
package com.example.hazir.fragments.intro


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.hazir.R
import com.example.hazir.activity.MainActivity
import com.example.hazir.data.LocationData

import com.example.hazir.databinding.FragmentSignInBinding
import com.example.hazir.utils.Resource
import com.example.hazir.viewModel.vm.SignInViewModel
import com.example.hazir.viewModel.vm.SignUpViewModel
import com.example.hazir.viewModel.vmf.SignInFactory
import com.example.hazir.viewModel.vmf.SignUpFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FragmentSignIn : Fragment(){
    private lateinit var binding: FragmentSignInBinding
    private var locationData : LocationData? = null
    private val navArgs by navArgs<FragmentSignInArgs>()
    val viewModel by viewModels<SignInViewModel>{
        val firstore = FirebaseFirestore.getInstance()
        val firebaseAuth = FirebaseAuth.getInstance()
        SignInFactory(firebaseAuth)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
        onClickListeners()
        observeOnLogin()
    }

    private fun getData() {
        locationData = navArgs.location
        Log.d("khan","location received in sign in ${locationData}")
    }

    private fun observeOnLogin() {
        lifecycleScope.launch {
            viewModel.login.collectLatest {
                when (it) {
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Try Again later", Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.INVISIBLE
                    }
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.INVISIBLE
                        val intent = Intent(requireContext(),MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }

    private fun onClickListeners() {
        binding.textView9.setOnClickListener {
            val bundle = Bundle().also {
                it.putParcelable("location",locationData)
            }
            findNavController().navigate(R.id.action_fragmentSignIn_to_fragmentSignUp,bundle)
        }
        binding.imageView6.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSignIn.setOnClickListener {
            val email = binding.editTextText.text.toString()
            val pass = binding.etPass.text.toString()
            viewModel.loginWithEmailAndPass(email,pass)
        }
    }


}
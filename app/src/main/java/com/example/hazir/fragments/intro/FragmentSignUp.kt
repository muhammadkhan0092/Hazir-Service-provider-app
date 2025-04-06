package com.example.hazir.fragments.intro


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.hazir.R
import com.example.hazir.activity.MainActivity
import com.example.hazir.data.LocationData

import com.example.hazir.databinding.FragmentSignUpBinding
import com.example.hazir.utils.Resource
import com.example.hazir.viewModel.vm.SignUpViewModel
import com.example.hazir.viewModel.vmf.SignUpFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FragmentSignUp : Fragment(){
    private lateinit var binding: FragmentSignUpBinding
    private val navArgs by navArgs<FragmentSignUpArgs>()
    private var locationData : LocationData? = null
    val viewModel by viewModels<SignUpViewModel>{
        val firstore = FirebaseFirestore.getInstance()
        val firebaseAuth = FirebaseAuth.getInstance()
        SignUpFactory(firebaseAuth,firstore)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
        onClickListeners()
        observeSignUp()

    }
    private fun getData() {
        locationData = navArgs.location
        Log.d("khan","location received in sign up ${locationData}")
    }

    private fun observeSignUp() {
        lifecycleScope.launch {
            viewModel.register.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.INVISIBLE
                    }
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.INVISIBLE
                        setupName()
                    }
                    is Resource.Unspecified -> {}

                }

            }
        }
    }

    private fun setupName() {
        val sharedPreferences = requireActivity().getSharedPreferences("mydata", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("name", binding.etName.text.toString())
        editor.apply()
        navigate()
    }

    private fun navigate() {
        Toast.makeText(requireContext(), "Sign Up Successfull", Toast.LENGTH_SHORT).show()
        val intent = Intent(requireContext(),MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun onClickListeners() {
        binding.btnSignUp.setOnClickListener {
            binding.apply {
                val username = etUserName.text.toString().trim()
                val name = etName.text.toString().trim()
                val cnic = etCnic.text.toString().trim()
                val pass = etPass.text.toString().trim()
                val city = etCity.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val phone = etPhone.text.toString().trim()
                if(locationData!=null) {
                    viewModel.registerUser(
                        name = name,
                        username = username,
                        email = email,
                        password = pass,
                        cnic = cnic,
                        city = city,
                        phone = phone,
                        locationData = locationData!!
                    )
                }
                else{
                    Toast.makeText(requireContext(), "LOCATION NOT FETCHED", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.textView12.setOnClickListener {
            val bundle = Bundle().also {
                it.putParcelable("location",locationData)
            }
            findNavController().navigate(R.id.action_fragmentSignUp_to_fragmentSignIn,bundle)
        }
        binding.imageView10.setOnClickListener {
            findNavController().navigateUp()
        }
    }


}
package com.example.hazir.fragments.main


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.hazir.R
import com.example.hazir.activity.IntroActivity
import com.example.hazir.activity.MainActivity
import com.example.hazir.data.UserData
import com.example.hazir.databinding.FragmentProfileBinding
import com.example.hazir.utils.Resource
import com.example.hazir.viewModel.vm.ProfileViewModel
import com.example.hazir.viewModel.vmf.ProfileFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FragmentProfile : Fragment(){
    private lateinit var binding: FragmentProfileBinding
    private lateinit var userData: UserData
    val viewModel by viewModels<ProfileViewModel>{
        val firstore = FirebaseFirestore.getInstance()
        val firebaseAuth = FirebaseAuth.getInstance()
        ProfileFactory(firstore,firebaseAuth)
    }
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
        hideBnB()
        onClickListeners()
        observeLogout()
        observeUser()
    }

    private fun observeUser() {
        lifecycleScope.launch {
            viewModel.getUser.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.progressBar15.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        binding.progressBar15.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar15.visibility = View.INVISIBLE
                        userData = it.data!!
                        setData(it.data!!)
                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }

    private fun setData(data: UserData) {
        binding.textView19.text = data.name
        binding.textView20.text = data.username
        if(!data.image.isNullOrEmpty()){
            Glide.with(requireContext()).load(data.image).into(binding.ivProfile)
        }
    }

    private fun hideBnB() {
        (activity as MainActivity).binding.bottomNavigationView.visibility = View.VISIBLE
    }

    private fun observeLogout() {
        lifecycleScope.launch {
            viewModel.sendLogout.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.progressBar15.visibility = View.INVISIBLE
                    }
                    is Resource.Loading -> {
                        binding.progressBar15.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar15.visibility = View.INVISIBLE
                        val intent = Intent(requireContext(),IntroActivity::class.java)
                        intent.putExtra("from","logout")
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
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tvEditProfile.setOnClickListener {
            val bundle = Bundle().also {
                it.putParcelable("user",userData)
            }
            findNavController().navigate(R.id.action_fragmentProfile_to_fragmentEditProfile,bundle)
        }
        binding.ivLogout.setOnClickListener {
            viewModel.logout()
        }
        binding.tvLogout.setOnClickListener {
            viewModel.logout()
        }

    }


}
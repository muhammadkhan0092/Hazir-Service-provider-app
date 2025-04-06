package com.example.hazir.fragments.main


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.hazir.data.UserData
import com.example.hazir.databinding.FragmentEditProfileBinding

import com.example.hazir.utils.Resource
import com.example.hazir.viewModel.vm.EditProfileViewModel
import com.example.hazir.viewModel.vmf.EditProfileFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FragmentEditProfile : Fragment(){
    private lateinit var binding: FragmentEditProfileBinding
    private val navArgs by navArgs<FragmentEditProfileArgs>()
    private lateinit var uri: Uri
    private var realPath : String? = null
    private val viewModel by viewModels<EditProfileViewModel>{
        val firstore = FirebaseFirestore.getInstance()
        val firebaseStorage = FirebaseStorage.getInstance()
        EditProfileFactory(firstore,firebaseStorage)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
        onClickListeners()
    }


    private fun setUserData(user: UserData) {
        binding.apply {
            etCity.setText(user.city)
            etEmail.setText(user.email)
            etProfession.setText(user.username)
            if(user.image.isNullOrBlank()){

            }
            else{
                Glide.with(requireContext()).load(user.image).into(binding.ivProfile)
            }
        }
    }

    private fun getData() {
       val user = navArgs.user
        setUserData(user)
    }

    private fun onClickListeners() {
        binding.ivProfile.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            pickProfileImage.launch(intent)
        }
        binding.btnUpdate.setOnClickListener {

        }
    }

    val pickProfileImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val intent = it.data
        val imageUri = intent?.data
        imageUri?.let {
            uri  = it
            realPath = viewModel.getRealPathFromUri(uri,requireActivity())
            if(realPath!=null){

            }
        }
    }



}
package com.example.hazir.fragments.main


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.example.hazir.activity.MainActivity
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
    private lateinit var user: UserData
    private val navArgs by navArgs<FragmentEditProfileArgs>()
    private  var uri: Uri? = null
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
        observeUpdateUser()
    }

    private fun observeUpdateUser() {
        lifecycleScope.launch {
            viewModel.updateUser.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.progressBar9.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        Log.d("khan","horya")
                        binding.progressBar9.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar9.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), "Update Successfull", Toast.LENGTH_SHORT).show()
                        setName()
                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }

    private fun setName() {
        val sharedPreferences = requireActivity().getSharedPreferences("mydata", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("name", binding.editTextText2.text.toString())
        editor.apply()
    }


    private fun setUserData(user: UserData) {
        binding.apply {
            binding.etCity.text = Editable.Factory.getInstance().newEditable(user.city)
            binding.etEmail.text = Editable.Factory.getInstance().newEditable(user.username)
            binding.editTextText2.text = Editable.Factory.getInstance().newEditable(user.name)
            if(user.image.isNullOrBlank()){

            }
            else{
                Glide.with(requireContext()).load(user.image).into(binding.ivProfile)
            }
        }
    }

    private fun getData() {
        (activity as MainActivity).binding.bottomNavigationView.visibility = View.GONE
        user = navArgs.user
        setUserData(user)
    }

    private fun onClickListeners() {
        binding.ivProfile.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            pickProfileImage.launch(intent)
        }
        binding.btnUpdate.setOnClickListener {
            val city = binding.etCity.text.toString()
            val userName = binding.etEmail.text.toString()
            val name = binding.editTextText2.text.toString()
            if(uri==null){
                if(city==user.city && userName==user.username && name==user.name){
                    Toast.makeText(requireContext(), "Please update the information", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    val data = user.copy(city = city, username = userName, name = name)
                    viewModel.updateUser(data)
                }
            }
            else
            {
                if(realPath!=null){
                    viewModel.uploadToCloudinary(
                        realPath!!,
                        requireContext()
                    ) {
                        val data =
                            user.copy(city = city, username = userName, name = name, image = it)
                        viewModel.updateUser(data)
                    }
                }
                else
                {
                    Toast.makeText(requireContext(), "Could not uplaod image . Try again later", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val pickProfileImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val intent = it.data
        val imageUri = intent?.data
        imageUri?.let {
            uri  = it
            Glide.with(requireContext()).load(uri).into(binding.ivProfile)
            realPath = viewModel.getRealPathFromUri(uri,requireActivity())
            if(realPath!=null){

            }
        }
    }



}
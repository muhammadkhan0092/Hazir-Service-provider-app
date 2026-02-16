package com.example.hazir.fragments.main
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.hazir.databinding.FragmentCreatePostBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FragmentCreatePost : Fragment(){
    private  var uri : Uri? = null
    private lateinit var binding: FragmentCreatePostBinding
    private var content : String? = null
    //val viewModel by viewModels<CreatePostViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreatePostBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        onClickListeners()
//        observeImageUpload()
//        observeDataUpload()
    }

//    private fun observeDataUpload() {
//        lifecycleScope.launch {
//            viewModel.sendPost.collectLatest {
//                when(it){
//                    is Resource.Error -> {
//                        binding.progressBar12.visibility = View.INVISIBLE
//                        Toast.makeText(requireContext(), "cannot create post", Toast.LENGTH_SHORT).show()
//                    }
//                    is Resource.Loading -> {
//                        binding.progressBar12.visibility = View.VISIBLE
//                    }
//                    is Resource.Success -> {
//                        binding.progressBar12.visibility = View.VISIBLE
//                        Toast.makeText(requireContext(), "POST CREATED SUCCESSFULLY", Toast.LENGTH_SHORT).show()
//                        findNavController().navigate(R.id.action_fragmentCreatePost_to_fragmentHome)
//                    }
//                    is Resource.Unspecified -> {
//
//                    }
//                }
//            }
//        }
//    }
//
//    private fun observeImageUpload() {
//        lifecycleScope.launch {
//            viewModel.sendProfile.collectLatest {
//                when(it){
//                    is Resource.Error -> {
//                        binding.progressBar12.visibility = View.VISIBLE
//                        Toast.makeText(requireContext(), "cannot create post", Toast.LENGTH_SHORT).show()
//                    }
//                    is Resource.Loading -> {
//                        binding.progressBar12.visibility = View.VISIBLE
//                    }
//                    is Resource.Success -> {
//                        val url = it.data
//                        Log.d("khan","download urls is ${url}")
//                        Log.d("khan","content is ${content}")
//                        if(url!=null){
//                            viewModel.setPost(url,content!!)
//                        }
//                        else
//                        {
//                            Toast.makeText(requireContext(), "Could not post", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                    is Resource.Unspecified -> {
//
//                    }
//                }
//            }
//        }
//    }
//
//    private fun onClickListeners() {
//        onButtonClick()
//        onImageCliked()
//    }
//
//    private fun onImageCliked() {
//        binding.imageView37.setOnClickListener {
//            imageIntent()
//        }
//        binding.textView45.setOnClickListener {
//            imageIntent()
//        }
//    }
//
//    private fun onButtonClick() {
//        binding.button8.setOnClickListener {
//            content = binding.editTextText6.text.toString()
//            if(content.isNullOrEmpty()){
//                Toast.makeText(requireContext(), "Fill the content", Toast.LENGTH_SHORT).show()
//            }
//            else if(uri==null){
//                Toast.makeText(requireContext(), "Select an image", Toast.LENGTH_SHORT).show()
//            }
//            else
//            {
//                val realPath = viewModel.getRealPathFromUri(uri,requireActivity())
//                if(realPath!=null){
//                    viewModel.uploadToCloudinary(realPath,requireContext())
//                }
//                else
//                {
//                    Toast.makeText(requireContext(), "Could not create post", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
//
//
//
//    val pickProfileImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//        val intent = it.data
//        val imageUri = intent?.data
//        imageUri?.let {
//            uri  = it
//            Glide.with(requireContext()).load(uri).into(binding.imageView37)
//        }
//    }
//    private fun imageIntent() {
//        val intent  = Intent(ACTION_GET_CONTENT)
//        intent.type = "image/*"
//        pickProfileImage.launch(intent)
//    }





}
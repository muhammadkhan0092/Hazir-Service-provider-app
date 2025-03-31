import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
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
import androidx.navigation.fragment.findNavController
import com.cloudinary.android.MediaManager
import com.example.hazir.R
import com.example.hazir.databinding.FragmentCreatePostBinding
import com.example.hazir.utils.Resource
import com.example.hazir.viewModel.vm.CreatePostViewModel
import com.example.hazir.viewModel.vmf.CreatePostFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FragmentCreatePost : Fragment(){
    private  var uri : Uri? = null
    private lateinit var binding: FragmentCreatePostBinding
    private var content : String? = null
    val viewModel by viewModels<CreatePostViewModel>{
        val firstore = FirebaseFirestore.getInstance()
        val firebaseAuth = FirebaseAuth.getInstance()
        CreatePostFactory(firstore,firebaseAuth)
    }
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
        initConfig()
        onClickListeners()
        observeImageUpload()
        observeDataUpload()
    }

    private fun observeDataUpload() {
        lifecycleScope.launch {
            viewModel.sendPost.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.progressBar12.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), "cannot create post", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        binding.progressBar12.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar12.visibility = View.VISIBLE
                        Toast.makeText(requireContext(), "POST CREATED SUCCESSFULLY", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_fragmentCreatePost_to_fragmentHome)
                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }

    private fun observeImageUpload() {
        lifecycleScope.launch {
            viewModel.sendProfile.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.progressBar12.visibility = View.VISIBLE
                        Toast.makeText(requireContext(), "cannot create post", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        binding.progressBar12.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        val url = it.data
                        Log.d("khan","download urls is ${url}")
                        Log.d("khan","content is ${content}")
                        if(url!=null){
                            viewModel.setPost(url,content!!)
                        }
                        else
                        {
                            Toast.makeText(requireContext(), "Could not post", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }

    private fun onClickListeners() {
        onButtonClick()
        onImageCliked()
    }

    private fun onImageCliked() {
        binding.imageView37.setOnClickListener {
            imageIntent()
        }
        binding.textView45.setOnClickListener {
            imageIntent()
        }
    }

    private fun onButtonClick() {
        binding.button8.setOnClickListener {
            content = binding.editTextText6.text.toString()
            if(content.isNullOrEmpty()){
                Toast.makeText(requireContext(), "Fill the content", Toast.LENGTH_SHORT).show()
            }
            else if(uri==null){
                Toast.makeText(requireContext(), "Select an image", Toast.LENGTH_SHORT).show()
            }
            else
            {
                val realPath = viewModel.getRealPathFromUri(uri,requireActivity())
                if(realPath!=null){
                    viewModel.uploadToCloudinary(realPath,requireContext())
                }
                else
                {
                    Toast.makeText(requireContext(), "Could not create post", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initConfig() {
        val config = mapOf(
            "cloud_name" to "djd7stvwg",
            "api_key" to "138931765972126",
            "api_secret" to "LVzZS46qrFQiVRuXsjjEEHbRptE",
            "secure" to true
        )
        MediaManager.init(requireContext(),config)
    }

    val pickProfileImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val intent = it.data
        val imageUri = intent?.data
        imageUri?.let {
            uri  = it
        }
    }
    private fun imageIntent() {
        val intent  = Intent(ACTION_GET_CONTENT)
        intent.type = "image/*"
        pickProfileImage.launch(intent)
    }





}
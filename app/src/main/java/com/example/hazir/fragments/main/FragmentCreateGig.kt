package com.example.hazir.fragments.main


import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cloudinary.android.MediaManager
import com.example.hazir.R
import com.example.hazir.adapters.ImagesAdapter
import com.example.hazir.adapters.ServiceAdapter
import com.example.hazir.data.GigData
import com.example.hazir.data.ImageData
import com.example.hazir.databinding.FragmentCreateGigBinding
import com.example.hazir.utils.Resource
import com.example.hazir.utils.constants.categories
import com.example.hazir.viewModel.vm.CreateGigViewModel
import com.example.hazir.viewModel.vmf.CreateGigFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID

class FragmentCreateGig : Fragment(){
    private lateinit var binding: FragmentCreateGigBinding
    private lateinit var serviceAdapter: ServiceAdapter
    private lateinit var profilePicUrl : String
    val newUris = mutableListOf<Uri>()
    private lateinit var imagesAdapter: ImagesAdapter
    private val services = mutableListOf<String>()
    var selectedCategory : String =""
    private lateinit var uri : Uri
    val viewModel by viewModels<CreateGigViewModel>{
        val firstore = FirebaseFirestore.getInstance()
        val firebaseStorage = FirebaseStorage.getInstance()
        CreateGigFactory(firstore,firebaseStorage)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initConfig()
        binding = FragmentCreateGigBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupServicesAdapter()
        setupImagesAdapter()
        onClickListeners()
        onAddClick()
        onItemDelete()
        observeCreateGig()
        observeProfilePictureUpload()
        setupCategorySpinner()
        onSpinnerClickListener()
    }

    private fun onSpinnerClickListener() {
        binding.etCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, view: View?, position: Int, id: Long) {
               selectedCategory = categories[position]
                Toast.makeText(requireContext(), "Selected: $selectedCategory", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
            }
        }
    }

    private fun setupCategorySpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categories
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.etCategory.adapter = adapter
    }

    private fun observeProfilePictureUpload() {
        lifecycleScope.launch {
            viewModel.sendProfile.collectLatest {
                when (it) {
                    is Resource.Error -> {

                    }
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        profilePicUrl = it.data.toString()
                    }
                    is Resource.Unspecified -> {

                    }

                }
            }
        }
    }
    private fun observeCreateGig() {
        lifecycleScope.launch {
            viewModel.createGig.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.INVISIBLE
                    }
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), "Gig Created Successfully", Toast.LENGTH_SHORT).show()
                      //  findNavController().popBackStack()
                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }

    private fun onItemDelete() {
        imagesAdapter.onClick = { item ->
            val updatedList = imagesAdapter.differ.currentList.toMutableList()
            updatedList.remove(item)
            imagesAdapter.differ.submitList(updatedList)
        }

        serviceAdapter.onClick = { item ->
            val updatedList = serviceAdapter.differ.currentList.toMutableList()
            updatedList.remove(item)
            serviceAdapter.differ.submitList(updatedList)
        }
        binding.imageView18.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    val pickProfileImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val intent = it.data
        val imageUri = intent?.data
        imageUri?.let {
           uri  = it
            val realPath = viewModel.getRealPathFromUri(uri,requireActivity())
            if(realPath!=null){
                viewModel.uploadToCloudinary(realPath,requireContext(),{

                },true)
            }
        }
    }
    val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val intent = it.data
        val newImages = mutableListOf<ImageData>()
        if (intent?.clipData != null) {
            val count = intent.clipData?.itemCount ?: 0
            (0 until count).forEach {
                val imageUri = intent.clipData?.getItemAt(it)?.uri
                imageUri?.let { uri ->
                    newUris.add(uri)
                    newImages.add(ImageData(uri, R.drawable.ic_delete))
                }
            }
        } else {
            val imageUri = intent?.data
            imageUri?.let {
                newImages.add(ImageData(it, R.drawable.ic_delete))
                newUris.add(imageUri)

            }
        }
        if (newImages.isNotEmpty()) {
            val currentImages = imagesAdapter.differ.currentList.toMutableList()
            currentImages.addAll(newImages)
            imagesAdapter.differ.submitList(currentImages)
        }
    }


    private fun onClickListeners() {
        binding.cvAddImages.setOnClickListener {
            val intent  = Intent(ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }
        binding.cardView8.setOnClickListener {
            val intent  = Intent(ACTION_GET_CONTENT)
            intent.type = "image/*"
            pickProfileImage.launch(intent)
        }
        binding.btnSubmit.setOnClickListener {
            val remainingUploads = newUris.size
            var uploadsCompleted = 0

            newUris.forEach { uri ->
                val realPath = viewModel.getRealPathFromUri(uri, requireActivity())
                if (realPath != null) {
                    viewModel.uploadToCloudinary(realPath, requireContext(), {
                        uploadsCompleted++
                        if (uploadsCompleted == remainingUploads) {
                            val id = generateId()
                            val uuid = FirebaseAuth.getInstance().uid
                            val image = profilePicUrl
                            val rating = 0.0
                            val startingPrice = binding.etPrice.text.toString()
                            val description = binding.etDescription.text.toString()
                            val totalOrders = 0
                            val title = selectedCategory
                            val list = serviceAdapter.differ.currentList
                            val images = viewModel.downloadUrls
                            val gigData = GigData(id,uuid,image,images,totalOrders,title,description,startingPrice,list,
                                mutableListOf()
                            )
                            viewModel.createGig(gigData)
                        }
                    },false)
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





    private fun onAddClick() {
        binding.btnService.setOnClickListener {
            val service = binding.etService.text.toString()
            if (service.isNotEmpty()) {
                if (!services.contains(service)) {
                    val newList = services + service
                    serviceAdapter.differ.submitList(newList)
                    services.clear()
                    services.addAll(newList)
                }
            }
        }
    }
    private fun setupImagesAdapter() {
        serviceAdapter = ServiceAdapter()
        binding.rv.adapter = serviceAdapter
        binding.rv.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
    }
    private fun setupServicesAdapter() {
        imagesAdapter = ImagesAdapter()
        binding.rvImages.adapter = imagesAdapter
        binding.rvImages.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
    }
    fun generateId() : String{
        return UUID.randomUUID().toString()
    }

}
package com.example.hazir.fragments.main
import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.hazir.R
import com.example.hazir.activity.MainActivity
import com.example.hazir.adapters.MessageDetailAdapter
import com.example.hazir.data.GigData
import com.example.hazir.data.MessageModel
import com.example.hazir.data.SingleMessage
import com.example.hazir.databinding.DialogSelectGigBinding
import com.example.hazir.databinding.FragmentMessageDetailBinding
import com.example.hazir.utils.Resource
import com.example.hazir.utils.VerticalDecoration
import com.example.hazir.viewModel.vm.MessageDetailViewModel
import com.example.hazir.viewModel.vmf.MessageDetailFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FragmentMessageDetail : Fragment(){
    private lateinit var binding: FragmentMessageDetailBinding
    private lateinit var dialog: Dialog
    private lateinit var messageDetailAdapter: MessageDetailAdapter
    private lateinit var dialogBinding : DialogSelectGigBinding
    private var authId = FirebaseAuth.getInstance().uid.toString()
    private var messages : MutableList<SingleMessage> = mutableListOf()
    private lateinit var messageModel: MessageModel
    private   var  list : MutableList<String> = mutableListOf()
    private   var  gigList : List<GigData> = listOf()
    private var currentGigSelected : Int = 0
    val viewModel by viewModels<MessageDetailViewModel>{
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        MessageDetailFactory(auth,firestore)
    }
    private val navArgs by navArgs<FragmentMessageDetailArgs>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessageDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         setupRecyclerView()
         hideBottomNav()
         onClickListeners()
         observeSendMessage()
         onSendClick()
         retreiveMessages()
         observeStatus()
         observeGetGigs()
         onLocationClicked()
         observeUserLocation()
    }

    private fun observeUserLocation() {
        lifecycleScope.launch {
            viewModel.getUser.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.progressBar5.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), "Error fetching location", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        binding.progressBar5.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        val data = it.data
                        if(data!=null){
                            openGoogleMaps(data.locationData.latitiude,data.locationData.longitude)
                        }
                        binding.progressBar5.visibility = View.INVISIBLE
                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }

    private fun onLocationClicked() {
        binding.imageView34.setOnClickListener {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            getUserLocation()
        } else {
            Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private fun getUserLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        viewModel.getUser(FirebaseAuth.getInstance().uid.toString())
    }

    private fun openGoogleMaps(latitude: Double, longitude: Double) {
        val uri = "geo:$latitude,$longitude?q=$latitude,$longitude(Current Location)"
        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(mapIntent)
    }


    private fun observeStatus() {
        lifecycleScope.launch {
            viewModel.sendStatus.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                        binding.progressBar5.visibility = View.INVISIBLE
                    }
                    is Resource.Loading -> {
                        binding.progressBar5.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        messageModel = it.data!!
                        binding.btnType.text = it.data.status
                        binding.progressBar5.visibility = View.INVISIBLE
                        dialog.dismiss()
                    }
                    is Resource.Unspecified ->{

                    }
                }
            }
        }
    }

    private fun retreiveMessages() {
        lifecycleScope.launch {
            viewModel.messageCreate.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.progressBar5.visibility = View.INVISIBLE
                        Log.d("khan","message not received")
                    }
                    is Resource.Loading -> {
                        binding.progressBar5.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar5.visibility = View.INVISIBLE
                        binding.editTextText4.text.clear()
                        messages = it.data as MutableList<SingleMessage>
                        messageDetailAdapter.differ.submitList(it.data)

                    }
                    is Resource.Unspecified -> {

                    }

                }
            }
        }
    }

    private fun onSendClick() {
        binding.imageView22.setOnClickListener {
            val content = binding.editTextText4.text.toString()
            if(content.isNullOrBlank()){
                Toast.makeText(requireContext(), "type a message", Toast.LENGTH_SHORT).show()
            }
            else
            {
                val message = SingleMessage(
                    "",
                    authId,
                    content
                )
                Log.d("khan","before sending : ${messageModel.messages.size}")
                messages.add(message)
                messageModel.messages = messages
                viewModel.addNewMessage(messageModel)
            }
        }
    }

    private fun observeSendMessage() {
        lifecycleScope.launch {
            viewModel.sendMessage.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.progressBar5.visibility = View.INVISIBLE
                    }
                    is Resource.Loading -> {
                        binding.progressBar5.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar5.visibility = View.INVISIBLE
                        binding.editTextText4.text.clear()
                        Log.d("khan","after sending : ${messageModel.messages.size}")
                    }
                    is Resource.Unspecified -> {

                    }

                }
            }
        }
    }

    private fun observeGetGigs() {
        lifecycleScope.launch {
            viewModel.getGigs.collectLatest {
                when(it){
                    is Resource.Error -> {
                       dialogBinding.progressBar11.visibility = View.INVISIBLE
                    }
                    is Resource.Loading -> {
                        dialogBinding.progressBar11.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        dialogBinding.progressBar11.visibility = View.INVISIBLE
                        Log.d("khan","after getting gigs : ${it.data}")
                        if(it.data!=null){
                            gigList = it.data
                            it.data.forEach {
                                list.add(it.title)
                            }
                            setupCategorySpinner(list)
                        }
                    }
                    is Resource.Unspecified -> {

                    }

                }
            }
        }
    }

    private fun getData() {
        messageModel = navArgs.chat
        messages = messageModel.messages.toMutableList()
        messageDetailAdapter.differ.submitList(messages)
        if(FirebaseAuth.getInstance().uid.toString()==messageModel.providerId){
            binding.textView25.text = messageModel.providerName.toString()
        }
        else
        {
            binding.textView25.text = messageModel.userName.toString()
        }
        Log.d("khan","before messages ${messages}")
        viewModel.retrieveMessages(messageModel)
        setupType(messageModel.status)
    }

    private fun setupType(status: String) {
        if(messageModel.providerId==authId){
            binding.btnType.visibility = View.INVISIBLE
        }
        else
        {
            when(status){
                "chat"->{
                    binding.btnType.text = "ORDER"
                }
                "ordered" -> {
                    binding.btnType.text = "COMPLETE"
                }
            }
        }
    }

    private fun onClickListeners() {
        binding.ivBack.setOnClickListener {
            showBottomNavigation()
            findNavController().navigate(com.example.hazir.R.id.action_fragmentMessageDetail_to_fragmentMessage)
        }
        binding.btnType.setOnClickListener {
            when(messageModel.status){
                "chat" ->{
                    showCustomDialog(requireContext())
                }
                "ordered" ->{
                    val bundle  =Bundle().also {
                        it.putParcelable("mode",messageModel)
                    }
                    findNavController().navigate(R.id.action_fragmentMessageDetail_to_fragmentRating,bundle)
                }
                else ->{

                }
            }
        }
    }

    private fun showBottomNavigation() {
        (activity as MainActivity).binding.bottomNavigationView.visibility  = View.VISIBLE
    }

    private fun hideBottomNav() {
        (activity as MainActivity).binding.bottomNavigationView.visibility  = View.GONE
    }


    private fun setupRecyclerView() {
        messageDetailAdapter = MessageDetailAdapter()
        binding.rvMessageDetail.adapter = messageDetailAdapter
        binding.rvMessageDetail.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.rvMessageDetail.addItemDecoration(VerticalDecoration(50))
        getData()
    }

    private fun showCustomDialog(context: Context) {
        dialog = Dialog(context)
        dialogBinding = DialogSelectGigBinding.inflate(LayoutInflater.from(context))
        viewModel.getGigs(messageModel.providerId)
        dialog.setContentView(dialogBinding.root)
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun setupCategorySpinner(l : List<String>) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            l
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogBinding.editTextText.adapter = adapter
        onSpinnerClickListener()
    }
    private fun onSpinnerClickListener() {
        dialogBinding.editTextText.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, view: View?, position: Int, id: Long) {
                currentGigSelected = position
                Toast.makeText(requireContext(), "selected is at ${position}", Toast.LENGTH_SHORT).show()
                Glide.with(requireContext()).load(gigList[position].profileImage).into(dialogBinding.imageView16)
                dialogBinding.textView42.text = "$ " + gigList[position].startingPrice
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
            }
        }
        dialogBinding.button2.setOnClickListener {
            messageModel.status = "ordered"
            messageModel.gigId = gigList[currentGigSelected].id
            viewModel.changeStatusToOrdered(messageModel)
        }
    }



}
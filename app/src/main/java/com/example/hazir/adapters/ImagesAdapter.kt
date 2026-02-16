package com.example.hazir.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.hazir.models.ImageData
import com.example.hazir.databinding.RvImagesBinding

class ImagesAdapter : RecyclerView.Adapter<ImagesAdapter.ImagesAdapterViewHolder>(){

    inner class ImagesAdapterViewHolder(val binding : RvImagesBinding) : ViewHolder(binding.root){
    }

    private val diffUtil =object : DiffUtil.ItemCallback<ImageData>(){
        override fun areItemsTheSame(oldItem: ImageData, newItem: ImageData): Boolean {
            return oldItem.image==newItem.image
        }

        override fun areContentsTheSame(oldItem: ImageData, newItem: ImageData): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this,diffUtil)



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImagesAdapterViewHolder {
        return ImagesAdapterViewHolder(
            RvImagesBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: ImagesAdapterViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.binding.apply {
            ivImageSelected.setImageURI(item.image)
        }
        holder.binding.ivDelete.setOnClickListener {
            onClick?.invoke(item)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick : ((ImageData) -> Unit)? = null


}
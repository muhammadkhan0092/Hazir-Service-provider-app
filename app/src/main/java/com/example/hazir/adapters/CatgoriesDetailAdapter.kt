package com.example.hazir.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.hazir.R
import com.example.hazir.data.GigData
import com.example.hazir.databinding.RvCatgoriesDetailItemBinding

class CatgoriesDetailAdapter : RecyclerView.Adapter<CatgoriesDetailAdapter.CatgoriesDetailViewHolder>(){

    inner class CatgoriesDetailViewHolder(val binding : RvCatgoriesDetailItemBinding) : ViewHolder(binding.root){
    }

    private val diffUtil =object : DiffUtil.ItemCallback<GigData>(){
        override fun areItemsTheSame(oldItem: GigData, newItem: GigData): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: GigData, newItem: GigData): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this,diffUtil)



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CatgoriesDetailViewHolder {
        return CatgoriesDetailViewHolder(
            RvCatgoriesDetailItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: CatgoriesDetailViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.binding.apply {
            Glide.with(holder.itemView).load(item.profileImage).into(ivGigIImage)
            textView49.text = item.distance.toString() + "km"
            if (item.reviews == null || item.reviews.size == 0) {
                tvRating.text = "0"
                tvTotalOrders.text = "(0)"
            }
                else{
            val totalReviews = item.reviews.size.toDouble()
            var sum: Double = 0.0
            item.reviews.forEach {
                sum += it.rating.toInt().toDouble()
            }
            val avg = sum / totalReviews
            tvRating.text = avg.toString()
            tvTotalOrders.text = "(" + item.reviews.size.toString() + ")"
        }
            tvGigTitle.text = item.title
            tvStartingPRICE.text = "Rs 8" + item.startingPrice.toString()
        }
        holder.itemView.setOnClickListener {
            onClick?.invoke(item)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick : ((GigData) -> Unit)? = null


}
package com.example.hazir.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.hazir.data.DataComments
import com.example.hazir.databinding.RvCommentBinding

class CommentsAdapter : RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder>(){

    inner class CommentsViewHolder(val binding : RvCommentBinding) : ViewHolder(binding.root){
    }

    private val diffUtil =object : DiffUtil.ItemCallback<DataComments>(){
        override fun areItemsTheSame(oldItem: DataComments, newItem: DataComments): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: DataComments, newItem: DataComments): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this,diffUtil)



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentsViewHolder {
        return CommentsViewHolder(
            RvCommentBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.binding.apply {
            tvSentMessage.text = item.content
            Log.d("khan","setting content ${item.content}")
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }



}
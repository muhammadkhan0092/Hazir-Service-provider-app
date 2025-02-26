package com.example.hazir.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.hazir.data.SingleMessage
import com.example.hazir.databinding.RvMessageReceivedBinding
import com.example.hazir.databinding.RvMessageSentBinding
import com.google.firebase.auth.FirebaseAuth

class MessageDetailAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_SENT = 1
    private val VIEW_TYPE_RECEIVED = 2

    private val diffUtil =object : DiffUtil.ItemCallback<SingleMessage>(){
        override fun areItemsTheSame(oldItem: SingleMessage, newItem: SingleMessage): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: SingleMessage, newItem: SingleMessage): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this,diffUtil)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SENT -> {
                val view = RvMessageSentBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                SentMessageViewHolder(view)
            }
            VIEW_TYPE_RECEIVED -> {
                val view = RvMessageReceivedBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                ReceivedMessageViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = differ.currentList[position]

        when (holder) {
            is SentMessageViewHolder -> {
                holder.bind(message)
            }
            is ReceivedMessageViewHolder -> {
                holder.bind(message)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (differ.currentList[position].senderId!=FirebaseAuth.getInstance().uid){
            return 1
        }
        else {
            return 2
        }
    }

    class SentMessageViewHolder(val binding : RvMessageSentBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: SingleMessage) {
          binding.tvSentMessage.text = message.content
        }
    }

    class ReceivedMessageViewHolder(val binding : RvMessageReceivedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: SingleMessage) {
            binding.tvReceivedMessage.text = message.content
        }
    }
}

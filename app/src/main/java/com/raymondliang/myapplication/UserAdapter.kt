package com.raymondliang.myapplication

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.raymondliang.myapplication.UserAdapter.DoctorItemViewHolder
import com.raymondliang.myapplication.databinding.UserPicsBinding
import java.util.*

class UserAdapter(private val userList: ArrayList<DoctorItem>, private val onClickListener: UserAdapterOnClickHandler) : RecyclerView.Adapter<DoctorItemViewHolder>() {
    interface UserAdapterOnClickHandler {
        fun onClick(doctorItem: DoctorItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorItemViewHolder {
        return DoctorItemViewHolder(UserPicsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: DoctorItemViewHolder, position: Int) {
        val currentItem = userList[position]
        holder.bind(currentItem, onClickListener)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class DoctorItemViewHolder(private val binding: UserPicsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(currentItem: DoctorItem, onClickListener: UserAdapterOnClickHandler) {
            binding.root.setOnClickListener { view: View? -> onClickListener.onClick(currentItem) }
            val rm = Glide.with(itemView)
            val rb: RequestBuilder<Drawable>
            rb = if (currentItem.url == null) {
                rm.load(currentItem.drawableIdBackup)
            } else {
                rm.load(currentItem.url)
            }
            rb.transition(DrawableTransitionOptions().dontTransition())
                    .into(binding.imageView)
            binding.textView.text = currentItem.doctorName
            binding.ivAvailability.setImageResource(if (currentItem.availability) R.drawable.button_online else R.drawable.button_offline)
            binding.textView2.text = if (currentItem.availability) "Online" else "Offline"
        }
    }
}
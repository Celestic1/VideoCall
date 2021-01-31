package com.raymondliang.myapplication;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.raymondliang.myapplication.databinding.UserPicsBinding;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.DoctorItemViewHolder> {
    private final ArrayList<DoctorItem> userList;
    private final UserAdapterOnClickHandler onClickListener;

    public interface UserAdapterOnClickHandler {
        void onClick(DoctorItem doctorItem);
    }

    public UserAdapter(ArrayList<DoctorItem> userList, UserAdapterOnClickHandler listener) {
        this.userList = userList;
        onClickListener = listener;
    }

    @Override
    public DoctorItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DoctorItemViewHolder(UserPicsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(DoctorItemViewHolder holder, int position) {
        DoctorItem currentItem = userList.get(position);
        holder.bind(currentItem, onClickListener);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class DoctorItemViewHolder extends RecyclerView.ViewHolder {
        private final UserPicsBinding binding;
        public DoctorItemViewHolder(final UserPicsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final DoctorItem currentItem, final UserAdapterOnClickHandler onClickListener) {
            binding.getRoot().setOnClickListener(view -> onClickListener.onClick(currentItem));

            RequestManager rm = Glide.with(itemView);
            final RequestBuilder<Drawable> rb;
            if (currentItem.url == null) {
                rb = rm.load(currentItem.drawableIdBackup);
            } else {
                rb = rm.load(currentItem.url);
            }
            rb.transition(new DrawableTransitionOptions().dontTransition())
                    .into(binding.imageView);

            binding.textView.setText(currentItem.doctorName);
            binding.ivAvailability.setImageResource(currentItem.availability ? R.drawable.button_online : R.drawable.button_offline);
            binding.textView2.setText(currentItem.availability ? "Online" : "Offline");
        }
    }
}
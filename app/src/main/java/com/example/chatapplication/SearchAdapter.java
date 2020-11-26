package com.example.chatapplication;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private ArrayList<User> searchUser;
    private ArrayList<Bitmap> imageList;

    @NonNull
    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_friends_list, parent, false);
        SearchAdapter.SearchViewHolder searchViewHolder = new SearchAdapter.SearchViewHolder(view, searchOnItemClickListener);
        return searchViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.SearchViewHolder holder, int position) {

        User currentItem = searchUser.get(position);
        holder.searchUsername.setText(currentItem.getName());
        holder.searchAddIcon.setBackgroundResource(R.drawable.add_icon);
        if (imageList.size() == 0) {
            holder.searchUserImage.setImageResource(R.drawable.defaultavatar);
            return;
        }
        holder.searchUserImage.setImageBitmap(imageList.get(position));
        // currentItem.isFriend()
        // holder.searchAddIcon = "Den ena ellse båda"
    }

    @Override
    public int getItemCount() {
        return searchUser.size();
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {

        public ImageView searchUserImage;
        public TextView searchUsername;
        public ImageView searchAddIcon;


        public SearchViewHolder(@NonNull View itemView, final SearchAdapter.OnItemClickListener listener) {
            super(itemView);
            searchUserImage = itemView.findViewById(R.id.search_user_image);
            searchUsername = itemView.findViewById(R.id.search_user_name);
            searchAddIcon = itemView.findViewById(R.id.search_add_icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null) {
                        if (position != RecyclerView.NO_POSITION) {
                            listener.OnItemClicked(position, v);
                        }
                    }
                }
            });
            searchAddIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null) {
                        if (position != RecyclerView.NO_POSITION) {
                            listener.OnAddIconClicked(position, v);
                        }
                    }
                }
            });

        }
    }


    public SearchAdapter(ArrayList<User> searchUser, ArrayList<Bitmap> imageList) {
        this.searchUser = searchUser;
        this.imageList = imageList;
        notifyDataSetChanged();
    }

    interface OnItemClickListener {
        void OnItemClicked(int position, View view);
        void OnAddIconClicked(int position, View view);
    }

    private OnItemClickListener searchOnItemClickListener;

    public void setSearchOnItemClickListener(OnItemClickListener searchOnItemClickListener) {
        this.searchOnItemClickListener = searchOnItemClickListener;
    }



}

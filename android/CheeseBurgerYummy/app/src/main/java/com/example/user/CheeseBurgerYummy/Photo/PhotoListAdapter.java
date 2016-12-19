package com.example.user.CheeseBurgerYummy.Photo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.user.CheeseBurgerYummy.Network.ServiceGenerator;
import com.example.user.CheeseBurgerYummy.PhotoListActivity;
import com.example.user.CheeseBurgerYummy.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 12/15/16.
 */

public class PhotoListAdapter extends RecyclerView.Adapter<PhotoListAdapter.ViewHolder> {
    private ArrayList<PhotoBook> photoListItems = new ArrayList<PhotoBook>();

    public PhotoListAdapter(ArrayList<PhotoBook> photoListItems) {
        this.photoListItems = photoListItems;
    }

    private InterfacePhotoListAdapter interfacePhotoListAdapter;

    public void setInterfacePhotoListAdapter(InterfacePhotoListAdapter event) {
        this.interfacePhotoListAdapter = event;
    }

    public interface InterfacePhotoListAdapter {
        void clickedItem(PhotoBook photoDetail);
        void longClickedItem(int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_list_item, parent, false);
        PhotoListAdapter.ViewHolder holder = new PhotoListAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final PhotoListAdapter.ViewHolder holder, int position) {
        Picasso.with(PhotoListActivity.context).load(ServiceGenerator.BASE_URL + photoListItems.get(position).getImageThumbFile()).into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interfacePhotoListAdapter.clickedItem(photoListItems.get(holder.getAdapterPosition()));
            }
        });

        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                interfacePhotoListAdapter.longClickedItem(holder.getAdapterPosition());
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return photoListItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageView) ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
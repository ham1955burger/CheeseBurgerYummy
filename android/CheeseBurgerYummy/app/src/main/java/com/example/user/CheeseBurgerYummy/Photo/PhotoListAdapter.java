package com.example.user.cheeseburgeryummy.Photo;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.user.cheeseburgeryummy.Network.ServiceGenerator;
import com.example.user.cheeseburgeryummy.PhotoListActivity;
import com.example.user.cheeseburgeryummy.R;
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

        WindowManager windowManager = (WindowManager) PhotoListActivity.context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        holder.imageView.getLayoutParams().width = size.x / 2;
        holder.imageView.getLayoutParams().height = size.x / 2;

        /*
        if ((position + 1) % 2 == 0) {
            holder.imageView.setPadding(0, 0, 10, 10);
        } else {
            holder.imageView.setPadding(0, 0, 0, 10);
        }*/

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

    /*
    static class DividerItemDecoration extends RecyclerView.ItemDecoration {
        private int margin;
        private int columns;

        public DividerItemDecoration(int margin, int columns) {
            this.margin = margin;
            this.columns = columns;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {

            int position = parent.getChildLayoutPosition(view);

            //set right margin to all
            outRect.right = margin;
            //set bottom margin to all
            outRect.bottom = margin;
            //we only add top margin to the first row
            if (position <columns) {
                outRect.top = margin;
            }

            //add left margin only to the first column
            if(position%columns==0){
                outRect.right = margin;
            }
        }
    }*/

    public static class ItemDecorationAlbumColumns extends RecyclerView.ItemDecoration {

        private int mSizeGridSpacingPx;
        private int mGridSize;

        private boolean mNeedLeftSpacing = false;

        public ItemDecorationAlbumColumns(int gridSpacingPx, int gridSize) {
            mSizeGridSpacingPx = gridSpacingPx;
            mGridSize = gridSize;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int frameWidth = (int) ((parent.getWidth() - (float) mSizeGridSpacingPx * (mGridSize - 1)) / mGridSize);
            int padding = parent.getWidth() / mGridSize - frameWidth;
            int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewAdapterPosition();
            if (itemPosition < mGridSize) {
                outRect.top = 0;
            } else {
                outRect.top = mSizeGridSpacingPx;
            }
            if (itemPosition % mGridSize == 0) {
                outRect.left = 0;
                outRect.right = padding;
                mNeedLeftSpacing = true;
            } else if ((itemPosition + 1) % mGridSize == 0) {
                mNeedLeftSpacing = false;
                outRect.right = 0;
                outRect.left = padding;
            } else if (mNeedLeftSpacing) {
                mNeedLeftSpacing = false;
                outRect.left = mSizeGridSpacingPx - padding;
                if ((itemPosition + 2) % mGridSize == 0) {
                    outRect.right = mSizeGridSpacingPx - padding;
                } else {
                    outRect.right = mSizeGridSpacingPx / 2;
                }
            } else if ((itemPosition + 2) % mGridSize == 0) {
                mNeedLeftSpacing = false;
                outRect.left = mSizeGridSpacingPx / 2;
                outRect.right = mSizeGridSpacingPx - padding;
            } else {
                mNeedLeftSpacing = false;
                outRect.left = mSizeGridSpacingPx / 2;
                outRect.right = mSizeGridSpacingPx / 2;
            }
            outRect.bottom = 0;
        }
    }
}
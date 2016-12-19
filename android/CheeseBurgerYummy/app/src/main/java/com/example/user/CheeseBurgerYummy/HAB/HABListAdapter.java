package com.example.user.CheeseBurgerYummy.HAB;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.example.user.CheeseBurgerYummy.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 12/13/16.
 */

public class HABListAdapter extends RecyclerView.Adapter<HABListAdapter.ViewHolder> {
    private ArrayList<HouseholdAccountBook> habListItems = new ArrayList<HouseholdAccountBook>();

    private InterfaceHABListAdapter interfaceHABListAdapter;

    public void setInterfaceHABListAdapter(InterfaceHABListAdapter event) {
        this.interfaceHABListAdapter = event;
    }

    public interface InterfaceHABListAdapter {
        void clickedItem(int position);
        void deletedItem(int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hab_list_item, parent, false);
        HABListAdapter.ViewHolder holder = new HABListAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final HABListAdapter.ViewHolder holder, int position) {
        holder.itemLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interfaceHABListAdapter.clickedItem(holder.getAdapterPosition());
            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interfaceHABListAdapter.deletedItem(holder.getAdapterPosition());
            }
        });

        holder.stateTextView.setText(habListItems.get(position).getState());
        holder.categoryPriceTextView.setText(habListItems.get(position).getCategory() + " / " + habListItems.get(position).getPrice());
        holder.memoTextView.setText(habListItems.get(position).getMemo());
    }

    @Override
    public int getItemCount() {
        return habListItems.size();
    }

    public HABListAdapter(ArrayList<HouseholdAccountBook> habListItems) {
        this.habListItems = habListItems;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.swipeLayout) SwipeLayout swipeLayout;
        @BindView(R.id.bottom_wrapper) LinearLayout bottomWrapperLinearLayout;
        @BindView(R.id.deleteButton) Button deleteButton;
        @BindView(R.id.itemLinearLayout) LinearLayout itemLinearLayout;
        @BindView(R.id.stateTextView) TextView stateTextView;
        @BindView(R.id.categoryPriceTextView) TextView categoryPriceTextView;
        @BindView(R.id.memoTextView) TextView memoTextView;

        /*
        private TextView stateTextView;
        private TextView categoryPriceTextView;
        private TextView memoTextView;*/


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            swipeLayout.addDrag(SwipeLayout.DragEdge.Top, bottomWrapperLinearLayout);
            swipeLayout.setClickToClose(true);
            swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onStartOpen(SwipeLayout layout) {

                }

                @Override
                public void onOpen(SwipeLayout layout) {

                }

                @Override
                public void onStartClose(SwipeLayout layout) {

                }

                @Override
                public void onClose(SwipeLayout layout) {

                }

                @Override
                public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

                }

                @Override
                public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

                }
            });
            /*
            stateTextView = (TextView) itemView.findViewById(R.id.stateTextView);
            categoryPriceTextView = (TextView) itemView.findViewById(R.id.categoryPriceTextView);
            memoTextView = (TextView) itemView.findViewById(R.id.memoTextView);*/
        }
    }

    public void addItem(String state, String category, int price, String memo, int pk, String date) {
        HouseholdAccountBook item = new HouseholdAccountBook();

        item.setState(state);
        item.setCategory(category);
        item.setPrice(price);
        item.setMemo(memo);
        item.setPk(pk);
        item.setDate(date);

        habListItems.add(item);
    }
}

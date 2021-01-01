package com.example.xyzreader;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;


public class MyItemRecyclerViewAdapter extends PagedListAdapter<String, MyItemRecyclerViewAdapter.ViewHolder> {

    // private final ArrayList<String> mValues;
    private static DiffUtil.ItemCallback<String> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<String>() {
                @Override
                public boolean areItemsTheSame(String oldConcert, String newConcert) {
                    return oldConcert == newConcert;
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(String oldConcert, String newConcert) {
                    return oldConcert == newConcert;
                }
            };

    public MyItemRecyclerViewAdapter() {
        super(DIFF_CALLBACK);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detail_recyclerview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        String concert = getItem(position);

        if (concert != null) {
            holder.mContentView.setText(concert);
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {

            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }


}
package com.example.xyzreader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class MainListAdapter extends ListAdapter<Book, MainListAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<Book> RESULT_COMPARATOR = new DiffUtil.ItemCallback<Book>() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public boolean areItemsTheSame(@NonNull Book oldItem, @NonNull Book newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Book oldItem, @NonNull Book newItem) {
            return oldItem == newItem;
        }
    };
    final private ItemClickListener mItemClickListener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2, 1, 1);
    private SimpleDateFormat outputFormat = new SimpleDateFormat();

    // Pass in the listener into the constructor
    public MainListAdapter(ItemClickListener listener) {
        super(RESULT_COMPARATOR);
        mItemClickListener = listener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View BookItem = inflater.inflate(R.layout.main_recyclerview_item, parent, false);

        // Return a new holder instance
        return new ViewHolder(BookItem);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public int getItemCount() {

        return getCurrentList().size();
    }

    public void setThumbnail(Book book, View itemView, ImageView thumbnailView) {

        String fullPath = book.getPhoto();
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_launcher_background);
        requestOptions.error(R.drawable.baseline_cloud_off_24);

        Glide.with(itemView.getContext())
                .load(fullPath)
                .apply(requestOptions)
                .into(thumbnailView);
    }

    public void setSubtitle(Book book, TextView subtitleView) {

        Date publishedDate;
        try {
            String date = book.getPublishedDate();
            publishedDate = dateFormat.parse(date);
        } catch (ParseException ex) {
            Log.e(TAG, ex.getMessage());
            Log.i(TAG, "passing today's date");
            publishedDate = new Date();
        }

        if (!publishedDate.before(START_OF_EPOCH.getTime())) {

            subtitleView.setText(Html.fromHtml(
                    DateUtils.getRelativeTimeSpanString(
                            publishedDate.getTime(),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString()
                            + "<br/>" + " by "
                            + book.getAuthor()));
        } else {
            subtitleView.setText(Html.fromHtml(
                    outputFormat.format(publishedDate)
                            + "<br/>" + " by "
                            + book.getAuthor()));
        }

    }

    public interface ItemClickListener {
        void onItemClickListener(int itemID);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView thumbnailView;
        public TextView titleView;
        public TextView subtitleView;

        ViewHolder(View itemView) {
            super(itemView);
            thumbnailView = itemView.findViewById(R.id.thumbnail);
            titleView = itemView.findViewById(R.id.article_title);
            subtitleView = itemView.findViewById(R.id.article_subtitle);
            itemView.setOnClickListener(this);
        }

        @SuppressLint("CheckResult")
        void bind(Book result) {
            titleView.setText(result.getTitle());
            setThumbnail(result, itemView, thumbnailView);
            setSubtitle(result, subtitleView);
        }


        @Override
        public void onClick(View v) {
            int itemID = getAdapterPosition();
            mItemClickListener.onItemClickListener(itemID);
        }
    }


}

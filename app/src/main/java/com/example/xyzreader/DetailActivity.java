package com.example.xyzreader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.util.Date;

import static com.example.xyzreader.Constants.EXTRA_RECIPE_ID;
import static com.example.xyzreader.Constants.START_OF_EPOCH;
import static com.example.xyzreader.Constants.dateFormat;
import static com.example.xyzreader.Constants.outputFormat;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity.Tag";
    Book[] book;
    CollapsingToolbarLayout collapsingToolbar;
    LinearLayoutManager mLayoutManager;
    MyItemRecyclerViewAdapter adapter = new MyItemRecyclerViewAdapter();
    String bookTitle;
    String bookAuthor;
    SharedPreferences appSharedPrefs;
    private ImageView mPhotoView;
    private int bookID;
    private TextView titleView;
    private TextView bylineView;
    private RecyclerView bodyView;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private DetailViewModel detailViewModel;
    private FastScroller fastScroller;
    private int mSavedLocation;
    private boolean snackbarShown;
    private LinearLayout metaBar;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Respond to the action bar's Up/Home button
        if (item.getItemId() == android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Transition enterTransition = TransitionInflater.from(this).inflateTransition(R.transition.slide_right);
        getWindow().setEnterTransition(enterTransition);

        Intent intent = getIntent();
        bookID = intent.getIntExtra(EXTRA_RECIPE_ID, 0);
        book = MoviesRepository.getInstance().getRecipesArray();

        appSharedPrefs = getPreferences(Context.MODE_PRIVATE);
        mSavedLocation = appSharedPrefs.getInt(String.valueOf(bookID), 0);

        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        fastScroller = (FastScroller) findViewById(R.id.fastscroll);
        mPhotoView = findViewById(R.id.photo);
        titleView = findViewById(R.id.article_title);
        bylineView = findViewById(R.id.article_byline);
        bodyView = findViewById(R.id.recyclerView);
        toolbar = findViewById(R.id.toolbar);
        collapsingToolbar = findViewById(R.id.toolbar_layout);
        metaBar = findViewById(R.id.meta_bar);

        mLayoutManager = new LinearLayoutManager(this) {
            @Override
            public void onLayoutCompleted(RecyclerView.State state) {

                super.onLayoutCompleted(state);
                mLayoutManager.scrollToPositionWithOffset(mSavedLocation, 0);

                if (mSavedLocation != 0 && !snackbarShown) {
                    appBarLayout.setExpanded(false, false);

                    Snackbar.make(findViewById(R.id.coordinatorLayout), "Restored last location..." + mSavedLocation + ".", Snackbar.LENGTH_LONG)
                            .setAction("Go UP", new MyUndoListener())
                            .show();

                    snackbarShown = true;
                }


            }
        };

        bodyView.setLayoutManager(mLayoutManager);
        setActivityTitle();
        bookTitle = book[bookID].getTitle();
        bookAuthor = book[bookID].getAuthor();

        // Setup viewmodel, once text is prepared, submit a list to adapter
        MyDetailsViewModelFactory factory = new MyDetailsViewModelFactory(this.getApplication(), bookID);
        detailViewModel = new ViewModelProvider(this, factory).get(DetailViewModel.class);
        detailViewModel.concertList.observe(this, new Observer<PagedList<String>>() {
            @Override
            public void onChanged(PagedList<String> strings) {
                adapter.submitList(strings);
            }
        });

        detailViewModel.isSnackbarShwon().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean snackbarBoolean) {
                snackbarShown = snackbarBoolean;
            }
        });

        bodyView.setAdapter(adapter);

        fastScroller.setRecyclerView(bodyView);
        setupFabButton();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // set photo, byline, title
        setPhoto();
        titleView.setText(bookTitle);
        setByline();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {

        detailViewModel.isSnackbarShwon().postValue(snackbarShown);
        mSavedLocation = mLayoutManager.findFirstVisibleItemPosition();
        SharedPreferences.Editor prefsEditor;
        prefsEditor = appSharedPrefs.edit();
        prefsEditor.putInt(String.valueOf(bookID), mSavedLocation);
        prefsEditor.apply();

        super.onPause();

    }

    private void setByline() {
        Date publishedDate = parsePublishedDate();
        if (!publishedDate.before(START_OF_EPOCH.getTime())) {
            bylineView.setText(Html.fromHtml(
                    DateUtils.getRelativeTimeSpanString(
                            publishedDate.getTime(),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString()
                            + " by <font color='#ffffff'>"
                            + book[bookID].getAuthor()
                            + "</font>"));

        } else {
            // If date is before 1902, just show the string
            bylineView.setText(Html.fromHtml(
                    outputFormat.format(publishedDate) + " by <font color='#ffffff'>"
                            + book[bookID].getAuthor()
                            + "</font>"));
        }
    }

    private Date parsePublishedDate() {
        try {
            String date = book[bookID].getPublishedDate();
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            Log.e(TAG, ex.getMessage());
            Log.i(TAG, "passing today's date");
            return new Date();
        }
    }

    public void setActivityTitle() {

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                int actionbarsize = toolbar.getHeight();

                int higher = appBarLayout.getTotalScrollRange();
                int lower = appBarLayout.getTotalScrollRange() - actionbarsize;

                if (Math.abs(verticalOffset) <= higher && Math.abs(verticalOffset) >= lower) {
                    // Between fully expanded and collapsed
                    collapsingToolbar.setTitle(book[bookID].getTitle());
                    metaBar.setVisibility(View.GONE);
                } else {
                    metaBar.setVisibility(View.VISIBLE);
                    collapsingToolbar.setTitle("");
                }
            }
        });
    }

    private void setupFabButton() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hi! I am now reading \"" + (bookTitle) + "\" by " + bookAuthor + "...");
                sendIntent.setType("text/plain");
                Intent.createChooser(sendIntent, "Share via");
                startActivity(sendIntent);

            }
        });
    }

    private void setPhoto() {
        String fullPath = book[bookID].getPhoto();

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.color.colorGrey);
        requestOptions.error(R.drawable.baseline_cloud_off_24);
        requestOptions.format(DecodeFormat.PREFER_RGB_565);

        Glide.with(this)
                .load(fullPath)
                .apply(requestOptions)
                .into(mPhotoView);

    }


    public class MyUndoListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            mLayoutManager.scrollToPositionWithOffset(0, 0);
            appBarLayout.setExpanded(true, true);
            mSavedLocation = 0;
        }
    }
}


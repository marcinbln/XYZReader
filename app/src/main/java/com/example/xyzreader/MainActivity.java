package com.example.xyzreader;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.AppBarLayout;

import java.util.Arrays;

import static com.example.xyzreader.Constants.EXTRA_RECIPE_ID;

public class MainActivity extends AppCompatActivity implements MainListAdapter.ItemClickListener {


    ImageView logoBig;
    ImageView logoSmall;
    boolean tabletSize;
    ConstraintLayout emptyTv;
    private MainListAdapter adapter;
    private boolean isCalculated = false;
    private float avatarAnimateStartPointY = 0F;
    private float avatarCollapseAnimationChangeWeight = 0F;
    private float EXPANDED_LOGO_SIZE = 0F;
    private float COLLAPSED_LOGO_SIZE = 0F;
    private float horizontalToolbarAvatarMargin = 0f;
    private SwipeRefreshLayout swipeRefreshLayout;

    private AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Transition transition2 = TransitionInflater.from(this).inflateTransition(R.transition.slide_left);
        getWindow().setExitTransition(transition2);

        setContentView(R.layout.activity_main);

        tabletSize = getResources().getBoolean(R.bool.isTablet);
        logoBig = findViewById(R.id.bigLogo_iv);
        logoSmall = findViewById(R.id.logoSmall_iv);
        appBarLayout = (AppBarLayout) findViewById(R.id.toolbar_container);
        emptyTv = findViewById(R.id.empty_tv);
        swipeRefreshLayout = findViewById(R.id.main_swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this::invalidateDataSource);

        handleOnScrollEvents();

        // Get screen width
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        if (tabletSize) {
            EXPANDED_LOGO_SIZE = getResources().getDimensionPixelSize(R.dimen.app_width);
        } else {
            EXPANDED_LOGO_SIZE = size.x;
        }

        COLLAPSED_LOGO_SIZE = getResources().getDimension(R.dimen.default_collapsed_image_size);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        adapter = new MainListAdapter(this);
        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.list_column_count));
        recyclerView.setLayoutManager(mLayoutManager);

        // Create a viewmodel and set an observer on a list of books, once it is downloaded submit it to the adapter
        BooksViewModel moviesViewModel = new ViewModelProvider(this).get(BooksViewModel.class);
        moviesViewModel.getRecipesVM().observe(this, new Observer<Book[]>() {
                    @Override
                    public void onChanged(Book[] recipes) {
                        emptyTv.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter.submitList(Arrays.asList(recipes));
                    }
                }
        );

    }

    private void invalidateDataSource() {
        swipeRefreshLayout.setRefreshing(true);
        MoviesRepository.getInstance().downloadData();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onItemClickListener(int itemID) {

        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(EXTRA_RECIPE_ID, itemID);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this);
        startActivity(intent, options.toBundle());


    }

    public void handleOnScrollEvents() {

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                // Calculate animation change weight
                if (!isCalculated) {
                    avatarAnimateStartPointY = Math.abs((appBarLayout.getHeight() - (EXPANDED_LOGO_SIZE + horizontalToolbarAvatarMargin)) / appBarLayout.getTotalScrollRange());
                    avatarCollapseAnimationChangeWeight = 1 / (1 - avatarAnimateStartPointY);
                    isCalculated = true;
                }

                float offsetFloat = (Math.abs(((float) verticalOffset / (float) (appBarLayout.getTotalScrollRange()))));

                updateLogoSize(offsetFloat);


                // Appbar fully expanded
                if (verticalOffset == 0) {
                    logoSmall.setVisibility(View.INVISIBLE);
                } else if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    // Appbar fully collapsed
                    logoSmall.setVisibility(View.VISIBLE);
                    logoBig.setVisibility(View.INVISIBLE);
                } else {
                    // Between fully expanded and collapsed
                    logoSmall.setVisibility(View.INVISIBLE);
                    logoBig.setVisibility(View.VISIBLE);
                    logoBig.setTranslationX(0.05f * Math.abs(verticalOffset));
                    logoBig.setTranslationY(-0.01f * Math.abs(verticalOffset));
                }
            }
        });
    }

    private void updateLogoSize(Float offset) {

        ViewGroup.LayoutParams layoutParams = logoBig.getLayoutParams();

        if (offset > avatarAnimateStartPointY) {
            float avatarCollapseAnimateOffset = (offset - avatarAnimateStartPointY) * avatarCollapseAnimationChangeWeight;
            float avatarSize = EXPANDED_LOGO_SIZE - (EXPANDED_LOGO_SIZE - COLLAPSED_LOGO_SIZE) * avatarCollapseAnimateOffset;
            layoutParams.width = Math.round(avatarSize);
            layoutParams.height = Math.round(avatarSize);
            logoBig.requestLayout();

        } else {
            if (layoutParams.height != (int) EXPANDED_LOGO_SIZE) {
                layoutParams.height = (int) EXPANDED_LOGO_SIZE;
                layoutParams.width = (int) EXPANDED_LOGO_SIZE;
                logoBig.requestLayout();
            }
        }
    }

}
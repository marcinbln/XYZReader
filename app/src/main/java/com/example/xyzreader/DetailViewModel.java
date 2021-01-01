package com.example.xyzreader;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import java.util.Arrays;
import java.util.List;

public class DetailViewModel extends AndroidViewModel {

    public LiveData<PagedList<String>> concertList = new MutableLiveData<>();
    String bodyText;
    int mBookId;
    private MutableLiveData<Boolean> snackbarShown = new MutableLiveData<>();

    public DetailViewModel(@NonNull Application application, int bookID) {
        super(application);
        mBookId = bookID;
        prepareText();
    }

    public MutableLiveData<Boolean> isSnackbarShwon() {
        return snackbarShown;
    }

    public void prepareText() {

        bodyText = MoviesRepository.getInstance().getRecipesArray()[mBookId].getBody();

        // Replace single \\r\\n with white space
        String pattern = "(?<!\\r\\n)(\\r\\n)(?!\\r\\n)";
        bodyText = bodyText.replaceAll(pattern, " ");

        //Replace more than 1 whitespace
        bodyText = bodyText.replaceAll("\\ +", " ");

        List<String> arrayList = Arrays.asList(bodyText.split("\r\n"));

        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(arrayList.size())
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(40)
                .setPrefetchDistance(40)
                .build();

        concertList = new LivePagedListBuilder<Integer, String>(new BooksDataSourceFactory(arrayList), config)
                .build();

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {

            }

        });

    }


}


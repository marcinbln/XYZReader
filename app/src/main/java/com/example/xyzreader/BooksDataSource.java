package com.example.xyzreader;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

import java.util.List;

public class BooksDataSource extends PositionalDataSource<String> {
    private List<String> mintegerBodyFactory;

    public BooksDataSource(List<String> integerBodyFactory) {
        mintegerBodyFactory = integerBodyFactory;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<String> callback) {

        int totalCount = computeCount();
        int position = computeInitialLoadPosition(params, totalCount);
        int loadSize = computeInitialLoadSize(params, position, totalCount);

        List<String> list = loadRangeInternal(position, loadSize);
        callback.onResult(list, position, totalCount);
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<String> callback) {

        Log.v("loadRange", String.valueOf(params.startPosition));
        List<String> list = loadRangeInternal(params.startPosition, params.startPosition + params.loadSize);
        callback.onResult(list);

    }

    private int computeCount() {
        return mintegerBodyFactory.size();
    }

    private List<String> loadRangeInternal(int startPosition, int loadCount) {
        return mintegerBodyFactory.subList(startPosition, loadCount);
    }


}

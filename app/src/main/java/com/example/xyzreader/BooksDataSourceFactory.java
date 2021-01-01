package com.example.xyzreader;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;

import java.util.List;

public class BooksDataSourceFactory extends DataSource.Factory<Integer, String> {

    public BooksDataSource moviesPageKeyedDataSource;
    List<String> mlist;

    public BooksDataSourceFactory(List<String> list) {
        //default query parameters = movie discover
        moviesPageKeyedDataSource = new BooksDataSource(list);
        mlist = list;
    }

    @NonNull
    @Override
    public DataSource<Integer, String> create() {
        return new BooksDataSource(mlist);
    }
}
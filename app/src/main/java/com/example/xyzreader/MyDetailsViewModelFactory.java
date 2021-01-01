package com.example.xyzreader;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MyDetailsViewModelFactory implements ViewModelProvider.Factory {
    Application mApplication;
    int mBookId;

    public MyDetailsViewModelFactory(Application application, int bookId) {
        mApplication = application;
        mBookId = bookId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DetailViewModel(mApplication, mBookId);
    }
}

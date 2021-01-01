package com.example.xyzreader;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class BooksViewModel extends AndroidViewModel {

    private final MoviesRepository repository;

    public BooksViewModel(@NonNull Application application) {
        super(application);
        repository = MoviesRepository.getInstance();
    }

    public MutableLiveData<Book[]> getRecipesVM() {
        return repository.getRecipesRepo();
    }

}

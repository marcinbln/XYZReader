package com.example.xyzreader;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.xyzreader.network.RetrofitClient;
import com.example.xyzreader.network.RetrofitServiceBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesRepository {

    public static MoviesRepository instance;
    private final MutableLiveData<Book[]> mRecipes;
    private Book[] mRecipesArray;

    private MoviesRepository() {

        mRecipes = new MediatorLiveData<>();

        downloadData();

    }

    public static MoviesRepository getInstance() {

        if (instance == null) {
            instance = new MoviesRepository();
        }
        return instance;
    }

    public MutableLiveData<Book[]> getRecipesRepo() {
        return mRecipes;
    }

    public void downloadData() {

        RetrofitClient service = RetrofitServiceBuilder.buildService(RetrofitClient.class);
        Call<Book[]> call = service.getJsonFile();

        call.enqueue(new Callback<Book[]>() {
            @Override
            public void onResponse(@NonNull Call<Book[]> call, @NonNull Response<Book[]> response) {

                final Book[] listView = response.body();

                mRecipes.postValue(listView);
                mRecipesArray = listView;

            }

            @Override
            public void onFailure(@NonNull Call<Book[]> call, @NonNull Throwable t) {
                Log.v("callFailure", String.valueOf(t));
            }
        });

    }

    public Book[] getRecipesArray() {
        return mRecipesArray;
    }


}

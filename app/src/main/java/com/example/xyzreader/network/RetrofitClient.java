package com.example.xyzreader.network;


import com.example.xyzreader.Book;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RetrofitClient {

    // url
    // https://d17h27t6h515a5.cloudfront.net/topher/2017/March/58c5d68f_xyz-reader/xyz-reader.json

    @GET("xyz-reader.json")
    Call<Book[]> getJsonFile();

}


